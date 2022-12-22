//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.services;

import at.ac.ait.lablink.clients.universalapiclient.universalapi.Sample;

import at.ac.ait.lablink.core.service.IServiceStateChangeNotifier;
import at.ac.ait.lablink.core.service.LlService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class SampleDataNotifier.
 *
 * <p>This class updates the state of a signal in case the
 * corresponding Lablink data service receives a new value.
 */
public class SampleDataNotifier<T> implements IServiceStateChangeNotifier<LlService, T> {

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( "SampleDataNotifier" );

  /** Reference to sample of associated channel. */
  private Sample<T> sample;

  /**
   * Set reference to sample associated to channel.
   *
   * @param sample  sample of associated channel
   */
  public void setSample( Sample<T> sample ) {
    this.sample = sample;
  }

  /**
   * @see at.ac.ait.lablink.core.service.IServiceStateChangeNotifier#stateChanged(
   * java.lang.Object, java.lang.Object, java.lang.Object
   * )
   */
  @Override
  public void stateChanged( LlService service, T oldVal, T newVal ) {
    if ( null != this.sample ) {
      sample.setTimestamp( 1e-3 * Double.valueOf( System.currentTimeMillis() ) );
      sample.setValue( newVal );
      logger.info( "{}: notifier -> state Changed from '{}' to '{}'",
          service.getName(), oldVal, newVal);
    } else {
      logger.info( "no signal associated with this state change notifier" );
    }
  }

}
