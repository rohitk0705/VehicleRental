@echo off
echo Starting Online Version (Web Server)...
if not exist bin mkdir bin
echo Compiling...
javac -d bin Backend/Common/*.java Backend/Web/*.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b
)
echo Server running at http://localhost:8080
java -cp bin Backend.Web.WebServer
pause