@echo off
echo Starting Student Management System with Simple Performance Optimizations...
echo.

REM Simple but effective performance optimizations
set JAVAFX_OPTS=-Djavafx.animation.fullspeed=true -Dprism.vsync=false -Dprism.order=d3d,sw -Dprism.text=t2k

echo Compiling...
javac -cp ".;sqlite-jdbc-3.50.3.0.jar" --module-path "C:\Program Files\OpenLogic\jdk-17.0.16.8-hotspot\jmods" --add-modules javafx.controls,javafx.fxml *.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilation successful! Starting with 60 FPS optimization...
    echo.
    
    java %JAVAFX_OPTS% -cp ".;sqlite-jdbc-3.50.3.0.jar" --module-path "C:\Program Files\OpenLogic\jdk-17.0.16.8-hotspot\jmods" --add-modules javafx.controls,javafx.fxml StudentManagementSystem
) else (
    echo.
    echo Compilation failed! Please check the errors above.
    pause
)


