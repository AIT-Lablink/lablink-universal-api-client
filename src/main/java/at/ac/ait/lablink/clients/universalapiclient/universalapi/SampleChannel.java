//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

/**
 * Class SampleChannel.
 *
 * <p>This class implements a data exchange channel for the
 * <a href="https://github.com/ERIGrid2/JRA-3.1-api">ERIGrid Universal API</a>
 */
public class SampleChannel<T>  implements Channel {

  /** Channel description. */
  private ChannelDescription description;

  /** Sample. */
  private Sample<T> sample;

  /** Datapoint name corresponding to the Channel. */
  private String dataPointName;

  /** 
   * Constructor.
   *
   * @param dpn datapoint name corresponding to the channel
   * @param id channel ID
   * @param payload payload type (samples or events)
   * @param datatype data type (float, integer, etc.)
   * @param ts time source (synchronized, unsynchronized, etc.)
   * @param val validity (valid, invalid, etc.)
   * @param src source type (process, test, etc.)
   * @param range allowd range for sample / event values (only for float, integer and string) 
   * @param unit associated unit (e.g., SI unit)
   * @param rate expected update rate
   * @param writable this flag indicates if the channel can be written
   * @param readable this flag indicates if the channel can be read
  */
  public SampleChannel( String dpn, String id, 
      ChannelDescription.Payload payload, 
      ChannelDescription.Datatype datatype,
      TimeSource ts, Validity val, Source src,
      Object range, String unit, Number rate, 
      boolean writable, boolean readable ) {
    this.dataPointName = dpn;
    this.description = new ChannelDescription( id, payload,
      datatype, range, unit, rate, writable, readable );
    this.sample = new Sample<T>( ts, val, src );
  }

  /**
   * Retrieve Channel description.
   *
   * @return Channel description
   */
  public ChannelDescription getDescription() {
    return this.description;
  }

  /**
   * Retrieve Channel sample.
   *
   * @return Channel sample
   */
  public Sample<T> getSample() {
    return this.sample;
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
