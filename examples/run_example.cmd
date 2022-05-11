@ECHO OFF

SETLOCAL

ECHO Start Lablink Config Server ...
START "Lablink Config Server" "%~DP0\0_config\run_config.cmd"

TIMEOUT 3

ECHO Start Lablink Data Point Bridge ...
START "Lablink Data Point Bridge" "%~DP0\1_client_test\dpb.cmd"

TIMEOUT 3

ECHO Start Lablink Clients ...
START "Lablink Plotter" "%~DP0\1_client_test\plot.cmd"
START "Lablink CSV Reader" "%~DP0\1_client_test\csv-reader.cmd"
START "Lablink UAPI CLient" "%~DP0\1_client_test\uapi-client.cmd"

PAUSE