@echo off
if %1.==. goto info
call mvn -f sml-pom.xml %*
goto end
:info
echo.
echo Please provide Maven arguments to this script.
echo   Example: %0 install
echo.
:end
