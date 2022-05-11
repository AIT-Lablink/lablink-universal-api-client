//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class SignalDescription.
 *
 * <p>This class implements the description of a data exchange signal for the
 * <a href="https://github.com/ERIGrid2/JRA-3.1-api">ERIGrid Universal API</a>.
 * It can be translated to/from JSON for use with the REST interface.
 */
@XmlRootElement
public class SignalDescription {
 
  /** SignalId. */
  private String id;

  /** SourceId. */
  private String source;

  /** This flag indicates if the signal can be written. */
  private boolean writable;

  /** This flag indicates if the signal can be read. */
  private boolean readable;

  /**
   * Constructor.
   *
   * @param id SignalId
   * @param source SourceId
   * @param writable this flag indicates if the signal can be written
   * @param readable this flag indicates if the signal can be read
   */
  public SignalDescription( String id, String source, boolean writable, boolean readable ) {
    if ( false == SchemaHelper.checkSignalId( id ) ) {
      throw new IllegalArgumentException( "Invalid SignalId: " + id );
    }

    if ( false == SchemaHelper.checkNodeId( source ) ) {
      throw new IllegalArgumentException( "Invalid NodeId: " + source );
    }

    this.id = id;
    this.source = source;
    this.writable = writable;
    this.readable = readable;
  }

  /**
   * Get SignalId of the signal.
   *
   * @return SignalId
   */
  public String getId() {
    return this.id;
  }

  /**
   * Get NodeId of the signal source.
   *
   * @return NodeId
   */
  public String getSource() {
    return this.source;
  }

  /**
   * Get writable flag.
   *
   * @return writable flag
   */
  public boolean getWritable() {
    return this.writable;
  }

  /**
   * Get readable flag.
   *
   * @return readable flag
   */
  public boolean getReadable() {
    return this.readable;
  }
}
