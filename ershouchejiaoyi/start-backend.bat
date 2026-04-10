@echo off
echo ================================================
echo Starting Spring Boot Backend Server
echo ================================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 8 and add it to your PATH
    pause
    exit /b 1
)

echo Java found. Starting backend...
echo.

REM Change to project directory
cd /d "%~dp0"

REM Check if Maven is available
where mvn >nul 2>&1
if %errorlevel% equ 0 (
    echo Using Maven to start the application...
    echo.
    mvn spring-boot:run
) else (
    echo Maven not found in PATH
    echo Trying to run with compiled classes...
    echo.

    REM Try to run using compiled classes
    if exist "target\classes\com\ershouchejiaoyiApplication.class" (
        echo Running from target\classes...
        java -cp "target\classes;%USERPROFILE%\.m2\repository\*" com.ershouchejiaoyiApplication
    ) else (
        echo ERROR: Application is not compiled
        echo.
        echo Please do one of the following:
        echo 1. Install Maven and run: mvn clean install
        echo 2. Compile the project in your IDE
        echo 3. Make sure target\classes directory exists with compiled classes
        pause
        exit /b 1
    )
)

pause
