@ECHO OFF

REM =============================================================
REM Edit the following variables to comply with your local setup.
REM =============================================================

REM Connection string for configuration server.
SET LLCONFIG=http://localhost:10101/get?id=

REM Version of Universal API client.
SET VERSION=0.0.2

REM Root directory of Universal API client package (only change this if you really know what you are doing).
SET UAPI_CLIENT_ROOT_DIR=%~DP0..

REM Path to Java JAR file of Universal API client.
SET UAPI_CLIENT_JAR_FILE=%UAPI_CLIENT_ROOT_DIR%\target\assembly\universalapiclient-%VERSION%-jar-with-dependencies.jar 

REM Path to Java JAR file of data point bridge.
SET DPB_JAR_FILE=%UAPI_CLIENT_ROOT_DIR%\target\dependency\dpbridge-0.0.2-jar-with-dependencies.jar

REM Path to Java JAR file of CSV client package.
SET CSV_CLIENT_JAR_FILE=%UAPI_CLIENT_ROOT_DIR%\target\dependency\csvclient-0.0.2-jar-with-dependencies.jar

REM Path to Java JAR file of data plotter.
SET PLOT_JAR_FILE=%UAPI_CLIENT_ROOT_DIR%\target\dependency\plotter-0.0.4-jar-with-dependencies.jar

REM Path to Java JAR file of config server.
SET CONFIG_JAR_FILE=%UAPI_CLIENT_ROOT_DIR%\target\dependency\config-0.1.1-jar-with-dependencies.jar

REM Check if environment variable JAVA_HOME has been defined.
IF NOT DEFINED JAVA_HOME (
    ECHO WARNING: environment variable JAVA_HOME has not been defined!
    PAUSE
)
