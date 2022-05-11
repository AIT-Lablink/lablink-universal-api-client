Invoking the clients from the command line
==========================================

When running the clients, the use of the ``-c`` command line flag followed by the URI to the configuration (see :doc:`here <configuration>`) is mandatory.
For example, on Windows this could look something like this:

.. code-block:: winbatch

   SET LLCONFIG=http://localhost:10101/get?id=
   SET CONFIG_FILE_URI=%LLCONFIG%ait.test.universalapiclient.config
   
   SET UAPI_CLIENT=at.ac.ait.lablink.clients.universalapiclient.UniversalApiClient
   SET UAPI_CLIENT_JAR_FILE=\path\to\lablink-universal-api-client\target\assembly\universalapiclient-<VERSION>-jar-with-dependencies.jar
   
   "%JAVA_HOME%\bin\java.exe" -cp "%UAPI_CLIENT_JAR_FILE%" %UAPI_CLIENT% -c %CONFIG_FILE_URI%
