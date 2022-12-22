//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class ChannelDescription.
 *
 * <p>This class implements the description of a data exchange channel for the
 * <a href="https://github.com/ERIGrid2/JRA-3.1-api">ERIGrid Universal API</a>.
 * It can be translated to/from JSON for use with the REST interface.
 */
@XmlRootElement
public class ChannelDescription {

  public enum Payload { EVENTS, SAMPLES }

  public enum Datatype { FLOAT, COMPLEX, INTEGER, STRING, BOOLEAN }

  /** SignalId. */
  private String id;

  /** Payload type. */
  private Payload payload;

  /** Data type. */
  private Datatype datatype;

  /** Range. **/
  private Object range;

  /** Associated physical unit. */
  private String unit;
  
  /** Expected refresh-rate of this channel in Hertz . */
  private Number rate;

  /** This flag indicates if the signal can be written. */
  private boolean writable;

  /** This flag indicates if the signal can be read. */
  private boolean readable;

  /**
   * Constructor.
   *
   * @param id ChannelId
   * @param payload payload type (sample or event)
   * @param datatype data type
   * @param range allowd range for sample / event values (only for float, integer and string) 
   * @param unit physical unit
   * @param rate expected refresh rate
   * @param writable this flag indicates if channel is writable
   * @param readable this flag indicates if channel is readable
   */
  public ChannelDescription( String id, Payload payload, Datatype datatype,
      Object range, String unit, Number rate, boolean writable, boolean readable ) {
    if ( false == SchemaHelper.checkChannelId( id ) ) {
      throw new IllegalArgumentException( "Invalid ChannelId: " + id );
    }
    this.id = id;
    this.payload = payload;
    this.datatype = datatype;
    this.range = range;
    this.unit = unit;
    this.rate = rate;
    this.writable = writable;
    this.readable = readable;
  }

  /**
   * Get ChannelId of the signal.
   *
   * @return channel ID
   */
  public String getId() {
    return this.id;
  }

  /**
   * Get payload type.
   *
   * @return payload type
   */
  public String getPayload() {
    return this.payload.toString().toLowerCase();
  }

  @JsonIgnore
  public Payload getPayloadEnum()  {
    return this.payload;
  }

  /**
   * Get data type.
   *
   * @return data type
   */
  public String getDatatype() {
    return this.datatype.toString().toLowerCase();
  }

  @JsonIgnore
  public Datatype getDatatypeEnum()  {
    return this.datatype;
  }

  @XmlElement( nillable = false )
  public Object getRange() {
    return this.range;
  }


  /**
   * Get unit.
   *
   * @return unit
   */
  @XmlElement( nillable = false )
  public String getUnit() {
    return this.unit;
  }

  /**
   * Get rate.
   *
   * @return rate
   */
  @XmlElement( nillable = false )
  public Number getRate() {
    return this.rate;
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
