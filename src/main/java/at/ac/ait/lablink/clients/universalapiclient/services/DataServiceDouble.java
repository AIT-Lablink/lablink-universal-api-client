//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.services;

import at.ac.ait.lablink.core.service.LlServiceDouble;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class UniversalApiClientData.
 * Subclass from a Service variation and provide the implementation
 */
public class DataServiceDouble extends LlServiceDouble {

  private static final Logger logger = LogManager.getLogger( "DataServiceDouble" );

  /**
   * @see at.ac.ait.lablink.core.service.LlService#get()
   */
  @Override
  public Double get() {
    return this.getCurState();
  }

  /**
   * @see at.ac.ait.lablink.core.service.LlService#set( java.lang.Object )
   */
  @Override
  public boolean set( Double newVal ) {
    logger.info( "{}: set new value to '{}'", this.getName(), newVal );
    this.setCurState( newVal );
    return true;
  }
}