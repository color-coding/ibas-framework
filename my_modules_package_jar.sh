#!/bin/bash
echo '*****************************************************************'
echo '    my_modules_package_jar.bat                                   '
echo '                      by eric.peng                               '
echo '                           2016.03.29                            '
echo '  说明：                                                         '
echo '     1. 安装apache-maven                                         '
echo '     2. 解压apache-maven，并设置系统变量%MAVEN_HOME%为解压的程序目录。  '
echo '     3. 添加PATH变量到%MAVEN_HOME%\bin，并检查JAVE_HOME配置是否正确。  '
echo '     4. 运行提示符运行mvn -v 检查安装是否成功。                        '
echo '     5. 此脚本会遍历当前目录的子目录，查找pom.xml并编译jar包到release目录。'
echo '     6. 可在compile_order.txt文件中调整编译顺序。                     '
echo '*****************************************************************'
# *******设置参数变量*******
WORK_FOLDER=`dirname $0`
OPNAME=`date '+%Y%m%d_%H%M%S'`
LOGFILE=${WORK_FOLDER}/my_modules_packing_log_${OPNAME}.txt

echo --create compile_folder_files.txt 
if [ ! -e ${WORK_FOLDER}/compile_folder_files.txt ]
then
ls -l | awk '/^d/{print $NF}' > ${WORK_FOLDER}/compile_folder_files.txt
fi

echo --install parent pom.xml
echo --mvn clean install
mvn clean install >>$LOGFILE

echo --start packing. 
echo --start packing. > $LOGFILE
if [ ! -e ${WORK_FOLDER}/release ]
then
mkdir ${WORK_FOLDER}/release
else
rm -rf ${WORK_FOLDER}/release/*
fi

while read line
do
if [ -e ${WORK_FOLDER}/${line}/pom.xml ]
then
echo --packing ${WORK_FOLDER}/${line} 
echo --packing ${WORK_FOLDER}/${line} >>$LOGFILE
if [ -e $MAVEN_HOME/bin/mvn ]
then
mvn clean compile -Dmaven.test.skip=true package >>$LOGFILE
else
echo --'Please install the apache-maven,the dowmload url is:http://ibas.club:8866/ibas/basis/' >>$LOGFILE
fi
if [ -e ${WORK_FOLDER}/${line}/target/*.jar ]
then
cp -r ${WORK_FOLDER}/${line}/target/*.jar ${WORK_FOLDER}/release >>$LOGFILE
echo --pack $line completed. >>$LOGFILE
echo "*****************************************************************" >>$LOGFILE
echo --pack $line completed. >>$LOGFILE
else
if [ -e ${WORK_FOLDER}/${line}/target/*.war ]
then
cp -r ${WORK_FOLDER}/${line}/target/*.war ${WORK_FOLDER}/release >>$LOGFILE
echo --pack $line completed. >>$LOGFILE
echo "*****************************************************************" >>$LOGFILE
echo --pack $line completed. >>$LOGFILE
else
echo --pack $line faild. >>$LOGFILE
echo "*****************************************************************" >>$LOGFILE
echo --pack $line faild.
fi
fi
fi
done < ${WORK_FOLDER}/compile_folder_files.txt

