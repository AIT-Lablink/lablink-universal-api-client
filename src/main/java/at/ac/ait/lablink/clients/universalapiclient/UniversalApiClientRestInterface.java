//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient;

import at.ac.ait.lablink.clients.universalapiclient.universalapi.SchemaHelper;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Signal;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.SignalState;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Class UniversalApiClientRestInterface.
 *
 * <p>This client implements the REST interface of the ERIGrid Universal API for data exchange.
 */
@Path( "/" )
public class UniversalApiClientRestInterface {

  /** Singleton instance of ERIGrid Universal API client. */
  private UniversalApiClient uapiClient = UniversalApiClient.getInstance();

  /**
   * Return the general information about this client.
   *
   * @return response for REST interface
   */
  @GET
  @Path( "info" )
  @Produces( MediaType.APPLICATION_JSON )
  public Response getApiInfos() {
    return Response.ok( uapiClient.getInfo() ).build();
  }

  /**
   * Return the user-provided configuration about this client.
   *
   * @return response for REST interface
   */
  @GET
  @Path( "config" )
  @Produces( MediaType.APPLICATION_JSON )
  public Response getConfiguration() {
    return Response.ok( uapiClient.getConfiguration() ).build();
  }

  /**
   * Return a list of descriptions for all available signals.
   *
   * @return response for REST interface
   */
  @GET
  @Path( "signals" )
  @Produces( MediaType.APPLICATION_JSON )
  public Response listAllAvailableSignals() {
    return Response.ok( uapiClient.getSignalDescriptions() ).build();
  }

  /**
   * Return the signal state for a specific signal.
   *
   * @param id SignalId
   * @return response for REST interface
   */
  @GET
  @Path( "signal/{id:.+}/state" )
  @Produces( MediaType.APPLICATION_JSON )
  public Response getCurrentValueOfSignal( @PathParam( "id" ) String id ) {
    if ( false == SchemaHelper.checkSignalId( id ) ) {
      return Response.status( Response.Status.BAD_REQUEST ).build();
    }

    Signal signal = uapiClient.getSignal( id );

    if ( null != signal ) {
      if ( true == signal.getDescription().getReadable() ) {
        return Response.ok( signal.getState() ).build();
      } else {
        // FIXME: Should this return METHOD_NOT_ALLOWED?
        return Response.status( Response.Status.BAD_REQUEST ).build();
      }
    } else {
      return Response.status( Response.Status.NOT_FOUND ).build();
    }
  }


  /**
   * Set the signal state for a specific signal.
   *
   * @param id SignalId
   * @param updateState new signal state
   * @return response for REST interface
   */
  @PUT
  @Path( "signal/{id:.+}/state" )
  @Consumes( MediaType.APPLICATION_JSON )
  public Response updateExistingSignal( @PathParam( "id" ) String id, SignalState updateState ) {
    if ( false == SchemaHelper.checkSignalId( id ) ) {
      return Response.status( Response.Status.BAD_REQUEST ).build();
    }

    Signal signal = uapiClient.getSignal( id );

    if ( null != signal ) {
      if ( true == signal.getDescription().getWritable() ) {
        try {
          // Set the new value. This also triggers an update of the signal 
          // state via the associated state change notifier.
          uapiClient.setServiceValue( id, updateState.getValue() );

          // The previous call updated the timestamp of the signal to the
          // current system time. The following lines overwrite this 
          // with the timestamp received as input.
          SignalState state = signal.getState();
          state.setTimestamp( updateState.getTimestamp() );

          return Response.ok().build();
        } catch ( Exception ex ) {
          return Response.status( Response.Status.METHOD_NOT_ALLOWED ).build();
        }
      } else {
        // FIXME: Should this return METHOD_NOT_ALLOWED?
        return Response.status( Response.Status.BAD_REQUEST ).build();
      }
    } else {
      return Response.status( Response.Status.NOT_FOUND ).build();
    }
  }

}
