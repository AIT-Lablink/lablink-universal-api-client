//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

/**
 * Collection of helper functions for the ERIGrid Universal API schema.
 */
public class SchemaHelper {

  /** Regular expression defining valid ChannelIds. */
  public static final String CHANNEL_ID_REGEX = "^[a-zA-Z0-9-_/.:]+$";

  /**
   * Check if string conforms to ChannelId definition.
   *
   * @param channelId  string to be checked
   * @return true, if string conforms to ChannelId definition
   */
  public static boolean checkChannelId( String channelId ) {
    return channelId.matches( CHANNEL_ID_REGEX );
  }
}
