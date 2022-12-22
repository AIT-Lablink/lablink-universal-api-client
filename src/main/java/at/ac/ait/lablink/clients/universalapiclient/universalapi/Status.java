//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

import at.ac.ait.lablink.core.client.impl.LlClient;

/**
 * Class Status.
 *
 * <p>This class implements the API status returned by the REST interface of the
 * <a href="https://github.com/ERIGrid2/JRA-3.1-api">ERIGrid Universal API</a>.
 * It can be translated to/from JSON for use with the REST interface.
 */
public class Status {

  public enum TransportStatus {
    YES( "yes" ), 
    NO( "no" ), 
    UNKNOWN( "unknown" );

    private final String transportStatus;

    /**
     * Constructor.
     * @param ts transport status
     */
    TransportStatus( final String ts ) {
      this.transportStatus = ts;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      return this.transportStatus;
    }
  }

  private LlClient client;

  /** 
   * Constructor.
   *
   * @param client Lablink client.
  */
  public Status( LlClient client ) {
    this.client = client;
  }

  /**
   * Retrieve connection status.
   *
   * @return connection status
   */
  public String getConnected() {
    return this.client.isConnected()
        ? TransportStatus.YES.toString() : TransportStatus.NO.toString();
  }
}
