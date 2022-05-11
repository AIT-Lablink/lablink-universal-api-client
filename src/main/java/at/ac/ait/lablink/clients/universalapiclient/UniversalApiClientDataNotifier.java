//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient;

import at.ac.ait.lablink.clients.universalapiclient.universalapi.Signal;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.SignalState;

import at.ac.ait.lablink.core.service.IServiceStateChangeNotifier;
import at.ac.ait.lablink.core.service.LlService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class UniversalApiClientDataNotifier.
 *
 * <p>This class updates the state of a signal in case the
 * corresponding Lablink data service receives a new value.
 */
class UniversalApiClientDataNotifier implements IServiceStateChangeNotifier<LlService, Double> {

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( "UniversalApiClientDataNotifier" );

  /** Reference to state of associated signal. */
  private SignalState signalState;

  /**
   * Set reference to state of associated signal.
   *
   * @param signalState  state of associated signal
   */
  public void setSignalState( SignalState signalState ) {
    this.signalState = signalState;
  }

  /**
   * @see at.ac.ait.lablink.core.service.IServiceStateChangeNotifier#stateChanged(
   * java.lang.Object, java.lang.Object, java.lang.Object
   * )
   */
  @Override
  public void stateChanged( LlService service, Double oldVal, Double newVal ) {
    if ( null != this.signalState ) {
      signalState.setTimestamp( 1e-3 * Double.valueOf( System.currentTimeMillis() ) );
      signalState.setValue( newVal );
      logger.info( "{}: notifier -> state Changed from '{}' to '{}'",
          service.getName(), oldVal, newVal);
    } else {
      logger.info( "no signal associated with this state change notifier" );
    }
  }

}
