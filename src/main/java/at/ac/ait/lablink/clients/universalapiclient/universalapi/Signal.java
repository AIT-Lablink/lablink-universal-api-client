//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

/**
 * Class Signal.
 *
 * <p>This class implements a data exchange signal for the
 * <a href="https://github.com/ERIGrid2/JRA-3.1-api">ERIGrid Universal API</a>
 */
public class Signal {

  /** Signal description. */
  private SignalDescription description;

  /** Signal state. */
  private SignalState state;

  /** Datapoint name corresponding to the signal. */
  private String dataPointName;

  /** 
   * Constructor.
   *
   * @param dpn datapoint name corresponding to the signal
   * @param id SignalId
   * @param source NodeId
   * @param writable This flag indicates if the signal can be written
   * @param readable This flag indicates if the signal can be read
   */
  public Signal( String dpn, String id, String source, 
      boolean writable, boolean readable ) {
    this.dataPointName = dpn;
    this.description = new SignalDescription( id, source, writable, readable );
    this.state = new SignalState();
  }

  /**
   * Retrieve signal description.
   *
   * @return signal description
   */
  public SignalDescription getDescription() {
    return this.description;
  }

  /**
   * Retrieve signal state.
   *
   * @return signal state
   */
  public SignalState getState() {
    return this.state;
  }

  /**
   * Retrieve the datapoint name.
   *
   * @return datapoint name
   */
  public String getDataPointName() {
    return this.dataPointName;
  }

}
