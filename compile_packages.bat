@echo off
setlocal EnableDelayedExpansion
echo ***************************************************************************
echo            compile_packages.bat
echo                     by niuren.zhu
echo                           2016.06.19
echo  ˵����
echo     1. ��װapache-maven�����ص�ַhttp://maven.apache.org/download.cgi��
echo     2. ��ѹapache-maven��������ϵͳ����MAVEN_HOMEΪ��ѹ�ĳ���Ŀ¼��
echo     3. ���PATH������%%MAVEN_HOME%%\bin�������JAVA_HOME�����Ƿ���ȷ��
echo     4. ������ʾ������mvn -v ��鰲װ�Ƿ�ɹ���
echo     5. �˽ű��������ǰĿ¼����Ŀ¼������pom.xml������jar����releaseĿ¼��
echo     6. ����compile_order.txt�ļ��е�������˳��
echo ****************************************************************************
rem ���ò�������
set WORK_FOLDER=%~dp0

echo --��ǰ������Ŀ¼��[%WORK_FOLDER%]
echo --������˳���ļ�[compile_order.txt]
if not exist %WORK_FOLDER%compile_order.txt dir /a:d /b %WORK_FOLDER% >%WORK_FOLDER%compile_order.txt

set MVN_BIN=mvn
if "%MAVEN_HOME%" neq "" (
  set MVN_BIN="%MAVEN_HOME%"\bin\mvn
)

echo --�����Ŀ����
if exist %WORK_FOLDER%release\ rd /s /q %WORK_FOLDER%release\ >nul
if not exist %WORK_FOLDER%release md %WORK_FOLDER%release >nul
if exist %WORK_FOLDER%pom.xml (
  call %MVN_BIN% -q clean install -f %WORK_FOLDER%pom.xml
)

echo --��ʼ����[compile_order.txt]����
for /f %%m in (%WORK_FOLDER%compile_order.txt) do (
  if exist %WORK_FOLDER%%%m\pom.xml (
    set MY_PACKAGES_FOLDER=%%m
    if !MY_PACKAGES_FOLDER:~-8!==.service (
      rem ��վ������war��
      echo --��ʼ����[%%m]
      call %MVN_BIN% -q clean package -Dmaven.test.skip=true -f %WORK_FOLDER%%%m\pom.xml
      if exist %WORK_FOLDER%%%m\target\%%m*.war copy /y %WORK_FOLDER%%%m\target\%%m*.war %WORK_FOLDER%release >nul
    ) else (
      rem ����վ������jar������װ������
      echo --��ʼ����[%%m]+��װ
      call %MVN_BIN% -q clean package install -Dmaven.test.skip=true -f %WORK_FOLDER%%%m\pom.xml
      if exist %WORK_FOLDER%%%m\target\%%m*.jar copy /y %WORK_FOLDER%%%m\target\%%m*.jar %WORK_FOLDER%release >nul
    )
    rem ��鲢���Ʊ�����
    if exist %WORK_FOLDER%release\%%m*.* (
      echo --����[%%m]�ɹ�
    ) else (
      echo --����[%%m]ʧ��
    )
  )
)
echo --�������
