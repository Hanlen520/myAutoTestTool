@echo off
echo "BAT!"
set timeStamp=%date:~0,4%-%date:~5,2%-%date:~8,2%-%time:~0,2%-%time:~3,2%-%time:~6,2%
echo %timeStamp%
rem java -jar target\autotestDemo-1.0-SNAPSHOT.jar testng.xml>log/%timeStamp%.txt
java -jar target\autotestDemo-1.0-SNAPSHOT.jar testng.xml>log/log.txt
pause
