//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

/**
 * Interface for channels.
 */ 
public interface Channel {

  /**
   * Retrieve Channel description.
   *
   * @return Channel description
   */
  public ChannelDescription getDescription();

  /**
   * Retrieve the datapoint name.
   *
   * @return datapoint name
   */
  public String getDataPointName();

}
