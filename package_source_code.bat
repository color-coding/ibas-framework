@echo off
echo ***************************************************************************
echo      package_source_code.bat
echo                     by niuren.zhu
echo                           2017.01.19
echo  说明：
echo     1. 安装7zip，下载地址http://www.7-zip.org/。
echo     2. 添加7zip到系统变量PATH，并运行7z -h检查是否正常。
echo     3. 脚本会打包当前目录源码到release/SourceCode.zip。
echo ****************************************************************************
REM 设置参数变量
SET WORK_FOLDER=%~dp0
SET RELEASE_FOLDER=%WORK_FOLDER%release\
SET PACKAGE_TYPE=zip
SET FILE_NAME=SourceCode.%PACKAGE_TYPE%

echo --当前工作的目录是[%WORK_FOLDER%]
if not exist "%RELEASE_FOLDER%" mkdir "%RELEASE_FOLDER%"
if exist "%RELEASE_FOLDER%%FILE_NAME%" del /q "%RELEASE_FOLDER%%FILE_NAME%"

7z a -t%PACKAGE_TYPE% "%RELEASE_FOLDER%%FILE_NAME%" * -xr!release -xr!.git -xr!target\ -x!*.%PACKAGE_TYPE%

if exist "%RELEASE_FOLDER%%FILE_NAME%" (
  echo --打包成功
) else (
  echo --打包失败
)
