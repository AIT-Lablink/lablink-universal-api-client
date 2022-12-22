//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class Event.
 *
 * <p>This class implements an event for a data exchange channel for the
 * <a href="https://github.com/ERIGrid2/JRA-3.1-api">ERIGrid Universal API</a>.
 * It can be translated to/from JSON for use with the REST interface.
 */
@XmlRootElement
public class Event<T> {

  /** Event value. */
  private T value;

  /** Event timestamp. */
  private Double timestamp;

  /** Event ID. */
  private Integer id;

  private TimeSource timeSource;

  private Validity validity;
  
  private Source source;

  /**
   * Constructor.
   * @param value value associated to event
   * @param timestamp time stamp of event
   * @param id event ID
   * @param ts time source (synchronized, unsynchronized, etc.)
   * @param val validity (valid, invalid, etc.)
   * @param src source (process, test, etc.)
  */
  public Event( T value, Double timestamp, Integer id, TimeSource ts, Validity val, Source src ) {
    this.value = value;
    this.timestamp = timestamp;    
    this.id = id;
    this.timeSource = ts;
    this.validity = val;
    this.source = src;
  }

  public Event() {}

  /**
   * Retrieve sample timestamp.
   *
   * @return unix timestamp (as double)
   */
  public double getTimestamp() {
    return this.timestamp;
  }

  /**
    * Set sample timestamp.
    *
    * @param timestamp  unix timestamp (as double)
    */
  @XmlElement( required = false )
  public void setTimestamp( double timestamp ) {
    this.timestamp = timestamp;
  }

  /**
   * Retrieve event value.
   *
   * @return event value
   */
  public T getValue() {
    return this.value;
  }

  /**
    * Set event value.
    *
    * @param value event value
    */
  public void setValue( T value ) {
    this.value = value;
  }

  /**
   * Retrieve event ID.
   *
   * @return event ID
   */
  public Integer getId() {
    return this.id;
  }

  /**
    * Set event ID.
    *
    * @param id event ID
    */
  public void setId( Integer id ) {
    this.id = id;
  }

  public String getTimesource() {
    return this.timeSource.toString().toLowerCase();
  }

  @XmlElement( required = false )
  public void setTimesource(String ts) {
    this.timeSource = TimeSource.valueOf( ts.toUpperCase() );
  }

  public String getValidity() {
    return this.validity.toString().toLowerCase();
  }

  @XmlElement( required = false )
  public void setValidity(String val) {
    this.validity = Validity.valueOf( val.toUpperCase() );
  }

  public String getSource() {
    return this.source.toString().toLowerCase();
  }

  @XmlElement( required = false )
  public void setSource(String src) {
    this.source = Source.valueOf( src.toUpperCase() );
  }
}
