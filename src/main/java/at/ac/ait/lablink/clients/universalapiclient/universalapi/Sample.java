//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class Sample.
 *
 * <p>This class implements a sample for a data exchange channel for the
 * <a href="https://github.com/ERIGrid2/JRA-3.1-api">ERIGrid Universal API</a>.
 * It can be translated to/from JSON for use with the REST interface.
 */
@XmlRootElement
public class Sample<T> {

  /** sample timestamp. */
  private Double timestamp;

  /** sample value. */
  private T value;

  private TimeSource timeSource;

  private Validity validity;
  
  
  private Source source;

  /**
   * Constructor.
   * @param ts time source (synchronized, unsynchronized, etc.)
   * @param val validity (valid, invalid, etc.)
   * @param src source (process, test, etc.)
   */
  public Sample( TimeSource ts, Validity val, Source src ) {
    this.timestamp = Double.NaN;    
    this.value = null;
    this.timeSource = ts;
    this.validity = val;
    this.source = src;
  }

  public Sample() {}

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
  public void setTimestamp(double timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Retrieve sample value.
   *
   * @return sample value (as double)
   */
  public T getValue() {
    return this.value;
  }

  /**
    * Set sample value.
    *
    * @param value  sample value (as double)
    */
  public void setValue(T value) {
    this.value = value;
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
