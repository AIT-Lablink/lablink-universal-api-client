//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class SignalState.
 *
 * <p>This class implements the state of a data exchange signal for the
 * <a href="https://github.com/ERIGrid2/JRA-3.1-api">ERIGrid Universal API</a>.
 * It can be translated to/from JSON for use with the REST interface.
 */
@XmlRootElement
public class SignalState {

  /** Signal timestamp. */
  private double timestamp;

  /** Signal value. */
  private double value;

  /**
   * Constructor.
   */
  public SignalState() {
    this.timestamp = Double.NaN;
    this.value = Double.NaN;
  }

  /**
   * Retrieve signal timestamp.
   *
   * @return unix timestamp (as double)
   */
  public double getTimestamp() {
    return this.timestamp;
  }

  /**
    * Set signal timestamp.
    *
    * @param timestamp  unix timestamp (as double)
    */
  public void setTimestamp(double timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Retrieve signal value.
   *
   * @return signal value (as double)
   */
  public double getValue() {
    return this.value;
  }

  /**
    * Set signal value.
    *
    * @param value  signal value (as double)
    */
  public void setValue(double value) {
    this.value = value;
  }

}
