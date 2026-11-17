@echo off
REM Compiles and starts the Movie Ticket Booking System
cd /d "%~dp0"
if not exist out mkdir out

echo Compiling...
javac -d out src\cinema\*.java src\cinema\model\*.java src\cinema\service\*.java src\cinema\ui\*.java
if errorlevel 1 (
    echo.
    echo Compile failed. Check that the JDK is installed and that javac works in Command Prompt.
    pause
    exit /b 1
)

echo Starting the app...
java -cp out cinema.Main
if errorlevel 1 pause
