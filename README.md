# ibas-framework
业务系统架构，提供了一套完整的系统分析，设计，开发规范及流程。<br>
[btulz.transforms](https://github.com/color-coding/btulz.transforms "业务系统工具")可实现根据模型自动生成此架构代码。<br>

## 鼓励师 | Encourager
[![encourager]](http://baike.baidu.com/view/10998931.htm)  
[encourager]:https://github.com/color-coding/ibas-framework/raw/master/encourager.gif "はしもとかんな"
* 姓名：桥本环奈（はしもとかんな）
* 生日：1999年2月3日
* 国籍：日本
* WOW ：[曼秀雷敦花絮](http://www.bilibili.com/video/av2708978/ "B站指日可待")

## 项目结构 | Projects
* bobas.businessobjectscommon           框架核心项目
* bobas.businessobjectscommon.db.*      数据库支持项目
* bobas.businessobjectscommon.*.demo    演示项目
* bobas.businessobjectscommon.jersey    webservice支持项目，jersey实现
* bobas.businessobjectscommon.cxf       webservice支持项目，cxf实现

## 说明 | Instructions
此架构从业务对象模型，业务规则，业务逻辑，业务对象仓库，数据权限，审批流程等方面分解业务应用系统。<br>

### 业务对象 | Business Object
业务对象，表示领域中的具有业务意义的完整数据，它可能由多个类组成。<br>
业务对象主要分为以下类型：<br>
主数据（Document），非时点数据，一般在业务流程中被引用。<br>
单据（MasterData），时点数据，反应当时的业务执行状况。<br>
简单对象（Simple），非时点数据，一般作为主数据的补充。<br>

### 业务规则 | Business Rules
业务规则，指业务对象内部的逻辑，如：订单对象，订单的总计金额 = 产品价格 × 产品数量；订单必须输入客户等。<br>

### 业务逻辑 | Business Logics
业务逻辑，指业务对象的外部逻辑，对其他业务对象的影响，如：出库单对象，会减少库存数据。<br>

### 对象仓库 | BO Repository
业务对象仓库，用于对象的持久化，或从持久状况中转为业务对象。目前支持，数据库持久化及文件持久化。<br>
数据库支持：MSSQL，MYSQL，PGSQL，HANA。<br>

### 数据权限 | BO Ownership
数据权限，指当前用户能否对某个业务对象进行读写，可根据组织结构实现按组织关系控制某个业务对象的读写权限。<br>

### 审批流程 | BO ApprovalProcess
审批流程，用于业务对象发生写入时，根据配置判断是否自动发起一个审批流程。<br>
审批流程可以配置为多步审批，全部步骤批准后业务对象置为批准状态，任意步骤拒绝则业务对象拒绝。<br>

## 鸣谢 | Thanks
[牛加人等于朱](http://baike.baidu.com/view/1769.htm "NiurenZhu")<br>
[老彭](http://baike.baidu.com/view/1828.htm "three-stones")<br>
[周周](http://baike.baidu.com/view/1751.htm "neilzhou0309")<br>
[Color-Coding](http://colorcoding.org/ "咔啦工作室")<br>