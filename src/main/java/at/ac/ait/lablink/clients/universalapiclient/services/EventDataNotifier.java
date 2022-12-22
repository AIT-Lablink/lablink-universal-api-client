//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.services;

import at.ac.ait.lablink.clients.universalapiclient.universalapi.Event;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.EventChannel;

import at.ac.ait.lablink.core.service.IServiceStateChangeNotifier;
import at.ac.ait.lablink.core.service.LlService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Class EventDataNotifier.
 *
 * <p>This class updates the state of a signal in case the
 * corresponding Lablink data service receives a new value.
 */
public class EventDataNotifier<T> implements IServiceStateChangeNotifier<LlService, T> {

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( "EventDataNotifier" );

  /** Reference to associated channel. */
  private EventChannel<T> channel;

  /**
   * Set reference to associated channel.
   *
   * @param channel associated channel
   */
  public void setChannel( EventChannel<T> channel ) {
    this.channel = channel;
  }

  /**
   * @see at.ac.ait.lablink.core.service.IServiceStateChangeNotifier#stateChanged(
   * java.lang.Object, java.lang.Object, java.lang.Object
   * )
   */
  @Override
  public void stateChanged( LlService service, T oldVal, T newVal ) {
    if ( null != this.channel ) {
      this.channel.addEvent( newVal, 1e-3 * Double.valueOf( System.currentTimeMillis() ) );
      logger.info( "{}: notifier -> added new event with value '{}'",
          service.getName(), oldVal, newVal);
    } else {
      logger.info( "no channel associated with this state change notifier" );
    }
  }

}
