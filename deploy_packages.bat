@echo off
setlocal EnableDelayedExpansion
echo ***************************************************************************
echo      deploy_packages.bat
echo                     by niuren.zhu
echo                           2017.09.06
echo  ˵����
echo     1. ����jar����maven�ֿ⡣
echo     2. ��setting.xml��^<servers^>�ڵ�����ӣ������û�����������Ҫ�����Ա���룩
echo           ^<server^>
echo             ^<id^>ibas-maven^<^/id^>
echo             ^<username^>�û���^<^/username^>
echo             ^<password^>����^<^/password^>
echo           ^<^/server^>
echo ****************************************************************************
rem ���ò�������
set WORK_FOLDER=%~dp0
rem �ֿ����ַ
set ROOT_URL=http://maven.colorcoding.org/repository/
rem �ֿ�����
set REPOSITORY=%1
rem ����Ĭ�ϲֿ�����
if "%REPOSITORY%"=="" set REPOSITORY=maven-releases
set REPOSITORY_URL=%ROOT_URL%%REPOSITORY%
set REPOSITORY_ID=ibas-maven

echo --���maven���л���
set MVN_BIN=mvn
if "%MAVEN_HOME%" neq "" (
  set MVN_BIN="%MAVEN_HOME%"\bin\mvn
)
call %MVN_BIN% -v >nul || goto :CHECK_MAVEN_FAILD

echo --������ַ��%REPOSITORY_URL%
rem ��������
if exist %WORK_FOLDER%\pom.xml (
  call %MVN_BIN% deploy:deploy-file ^
    -Dfile=%WORK_FOLDER%\pom.xml ^
    -DpomFile=%WORK_FOLDER%\pom.xml ^
    -Durl=%REPOSITORY_URL% ^
    -DrepositoryId=%REPOSITORY_ID% ^
    -Dpackaging=pom
)
rem ��������
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
echo --�������

goto :EOF
rem ********************************����Ϊ����************************************
:CHECK_MAVEN_FAILD
echo û�м�⵽MAVEN���밴�����²�����
echo 1. �Ƿ�װ�����ص�ַ��http://maven.apache.org/download.cgi
echo 2. �Ƿ����õ�PATH���������ú���Ҫ����
echo 3. ����mvn -v��������Ƿ�ɹ�
goto :EOF