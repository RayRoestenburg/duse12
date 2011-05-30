@echo off
set AKKA_HOME=%~dp0..
set AKKA_CLASSPATH=%AKKA_HOME%\lib\*;%AKKA_HOME%\config;%AKKA_HOME%\deploy\junction_2.9.0-1.0.jar
set JAVA_OPTS=-Xms1536M -Xmx1536M -Xss1M -XX:MaxPermSize=256M -XX:+UseParallelGC

java %JAVA_OPTS% -cp "%AKKA_CLASSPATH%" -Dakka.config=%AKKA_HOME%\config\akka-gui.conf akka.kernel.Main
