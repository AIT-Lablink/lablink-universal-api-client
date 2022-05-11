//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

/**
 * Collection of helper functions for the ERIGrid Universal API schema.
 */
public class SchemaHelper {

  /** Regular expression defining valid SignalIds. */
  public static final String SIGNAL_ID_REGEX = "^[a-z0-9-_/]+$";

  /** Regular expression defining valid NodeIds. */
  public static final String NODE_ID_REGEX = "^[a-z0-9-]{3,}$";

  /**
   * Check if string conforms to SignalId definition.
   *
   * @param signalId  string to be checked
   * @return true, if string conforms to SignalId definition
   */
  public static boolean checkSignalId( String signalId ) {
    return signalId.matches( SIGNAL_ID_REGEX );
  }

  /**
   * Check if string conforms to NodelId definition.
   *
   * @param nodeId  string to be checked
   * @return true, if string conforms to NodeId definition
   */
  public static boolean checkNodeId( String nodeId ) {
    return nodeId.matches( NODE_ID_REGEX );
  }

}
