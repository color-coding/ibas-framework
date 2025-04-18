@echo off
setlocal EnableDelayedExpansion
echo ***************************************************************************
echo      deploy_packages.bat
echo                     by niuren.zhu
echo                           2017.09.06
echo  说明：
echo     1. 发布jar包到maven仓库。
echo     2. 在setting.xml的^<servers^>节点下添加（其中用户名与密码需要向管理员申请）
echo           ^<server^>
echo             ^<id^>ibas-maven^<^/id^>
echo             ^<username^>用户名^<^/username^>
echo             ^<password^>密码^<^/password^>
echo           ^<^/server^>
echo ****************************************************************************
rem 设置参数变量
set WORK_FOLDER=%~dp0
rem 仓库根地址
set ROOT_URL=http://maven.colorcoding.org/repository/
rem 仓库名称
set REPOSITORY=%1
rem 设置默认仓库名称
if "%REPOSITORY%"=="" set REPOSITORY=maven-releases
set REPOSITORY_URL=%ROOT_URL%%REPOSITORY%
set REPOSITORY_ID=ibas-maven

echo --检查maven运行环境
set MVN_BIN=mvn
if "%MAVEN_HOME%" neq "" (
  set MVN_BIN="%MAVEN_HOME%"\bin\mvn
)
call %MVN_BIN% -v >nul || goto :CHECK_MAVEN_FAILD

echo --发布地址：%REPOSITORY_URL%
rem 发布父项
if exist %WORK_FOLDER%\pom.xml (
  call %MVN_BIN% deploy:deploy-file ^
    -Dfile=%WORK_FOLDER%\pom.xml ^
    -DpomFile=%WORK_FOLDER%\pom.xml ^
    -Durl=%REPOSITORY_URL% ^
    -DrepositoryId=%REPOSITORY_ID% ^
    -Dpackaging=pom
)
rem 发布子项
for /f %%m in (%WORKFOLDER%compile_order.txt) do (
  if exist %WORK_FOLDER%%%m\pom.xml (
    for /f %%l in ('dir /s /a /b %WORK_FOLDER%release\%%m-*.jar' ) do (
        set NAME=%%l
        if "!NAME:~-12!" equ "-sources.jar" (
          call %MVN_BIN% deploy:deploy-file ^
            -Dclassifier=sources ^
            -Dfile=%%l ^
            -DpomFile=%WORK_FOLDER%%%m\pom.xml ^
            -Durl=%REPOSITORY_URL% ^
            -DrepositoryId=%REPOSITORY_ID% ^
            -Dpackaging=jar
        ) else (
          call %MVN_BIN% deploy:deploy-file ^
            -Dfile=%%l ^
            -DpomFile=%WORK_FOLDER%%%m\pom.xml ^
            -Durl=%REPOSITORY_URL% ^
            -DrepositoryId=%REPOSITORY_ID% ^
            -Dpackaging=jar
        )
    )
  )
)
echo --操作完成

goto :EOF
rem ********************************以下为函数************************************
:CHECK_MAVEN_FAILD
echo 没有检测到MAVEN，请按照以下步骤检查
echo 1. 是否安装，下载地址：http://maven.apache.org/download.cgi
echo 2. 是否配置到PATH变量，配置后需要重启
echo 3. 运行mvn -v检查配置是否成功
goto :EOF