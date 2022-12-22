//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.handlers;

import at.ac.ait.lablink.clients.universalapiclient.UniversalApiClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Class InfoHandler.
 *
 * <p>This class implements part of the REST interface of the ERIGrid Universal API for data 
 * exchange. It returns the API information (handler for enpoint '/info').
 */
public class InfoHandler implements HttpHandler {
  
  /** Endpoint of this handler. */
  public static final String ENDPOINT = "info";

  /** Used for (de-)serialization of JSON data. */
  private ObjectMapper mapper = new ObjectMapper();

  /** Singleton instance of ERIGrid Universal API client. */
  private UniversalApiClient uapiClient = UniversalApiClient.getInstance();

  /** Lenght of URI prefix. */
  private int endpointUriPrefixLength;

  public InfoHandler() {
    // Specify length of URI prefix.
    this.endpointUriPrefixLength = uapiClient.getEndpointUriPrefix().length() + 2;
  }

  @Override
  public void handle( HttpExchange exchange ) throws IOException {

    String endpoint = exchange.getRequestURI().getPath().substring( endpointUriPrefixLength );

    if ( false == ENDPOINT.equals( endpoint ) ) {
      exchange.sendResponseHeaders( Response.Status.NOT_FOUND.getStatusCode(), -1 );
    } else if ( "GET".equals( exchange.getRequestMethod() ) ) {
      String response = this.mapper.writeValueAsString( uapiClient.getInfo() );

      exchange.getResponseHeaders().add( "Content-Type", MediaType.APPLICATION_JSON );
      exchange.sendResponseHeaders( Response.Status.OK.getStatusCode(), response.length() );

      OutputStream outputStream = exchange.getResponseBody();
      outputStream.write( response.getBytes( "UTF-8" ) );
      outputStream.close();
    } else {
      exchange.getResponseHeaders().add( "Allow", "GET" );
      exchange.sendResponseHeaders( Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), -1 );
    }
  }
}
