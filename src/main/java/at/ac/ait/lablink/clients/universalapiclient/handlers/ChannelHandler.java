//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.handlers;

import at.ac.ait.lablink.clients.universalapiclient.UniversalApiClient;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Channel;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.ChannelDescription;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.ComplexSerializer;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.ComplexValue;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Event;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.EventChannel;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Sample;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.SampleChannel;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.SchemaHelper;
import at.ac.ait.lablink.core.service.types.Complex;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.net.URI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Class ChannelHandler.
 *
 * <p>This class implements part of the REST interface of the ERIGrid Universal API for
 * data exchange. It allows to get / set a sample value or event (handler for endpoints
 * '/channel/{id}/sample' and '/channel/{id}/event').
 */
public class ChannelHandler implements HttpHandler {

  /** Endpoint of this handler. */
  public static final String ENDPOINT = "channel";

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( "ChannelHandler" );

  /** Used for (de-)serialization of JSON data. */
  private ObjectMapper mapper = new ObjectMapper();

  /** Singleton instance of ERIGrid Universal API client. */
  private UniversalApiClient uapiClient = UniversalApiClient.getInstance();

  /** Lenght of URI prefix. */
  private int endpointUriPrefixLength;

  /**
   * Constructor.
   */
  public ChannelHandler() {
    // Specify length of URI prefix.
    this.endpointUriPrefixLength = uapiClient.getEndpointUriPrefix().length() + 2;

    // Add serializer for type complex.
    SimpleModule moduleComplexSerializer = new SimpleModule();
    moduleComplexSerializer.addSerializer( Complex.class, new ComplexSerializer() );
    mapper.registerModule( moduleComplexSerializer );
  }

  @Override
  public void handle( HttpExchange exchange ) throws IOException {

    // Retrieve enpoint of HTTP request (with URI prefix).
    String endpoint = exchange.getRequestURI().getPath().substring( endpointUriPrefixLength );

    logger.debug( "Handle request for endpoint: " + endpoint  );

    // Split components of endpoint.
    String[] endpointSegments = endpoint.split("/");

// Incorrect endpoint.
    if ( false == ENDPOINT.equals( endpointSegments[0] ) || ( endpointSegments.length < 3 ) ) {
      exchange.sendResponseHeaders( Response.Status.NOT_FOUND.getStatusCode(), -1 );
      return;
    }

    // Get payload type.
    String payload = endpointSegments[ endpointSegments.length - 1 ];

    // Get channel ID.
    String channelId = String.join( "/",
        Arrays.copyOfRange( endpointSegments, 1, endpointSegments.length - 1 ) );

    // Handle case of invalid channel ID.
    if ( false == SchemaHelper.checkChannelId( channelId ) ) {
      // Error message.
      String response = "Invalid / malformed channel ID supplied";
      // Send back BAD_REQUEST status.
      exchange.sendResponseHeaders( Response.Status.BAD_REQUEST.getStatusCode(),
          response.length() );
      // Send response.
      OutputStream outputStream = exchange.getResponseBody();
      outputStream.write( response.getBytes( "UTF-8" ) );
      outputStream.close();
      return;
    }

    if ( "sample".equals( payload ) ) {
      // Handle request for samples.
      handleSample( channelId, exchange );
    } else if ( "event".equals( payload ) ) {
      // Handle request for events.
      handleEvent( channelId, exchange );
    } else {
      // Invalid request, send back NOT_FOUND status.
      exchange.sendResponseHeaders( Response.Status.NOT_FOUND.getStatusCode(), -1 );
    }
  }

  private void handleSample( String channelId, HttpExchange exchange ) throws IOException {

    logger.debug("Handle sample request for channel ID: " + channelId );

    // Retrieve channel.
    Channel channel = uapiClient.getChannel( channelId );

    String response = null;

    if ( null == channel ) {
      // This channel ID does not exist.
      response = "Channel ID not found";
      exchange.sendResponseHeaders( Response.Status.NOT_FOUND.getStatusCode(),
          response.length() );
    } else if ( channel.getDescription().getPayloadEnum() != ChannelDescription.Payload.SAMPLES ) {
      // The channel associated with this channel ID does not support samples.
      response = "Validation error: This channel does not supports samples.";
      exchange.sendResponseHeaders( Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),
          response.length() );
    } else if ( "GET".equals( exchange.getRequestMethod() ) ) {
      // Retrieve sample value in case the channel is readable.
      if ( true == channel.getDescription().getReadable() ) {
        try {
          // Get the sample value and serialize it as JSON string.
          response = this.mapper.writeValueAsString( ( (SampleChannel) channel ).getSample() );
          // Set content type to JSON.
          exchange.getResponseHeaders().add( "Content-Type", MediaType.APPLICATION_JSON );
          // Send back OK status.
          exchange.sendResponseHeaders( Response.Status.OK.getStatusCode(),
              response.length() );
        } catch ( Exception ex ) {
          response = "Validation error: " + ex.toString();
          exchange.sendResponseHeaders( Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),
              response.length() );
        }
      } else {
        // Channel is not readable, send back FORBIDDEN status.
        response = "Channel not readable";
        exchange.sendResponseHeaders( Response.Status.FORBIDDEN.getStatusCode(),
            response.length() );
      }
    } else if ( "PUT".equals( exchange.getRequestMethod() ) ) {
      // Set new sample value in case the channel is writable.
      if ( true == channel.getDescription().getWritable() ) {
        try {
          String responseBody = getResponseBodyAsString( exchange );
          Sample updateSample = null;

          // De-serialize the JSON string and set the new value. This also triggers an update
          // of the sample via the associated state change notifier.
          ChannelDescription.Datatype datatype = channel.getDescription().getDatatypeEnum();
          switch ( datatype ) {
            case BOOLEAN:
              JavaType typeSampleBoolean =
                  mapper.getTypeFactory().constructParametricType(Sample.class, Boolean.class);
              Sample<Boolean> updateSampleBoolean =
                  mapper.readValue(responseBody, typeSampleBoolean);
              updateSample = updateSampleBoolean;
              uapiClient.setServiceValueBoolean( channel.getDataPointName(),
                  updateSampleBoolean.getValue() );
              break;
            case FLOAT:
              JavaType typeSampleFloat =
                  mapper.getTypeFactory().constructParametricType(Sample.class, Double.class);
              Sample<Double> updateSampleFloat =
                  mapper.readValue(responseBody, typeSampleFloat);
              updateSample = updateSampleFloat;
              uapiClient.setServiceValueDouble( channel.getDataPointName(),
                  updateSampleFloat.getValue() );
              break;
            case INTEGER:
              JavaType typeSampleInteger =
                  mapper.getTypeFactory().constructParametricType(Sample.class, Long.class);
              Sample<Long> updateSampleInteger =
                  mapper.readValue(responseBody, typeSampleInteger);
              updateSample = updateSampleInteger;
              uapiClient.setServiceValueLong( channel.getDataPointName(),
                  updateSampleInteger.getValue() );
              break;
            case STRING:
              JavaType typeSampleString =
                  mapper.getTypeFactory().constructParametricType(Sample.class, String.class);
              Sample<String> updateSampleString =
                  mapper.readValue(responseBody, typeSampleString);
              updateSample = updateSampleString;
              uapiClient.setServiceValueString( channel.getDataPointName(),
                  updateSampleString.getValue() );
              break;
            case COMPLEX:
              JavaType typeSampleComplex =
                  mapper.getTypeFactory().constructParametricType(Sample.class, ComplexValue.class);
              Sample<ComplexValue> updateSampleComplex =
                  mapper.readValue(responseBody, typeSampleComplex);
              updateSample = updateSampleComplex;
              uapiClient.setServiceValueComplex( channel.getDataPointName(),
                  updateSampleComplex.getValue() );
              break;
            default:
              // This default switch cannot be reached.
              break;
          }

          // The previous call updated the timestamp of the sample to the
          // current system time. The following lines overwrite this
          // with the timestamp received as input.
          Sample sample = ( (SampleChannel) channel ).getSample();
          sample.setTimestamp( updateSample.getTimestamp() );

          // Send back OK status.
          response = "Success. Channel has been updated.";
          exchange.sendResponseHeaders( Response.Status.OK.getStatusCode(),
              response.length() );
        } catch ( Exception ex ) {
          response = "Validation error: " + ex.toString();
          exchange.sendResponseHeaders( Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),
              response.length() );
        }
      } else {
        // Channel is not writable, send back FORBIDDEN status.
        response = "Channel not writable";
        exchange.sendResponseHeaders( Response.Status.FORBIDDEN.getStatusCode(),
            response.length() );
      }
    } else {
      // Unsupported request, send back METHOD_NOT_ALLOWED.
      exchange.getResponseHeaders().add( "Allow", "GET, PUT" );
      exchange.sendResponseHeaders( Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), -1 );
    }

    // Send response.
    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write( response.getBytes( "UTF-8" ) );
    outputStream.close();
  }

  private void handleEvent( String channelId, HttpExchange exchange ) throws IOException {

    logger.debug("Handle event request for channel ID: " + channelId );

    // Retrieve channel.
    Channel channel = uapiClient.getChannel( channelId );

    String response = null;

    if ( null == channel ) {
      // This channel ID does not exist.
      response = "Channel ID not found";
      exchange.sendResponseHeaders( Response.Status.NOT_FOUND.getStatusCode(),
          response.length() );
    } else if ( channel.getDescription().getPayloadEnum() != ChannelDescription.Payload.EVENTS ) {
      // The channel associated with this channel ID does not support samples.
      response = "Validation error: This channel does not supports events.";
      exchange.sendResponseHeaders( Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),
          response.length() );
    } else if ( "GET".equals( exchange.getRequestMethod() ) ) {
      // Retrieve events in case the channel is readable.
      if ( true == channel.getDescription().getReadable() ) {
        try {
          // Get query parameters.
          Map<String,String> queryParameters = getQueryParameters( exchange );

          // Retrieve list of events.
          List<Event> events = getEvents( queryParameters, channel );

          // Serialize list of events to JSON string.
          response = this.mapper.writeValueAsString( events );
          exchange.getResponseHeaders().add( "Content-Type", MediaType.APPLICATION_JSON );
          exchange.sendResponseHeaders( Response.Status.OK.getStatusCode(),
              response.length() );
        } catch ( Exception ex ) {
          response = "Validation error: " + ex.toString();
          exchange.sendResponseHeaders( Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),
              response.length() );
        }
      } else {
        // This channel is not readable, send back FORBIDDEN status.
        response = "Channel not readable";
        exchange.sendResponseHeaders( Response.Status.FORBIDDEN.getStatusCode(),
            response.length() );
      }
    } else if ( "PUT".equals( exchange.getRequestMethod() ) ) {
      // Set new event in case the channel is writable.
      if ( true == channel.getDescription().getWritable() ) {
        try {
          String responseBody = getResponseBodyAsString( exchange );
          Event updateEvent = null;

          // De-serialize the JSON string and set the new value. This also triggers an update
          // of the event via the associated state change notifier.
          ChannelDescription.Datatype datatype = channel.getDescription().getDatatypeEnum();
          switch ( datatype ) {
            case BOOLEAN:
              JavaType typeEventBoolean =
                  mapper.getTypeFactory().constructParametricType(Event.class, Boolean.class);
              Event<Boolean> updateEventBoolean =
                  mapper.readValue(responseBody, typeEventBoolean);
              updateEvent = updateEventBoolean;
              uapiClient.setServiceValueBoolean( channel.getDataPointName(),
                  updateEventBoolean.getValue() );
              break;
            case FLOAT:
              JavaType typeEventFloat =
                  mapper.getTypeFactory().constructParametricType(Event.class, Double.class);
              Event<Double> updateEventFloat =
                  mapper.readValue(responseBody, typeEventFloat);
              updateEvent = updateEventFloat;
              uapiClient.setServiceValueDouble( channel.getDataPointName(),
                  updateEventFloat.getValue() );
              break;
            case INTEGER:
              JavaType typeEventInteger =
                  mapper.getTypeFactory().constructParametricType(Event.class, Long.class);
              Event<Long> updateEventInteger =
                  mapper.readValue(responseBody, typeEventInteger);
              updateEvent = updateEventInteger;
              uapiClient.setServiceValueLong( channel.getDataPointName(),
                  updateEventInteger.getValue() );
              break;
            case STRING:
              JavaType typeEventString =
                  mapper.getTypeFactory().constructParametricType(Event.class, String.class);
              Event<String> updateEventString =
                  mapper.readValue(responseBody, typeEventString);
              updateEvent = updateEventString;
              uapiClient.setServiceValueString( channel.getDataPointName(),
                  updateEventString.getValue() );
              break;
            case COMPLEX:
              JavaType typeEventComplex =
                  mapper.getTypeFactory().constructParametricType(Event.class, ComplexValue.class);
              Event<ComplexValue> updateEventComplex =
                  mapper.readValue(responseBody, typeEventComplex);
              updateEvent = updateEventComplex;
              uapiClient.setServiceValueComplex( channel.getDataPointName(),
                  updateEventComplex.getValue() );
              break;
            default:
              // This default switch cannot be reached.
              break;
          }

          // The previous call updated the timestamp of the event to the
          // current system time. The following lines overwrite this
          // with the timestamp received as input.
          List<Event> events = getEvents( null, channel );
          // List<Event> events = ( (EventChannel) channel ).getEvents();
          Event event = events.get( events.size() - 1 );
          event.setTimestamp( updateEvent.getTimestamp() );

          // Send back OK status.
          response = "Success. Channel has been updated.";
          exchange.sendResponseHeaders( Response.Status.OK.getStatusCode(),
              response.length() );
        } catch ( Exception ex ) {
          response = "Validation error: " + ex.toString();
          exchange.sendResponseHeaders( Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),
              response.length() );
        }
      } else {
        // Channel not writable, send back FORBIDDEN status.
        response = "Channel not writable";
        exchange.sendResponseHeaders( Response.Status.FORBIDDEN.getStatusCode(),
            response.length() );
      }
    } else {
      // Unsupported request, send back METHOD_NOT_ALLOWED status.
      exchange.getResponseHeaders().add( "Allow", "GET, PUT" );
      exchange.sendResponseHeaders( Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), -1 );
    }

    // Send response.
    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write( response.getBytes( "UTF-8" ) );
    outputStream.close();
  }

  private String getResponseBodyAsString( HttpExchange exchange )
      throws UnsupportedEncodingException, IOException {
    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
    BufferedReader br = new BufferedReader(isr);

    int bufChar;
    StringBuilder buf = new StringBuilder();
    while ( ( bufChar = br.read() ) != -1 ) {
      buf.append( (char) bufChar );
    }

    br.close();
    isr.close();

    return buf.toString();
  }

  @SuppressWarnings( "unchecked" )
  private List<Event> getEvents( Map<String, String> queryParameters, Channel channel ) {
    if ( ( null != queryParameters ) && queryParameters.containsKey( "since_id" ) ) {
      Integer sinceId = Integer.valueOf( queryParameters.get( "since_id" ) );
      return ( (EventChannel) channel ).getEvents( sinceId );
    } else {
      return ( (EventChannel) channel ).getEvents();
    }
  }

  private Map<String,String> getQueryParameters( HttpExchange exchange ) {
    String query = exchange.getRequestURI().getQuery();

    if ( query == null ) {
      return null;
    }

    Map<String, String> result = new HashMap<>();
    for ( String param : query.split( "&" ) ) {
      String[] entry = param.split( "=" );
      if ( entry.length > 1 ) {
        result.put( entry[0], entry[1] );
      } else {
        result.put(entry[0], "");
      }
    }
    return result;
  }
}