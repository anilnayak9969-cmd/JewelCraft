@echo off
REM Loads JewelCraft.sql from this folder into MySQL (re-creates the tables and all data).
REM WARNING: this replaces the current contents of the JewelCraft database. MySQL asks for the root password.
setlocal
set MYSQL_BIN=C:\Program Files\MySQL\MySQL Server 9.7\bin
set IN=%~dp0JewelCraft.sql

choice /m "Replace the JewelCraft database with %IN%"
if errorlevel 2 exit /b

"%MYSQL_BIN%\mysql.exe" -u root -p --default-character-set=utf8mb4 < "%IN%"

if %errorlevel%==0 (echo Database restored.) else (echo Restore failed.)
pause
