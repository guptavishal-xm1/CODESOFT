@echo off
echo Starting Student Management System with Performance Optimizations...
echo.

REM Simple performance optimized JVM arguments for 60 FPS
set JAVA_OPTS=-XX:+UseG1GC -XX:MaxGCPauseMillis=16 -XX:+UseStringDeduplication -XX:+OptimizeStringConcat -XX:+UseCompressedOops -XX:+UseCompressedClassPointers -XX:+TieredCompilation -XX:MaxMetaspaceSize=256m

REM JavaFX performance properties
set JAVAFX_OPTS=-Djavafx.animation.fullspeed=true -Djavafx.animation.pulse=60 -Dprism.vsync=false -Dprism.order=d3d,sw -Dprism.text=t2k -Dprism.forceGPU=true

REM Memory optimization
set MEMORY_OPTS=-Xms512m -Xmx2g -XX:NewRatio=3 -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=15

echo Compiling with optimizations...
javac -cp ".;sqlite-jdbc-3.50.3.0.jar" --module-path "C:\Program Files\OpenLogic\jdk-17.0.16.8-hotspot\jmods" --add-modules javafx.controls,javafx.fxml *.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilation successful! Starting application...
    echo.
    echo Performance Mode: ENABLED
    echo Target FPS: 60
    echo GPU Acceleration: ENABLED
    echo Memory Optimization: ENABLED
    echo.
    
    java %JAVA_OPTS% %JAVAFX_OPTS% %MEMORY_OPTS% -cp ".;sqlite-jdbc-3.50.3.0.jar" --module-path "C:\Program Files\OpenLogic\jdk-17.0.16.8-hotspot\jmods" --add-modules javafx.controls,javafx.fxml StudentManagementSystem
) else (
    echo.
    echo Compilation failed! Please check the errors above.
    pause
)


