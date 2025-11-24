@echo off
echo Moving to project root...
cd ..
echo Compiling Java files...
javac VehicleRental/*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Creating Runnable JAR...
jar cfm VehicleRental/VehicleRental.jar VehicleRental/MANIFEST.MF VehicleRental/*.class

echo.
echo -------------------------------------------------------
echo SUCCESS! 
echo The file 'VehicleRental.jar' has been created in the folder.
echo You can upload this file to GitHub or share it with friends.
echo -------------------------------------------------------
pause
