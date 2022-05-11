Overview
========

The configuration has to be JSON-formatted.
It is divided into the following categories:

  :*Client*: basic configuration of the Lablink client (JSON object)
  :*UniversalAPI*: basic configuration related to the REST API server (JSON object)
  :*Signals*: configuration of the data exchange signals, accessible through the REST API and via Lablink

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

  :*Id*: NodeId of Universal API instance

.. topic:: Optional parameters for **UniversalAPI**

  :*EndpointPrefix*: prefix for URL of REST API
  :*Port*: port of REST API server (default: ``7000``)


Signal Configuration
====================

.. topic:: Required configuration parameters for each signal defined in category *Signals*

  :*Id:*: SignalId of data exchange signal
  :*Source*: NodeId of data exchange signal source
  
.. topic:: Optional configuration parameters for each input/output in category *Signals*
  
  :*Readable*: this flag indicates if the signal can be read via the REST API
  :*Writable*: this flag indicates if the signal can be written via the REST API

Example Configuration
=====================

The following is an example configuration for a *UniversalApiClient* client:

.. code-block:: json

   {
       "Client": {
           "ClientDescription": "Universal data exchamge API client example.",
           "ClientName": "UniversalAPIClientTest",
           "ClientShell": true,
           "GroupName": "UniversalAPIClientDemo",
           "ScenarioName": "UniversalAPIClientExample",
           "labLinkPropertiesUrl": "http://localhost:10101/get?id=ait.all.all.llproperties",
           "syncHostPropertiesUrl": "http://localhost:10101/get?id=ait.test.universalapiclient.sync-host.properties"
       },
       "Signals": [
           {
               "Id": "test/signal1",
               "Readable": true,
               "Source": "test1-source",
               "Writable": true
           },
           {
               "Id": "test/signal2",
               "Readable": false,
               "Source": "test2-source",
               "Writable": true
           },
           {
               "Id": "test/signal3",
               "Readable": true,
               "Source": "test2-source",
               "Writable": false
           }
       ],
       "UniversalAPI": {
           "EndpointPrefix": "uapi-test",
           "NodeId": "lablink-uapi-test",
           "Port": 7000
       }
   }