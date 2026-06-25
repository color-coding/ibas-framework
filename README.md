<div align="center">

# IBAS Framework

**BOBAS · Business Objects Framework**

企业级 Java 业务对象框架 —— 从业务对象模型、业务规则、业务逻辑、对象仓库、数据权限到审批流程，提供一套完整的系统分析、设计与开发规范。

Enterprise-grade Java Business Object Framework — a complete set of analysis, design, and development specifications from business object models to approval workflows.

[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-1.8+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.x-red.svg)](https://maven.apache.org/)
[![Version](https://img.shields.io/badge/version-0.2.0-green.svg)](pom.xml)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](#-贡献--contributing)

</div>

---

## 📖 目录 | Table of Contents

- [✨ 特性 | Features](#-特性--features)
- [🏗️ 架构概览 | Architecture](#️-架构概览--architecture)
- [📦 模块结构 | Modules](#-模块结构--modules)
- [🚀 快速开始 | Quick Start](#-快速开始--quick-start)
- [💡 核心概念 | Core Concepts](#-核心概念--core-concepts)
- [🧩 代码示例 | Code Examples](#-代码示例--code-examples)
- [🗄️ 数据库支持 | Database Support](#️-数据库支持--database-support)
- [🔧 构建与部署 | Build & Deploy](#-构建与部署--build--deploy)
- [📚 相关项目 | Related Projects](#-相关项目--related-projects)
- [🤝 贡献 | Contributing](#-贡献--contributing)
- [📄 许可证 | License](#-许可证--license)
- [🙏 鸣谢 | Thanks](#-鸣谢--thanks)

---

## ✨ 特性 | Features

- **📐 业务对象模型** — 将领域知识抽象为结构化的业务对象，支持主数据（MasterData）、单据（Document）、简单对象（Simple）三种类型
- **⚙️ 业务规则引擎** — 17 个内置规则（必填、长度、求和、乘法、状态流转等），支持自定义扩展
- **🔗 业务逻辑链** — 责任链模式实现跨对象业务逻辑，通过契约接口解耦模块间依赖
- **🗃️ 仓储模式** — 统一的数据访问抽象，支持数据库、文件、REST 服务三种持久化方式
- **🌐 多数据库适配** — 开箱即用支持 MSSQL、MySQL、PostgreSQL、SAP HANA、SQLite、Sybase、达梦 DM8 共 7 种数据库
- **✅ 审批工作流** — 可配置的多步审批流程，支持条件表达式引擎进行路由判断
- **🔐 数据权限** — 基于组织结构的读写权限控制
- **🌍 国际化** — 内置多语言支持
- **🔄 多格式序列化** — XML / JSON / CSV 序列化器，JAXB 注解驱动
- **☁️ S3 文件存储** — 集成 AWS S3 兼容的对象存储
- **🧪 完善的测试** — JUnit 4 测试覆盖核心模块

---

## 🏗️ 架构概览 | Architecture

```
┌─────────────────────────────────────────────────────┐
│              Application Layer                       │
│       (Demo · Jersey REST Services · Logic)         │
├─────────────────────────────────────────────────────┤
│            Business Object Layer                     │
│      (BusinessObject · IBOMasterData · IBODocument) │
├─────────────────────────────────────────────────────┤
│              Repository Layer                        │
│     (BORepository4DB · FileRepository · Service)    │
├─────────────────────────────────────────────────────┤
│              Database Layer                          │
│       (DbAdapter · DbFactory · SQL Builders)        │
├─────────────────────────────────────────────────────┤
│                Core Layer                            │
│   Serializable → Bindable → Trackable → FieldedObj  │
└─────────────────────────────────────────────────────┘
```

### 业务对象继承体系 | BO Inheritance

**接口层**:

```
ITrackable → IBusinessObject
                ├── IBOMasterData   主数据（Code / Name）
                ├── IBODocument      单据（DocEntry / Status）
                ├── IBOSimple        简单键值对象
                └── IBOxxxLine       子项接口
```

**实现层**:

```
Serializable   →  框架基类（JAXB 序列化钩子）
  Bindable     →  属性变更监听
    Trackable  →  状态追踪（isSavable / isDirty / isDeleted / isNew）
      FieldedObject  →  属性注册与字段管理
        BusinessObject<T>  →  业务规则 · 验证 · 审批
          具体业务对象
```

---

## 📦 模块结构 | Modules

| 模块 | 说明 |
|------|------|
| `bobas.businessobjectscommon` | **框架核心** — 业务对象基类、规则引擎、仓储、数据库抽象等 |
| `bobas.businessobjectscommon.db.mssql` | MSSQL 数据库适配器 |
| `bobas.businessobjectscommon.db.mysql` | MySQL 数据库适配器 |
| `bobas.businessobjectscommon.db.pgsql` | PostgreSQL 数据库适配器 |
| `bobas.businessobjectscommon.db.hana` | SAP HANA 数据库适配器 |
| `bobas.businessobjectscommon.db.sqlite` | SQLite 数据库适配器 |
| `bobas.businessobjectscommon.db.sybase` | Sybase 数据库适配器 |
| `bobas.businessobjectscommon.db.dm8` | 达梦 DM8 数据库适配器 |
| `bobas.businessobjectscommon.file.s3` | AWS S3 对象存储适配器 |
| `bobas.businessobjectscommon.jersey` | Jersey REST WebService 支持 |
| `bobas.businessobjectscommon.demo` | 演示项目（销售订单、物料管理） |

> 💡 模块编译顺序由 [`compile_order.txt`](compile_order.txt) 定义，核心模块须最先编译。

### 核心模块包结构 | Core Packages

| 包 | 职责 |
|---|------|
| `bo` | 业务对象定义与基类 |
| `core` | 框架核心（属性注册、字段管理、状态追踪） |
| `rule` | 业务规则引擎 |
| `rule/common` | 17 个内置业务规则实现 |
| `repository` | 数据访问层（DB / File / Service 仓储） |
| `db` | 数据库抽象层（适配器、字段类型、SQL 构建器） |
| `logic` | 业务逻辑链与契约接口 |
| `approval` | 审批工作流 |
| `expression` | 条件表达式引擎 |
| `ownership` | 数据权限 |
| `organization` | 用户与组织管理 |
| `period` | 期间管理 |
| `i18n` | 国际化 |
| `serialization` | 序列化（XML / JSON / CSV） |
| `configuration` | 配置管理 |
| `common` | 通用工具类（Strings / Bytes / DateTimes / Criteria） |
| `message` | 日志与消息 |
| `task` | 后台任务（Daemon） |

---

## 🚀 快速开始 | Quick Start

### 环境要求 | Prerequisites

- **JDK** 1.8+
- **Maven** 3.x
- 数据库（可选，开发可使用 SQLite）

### 构建 | Build

```bash
# 克隆仓库
git clone https://github.com/color-coding/ibas-framework.git
cd ibas-framework

# 构建全部模块（按 compile_order.txt 顺序，输出到 release/）
./compile_packages.sh            # Linux / macOS
compile_packages.bat             # Windows

# 或使用 Maven 直接构建核心模块
mvn clean package install -Dmaven.test.skip=true -f bobas.businessobjectscommon/pom.xml
```

### 运行测试 | Run Tests

```bash
# 核心模块全部测试
mvn test -f bobas.businessobjectscommon/pom.xml

# 单个测试类
mvn test -Dtest=TestCore -f bobas.businessobjectscommon/pom.xml

# 单个测试方法
mvn test -Dtest=TestCore#testPropertyValue -f bobas.businessobjectscommon/pom.xml
```

### Maven 依赖 | Maven Dependency

```xml
<dependency>
    <groupId>org.colorcoding.ibas</groupId>
    <artifactId>bobas.businessobjectscommon</artifactId>
    <version>0.2.0</version>
</dependency>
```

> 私有 Maven 仓库: `http://maven.colorcoding.org/repository/maven-public/`

---

## 💡 核心概念 | Core Concepts

### 业务对象 | Business Object

业务对象表示领域中具有业务意义的完整数据，可能由多个类（主表 + 子项行）组成。

| 类型 | 说明 | 主键 | 特点 |
|------|------|------|------|
| **主数据** MasterData | 非时点数据，一般在业务流程中被引用 | `Code` | 如：客户、物料、供应商 |
| **单据** Document | 时点数据，反映当时的业务执行状况 | `DocEntry` | 如：销售订单、采购订单 |
| **简单对象** Simple | 非时点数据，一般作为主数据的补充 | `ObjectKey` | 如：配置项、参数 |

### 业务规则 | Business Rules

业务对象内部的逻辑，例如：订单总计金额 = 产品价格 × 产品数量；订单必须输入客户。

### 业务逻辑 | Business Logics

业务对象的外部逻辑，即对其他业务对象的影响，例如：出库单减少库存数据。通过逻辑链（责任链模式）实现跨对象联动。

### 对象仓库 | Repository

用于业务对象的持久化及从持久化状态重建为业务对象。

### 数据权限 | Ownership

控制当前用户能否对某个业务对象进行读写，可根据组织结构按组织关系控制权限。

### 审批流程 | ApprovalProcess

业务对象发生写入时，根据配置判断是否自动发起审批流程。支持多步审批，全部步骤批准后置为批准状态，任意步骤拒绝则拒绝。

---

## 🧩 代码示例 | Code Examples

### 定义业务对象 | Define a Business Object

```java
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = SalesOrder.BUSINESS_OBJECT_NAME, namespace = MyConfiguration.NAMESPACE_BO)
@XmlRootElement(name = SalesOrder.BUSINESS_OBJECT_NAME, namespace = MyConfiguration.NAMESPACE_BO)
@BusinessObjectUnit(code = SalesOrder.BUSINESS_OBJECT_CODE)
public class SalesOrder extends BusinessObject<SalesOrder> implements ISalesOrder {

    private static final long serialVersionUID = 1L;
    private static final Class<?> MY_CLASS = SalesOrder.class;
    public static final String DB_TABLE_NAME = "CC_TT_ORDR";
    public static final String BUSINESS_OBJECT_CODE = "CC_TT_SALESORDER";

    // ── 属性定义 ──
    private static final String PROPERTY_DOCENTRY_NAME = "DocEntry";

    @DbField(name = "DocEntry", type = DataType.NUMERIC, table = DB_TABLE_NAME, primaryKey = true)
    public static final IPropertyInfo<Integer> PROPERTY_DOCENTRY =
        registerProperty(PROPERTY_DOCENTRY_NAME, Integer.class, MY_CLASS);

    @XmlElement(name = PROPERTY_DOCENTRY_NAME)
    public final Integer getDocEntry() {
        return this.getProperty(PROPERTY_DOCENTRY);
    }

    public final void setDocEntry(Integer value) {
        this.setProperty(PROPERTY_DOCENTRY, value);
    }

    // ── 集合属性 ──
    @XmlElementWrapper(name = "SalesOrderItems")
    @XmlElement(name = SalesOrderItem.BUSINESS_OBJECT_NAME, type = SalesOrderItem.class)
    public final ISalesOrderItems getSalesOrderItems() {
        return this.getProperty(PROPERTY_SALESORDERITEMS);
    }

    // ── 初始化 ──
    @Override
    protected void initialize() {
        super.initialize();
        this.setSalesOrderItems(new SalesOrderItems(this));
        this.setDocumentStatus(emDocumentStatus.RELEASED);
    }

    // ── 注册业务规则 ──
    @Override
    protected IBusinessRule[] registerRules() {
        return new IBusinessRule[] {
            new BusinessRuleRequired(PROPERTY_CUSTOMERCODE),                          // 必填
            new BusinessRuleMaxLength(20, PROPERTY_CUSTOMERCODE),                     // 最大长度
            new BusinessRuleRequiredElements(PROPERTY_SALESORDERITEMS),               // 要求有行项
            new BusinessRuleSumElements(PROPERTY_DOCUMENTTOTAL,                       // 行汇总
                PROPERTY_SALESORDERITEMS, SalesOrderItem.PROPERTY_LINETOTAL),
            new BusinessRuleDocumentStatus(PROPERTY_DOCUMENTSTATUS,                   // 状态联动
                PROPERTY_SALESORDERITEMS, SalesOrderItem.PROPERTY_LINESTATUS),
        };
    }
}
```

### 查询数据 | Query Data

```java
ICriteria criteria = new Criteria();
criteria.setBusinessObject("CC_TT_SALESORDER");

ICondition cond = criteria.getConditions().create();
cond.setAlias("DocEntry");
cond.setValue(123);

ISort sort = criteria.getSorts().create();
sort.setAlias("CreateDate");
sort.setSortType(SortType.DESCENDING);
```

### 调用仓储 | Use Repository

```java
IBORepositoryApplication<ISalesOrder> repo =
    new BORepositoryServiceApplication<>(ISalesOrder.class);

// 查询
ISalesOrder order = repo.fetch(criteria);

// 保存
OperationResult<ISalesOrder> result = repo.save(order);
```

### 内置业务规则 | Built-in Business Rules

| 类别 | 规则类 | 说明 |
|------|--------|------|
| 验证 | `BusinessRuleRequired` | 必填校验 |
| 验证 | `BusinessRuleMaxLength` | 最大长度限制 |
| 验证 | `BusinessRuleMinValue` | 最小值限制 |
| 验证 | `BusinessRuleMaxValue` | 最大值限制 |
| 验证 | `BusinessRuleRequiredElements` | 集合元素必填 |
| 验证 | `BusinessRuleTrim` | 去除空白 |
| 计算 | `BusinessRuleSumElements` | 集合元素求和 |
| 计算 | `BusinessRuleSummation` | 属性求和 |
| 计算 | `BusinessRuleMultiplication` | 乘法 |
| 计算 | `BusinessRuleDivision` | 除法 |
| 计算 | `BusinessRuleSubtraction` | 减法 |
| 计算 | `BusinessRuleAdditiveDeduction` | 加扣减 |
| 计算 | `BusinessRuleMultiplicativeDeduction` | 乘扣减 |
| 比较 | `BusinessRuleMaxProperty` | 不超过另一属性值 |
| 比较 | `BusinessRuleMinProperty` | 不低于另一属性值 |
| 状态 | `BusinessRuleDocumentStatus` | 单据状态联动 |

---

## 🗄️ 数据库支持 | Database Support

| 数据库 | 模块 | 状态 |
|--------|------|:----:|
| Microsoft SQL Server | `bobas.businessobjectscommon.db.mssql` | ✅ |
| MySQL | `bobas.businessobjectscommon.db.mysql` | ✅ |
| PostgreSQL | `bobas.businessobjectscommon.db.pgsql` | ✅ |
| SAP HANA | `bobas.businessobjectscommon.db.hana` | ✅ |
| SQLite | `bobas.businessobjectscommon.db.sqlite` | ✅ |
| Sybase | `bobas.businessobjectscommon.db.sybase` | ✅ |
| 达梦 DM8 | `bobas.businessobjectscommon.db.dm8` | ✅ |

---

## 🔧 构建与部署 | Build & Deploy

```bash
# 构建全部模块，输出 JAR/WAR 到 release/
./compile_packages.sh

# 部署到 Maven 仓库
./deploy_packages.sh maven-releases

# 构建单个模块
mvn clean package install -Dmaven.test.skip=true -f bobas.businessobjectscommon/pom.xml
```

**编译顺序**（定义于 `compile_order.txt`）:

```
1.  bobas.businessobjectscommon            ← 核心，最先编译
2.  bobas.businessobjectscommon.db.hana
3.  bobas.businessobjectscommon.db.mssql
4.  bobas.businessobjectscommon.db.mysql
5.  bobas.businessobjectscommon.db.pgsql
6.  bobas.businessobjectscommon.db.sybase
7.  bobas.businessobjectscommon.db.sqlite
8.  bobas.businessobjectscommon.db.dm8
9.  bobas.businessobjectscommon.file.s3
10. bobas.businessobjectscommon.jersey
11. bobas.businessobjectscommon.demo       ← 最后编译
```

---

## 📚 相关项目 | Related Projects

| 项目 | 说明 |
|------|------|
| [btulz.transforms](https://github.com/color-coding/btulz.transforms) | 代码生成工具，可根据领域模型自动生成本框架代码、数据库结构、SQL 脚本 |
| [ibas-typescript](https://github.com/color-coding/ibas-typescript) | 前端架构，配合 ibas-framework 服务端，基于 SAP OpenUI5 |
| [ibas-businessone](https://github.com/color-coding/ibas-businessone) | SAP Business One 集成层 |

---

## 🤝 贡献 | Contributing

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支（`git checkout -b feature/amazing-feature`）
3. 提交更改（`git commit -m 'Add amazing feature'`）
4. 推送到分支（`git push origin feature/amazing-feature`）
5. 发起 Pull Request

---

## 📄 许可证 | License

本项目基于 [Apache License 2.0](LICENSE) 开源。
---

## 🙏 鸣谢 | Thanks

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/NiurenZhu">牛加人等于朱</a><br>
      <sub>NiurenZhu</sub>
    </td>
    <td align="center">
      <a href="https://github.com/three-stones">老彭</a><br>
      <sub>three-stones</sub>
    </td>
    <td align="center">
      <a href="https://github.com/neilzhou0309">老周</a><br>
      <sub>neilzhou0309</sub>
    </td>
  </tr>
</table>

<div align="center">

**[Color-Coding Studio](http://colorcoding.org/)** · 咔啦工作室

</div>
