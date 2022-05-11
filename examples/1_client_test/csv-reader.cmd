@ECHO OFF

SETLOCAL

REM Load the setup for the examples.
CALL "%~DP0\..\setup.cmd"

REM CSV client configuration.
SET CONFIG_FILE_URI=%LLCONFIG%ait.test.universalapiclient.csvreader.config

REM Logger configuration.
SET LOGGER_CONFIG=-Dlog4j.configurationFile=%LLCONFIG%ait.all.log4j2

REM Run the example.
"%JAVA_HOME%\bin\java.exe" %LOGGER_CONFIG% -jar "%CSV_CLIENT_JAR_FILE%" -c "%CONFIG_FILE_URI%"

PAUSE
