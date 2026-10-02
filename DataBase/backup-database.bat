@echo off
REM Saves the whole JewelCraft database (tables + all data) into JewelCraft.sql in this folder.
REM Run it whenever you want an up-to-date copy. MySQL asks for the root password.
setlocal
set MYSQL_BIN=C:\Program Files\MySQL\MySQL Server 9.7\bin
set OUT=%~dp0JewelCraft.sql

"%MYSQL_BIN%\mysqldump.exe" -u root -p --databases JewelCraft --add-drop-table --routines --triggers ^
  --single-transaction --set-gtid-purged=OFF --default-character-set=utf8mb4 --skip-dump-date --result-file="%OUT%"

if %errorlevel%==0 (echo Saved to %OUT%) else (echo Backup failed.)
pause
