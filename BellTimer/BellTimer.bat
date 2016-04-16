rem Bell Schedule Countdown timer start script
rem to create jar file, use (remove the "rem")
rem "%JAVA_HOME%"\jar fc0ve BellTimer.jar BellTimer *.class
c:
cd \"Program Files"\BellTimer
java -jar BellTimer.jar
IF ERRORLEVEL 1 PAUSE
