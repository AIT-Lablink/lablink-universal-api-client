//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.universalapiclient;

import at.ac.ait.lablink.clients.universalapiclient.universalapi.Configuration;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Info;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.Signal;
import at.ac.ait.lablink.clients.universalapiclient.universalapi.SignalDescription;
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

import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;

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
  protected static final String SIGNAL_CONFIG_TAG = "Signals";
  protected static final String SIGNAL_DPNAME_TAG = "DPName";
  protected static final String SIGNAL_ID_TAG = "Id";
  protected static final String SIGNAL_SOURCE_TAG = "Source";
  protected static final String SIGNAL_WRITABLE_TAG = "Writable";
  protected static final String SIGNAL_READABLE_TAG = "Readable";

  /** Flag for testing (write config and exit). */
  private static boolean writeConfigAndExitFlag;

  /** Lablink client instance. */
  protected LlClient client;

  /** Singleton instance of Universal API client. */
  private static UniversalApiClient instance;

  private Map<String, Signal> signals = new ConcurrentHashMap<>();

  private Info info;

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

    // Retrieve config for signals.
    JSONArray signalConfigList = ConfigUtil.<JSONArray>getRequiredConfigParam( jsonConfig,
        SIGNAL_CONFIG_TAG, String.format( "Universal API signal definition (JSON array with tag "
        + "'%1$s') is missing", SIGNAL_CONFIG_TAG ) );

    // Configure signals.
    configureSignals( signalConfigList );

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
      HttpContextBuilder contextBuilder = new HttpContextBuilder();
      contextBuilder.getDeployment()
          .getActualResourceClasses()
          .add( UniversalApiClientRestInterface.class );
      contextBuilder.setPath( endpointUriPrefix );
      contextBuilder.bind( server );
      server.start();
    }
  }

  /**
   * Get a list of descriptions of all signals accessible through the REST interface.
   *
   * @return list of signal descriptions
   */
  public List<SignalDescription> getSignalDescriptions() {
    List<SignalDescription> descriptions = new ArrayList<SignalDescription>();

    for ( Signal signal: this.signals.values() ) {
      descriptions.add( signal.getDescription() );
    }
 
    return descriptions;
  }

  /**
   * Retrieve data structure representing a signal that is accessible through the REST interface.
   *
   * @param id SignalId
   * @return signal
   */
  public Signal getSignal( String id ) {
    return this.signals.get( id );
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
   * Retrieve the Lablink-specific configuration about this client as served by the REST interface.
   *
   * @return configuration info
   */
  public Configuration getConfiguration() {
    return this.configuration;
  }

  /**
   * Set the value of the Lablink data service associated to a signal.
   *
   * @param id SignalId
   * @param value new value of signal
   * @throws at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException
   *   service is not registered with client exception
   */
  public void setServiceValue( String id, double value ) throws
      at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException {
    this.client.setServiceValue( id, Double.valueOf( value ) );
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
    client = new LlClient( clientName,
        MqttCommInterfaceUtility.SP_ACCESS_NAME, giveShell, isPseudo );

    // Specify client configuration (no sync host).
    MqttCommInterfaceUtility.addClientProperties( client, clientDesc,
        scenarioName, groupName, clientName, llPropUri, llSyncUri, null );

    configuration = new Configuration( clientDesc, clientName, groupName,
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
   * Configure the signals for the REST API.
   *
   * @param signalConfigList configuration data (JSON format)
   */
  protected void configureSignals( JSONArray signalConfigList )  {
    @SuppressWarnings("rawtypes")
    Iterator signalConfigListIter = signalConfigList.iterator();

    // Create data point consumer for each input.
    while ( signalConfigListIter.hasNext() ) {
      JSONObject signalConfig = (JSONObject) signalConfigListIter.next();

      // Datapoint name corresponding to signal.
      String dpName = ConfigUtil.<String>getRequiredConfigParam( signalConfig,
          SIGNAL_DPNAME_TAG, String.format( "Datapoint name of signal missing (%1$s)",
          SIGNAL_DPNAME_TAG ) );

      // SignalId of signal.
      String signalId = ConfigUtil.<String>getRequiredConfigParam( signalConfig,
          SIGNAL_ID_TAG, String.format( "Id of signal missing (%1$s)",
          SIGNAL_ID_TAG ) );

      // NodeId of source.
      String sourceId = ConfigUtil.<String>getRequiredConfigParam( signalConfig,
          SIGNAL_SOURCE_TAG, String.format( "Source Id of signal missing (%1$s)",
          SIGNAL_SOURCE_TAG ) );

      boolean writable = ConfigUtil.getOptionalConfigParam( signalConfig,
          SIGNAL_WRITABLE_TAG, true );

      boolean readable = ConfigUtil.getOptionalConfigParam( signalConfig,
          SIGNAL_READABLE_TAG, true );

      signals.put( signalId, new Signal( dpName, signalId, sourceId, writable, readable ) );
    }
  }


  /**
   * Configure the data services, which connect the signals (i.e, the REST endpoints) with Lablink.
   *
   * @throws at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType
   *   service type does not match client type
   */
  protected void configureLablinkDataServices() throws
      at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType {
    
    for ( Map.Entry<String, Signal> entry : signals.entrySet() ) {

      String signalName = entry.getKey();
      Signal signal = entry.getValue();

      // Data service name.
      String serviceName = signal.getDataPointName();

      // Data service description.
      String serviceDesc = "data service for signal " + signalName;

      // Unit associated to values handled by the data service.
      String serviceUnit = "none";

      // Create new data service.
      UniversalApiClientDataService dataService = new UniversalApiClientDataService();
      dataService.setName( serviceName );

      // Specify data service properties.
      MqttCommInterfaceUtility.addDataPointProperties( dataService,
          serviceName, serviceDesc, serviceName, serviceUnit );

      if ( true == signal.getDescription().getReadable() ) {
        // Create new notifier.
        UniversalApiClientDataNotifier notifier = new UniversalApiClientDataNotifier();

        // Associate notifer to signal.
        notifier.setSignalState( signal.getState() );

        // Add notifier.
        dataService.addStateChangeNotifier( notifier );
      }

      // Add service to the client.
      client.addService( dataService );
    }
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
