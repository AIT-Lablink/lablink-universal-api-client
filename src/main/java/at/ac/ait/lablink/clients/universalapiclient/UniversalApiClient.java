//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient;

import at.ac.ait.lablink.clients.universalapiclient.handlers.ChannelDescriptionHandler;
import at.ac.ait.lablink.clients.universalapiclient.handlers.ChannelHandler;
import at.ac.ait.lablink.clients.universalapiclient.handlers.InfoHandler;
import at.ac.ait.lablink.clients.universalapiclient.handlers.StatusHandler;
import at.ac.ait.lablink.clients.universalapiclient.services.DataServiceBoolean;
import at.ac.ait.lablink.clients.universalapiclient.services.DataServiceComplex;
import at.ac.ait.lablink.clients.universalapiclient.services.DataServiceDouble;
import at.ac.ait.lablink.clients.universalapiclient.services.DataServiceLong;
import at.ac.ait.lablink.clients.universalapiclient.services.DataServiceString;
import at.ac.ait.lablink.clients.universalapiclient.services.EventDataNotifier;
import at.ac.ait.lablink.clients.universalapiclient.services.SampleDataNotifier;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Channel;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.ChannelDescription;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.ComplexValue;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Configuration;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Event;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.EventChannel;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Info;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.NumericRange;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Sample;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.SampleChannel;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Source;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Status;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.TimeSource;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Validity;
import at.ac.ait.lablink.clients.universalapiclient.util.ConfigUtil;
import at.ac.ait.lablink.clients.universalapiclient.util.TestUtil;

import at.ac.ait.lablink.core.client.ci.mqtt.impl.MqttCommInterfaceUtility;
import at.ac.ait.lablink.core.client.ex.ClientNotReadyException;
import at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException;
import at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException;
import at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException;
import at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException;
import at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException;
import at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException;
import at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType;
import at.ac.ait.lablink.core.client.impl.LlClient;
import at.ac.ait.lablink.core.service.IImplementedService;
import at.ac.ait.lablink.core.service.LlService;
import at.ac.ait.lablink.core.service.types.Complex;
import at.ac.ait.lablink.core.utility.Utility;

import com.sun.net.httpserver.HttpServer;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;


/**
 * Class UniversalApiClient.
 *
 * <p>This client implements the ERIGrid Universal API for data exchange.
 */
public class UniversalApiClient {

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( "UniversalApiClient" );

  // Flags for CLI setup.
  private static final String CLI_CONF_FLAG = "c";
  private static final String CLI_CONF_LONG_FLAG = "config";
  private static final String CLI_TEST_FLAG = "w";

  // Tags for client setup.
  protected static final String CLIENT_CONFIG_TAG = "Client";
  protected static final String CLIENT_DESC_TAG = "ClientDescription";
  protected static final String CLIENT_GROUP_NAME_TAG = "GroupName";
  protected static final String CLIENT_NAME_TAG = "ClientName";
  protected static final String CLIENT_SCENARIO_NAME_TAG = "ScenarioName";
  protected static final String CLIENT_SHELL_TAG = "ClientShell";
  protected static final String CLIENT_URI_LL_PROPERTIES = "labLinkPropertiesUrl";
  protected static final String CLIENT_URI_SYNC_PROPERTIES = "syncHostPropertiesUrl";

  // Tags for general FMU simulator setup.
  protected static final String UAPI_CONFIG_TAG = "UniversalAPI";
  protected static final String UAPI_NODE_ID_TAG = "NodeId";
  protected static final String UAPI_ENDPOINT_PREFIX_CONFIG_TAG = "EndpointPrefix";
  protected static final String UAPI_PORT_CONFIG_TAG = "Port";

  // Tags for input configuration.
  protected static final String CHANNEL_CONFIG_TAG = "Channels";
  protected static final String CHANNEL_DPNAME_TAG = "DPName";
  protected static final String CHANNEL_ID_TAG = "Id";
  protected static final String CHANNEL_PAYLOAD_TAG = "Payload";
  protected static final String CHANNEL_WRITABLE_TAG = "Writable";
  protected static final String CHANNEL_READABLE_TAG = "Readable";
  protected static final String CHANNEL_DATATYPE_TAG = "Datatype";
  protected static final String CHANNEL_UNIT_TAG = "Unit";
  protected static final String CHANNEL_RATE_TAG = "Rate";
  protected static final String CHANNEL_RANGE_TAG = "Range";
  protected static final String CHANNEL_RANGE_MIN_TAG = "Min";
  protected static final String CHANNEL_RANGE_MAX_TAG = "Max";

  protected static final String SAMPLE_TIMESOURCE_TAG = "TimeSource";
  protected static final String SAMPLE_VALIDITY_TAG = "Validity";
  protected static final String SAMPLE_SOURCE_TAG = "Source";

  /** Flag for testing (write config and exit). */
  private static boolean writeConfigAndExitFlag;

  /** Lablink client instance. */
  protected LlClient client;

  /** Singleton instance of Universal API client. */
  private static UniversalApiClient instance;

  private Map<String, Channel> channels = new ConcurrentHashMap<>();

  private Info info;

  private Status status;

  private Configuration configuration;

  private Long portNumber;

  private String endpointUriPrefix;

  /**
   * The main method.
   *
   * @param args arguments to main method
   * @throws at.ac.ait.lablink.core.client.ex.ClientNotReadyException
   *   client not ready
   * @throws at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException
   *   comm interface not supported
   * @throws at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException
   *   data type not supported
   * @throws at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException
   *   invalid cast for service value
   * @throws at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException
   *   no services in client logic
   * @throws at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException
   *   no such comm interface
   * @throws at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException
   *   service is not registered with client
   * @throws at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType
   *   service type does not match client type
   * @throws org.apache.commons.cli.ParseException
   *   parse exception
   * @throws org.apache.commons.configuration.ConfigurationException
   *   configuration error
   * @throws org.json.simple.parser.ParseException
   *   parse error
   * @throws java.io.IOException
   *   IO error
   * @throws java.io.IOException
   *   IO exception error
   * @throws java.net.MalformedURLException
   *   malformed URL
   * @throws java.net.URISyntaxException
   *   URI syntax error
   * @throws java.util.NoSuchElementException
   *   no such element
   */
  public static void main( String[] args ) throws
      at.ac.ait.lablink.core.client.ex.ClientNotReadyException,
      at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException,
      at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException,
      at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException,
      at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException,
      at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException,
      at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException,
      at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType,
      org.apache.commons.cli.ParseException,
      org.apache.commons.configuration.ConfigurationException,
      org.json.simple.parser.ParseException,
      java.io.IOException,
      java.net.MalformedURLException,
      java.net.URISyntaxException,
      java.util.NoSuchElementException {

    // Retrieve configuration.
    JSONObject jsonConfig = getConfig( args );

    // Instantiate Universal API client.
    UniversalApiClient uapiClient = UniversalApiClient.getInstance();

    // Initialize Universal API client.
    uapiClient.initialize( jsonConfig );

    // Run the client.
    uapiClient.run();
  }

  /**
   * Retrieve singleton instance of Universal API client.
   *
   * @return client singleton instance
   */
  public static UniversalApiClient getInstance() {
    if ( instance == null ) {
      instance = new UniversalApiClient();
    }
    return instance;
  }


  /**
   * Constructor.
   */
  private UniversalApiClient() {}

  /**
   * Initialize the Universal API client.
   *
   * @param jsonConfig configuration data (JSON format)
   * @throws at.ac.ait.lablink.core.client.ex.ClientNotReadyException
   *   client not ready
   * @throws at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException
   *   comm interface not supported
   * @throws at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException
   *   data type not supported
   * @throws at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException
   *   invalid cast for service value
   * @throws at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException
   *   no services in client logic
   * @throws at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException
   *   no such comm interface
   * @throws at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException
   *   service is not registered with client
   * @throws at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType
   *   service type does not match client type
   * @throws org.apache.commons.configuration.ConfigurationException
   *   configuration error
   * @throws java.io.IOException
   *   IO exception error
   * @throws java.net.URISyntaxException
   *   URI syntax error
   * @throws java.util.NoSuchElementException
   *   no such element
   */
  public void initialize( JSONObject jsonConfig ) throws
      at.ac.ait.lablink.core.client.ex.ClientNotReadyException,
      at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException,
      at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException,
      at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException,
      at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException,
      at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException,
      at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException,
      at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType,
      org.apache.commons.configuration.ConfigurationException,
      java.io.IOException,
      java.net.URISyntaxException,
      java.util.NoSuchElementException {

    // Retrieve basic client configuration.
    JSONObject clientConfig = ConfigUtil.<JSONObject>getRequiredConfigParam( jsonConfig,
        CLIENT_CONFIG_TAG, String.format( "Client configuration (JSON object with tag '%1$s') "
        + "is missing", CLIENT_CONFIG_TAG ) );

    // Basic client configuration.
    configureClient( clientConfig );

    // Retrieve general config for Universal API client.
    JSONObject uapiConfig = ConfigUtil.<JSONObject>getRequiredConfigParam( jsonConfig,
        UAPI_CONFIG_TAG, String.format( "Universal API configuration (JSON object with tag "
        + "'%1$s') is missing", UAPI_CONFIG_TAG ) );

    // Configure connection parameters of REST interface.
    configureRestApi( uapiConfig );

    // Retrieve config for channels.
    JSONArray channelConfigList = ConfigUtil.<JSONArray>getRequiredConfigParam( jsonConfig,
        CHANNEL_CONFIG_TAG, String.format( "Universal API channel definition (JSON array with tag "
        + "'%1$s') is missing", CHANNEL_CONFIG_TAG ) );

    // Configure channels.
    configureChannels( channelConfigList );

    // Add data services to the client.
    configureLablinkDataServices();

    // Create the client.
    client.create();

    // Initialize the client.
    client.init();

    // Start the client.
    client.start();
  }

  /**
   * Start a simple server und bring up the REST interface.
   *
   * @throws java.io.IOException
   *   IO exception error
   */
  public void run() throws java.io.IOException {
    if ( true == UniversalApiClient.getWriteConfigAndExitFlag() ) {
      // Run a test (write client config and exit).
      TestUtil.writeConfigAndExit( this );
    } else {
      HttpServer server = HttpServer.create( new InetSocketAddress( portNumber.intValue() ), 0 );

      // Add handlers for REST API endpoints.
      server.createContext(
          "/" + this.endpointUriPrefix + "/" + InfoHandler.ENDPOINT,
          new InfoHandler() );
      server.createContext(
          "/" + this.endpointUriPrefix + "/" + StatusHandler.ENDPOINT,
          new StatusHandler() );
      server.createContext(
          "/" + this.endpointUriPrefix + "/" + ChannelDescriptionHandler.ENDPOINT,
          new ChannelDescriptionHandler() );
      server.createContext(
          "/" + this.endpointUriPrefix + "/" + ChannelHandler.ENDPOINT,
          new ChannelHandler() );

      // Do not use thread pool.
      server.setExecutor( null );

      server.start();
    }
  }

  /**
   * Get a list of descriptions of all channels accessible through the REST interface.
   *
   * @return list of channel descriptions
   */
  public List<ChannelDescription> getChannelDescriptions() {
    List<ChannelDescription> descriptions = new ArrayList<ChannelDescription>();

    for ( Channel channel: this.channels.values() ) {
      descriptions.add( channel.getDescription() );
    }

    return descriptions;
  }

  /**
   * Retrieve data structure representing a channel that is accessible through the REST interface.
   *
   * @param id ChannelId
   * @return channel
   */
  public Channel getChannel( String id ) {
    return this.channels.get( id );
  }

  /**
   * Retrieve the API info about this client as served by the REST interface.
   *
   * @return API info
   */
  public Info getInfo() {
    return this.info;
  }

  /**
   * Retrieve the API status for this client as served by the REST interface.
   *
   * @return API status
   */
  public Status getStatus() {
    return this.status;
  }

  /**
   * Set the value of the Lablink data service associated to a channel.
   *
   * @param id ChannelId
   * @param value new sample value of channel
   * @throws at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException
   *   service is not registered with client exception
   */
  public void setServiceValueBoolean( String id, Boolean value ) throws
      at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException {
    this.client.setServiceValue( id, Boolean.valueOf( value ) );
  }

  public void setServiceValueDouble( String id, Double value ) throws
      at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException {
    this.client.setServiceValue( id, Double.valueOf( value ) );
  }

  public void setServiceValueLong( String id, Long value ) throws
      at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException {
    this.client.setServiceValue( id, Long.valueOf( value ) );
  }

  public void setServiceValueString( String id, String value ) throws
      at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException {
    this.client.setServiceValue( id, String.valueOf( value ) );
  }

  public void setServiceValueComplex( String id, ComplexValue value ) throws
      at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException {
    this.client.setServiceValue( id, new Complex( value.real, value.imag ) );
  }

  public String getEndpointUriPrefix() {
    return this.endpointUriPrefix;
  }

  //
  // Functionality for configuring implemented clients.
  //

  /**
   * Configure the Lablink client.
   *
   * @param clientConfig configuration data (JSON format)
   * @throws at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException
   *   comm interface not supported
   */
  protected void configureClient( JSONObject clientConfig ) throws
      at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException {
    logger.info( "Basic client configuration ..." );

    // General Lablink properties configuration.
    String llPropUri = ConfigUtil.<String>getRequiredConfigParam( clientConfig,
        CLIENT_URI_LL_PROPERTIES, String.format( "Lablink client configuration URI missing "
        + "(%1$s)", CLIENT_URI_LL_PROPERTIES ) );

    // Sync properties configuration.
    String llSyncUri = ConfigUtil.<String>getRequiredConfigParam( clientConfig,
        CLIENT_URI_SYNC_PROPERTIES, String.format( "Sync host configuration URI missing "
        + "(%1$s)", CLIENT_URI_SYNC_PROPERTIES ) );

    // Scenario name.
    String scenarioName = ConfigUtil.<String>getRequiredConfigParam( clientConfig,
        CLIENT_SCENARIO_NAME_TAG, String.format( "Scenario name missing (%1$s)",
        CLIENT_SCENARIO_NAME_TAG ) );

    // Group name.
    String groupName = ConfigUtil.<String>getRequiredConfigParam( clientConfig,
        CLIENT_GROUP_NAME_TAG, String.format( "Group name missing (%1$s)",
        CLIENT_GROUP_NAME_TAG ) );

    // Client name.
    String clientName = ConfigUtil.<String>getRequiredConfigParam( clientConfig,
        CLIENT_NAME_TAG, String.format( "Client name missing (%1$s)", CLIENT_NAME_TAG ) );

    // Client description (optional).
    String clientDesc = ConfigUtil.getOptionalConfigParam( clientConfig,
        CLIENT_DESC_TAG, clientName );

    // Activate shell (optional, default: false).
    boolean giveShell = ConfigUtil.getOptionalConfigParam( clientConfig,
        CLIENT_SHELL_TAG, false );

    boolean isPseudo = false;

    // Declare the client with required interface.
    this.client = new LlClient( clientName,
        MqttCommInterfaceUtility.SP_ACCESS_NAME, giveShell, isPseudo );

    this.status = new Status( this.client );

    // Specify client configuration (no sync host).
    MqttCommInterfaceUtility.addClientProperties( client, clientDesc,
        scenarioName, groupName, clientName, llPropUri, llSyncUri, null );

    this.configuration = new Configuration( clientDesc, clientName, groupName,
        scenarioName, llPropUri, llSyncUri );
  }

  /**
   * Configure the REST API connection parameters.
   *
   * @param uapiConfig configuration data (JSON format)
   */
  protected void configureRestApi( JSONObject uapiConfig )  {
    // REST interface URI prefix (optional).
    this.endpointUriPrefix = ConfigUtil.getOptionalConfigParam( uapiConfig,
        UAPI_ENDPOINT_PREFIX_CONFIG_TAG, "" );

    // Remove leading and trailing "/".
    this.endpointUriPrefix = this.endpointUriPrefix.replaceAll( "^/+(?!$)", "" );
    this.endpointUriPrefix = this.endpointUriPrefix.replaceAll( "(?!^)/+$", "" );

    // REST interface port number (optional).
    this.portNumber = ConfigUtil.getOptionalConfigParam( uapiConfig,
        UAPI_PORT_CONFIG_TAG, 7000L );

    // NodeId of interface instance.
    String nodeId = ConfigUtil.<String>getRequiredConfigParam( uapiConfig,
        UAPI_NODE_ID_TAG, String.format( "Id of Universal API missing (%1$s)",
        CLIENT_URI_LL_PROPERTIES ) );

    this.info = new Info( nodeId );
  }


  /**
   * Configure the channels for the REST API.
   *
   * @param channelConfigList configuration data (JSON format)
   */
  protected void configureChannels( JSONArray channelConfigList ) {
    @SuppressWarnings("rawtypes")
    Iterator channelConfigListIter = channelConfigList.iterator();

    // Create data point consumer for each input.
    while ( channelConfigListIter.hasNext() ) {
      JSONObject channelConfig = (JSONObject) channelConfigListIter.next();

      // Datapoint name corresponding to channel.
      String dpName = ConfigUtil.<String>getRequiredConfigParam( channelConfig,
          CHANNEL_DPNAME_TAG, String.format( "Datapoint name of channel missing (%1$s)",
          CHANNEL_DPNAME_TAG ) );

      // ChannelId of channel.
      String channelId = ConfigUtil.<String>getRequiredConfigParam( channelConfig,
          CHANNEL_ID_TAG, String.format( "Id of channel missing (%1$s)",
          CHANNEL_ID_TAG ) );

      // Payload type of channel.
      String payload = ConfigUtil.<String>getRequiredConfigParam( channelConfig,
          CHANNEL_PAYLOAD_TAG, String.format( "Payload type of channel missing (%1$s)",
          CHANNEL_PAYLOAD_TAG ) );

      // Data type of channel.
      String datatype = ConfigUtil.<String>getRequiredConfigParam( channelConfig,
          CHANNEL_DATATYPE_TAG, String.format( "Datatype of channel missing (%1$s)",
          CHANNEL_DATATYPE_TAG ) );

      boolean writable = ConfigUtil.getOptionalConfigParam( channelConfig,
          CHANNEL_WRITABLE_TAG, true );

      boolean readable = ConfigUtil.getOptionalConfigParam( channelConfig,
          CHANNEL_READABLE_TAG, true );

      String unit = ConfigUtil.getOptionalConfigParam( channelConfig,
          CHANNEL_UNIT_TAG, null );

      Number rate = ConfigUtil.getOptionalConfigParam( channelConfig,
          CHANNEL_RATE_TAG, null );

      String timeSource = ConfigUtil.getOptionalConfigParam( channelConfig,
          SAMPLE_TIMESOURCE_TAG, TimeSource.UNKNOWN.toString() );

      String validity = ConfigUtil.getOptionalConfigParam( channelConfig,
          SAMPLE_VALIDITY_TAG, Validity.UNKNOWN.toString() );

      String source = ConfigUtil.getOptionalConfigParam( channelConfig,
          SAMPLE_SOURCE_TAG, Source.UNKNOWN.toString() );

      switch ( datatype.toUpperCase() ) {
        case "FLOAT":
          configureChannelFloat( channelConfig, dpName, channelId,
              ChannelDescription.Payload.valueOf( payload.toUpperCase() ),
              ChannelDescription.Datatype.valueOf( datatype.toUpperCase() ),
              TimeSource.valueOf( timeSource.toUpperCase() ),
              Validity.valueOf( validity.toUpperCase() ),
              Source.valueOf( source.toUpperCase() ),
              unit, rate, writable, readable );
          break;
        case "INTEGER":
          configureChannelInteger( channelConfig, dpName, channelId,
              ChannelDescription.Payload.valueOf( payload.toUpperCase() ),
              ChannelDescription.Datatype.valueOf( datatype.toUpperCase() ),
              TimeSource.valueOf( timeSource.toUpperCase() ),
              Validity.valueOf( validity.toUpperCase() ),
              Source.valueOf( source.toUpperCase() ),
              unit, rate, writable, readable );
          break;
        case "STRING":
          configureChannelString( channelConfig, dpName, channelId,
              ChannelDescription.Payload.valueOf( payload.toUpperCase() ),
              ChannelDescription.Datatype.valueOf( datatype.toUpperCase() ),
              TimeSource.valueOf( timeSource.toUpperCase() ),
              Validity.valueOf( validity.toUpperCase() ),
              Source.valueOf( source.toUpperCase() ),
              unit, rate, writable, readable );
          break;
        case "BOOLEAN":
          configureChannelBoolean( channelConfig, dpName, channelId,
              ChannelDescription.Payload.valueOf( payload.toUpperCase() ),
              ChannelDescription.Datatype.valueOf( datatype.toUpperCase() ),
              TimeSource.valueOf( timeSource.toUpperCase() ),
              Validity.valueOf( validity.toUpperCase() ),
              Source.valueOf( source.toUpperCase() ),
              unit, rate, writable, readable );
          break;
        case "COMPLEX":
          configureChannelComplex( channelConfig, dpName, channelId,
              ChannelDescription.Payload.valueOf( payload.toUpperCase() ),
              ChannelDescription.Datatype.valueOf( datatype.toUpperCase() ),
              TimeSource.valueOf( timeSource.toUpperCase() ),
              Validity.valueOf( validity.toUpperCase() ),
              Source.valueOf( source.toUpperCase() ),
              unit, rate, writable, readable );
          break;
        default:
          throw new IllegalArgumentException( "Unknown data type: " + datatype );
      }
    }
  }

  @SuppressWarnings( "unchecked" )
  private void configureChannelFloat( JSONObject channelConfig, String dpName, String channelId,
      ChannelDescription.Payload payload, ChannelDescription.Datatype datatype,
      TimeSource timeSource, Validity validity, Source source,
      String unit, Number rate, boolean writable, boolean readable ) {
    Object rangeFloat = null;
    JSONObject rangeFloatConfig = ConfigUtil.getOptionalConfigParam( channelConfig,
        CHANNEL_RANGE_TAG, null );

    if ( null != rangeFloatConfig ) {
      Double min = ConfigUtil.<Double>getRequiredConfigParam( rangeFloatConfig,
          CHANNEL_RANGE_MIN_TAG, String.format( "Minimum range missing (%1$s)",
          CHANNEL_RANGE_MIN_TAG ) );

      Double max = ConfigUtil.<Double>getRequiredConfigParam( rangeFloatConfig,
          CHANNEL_RANGE_MAX_TAG, String.format( "Minimum range missing (%1$s)",
          CHANNEL_RANGE_MAX_TAG ) );

      rangeFloat = new NumericRange( min,  max );
    }

    if ( payload == ChannelDescription.Payload.SAMPLES ) {
      this.channels.put( channelId, new SampleChannel<Double>( dpName, channelId, payload,
          datatype, timeSource, validity, source, rangeFloat, unit, rate, writable, readable ) );
    } else {
      this.channels.put( channelId, new EventChannel<Double>( dpName, channelId, payload,
          datatype, timeSource, validity, source, rangeFloat, unit, rate, writable, readable ) );
    }
  }

  @SuppressWarnings( "unchecked" )
  private void configureChannelInteger( JSONObject channelConfig, String dpName, String channelId,
      ChannelDescription.Payload payload, ChannelDescription.Datatype datatype,
      TimeSource timeSource, Validity validity, Source source,
      String unit, Number rate, boolean writable, boolean readable ) {
    Object rangeInteger = null;
    JSONObject rangeIntegerConfig = ConfigUtil.getOptionalConfigParam( channelConfig,
        CHANNEL_RANGE_TAG, null );

    if ( null != rangeIntegerConfig ) {
      Long min = ConfigUtil.<Long>getRequiredConfigParam( rangeIntegerConfig,
          CHANNEL_RANGE_MIN_TAG, String.format( "Minimum range missing (%1$s)",
          CHANNEL_RANGE_MIN_TAG ) );

      Long max = ConfigUtil.<Long>getRequiredConfigParam( rangeIntegerConfig,
          CHANNEL_RANGE_MAX_TAG, String.format( "Minimum range missing (%1$s)",
          CHANNEL_RANGE_MAX_TAG ) );

      rangeInteger = new NumericRange( min,  max );
    }

    if ( payload == ChannelDescription.Payload.SAMPLES ) {
      this.channels.put( channelId, new SampleChannel<Integer>( dpName, channelId, payload,
          datatype, timeSource, validity, source, rangeInteger, unit, rate, writable, readable ) );
    } else {
      this.channels.put( channelId, new EventChannel<Integer>( dpName, channelId, payload,
          datatype, timeSource, validity, source, rangeInteger, unit, rate, writable, readable ) );
    }
  }

  @SuppressWarnings( "unchecked" )
  private void configureChannelString( JSONObject channelConfig, String dpName, String channelId,
      ChannelDescription.Payload payload, ChannelDescription.Datatype datatype,
      TimeSource timeSource, Validity validity, Source source,
      String unit, Number rate, boolean writable, boolean readable ) {
    Object rangeString = null;
    JSONArray rangeStringConfig = ConfigUtil.getOptionalConfigParam( channelConfig,
        CHANNEL_RANGE_TAG, null );

    if ( null != rangeStringConfig ) {
      String[] arr = new String[rangeStringConfig.size()];
      for ( int i = 0; i < arr.length; i++ ) {
        arr[i] = rangeStringConfig.get(i).toString();
      }
      rangeString = arr;
    }

    if ( payload == ChannelDescription.Payload.SAMPLES ) {
      this.channels.put( channelId, new SampleChannel<String>( dpName, channelId, payload,
          datatype, timeSource, validity, source, rangeString, unit, rate, writable, readable ) );
    } else {
      this.channels.put( channelId, new EventChannel<String>( dpName, channelId, payload,
          datatype, timeSource, validity, source, rangeString, unit, rate, writable, readable ) );
    }
  }

  private void configureChannelBoolean( JSONObject channelConfig, String dpName, String channelId,
      ChannelDescription.Payload payload, ChannelDescription.Datatype datatype,
      TimeSource timeSource, Validity validity, Source source,
      String unit, Number rate, boolean writable, boolean readable ) {
    if ( payload == ChannelDescription.Payload.SAMPLES ) {
      this.channels.put( channelId, new SampleChannel<Boolean>( dpName, channelId, payload,
          datatype, timeSource, validity, source, null, unit, rate, writable, readable ) );
    } else {
      this.channels.put( channelId, new EventChannel<Boolean>( dpName, channelId, payload,
          datatype, timeSource, validity, source, null, unit, rate, writable, readable ) );
    }
  }

  private void configureChannelComplex( JSONObject channelConfig, String dpName, String channelId,
      ChannelDescription.Payload payload, ChannelDescription.Datatype datatype,
      TimeSource timeSource, Validity validity, Source source,
      String unit, Number rate, boolean writable, boolean readable ) {
    if ( payload == ChannelDescription.Payload.SAMPLES ) {
      this.channels.put( channelId, new SampleChannel<Complex>( dpName, channelId, payload,
          datatype, timeSource, validity, source, null, unit, rate, writable, readable ) );
    } else {
      this.channels.put( channelId, new EventChannel<Complex>( dpName, channelId, payload,
          datatype, timeSource, validity, source, null, unit, rate, writable, readable ) );
    }
  }

  /**
   * Configure the data services, which connect the channels (i.e, the REST endpoints) with Lablink.
   *
   * @throws at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType
   *   service type does not match client type
   */
  protected void configureLablinkDataServices() throws
      at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType {

    for ( Map.Entry<String, Channel> entry : channels.entrySet() ) {

      String channelName = entry.getKey();
      Channel channel = entry.getValue();

      // Retrieve the channel's description.
      ChannelDescription description = channel.getDescription();

      // Data service name.
      String serviceName = channel.getDataPointName();

      // Data service description.
      String serviceDesc = "data service for channel " + channelName;

      // Unit associated to values handled by the data service.
      String serviceUnit = description.getUnit();

      // Get the channel's datatype.
      ChannelDescription.Datatype datatype = description.getDatatypeEnum();

      // Create new data service.
      LlService dataService = null;
      switch ( datatype ) {
        case BOOLEAN:
          dataService = new DataServiceBoolean();
          break;
        case FLOAT:
          dataService = new DataServiceDouble();
          break;
        case INTEGER:
          dataService = new DataServiceLong();
          break;
        case STRING:
          dataService = new DataServiceString();
          break;
        case COMPLEX:
          dataService = new DataServiceComplex();
          break;
        default:
          // This default switch cannot be reached.
          break;
      }

      // Set the name of the associated data service.
      dataService.setName( serviceName );

      // Specify data service properties.
      MqttCommInterfaceUtility.addDataPointProperties( dataService,
          serviceName, serviceDesc, serviceName, serviceUnit );

      if ( description.getPayloadEnum() == ChannelDescription.Payload.SAMPLES ) {
        // Create new notifier for samples.
        SampleDataNotifier sampleNotifier = null;
        switch ( datatype ) {
          case BOOLEAN:
            sampleNotifier = new SampleDataNotifier<Boolean>();
            break;
          case FLOAT:
            sampleNotifier = new SampleDataNotifier<Double>();
            break;
          case INTEGER:
            sampleNotifier = new SampleDataNotifier<Long>();
            break;
          case STRING:
            sampleNotifier = new SampleDataNotifier<String>();
            break;
          case COMPLEX:
            sampleNotifier = new SampleDataNotifier<Complex>();
            break;
          default:
            // This default switch cannot be reached.
            break;
        }

        // Associate notifier to channel.
        associateSampleNotifierToChannel( dataService, sampleNotifier, channel );

      } else {
        // Create new notifier for events.
        EventDataNotifier eventNotifier = null;
        switch ( datatype ) {
          case BOOLEAN:
            eventNotifier = new EventDataNotifier<Boolean>();
            break;
          case FLOAT:
            eventNotifier = new EventDataNotifier<Double>();
            break;
          case INTEGER:
            eventNotifier = new EventDataNotifier<Long>();
            break;
          case STRING:
            eventNotifier = new EventDataNotifier<String>();
            break;
          case COMPLEX:
            eventNotifier = new EventDataNotifier<Complex>();
            break;
          default:
            // This default switch cannot be reached.
            break;
        }

        // Associate notifier to channel.
        associateEventNotifierToChannel( dataService, eventNotifier, channel );
      }

      // Add service to the client.
      client.addService( dataService );
    }
  }

  @SuppressWarnings( "unchecked" )
  private void associateSampleNotifierToChannel( LlService service,
      SampleDataNotifier notifier, Channel channel ) {
    // Associate notifier to channel.
    notifier.setSample( ( (SampleChannel) channel ).getSample() );
    // Add notifier.
    service.addStateChangeNotifier( notifier );
  }

  @SuppressWarnings( "unchecked" )
  private void associateEventNotifierToChannel( LlService service,
      EventDataNotifier notifier, Channel channel ) {
    // Associate notifier to channel.
    notifier.setChannel( (EventChannel) channel );
    // Add notifier.
    service.addStateChangeNotifier( notifier );
  }

  /**
   * Parse the command line arguments to retrieve the configuration.
   *
   * @param args arguments to main method
   * @return configuration data (JSON format)
   * @throws org.apache.commons.cli.ParseException
   *   parse exception
   * @throws org.apache.commons.configuration.ConfigurationException
   *   configuration error
   * @throws org.json.simple.parser.ParseException
   *   parse error
   * @throws java.io.IOException
   *   IO error
   * @throws java.net.MalformedURLException
   *   malformed URL
   * @throws java.util.NoSuchElementException
   *   no such element
   */
  static JSONObject getConfig( String[] args ) throws
      org.apache.commons.cli.ParseException,
      org.apache.commons.configuration.ConfigurationException,
      org.json.simple.parser.ParseException,
      java.io.IOException,
      java.net.MalformedURLException,
      java.util.NoSuchElementException {

    // Define command line option.
    Options cliOptions = new Options();
    cliOptions.addOption( CLI_CONF_FLAG, CLI_CONF_LONG_FLAG,
        true, "Universal API client configuration URI" );
    cliOptions.addOption( CLI_TEST_FLAG,
        false, "write config and exit" );

    // Parse command line options.
    CommandLineParser parser = new BasicParser();
    CommandLine commandLine = parser.parse( cliOptions, args );

    // Set flag for testing (write config and exit).
    writeConfigAndExitFlag = commandLine.hasOption( CLI_TEST_FLAG );

    // Retrieve configuration URI from command line.
    String configUri = commandLine.getOptionValue( CLI_CONF_FLAG );

    // Get configuration URL, resolve environment variables if necessary.
    URL fullConfigUrl = new URL( Utility.parseWithEnvironmentVariable( configUri ) );

    // Read configuration, remove existing comments.
    Scanner scanner = new Scanner( fullConfigUrl.openStream() );
    String rawConfig = scanner.useDelimiter( "\\Z" ).next();
    rawConfig = rawConfig.replaceAll( "#.*#", "" );

    // Check if comments have been removed properly.
    int still = rawConfig.length() - rawConfig.replace( "#", "" ).length();
    if ( still > 0 ) {
      throw new IllegalArgumentException(
          String.format( "Config file contains at least %1$d line(s) with incorrectly"
              + "started/terminated comments: %2$s", still, fullConfigUrl.toString() )
        );
    }

    logger.info( "Parsing configuration file..." );

    // Parse configuration (JSON format).
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonConfig = ( JSONObject ) jsonParser.parse( rawConfig );

    return jsonConfig;
  }

  //
  // Functionality for testing.
  //

  /**
   * Retrieve value of flag {@code writeConfigAndExitFlag} (used for testing).
   *
   * @return value of flag {@code writeConfigAndExitFlag}
   */
  public static boolean getWriteConfigAndExitFlag() {
    return writeConfigAndExitFlag;
  }


  /**
   * Returns the yellow pages info (JSON format) of the Lablink client.
   *
   * @return yellow pages
   */
  public String getYellowPageJson() {
    return this.client.getYellowPageJson();
  }
}
