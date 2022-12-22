//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient.universalapi;

import java.util.ArrayList;
import java.util.List;

/**
 * Class EventChannel.
 *
 * <p>This class implements a data exchange channel for the
 * <a href="https://github.com/ERIGrid2/JRA-3.1-api">ERIGrid Universal API</a>
 */
public class EventChannel<T> implements Channel {

  private static final Integer N_MAX_EVENTS = 100;

  /** Channel description. */
  private ChannelDescription description;

  /** Events. */
  private List<Event<T>> events;

  /** Datapoint name corresponding to the Channel. */
  private String dataPointName;

  private Integer nextEventId;

  private TimeSource timeSource;

  private Validity validity;

  private Source source;

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
  public EventChannel( String dpn, String id,
      ChannelDescription.Payload payload,
      ChannelDescription.Datatype datatype,
      TimeSource ts, Validity val, Source src,
      Object range, String unit, Number rate,
      boolean writable, boolean readable ) {
    this.dataPointName = dpn;
    this.description = new ChannelDescription( id, payload,
      datatype, range, unit, rate, writable, readable );
    this.events = new ArrayList<Event<T>>();
    this.nextEventId = 0;
    this.timeSource = ts;
    this.validity = val;
    this.source = src;
  }

  /**
   * Add a new event to this channel.
   *
   * @param value value of the event
   * @param timestamp time stamp of the event
   */
  @SuppressWarnings( "unchecked" )
  public void addEvent( T value, Double timestamp ) {
    if ( this.events.size() == N_MAX_EVENTS ) {
      this.events.remove( 0 );
    }
    this.events.add( new Event( value, timestamp, nextEventId, timeSource, validity, source ) );
    nextEventId++;
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
   * Retrieve list of events.
   *
   * @return list of events
   */
  public List<Event<T>> getEvents() {
    return this.events;
  }

  /**
   * Retrieve list of events.
   *
   * @param sinceId the oldest event ID the requester does not want to request
   * @return list of events
   */
  public List<Event<T>> getEvents( Integer sinceId ) {
    List<Event<T>> selectedEvents = new ArrayList<Event<T>>();

    for ( Event<T> ev : this.events ) {
      if ( ev.getId() > sinceId ) {
        selectedEvents.add( ev );
      }
    }

    return selectedEvents;
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
