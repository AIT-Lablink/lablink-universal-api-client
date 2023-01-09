Overview
========

The configuration has to be JSON-formatted.
It is divided into the following categories:

  :*Client*: basic configuration of the Lablink client (JSON object)
  :*UniversalAPI*: basic configuration related to the REST API server (JSON object)
  :*Channels*: configuration of the data exchange channels, accessible through the REST API and via Lablink

In the following, the configuration parameters for these categories are listed.

.. seealso:: See `below <#example-configuration>`_ for an example of a complete JSON configuration.

Basic Lablink Client Configuration
==================================

.. topic:: Required parameters for category *Client*

  :*ClientName*: client name
  :*GroupName*: group name
  :*ScenarioName*: scenario name
  :*labLinkPropertiesUrl*: URI to Lablink configuration
  :*syncHostPropertiesUrl*: URI to sync host configuration (*currently not supported, use dummy value here*)

.. topic:: Optional parameters for category *Client*

  :*ClientDescription*: description of the client
  :*ClientShell*: activate Lablink shell (default: ``false``).

Universal API Configuration
===========================

.. topic:: Required parameters for category *UniversalAPI*

  :*NodeId*: node ID of Universal API instance

.. topic:: Optional parameters for **UniversalAPI**

  :*EndpointPrefix*: prefix for URL of REST API
  :*Port*: port of REST API server (default: ``7000``)


Channel Configuration
=====================

.. topic:: Required configuration parameters for each channel defined in category *Channels*

  :*Id*: channel Id (may contain dashes ``/``)
  :*Payload*: describes the type of information which is exchanged over the channel, must be either ``samples`` or ``events``
  :*Datatype*: type of data exchanged via this channel, must be either ``float``, ``complex``, ``integer``, ``string`` or ``boolean``
  :*DPName*: datapoint name of Lablink service associated to channel (must not contain dashes ``/``)
  
.. topic:: Optional configuration parameters for each channel defined in category *Channels*
  
  :*Readable*: this flag indicates if the channel can be read via the REST API
  :*Writable*: this flag indicates if the channel can be written via the REST API
  :*Unit*: the (physical) unit associated with the channel
  :*Range*: allowed range of values; for payloads of type ``float`` and ``integer`` this should be an object with attributes *Min* and *Max*; for payloads of type ``string`` this should be an array of allowed values 
  :*Rate*: expected refresh-rate in Hertz of this channel; does not apply to channels which have event payloads.

  :*TimeSource*: allowed values are ``synchronized``, ``unsynchronized`` or ``unknown``
  :*Source*: allowed values are ``unknown``, ``process``, ``test``, ``calculated`` or ``simulated``
  :*Validity*: allowed values are ``unknown``, ``valid``, ``invalid``, ``questionable`` or ``indeterminate``

Example Configuration
=====================

The following is an example configuration for a *UniversalApiClient* client:

.. code-block:: json

   {
      "Channels": [
         {
            "DPName": "channel1",
            "Datatype": "float",
            "Id": "test/channel1",
            "Payload": "samples",
            "Range": {
               "Max": 15.0,
               "Min": 5.0
            },
            "Readable": true,
            "Source": "calculated",
            "Validity": "valid",
            "Writable": false
         },
         {
            "DPName": "channel2",
            "Datatype": "float",
            "Id": "test/channel2",
            "Payload": "samples",
            "Readable": false,
            "TimeSource": "unsynchronized",
            "Writable": true
         }
      ],
      "Client": {
         "ClientDescription": "Universal data exchamge API client example.",
         "ClientName": "UniversalAPIClient",
         "ClientShell": true,
         "GroupName": "UniversalAPIClientDemo",
         "ScenarioName": "UniversalAPIClientExample",
         "labLinkPropertiesUrl": "http://localhost:10101/get?id=ait.all.llproperties",
         "syncHostPropertiesUrl": "http://localhost:10101/get?id=ait.test.universalapiclient.sync-host.properties"
      },
      "UniversalAPI": {
         "EndpointPrefix": "uapi-test",
         "NodeId": "lablink-uapi-test",
         "Port": 7000
      }
   }