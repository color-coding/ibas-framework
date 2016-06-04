@echo off
echo *****************************************************************
echo      my_modules_package_jar.bat
echo                     by kingrui.zhang
echo                           2016.03.29
echo  说明：
echo     1. 安装apache-maven。
echo     2. 解压apache-maven，并设置系统变量%MAVEN_HOME%为解压的程序目录。
echo     3. 添加PATH变量到%MAVEN_HOME%\bin，并检查JAVE_HOME配置是否正确。
echo     4. 运行提示符运行mvn -v 检查安装是否成功。
echo     5. 此脚本会遍历当前目录的子目录，查找pom.xml并编译jar包到release目录。
echo     6. 可在compile_order.txt文件中调整编译顺序。
echo *****************************************************************

REM *******设置参数变量*******
SET WORK_FOLDER=%~dp0
SET h=%time:~0,2%
SET hh=%h: =0%
SET OPNAME=%date:~0,4%%date:~5,2%%date:~8,2%_%hh%%time:~3,2%%time:~6,2%
SET LOGFILE=%WORK_FOLDER%my_modules_packing_log_%OPNAME%.txt

echo --checking compile_order.txt.
if not exist %WORKFOLDER%compile_order.txt dir /a:d /b %WORKFOLDER% >%WORKFOLDER%compile_order.txt

echo --install parent pom.xml
echo --mvn clean install
call mvn clean install >>%LOGFILE%

echo --start packing.
if not exist %WORK_FOLDER%release md %WORK_FOLDER%release
if exist %WORK_FOLDER%release del /f /s /q %WORK_FOLDER%release\*.* >>%LOGFILE%

for /f %%m in (%WORKFOLDER%compile_order.txt) DO (
if exist %WORK_FOLDER%%%m\pom.xml (
echo --packing %%m.
echo --packing %%m. >>%LOGFILE%
cd /d %WORK_FOLDER%%%m
call mvn clean compile -Dmaven.test.skip=true package >>%LOGFILE%
if exist %WORK_FOLDER%%%m\target\%%m*.jar (
copy /y %WORK_FOLDER%%%m\target\%%m*.jar %WORK_FOLDER%release >>%LOGFILE%
echo --pack %%m completed. >>%LOGFILE%
echo ******************************************************** >>%LOGFILE%
echo --pack %%m completed.
) else (
if exist %WORK_FOLDER%%%m\target\%%m*.war (
copy /y %WORK_FOLDER%%%m\target\%%m*.war %WORK_FOLDER%release >>%LOGFILE%
echo --pack %%m completed. >>%LOGFILE%
echo ******************************************************** >>%LOGFILE%
echo --pack %%m completed.
)else (
echo --pack %%m faild. >>%LOGFILE%
echo ******************************************************** >>%LOGFILE%
echo --pack %%m faild.
)
) 
)else (
cd /d %WORK_FOLDER%
)
)
cd /d %WORK_FOLDER%

echo --rename packages.
for /f "tokens=1,2,* delims=- " %%a in ('dir /b %WORK_FOLDER%release\') do (
if exist "%WORK_FOLDER%release\*-.jar" ren "%WORK_FOLDER%release\%%a-%%b-%%c" "%%a.%%b.jar" >>%LOGFILE%
)
echo --see [%LOGFILE%] for details.
echo --done.