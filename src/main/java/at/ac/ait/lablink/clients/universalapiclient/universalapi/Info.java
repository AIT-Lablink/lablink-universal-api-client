//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class SignalDescription.
 *
 * <p>This class implements the API info returned by the REST interface of the
 * <a href="https://github.com/ERIGrid2/JRA-3.1-api">ERIGrid Universal API</a>.
 * It can be translated to/from JSON for use with the REST interface.
 */
@XmlRootElement
public class Info {

  /** NodeId. */
  private String id;

  /** Transport info. */
  private Transport transport;

  /**
   * Constructor.
   *
   * @param id NodeId
   */
  public Info( String id ) {
    this.id = id;
    this.transport = new Transport();
  }
  
  /**
   * Retrieve NodeId.
   *
   * @return NodeId
   */
  public String getId() {
    return this.id;
  }

  /**
   * Retrieve transport info.
   *
   * @return transport info
   */
  public Transport getTransport() {
    return this.transport;
  }

  /**
   * Class Transport.
   *
   * <p>This class implements the transport info returned by the REST interface of the
   * <a href="https://github.com/ERIGrid2/JRA-3.1-api">ERIGrid Universal API</a>.
   * It can be translated to/from JSON for use with the REST interface.
   */
  @XmlRootElement
  public class Transport {

    /**
     * Retrieve transport type.
     *
     * @return transport type
     */
    public String getType() {
      return this.getClass().getPackage().getImplementationTitle();
    }

    /**
     * Retrieve transport version.
     *
     * @return transport version
     */
    public String getVersion() {
      return this.getClass().getPackage().getImplementationVersion();
    }
  }

}
