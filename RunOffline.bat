@echo off
echo Starting Offline Version (GUI)...
if not exist bin mkdir bin
echo Compiling...
javac -d bin Backend/Common/*.java Backend/Desktop/*.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b
)
java -cp bin Backend.Desktop.FleetGUILauncher
pause