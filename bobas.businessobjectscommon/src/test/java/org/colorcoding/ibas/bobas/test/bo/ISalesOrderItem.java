package org.colorcoding.ibas.bobas.test.bo;

import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 销售订单-行
 * 
 */
public interface ISalesOrderItem extends IBODocumentLine {

	/**
	 * 获取-编码
	 * 
	 * @return 值
	 */
	Integer getDocEntry();

	/**
	 * 设置-编码
	 * 
	 * @param value
	 *            值
	 */
	void setDocEntry(Integer value);

	/**
	 * 获取-行号
	 * 
	 * @return 值
	 */
	Integer getLineId();

	/**
	 * 设置-行号
	 * 
	 * @param value
	 *            值
	 */
	void setLineId(Integer value);

	/**
	 * 获取-显示顺序
	 * 
	 * @return 值
	 */
	Integer getVisOrder();

	/**
	 * 设置-显示顺序
	 * 
	 * @param value
	 *            值
	 */
	void setVisOrder(Integer value);

	/**
	 * 获取-类型
	 * 
	 * @return 值
	 */
	String getObjectCode();

	/**
	 * 设置-类型
	 * 
	 * @param value
	 *            值
	 */
	void setObjectCode(String value);

	/**
	 * 获取-数据源
	 * 
	 * @return 值
	 */
	String getDataSource();

	/**
	 * 设置-数据源
	 * 
	 * @param value
	 *            值
	 */
	void setDataSource(String value);

	/**
	 * 获取-实例号（版本）
	 * 
	 * @return 值
	 */
	Integer getLogInst();

	/**
	 * 设置-实例号（版本）
	 * 
	 * @param value
	 *            值
	 */
	void setLogInst(Integer value);

	/**
	 * 获取-取消
	 * 
	 * @return 值
	 */
	emYesNo getCanceled();

	/**
	 * 设置-取消
	 * 
	 * @param value
	 *            值
	 */
	void setCanceled(emYesNo value);

	/**
	 * 获取-状态
	 * 
	 * @return 值
	 */
	emBOStatus getStatus();

	/**
	 * 设置-状态
	 * 
	 * @param value
	 *            值
	 */
	void setStatus(emBOStatus value);

	/**
	 * 获取-单据状态
	 * 
	 * @return 值
	 */
	emDocumentStatus getLineStatus();

	/**
	 * 设置-单据状态
	 * 
	 * @param value
	 *            值
	 */
	void setLineStatus(emDocumentStatus value);

	/**
	 * 获取-创建日期
	 * 
	 * @return 值
	 */
	DateTime getCreateDate();

	/**
	 * 设置-创建日期
	 * 
	 * @param value
	 *            值
	 */
	void setCreateDate(DateTime value);

	/**
	 * 获取-创建时间
	 * 
	 * @return 值
	 */
	Short getCreateTime();

	/**
	 * 设置-创建时间
	 * 
	 * @param value
	 *            值
	 */
	void setCreateTime(Short value);

	/**
	 * 获取-修改日期
	 * 
	 * @return 值
	 */
	DateTime getUpdateDate();

	/**
	 * 设置-修改日期
	 * 
	 * @param value
	 *            值
	 */
	void setUpdateDate(DateTime value);

	/**
	 * 获取-修改时间
	 * 
	 * @return 值
	 */
	Short getUpdateTime();

	/**
	 * 设置-修改时间
	 * 
	 * @param value
	 *            值
	 */
	void setUpdateTime(Short value);

	/**
	 * 获取-创建用户
	 * 
	 * @return 值
	 */
	Integer getCreateUserSign();

	/**
	 * 设置-创建用户
	 * 
	 * @param value
	 *            值
	 */
	void setCreateUserSign(Integer value);

	/**
	 * 获取-修改用户
	 * 
	 * @return 值
	 */
	Integer getUpdateUserSign();

	/**
	 * 设置-修改用户
	 * 
	 * @param value
	 *            值
	 */
	void setUpdateUserSign(Integer value);

	/**
	 * 获取-创建动作标识
	 * 
	 * @return 值
	 */
	String getCreateActionId();

	/**
	 * 设置-创建动作标识
	 * 
	 * @param value
	 *            值
	 */
	void setCreateActionId(String value);

	/**
	 * 获取-更新动作标识
	 * 
	 * @return 值
	 */
	String getUpdateActionId();

	/**
	 * 设置-更新动作标识
	 * 
	 * @param value
	 *            值
	 */
	void setUpdateActionId(String value);

	/**
	 * 获取-参考1
	 * 
	 * @return 值
	 */
	String getReference1();

	/**
	 * 设置-参考1
	 * 
	 * @param value
	 *            值
	 */
	void setReference1(String value);

	/**
	 * 获取-参考2
	 * 
	 * @return 值
	 */
	String getReference2();

	/**
	 * 设置-参考2
	 * 
	 * @param value
	 *            值
	 */
	void setReference2(String value);

	/**
	 * 获取-已引用
	 * 
	 * @return 值
	 */
	emYesNo getReferenced();

	/**
	 * 设置-已引用
	 * 
	 * @param value
	 *            值
	 */
	void setReferenced(emYesNo value);

	/**
	 * 获取-已删除
	 * 
	 * @return 值
	 */
	emYesNo getDeleted();

	/**
	 * 设置-已删除
	 * 
	 * @param value
	 *            值
	 */
	void setDeleted(emYesNo value);

	/**
	 * 获取-目标单据类型
	 * 
	 * @return 值
	 */
	String getTargetDocumentType();

	/**
	 * 设置-目标单据类型
	 * 
	 * @param value
	 *            值
	 */
	void setTargetDocumentType(String value);

	/**
	 * 获取-目标单据内部标识
	 * 
	 * @return 值
	 */
	Integer getTargetDocumentEntry();

	/**
	 * 设置-目标单据内部标识
	 * 
	 * @param value
	 *            值
	 */
	void setTargetDocumentEntry(Integer value);

	/**
	 * 获取-基本单据参考
	 * 
	 * @return 值
	 */
	String getBaseReference();

	/**
	 * 设置-基本单据参考
	 * 
	 * @param value
	 *            值
	 */
	void setBaseReference(String value);

	/**
	 * 获取-基本单据类型
	 * 
	 * @return 值
	 */
	String getBaseDocumentType();

	/**
	 * 设置-基本单据类型
	 * 
	 * @param value
	 *            值
	 */
	void setBaseDocumentType(String value);

	/**
	 * 获取-基本单据内部标识
	 * 
	 * @return 值
	 */
	Integer getBaseDocumentEntry();

	/**
	 * 设置-基本单据内部标识
	 * 
	 * @param value
	 *            值
	 */
	void setBaseDocumentEntry(Integer value);

	/**
	 * 获取-基本凭证中的行编号
	 * 
	 * @return 值
	 */
	Integer getBaseDocumentLineId();

	/**
	 * 设置-基本凭证中的行编号
	 * 
	 * @param value
	 *            值
	 */
	void setBaseDocumentLineId(Integer value);

	/**
	 * 获取-物料编号
	 * 
	 * @return 值
	 */
	String getItemCode();

	/**
	 * 设置-物料编号
	 * 
	 * @param value
	 *            值
	 */
	void setItemCode(String value);

	/**
	 * 获取-物料/服务描述
	 * 
	 * @return 值
	 */
	String getItemDescription();

	/**
	 * 设置-物料/服务描述
	 * 
	 * @param value
	 *            值
	 */
	void setItemDescription(String value);

	/**
	 * 获取-序号管理
	 * 
	 * @return 值
	 */
	emYesNo getSerialNumberManagement();

	/**
	 * 设置-序号管理
	 * 
	 * @param value
	 *            值
	 */
	void setSerialNumberManagement(emYesNo value);

	/**
	 * 获取-管理批号
	 * 
	 * @return 值
	 */
	emYesNo getBatchNumberManagement();

	/**
	 * 设置-管理批号
	 * 
	 * @param value
	 *            值
	 */
	void setBatchNumberManagement(emYesNo value);

	/**
	 * 获取-服务卡号管理
	 * 
	 * @return 值
	 */
	emYesNo getServiceNumberManagement();

	/**
	 * 设置-服务卡号管理
	 * 
	 * @param value
	 *            值
	 */
	void setServiceNumberManagement(emYesNo value);

	/**
	 * 获取-数量
	 * 
	 * @return 值
	 */
	Decimal getQuantity();

	/**
	 * 设置-数量
	 * 
	 * @param value
	 *            值
	 */
	void setQuantity(Decimal value);

	/**
	 * 设置-数量
	 * 
	 * @param value
	 *            值
	 */
	void setQuantity(String value);

	/**
	 * 设置-数量
	 * 
	 * @param value
	 *            值
	 */
	void setQuantity(int value);

	/**
	 * 设置-数量
	 * 
	 * @param value
	 *            值
	 */
	void setQuantity(double value);

	/**
	 * 获取-行交货日期
	 * 
	 * @return 值
	 */
	DateTime getDeliveryDate();

	/**
	 * 设置-行交货日期
	 * 
	 * @param value
	 *            值
	 */
	void setDeliveryDate(DateTime value);

	/**
	 * 获取-剩余未清数量
	 * 
	 * @return 值
	 */
	Decimal getOpenQuantity();

	/**
	 * 设置-剩余未清数量
	 * 
	 * @param value
	 *            值
	 */
	void setOpenQuantity(Decimal value);

	/**
	 * 设置-剩余未清数量
	 * 
	 * @param value
	 *            值
	 */
	void setOpenQuantity(String value);

	/**
	 * 设置-剩余未清数量
	 * 
	 * @param value
	 *            值
	 */
	void setOpenQuantity(int value);

	/**
	 * 设置-剩余未清数量
	 * 
	 * @param value
	 *            值
	 */
	void setOpenQuantity(double value);

	/**
	 * 获取-单价
	 * 
	 * @return 值
	 */
	Decimal getPrice();

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	void setPrice(Decimal value);

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	void setPrice(String value);

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	void setPrice(int value);

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	void setPrice(double value);

	/**
	 * 获取-价格货币
	 * 
	 * @return 值
	 */
	String getPriceCurrency();

	/**
	 * 设置-价格货币
	 * 
	 * @param value
	 *            值
	 */
	void setPriceCurrency(String value);

	/**
	 * 获取-货币汇率
	 * 
	 * @return 值
	 */
	Decimal getCurrencyRate();

	/**
	 * 设置-货币汇率
	 * 
	 * @param value
	 *            值
	 */
	void setCurrencyRate(Decimal value);

	/**
	 * 设置-货币汇率
	 * 
	 * @param value
	 *            值
	 */
	void setCurrencyRate(String value);

	/**
	 * 设置-货币汇率
	 * 
	 * @param value
	 *            值
	 */
	void setCurrencyRate(int value);

	/**
	 * 设置-货币汇率
	 * 
	 * @param value
	 *            值
	 */
	void setCurrencyRate(double value);

	/**
	 * 获取-每行折扣 %
	 * 
	 * @return 值
	 */
	Decimal getDiscountPerLine();

	/**
	 * 设置-每行折扣 %
	 * 
	 * @param value
	 *            值
	 */
	void setDiscountPerLine(Decimal value);

	/**
	 * 设置-每行折扣 %
	 * 
	 * @param value
	 *            值
	 */
	void setDiscountPerLine(String value);

	/**
	 * 设置-每行折扣 %
	 * 
	 * @param value
	 *            值
	 */
	void setDiscountPerLine(int value);

	/**
	 * 设置-每行折扣 %
	 * 
	 * @param value
	 *            值
	 */
	void setDiscountPerLine(double value);

	/**
	 * 获取-行总计
	 * 
	 * @return 值
	 */
	Decimal getLineTotal();

	/**
	 * 设置-行总计
	 * 
	 * @param value
	 *            值
	 */
	void setLineTotal(Decimal value);

	/**
	 * 设置-行总计
	 * 
	 * @param value
	 *            值
	 */
	void setLineTotal(String value);

	/**
	 * 设置-行总计
	 * 
	 * @param value
	 *            值
	 */
	void setLineTotal(int value);

	/**
	 * 设置-行总计
	 * 
	 * @param value
	 *            值
	 */
	void setLineTotal(double value);

	/**
	 * 获取-未清金额
	 * 
	 * @return 值
	 */
	Decimal getOpenAmount();

	/**
	 * 设置-未清金额
	 * 
	 * @param value
	 *            值
	 */
	void setOpenAmount(Decimal value);

	/**
	 * 设置-未清金额
	 * 
	 * @param value
	 *            值
	 */
	void setOpenAmount(String value);

	/**
	 * 设置-未清金额
	 * 
	 * @param value
	 *            值
	 */
	void setOpenAmount(int value);

	/**
	 * 设置-未清金额
	 * 
	 * @param value
	 *            值
	 */
	void setOpenAmount(double value);

	/**
	 * 获取-供应商目录编号
	 * 
	 * @return 值
	 */
	String getVendorCatalogNumber();

	/**
	 * 设置-供应商目录编号
	 * 
	 * @param value
	 *            值
	 */
	void setVendorCatalogNumber(String value);

	/**
	 * 获取-仓库代码
	 * 
	 * @return 值
	 */
	String getWarehouse();

	/**
	 * 设置-仓库代码
	 * 
	 * @param value
	 *            值
	 */
	void setWarehouse(String value);

	/**
	 * 获取-物料单基础数量
	 * 
	 * @return 值
	 */
	Decimal getTreeBasisQuantity();

	/**
	 * 设置-物料单基础数量
	 * 
	 * @param value
	 *            值
	 */
	void setTreeBasisQuantity(Decimal value);

	/**
	 * 设置-物料单基础数量
	 * 
	 * @param value
	 *            值
	 */
	void setTreeBasisQuantity(String value);

	/**
	 * 设置-物料单基础数量
	 * 
	 * @param value
	 *            值
	 */
	void setTreeBasisQuantity(int value);

	/**
	 * 设置-物料单基础数量
	 * 
	 * @param value
	 *            值
	 */
	void setTreeBasisQuantity(double value);

	/**
	 * 获取-行标志号
	 * 
	 * @return 值
	 */
	String getLineSign();

	/**
	 * 设置-行标志号
	 * 
	 * @param value
	 *            值
	 */
	void setLineSign(String value);

	/**
	 * 获取-父项行标志号
	 * 
	 * @return 值
	 */
	String getParentLineSign();

	/**
	 * 设置-父项行标志号
	 * 
	 * @param value
	 *            值
	 */
	void setParentLineSign(String value);

	/**
	 * 获取-科目代码
	 * 
	 * @return 值
	 */
	String getAccountCode();

	/**
	 * 设置-科目代码
	 * 
	 * @param value
	 *            值
	 */
	void setAccountCode(String value);

	/**
	 * 获取-毛利的基础价格
	 * 
	 * @return 值
	 */
	Decimal getBasePriceforGrossProfit();

	/**
	 * 设置-毛利的基础价格
	 * 
	 * @param value
	 *            值
	 */
	void setBasePriceforGrossProfit(Decimal value);

	/**
	 * 设置-毛利的基础价格
	 * 
	 * @param value
	 *            值
	 */
	void setBasePriceforGrossProfit(String value);

	/**
	 * 设置-毛利的基础价格
	 * 
	 * @param value
	 *            值
	 */
	void setBasePriceforGrossProfit(int value);

	/**
	 * 设置-毛利的基础价格
	 * 
	 * @param value
	 *            值
	 */
	void setBasePriceforGrossProfit(double value);

	/**
	 * 获取-单价
	 * 
	 * @return 值
	 */
	Decimal getUnitPrice();

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	void setUnitPrice(Decimal value);

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	void setUnitPrice(String value);

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	void setUnitPrice(int value);

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	void setUnitPrice(double value);

	/**
	 * 获取-库存计量单位
	 * 
	 * @return 值
	 */
	String getInventoryUoM();

	/**
	 * 设置-库存计量单位
	 * 
	 * @param value
	 *            值
	 */
	void setInventoryUoM(String value);

	/**
	 * 获取-条形码
	 * 
	 * @return 值
	 */
	String getBarCode();

	/**
	 * 设置-条形码
	 * 
	 * @param value
	 *            值
	 */
	void setBarCode(String value);

	/**
	 * 获取-每行税率
	 * 
	 * @return 值
	 */
	Decimal getTaxRateperLine();

	/**
	 * 设置-每行税率
	 * 
	 * @param value
	 *            值
	 */
	void setTaxRateperLine(Decimal value);

	/**
	 * 设置-每行税率
	 * 
	 * @param value
	 *            值
	 */
	void setTaxRateperLine(String value);

	/**
	 * 设置-每行税率
	 * 
	 * @param value
	 *            值
	 */
	void setTaxRateperLine(int value);

	/**
	 * 设置-每行税率
	 * 
	 * @param value
	 *            值
	 */
	void setTaxRateperLine(double value);

	/**
	 * 获取-税定义
	 * 
	 * @return 值
	 */
	String getTaxDefinition();

	/**
	 * 设置-税定义
	 * 
	 * @param value
	 *            值
	 */
	void setTaxDefinition(String value);

	/**
	 * 获取-毛价
	 * 
	 * @return 值
	 */
	Decimal getGrossPrice();

	/**
	 * 设置-毛价
	 * 
	 * @param value
	 *            值
	 */
	void setGrossPrice(Decimal value);

	/**
	 * 设置-毛价
	 * 
	 * @param value
	 *            值
	 */
	void setGrossPrice(String value);

	/**
	 * 设置-毛价
	 * 
	 * @param value
	 *            值
	 */
	void setGrossPrice(int value);

	/**
	 * 设置-毛价
	 * 
	 * @param value
	 *            值
	 */
	void setGrossPrice(double value);

	/**
	 * 获取-总税收 - 行
	 * 
	 * @return 值
	 */
	Decimal getTotalTaxLine();

	/**
	 * 设置-总税收 - 行
	 * 
	 * @param value
	 *            值
	 */
	void setTotalTaxLine(Decimal value);

	/**
	 * 设置-总税收 - 行
	 * 
	 * @param value
	 *            值
	 */
	void setTotalTaxLine(String value);

	/**
	 * 设置-总税收 - 行
	 * 
	 * @param value
	 *            值
	 */
	void setTotalTaxLine(int value);

	/**
	 * 设置-总税收 - 行
	 * 
	 * @param value
	 *            值
	 */
	void setTotalTaxLine(double value);

	/**
	 * 获取-行毛利
	 * 
	 * @return 值
	 */
	Decimal getLineGrossProfit();

	/**
	 * 设置-行毛利
	 * 
	 * @param value
	 *            值
	 */
	void setLineGrossProfit(Decimal value);

	/**
	 * 设置-行毛利
	 * 
	 * @param value
	 *            值
	 */
	void setLineGrossProfit(String value);

	/**
	 * 设置-行毛利
	 * 
	 * @param value
	 *            值
	 */
	void setLineGrossProfit(int value);

	/**
	 * 设置-行毛利
	 * 
	 * @param value
	 *            值
	 */
	void setLineGrossProfit(double value);

	/**
	 * 获取-总额
	 * 
	 * @return 值
	 */
	Decimal getGrossTotal();

	/**
	 * 设置-总额
	 * 
	 * @param value
	 *            值
	 */
	void setGrossTotal(Decimal value);

	/**
	 * 设置-总额
	 * 
	 * @param value
	 *            值
	 */
	void setGrossTotal(String value);

	/**
	 * 设置-总额
	 * 
	 * @param value
	 *            值
	 */
	void setGrossTotal(int value);

	/**
	 * 设置-总额
	 * 
	 * @param value
	 *            值
	 */
	void setGrossTotal(double value);

	/**
	 * 获取-已交货的数量
	 * 
	 * @return 值
	 */
	Decimal getDeliveredQuantity();

	/**
	 * 设置-已交货的数量
	 * 
	 * @param value
	 *            值
	 */
	void setDeliveredQuantity(Decimal value);

	/**
	 * 设置-已交货的数量
	 * 
	 * @param value
	 *            值
	 */
	void setDeliveredQuantity(String value);

	/**
	 * 设置-已交货的数量
	 * 
	 * @param value
	 *            值
	 */
	void setDeliveredQuantity(int value);

	/**
	 * 设置-已交货的数量
	 * 
	 * @param value
	 *            值
	 */
	void setDeliveredQuantity(double value);

	/**
	 * 获取-已手动关闭行
	 * 
	 * @return 值
	 */
	emYesNo getLineWasClosedManually();

	/**
	 * 设置-已手动关闭行
	 * 
	 * @param value
	 *            值
	 */
	void setLineWasClosedManually(emYesNo value);

	/**
	 * 获取-项目代码
	 * 
	 * @return 值
	 */
	String getProjectCode();

	/**
	 * 设置-项目代码
	 * 
	 * @param value
	 *            值
	 */
	void setProjectCode(String value);

	/**
	 * 获取-分配规则
	 * 
	 * @return 值
	 */
	String getDistributionRule1();

	/**
	 * 设置-分配规则
	 * 
	 * @param value
	 *            值
	 */
	void setDistributionRule1(String value);

	/**
	 * 获取-分配规则2
	 * 
	 * @return 值
	 */
	String getDistributionRule2();

	/**
	 * 设置-分配规则2
	 * 
	 * @param value
	 *            值
	 */
	void setDistributionRule2(String value);

	/**
	 * 获取-分配规则3
	 * 
	 * @return 值
	 */
	String getDistributionRule3();

	/**
	 * 设置-分配规则3
	 * 
	 * @param value
	 *            值
	 */
	void setDistributionRule3(String value);

	/**
	 * 获取-分配规则4
	 * 
	 * @return 值
	 */
	String getDistributionRule4();

	/**
	 * 设置-分配规则4
	 * 
	 * @param value
	 *            值
	 */
	void setDistributionRule4(String value);

	/**
	 * 获取-分配规则5
	 * 
	 * @return 值
	 */
	String getDistributionRule5();

	/**
	 * 设置-分配规则5
	 * 
	 * @param value
	 *            值
	 */
	void setDistributionRule5(String value);

	/**
	 * 获取-原始凭证类型
	 * 
	 * @return 值
	 */
	String getOriginalDocumentType();

	/**
	 * 设置-原始凭证类型
	 * 
	 * @param value
	 *            值
	 */
	void setOriginalDocumentType(String value);

	/**
	 * 获取-原始凭证代码
	 * 
	 * @return 值
	 */
	Integer getOriginalDocumentEntry();

	/**
	 * 设置-原始凭证代码
	 * 
	 * @param value
	 *            值
	 */
	void setOriginalDocumentEntry(Integer value);

	/**
	 * 获取-原始凭证中的行编号
	 * 
	 * @return 值
	 */
	Integer getOriginalDocumentLineId();

	/**
	 * 设置-原始凭证中的行编号
	 * 
	 * @param value
	 *            值
	 */
	void setOriginalDocumentLineId(Integer value);

	/**
	 * 获取-毛利价格清单
	 * 
	 * @return 值
	 */
	Integer getPriceListforGrossProfit();

	/**
	 * 设置-毛利价格清单
	 * 
	 * @param value
	 *            值
	 */
	void setPriceListforGrossProfit(Integer value);

	/**
	 * 获取-促销活动代码
	 * 
	 * @return 值
	 */
	String getPromotionCode();

	/**
	 * 设置-促销活动代码
	 * 
	 * @param value
	 *            值
	 */
	void setPromotionCode(String value);

}
