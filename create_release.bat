@echo off
echo Compiling Java files...
javac Backend/*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Creating Runnable JAR...
jar cfm VehicleRental.jar MANIFEST.MF Backend/*.class

echo.
echo -------------------------------------------------------
echo SUCCESS! 
echo The file 'VehicleRental.jar' has been created in the folder.
echo You can upload this file to GitHub or share it with friends.
echo -------------------------------------------------------
pause
