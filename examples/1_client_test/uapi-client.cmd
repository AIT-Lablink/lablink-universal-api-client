@ECHO OFF

SETLOCAL

REM Load the setup for the examples.
CALL "%~DP0\..\setup.cmd"

REM Path to class implementing the main routine.
SET UAPI_CLIENT=at.ac.ait.lablink.clients.universalapiclient.UniversalApiClient

REM Logger configuration.
SET LOGGER_CONFIG=-Dlog4j.configurationFile=%LLCONFIG%ait.all.log4j2

REM Data point bridge configuration.
SET CONFIG_FILE_URI=%LLCONFIG%ait.test.universalapiclient.universalapiclient.config

REM Run the example.
"%JAVA_HOME%\bin\java.exe" %LOGGER_CONFIG% -cp "%UAPI_CLIENT_JAR_FILE%" %UAPI_CLIENT% -c %CONFIG_FILE_URI%

PAUSE
