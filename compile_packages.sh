#!/bin/sh
echo '****************************************************************************'
echo '             compile_packages.sh                                            '
echo '                      by niuren.zhu                                         '
echo '                           2016.06.17                                       '
echo '  说明：                                                                    '
echo '    1. 安装apache-maven，sudo apt-get install maven                         '
echo '    2. 解压apache-maven，并设置系统变量MAVEN_HOME为解压的程序目录。         '
echo '    3. 添加PATH变量到MAVEN_HOME\bin，并检查JAVE_HOME配置是否正确。          '
echo '    4. 运行提示符运行mvn -v 检查安装是否成功。                              '
echo '    5. 此脚本会遍历当前目录的子目录，查找pom.xml并编译jar包到release目录。  '
echo '    6. 可在compile_order.txt文件中调整编译顺序。                            '
echo '****************************************************************************'
# 设置参数变量
WORK_FOLDER=`pwd`
OPNAME=`date '+%Y%m%d_%H%M%S'`
LOGFILE=${WORK_FOLDER}/compile_packages_log_${OPNAME}.txt

echo --当前工作的目录是[${WORK_FOLDER}]
echo --检查编译顺序文件[compile_order.txt]
if [ ! -e ${WORK_FOLDER}/compile_order.txt ]
then
  ls -l | awk '/^d/{print $NF}' > ${WORK_FOLDER}/compile_order.txt
fi

echo --清除项目缓存
if [ -e ${WORK_FOLDER}/release/ ]
then
  rm -rf ${WORK_FOLDER}/release/
fi
mkdir -p ${WORK_FOLDER}/release/
if [ -e ${WORK_FOLDER}/pom.xml ]
then
  mvn clean install -f ${WORK_FOLDER}/pom.xml >>$LOGFILE
fi

echo --开始编译[compile_order.txt]内容
while read line
do
  if [ -e ${WORK_FOLDER}/${line}/pom.xml ]
  then
    isService=`echo ${line}|grep '.service'|wc -l`
    if [ ${isService} != 0 ]
    then
      # 网站，编译war包
      echo --开始编译[${line}]
      mvn clean package -Dmaven.test.skip=true -f ${WORK_FOLDER}/${line}/pom.xml >>$LOGFILE

      if [ -e ${WORK_FOLDER}/${line}/target/*.war ]
      then
        cp -r ${WORK_FOLDER}/${line}/target/*.war ${WORK_FOLDER}/release >>$LOGFILE
      fi
    else
      # 非网站，编译jar包并安装到本地
      echo --开始编译[${line}]+安装
      mvn clean package install -Dmaven.test.skip=true -f ${WORK_FOLDER}/${line}/pom.xml >>$LOGFILE

      if [ -e ${WORK_FOLDER}/${line}/target/*.jar ]
      then
        cp -r ${WORK_FOLDER}/${line}/target/*.jar ${WORK_FOLDER}/release >>$LOGFILE
      fi
    fi
    # 检查编译结果
    if [ -e ${WORK_FOLDER}/release/${line}*.* ]
    then
      echo --编译[${line}]成功
    else
      echo --编译[${line}]失败
    fi
  fi
done < ${WORK_FOLDER}/compile_order.txt | sed 's/\r//g'

echo --编译完成，更多信息请查看[compile_packages_log_${OPNAME}.txt]
