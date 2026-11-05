@echo off
setlocal
if "%MAVEN_HOME%"=="" set MAVEN_HOME=C:\Users\raphael\.maven\maven-3.10.0
set MAVEN_BIN=%MAVEN_HOME%\bin\mvn.cmd
if not exist "%MAVEN_BIN%" (
  echo Maven not found at %MAVEN_BIN%. Install Maven first or set MAVEN_HOME.
  exit /b 1
)
call "%MAVEN_BIN%" %*
