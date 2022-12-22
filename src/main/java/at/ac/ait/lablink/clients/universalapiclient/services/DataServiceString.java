//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.services;

import at.ac.ait.lablink.core.service.LlServiceString;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class UniversalApiClientData.
 * Subclass from a Service variation and provide the implementation
 */
public class DataServiceString extends LlServiceString {

  private static final Logger logger = LogManager.getLogger( "DataServiceString" );

  /**
   * @see at.ac.ait.lablink.core.service.LlService#get()
   */
  @Override
  public String get() {
    return this.getCurState();
  }

  /**
   * @see at.ac.ait.lablink.core.service.LlService#set( java.lang.Object )
   */
  @Override
  public boolean set( String newVal ) {
    logger.info( "{}: set new value to '{}'", this.getName(), newVal );
    this.setCurState( newVal );
    return true;
  }
}