@ECHO OFF

REM Check if environment variable JAVA_HOME has been defined.
IF NOT DEFINED JAVA_HOME (
    ECHO WARNING: environment variable JAVA_HOME has not been defined!
    PAUSE
	EXIT
)

REM Call the Maven wrapper to install dependencies
CALL %~DP0\mvnw clean package
