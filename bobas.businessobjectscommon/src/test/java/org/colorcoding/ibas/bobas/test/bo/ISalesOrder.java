package org.colorcoding.ibas.bobas.test.bo;

import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBOUserFields;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.data.measurement.Time;

/**
 * 销售订单
 * 
 */
public interface ISalesOrder extends IBODocument, IBOUserFields {

	/**
	 * 获取-凭证编号
	 * 
	 * @return 值
	 */
	@Override
	Integer getDocEntry();

	/**
	 * 设置-凭证编号
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setDocEntry(Integer value);

	/**
	 * 获取-期间编号
	 * 
	 * @return 值
	 */
	@Override
	Integer getDocNum();

	/**
	 * 设置-期间编号
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setDocNum(Integer value);

	/**
	 * 获取-期间
	 * 
	 * @return 值
	 */
	@Override
	Integer getPeriod();

	/**
	 * 设置-期间
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setPeriod(Integer value);

	/**
	 * 获取-Instance
	 * 
	 * @return 值
	 */
	Integer getInstance();

	/**
	 * 设置-Instance
	 * 
	 * @param value
	 *            值
	 */
	void setInstance(Integer value);

	/**
	 * 获取-服务系列
	 * 
	 * @return 值
	 */
	Integer getSeries();

	/**
	 * 设置-服务系列
	 * 
	 * @param value
	 *            值
	 */
	void setSeries(Integer value);

	/**
	 * 获取-手写
	 * 
	 * @return 值
	 */
	emYesNo getHandwritten();

	/**
	 * 设置-手写
	 * 
	 * @param value
	 *            值
	 */
	void setHandwritten(emYesNo value);

	/**
	 * 获取-取消
	 * 
	 * @return 值
	 */
	@Override
	emYesNo getCanceled();

	/**
	 * 设置-取消
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setCanceled(emYesNo value);

	/**
	 * 获取-类型
	 * 
	 * @return 值
	 */
	@Override
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
	@Override
	String getDataSource();

	/**
	 * 设置-数据源
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setDataSource(String value);

	/**
	 * 获取-实例号（版本）
	 * 
	 * @return 值
	 */
	@Override
	Integer getLogInst();

	/**
	 * 设置-实例号（版本）
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setLogInst(Integer value);

	/**
	 * 获取-用户
	 * 
	 * @return 值
	 */
	Integer getUserSign();

	/**
	 * 设置-用户
	 * 
	 * @param value
	 *            值
	 */
	void setUserSign(Integer value);

	/**
	 * 获取-是否结转
	 * 
	 * @return 值
	 */
	emYesNo getTransfered();

	/**
	 * 设置-是否结转
	 * 
	 * @param value
	 *            值
	 */
	void setTransfered(emYesNo value);

	/**
	 * 获取-状态
	 * 
	 * @return 值
	 */
	@Override
	emBOStatus getStatus();

	/**
	 * 设置-状态
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setStatus(emBOStatus value);

	/**
	 * 获取-创建日期
	 * 
	 * @return 值
	 */
	@Override
	DateTime getCreateDate();

	/**
	 * 设置-创建日期
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setCreateDate(DateTime value);

	/**
	 * 获取-创建时间
	 * 
	 * @return 值
	 */
	@Override
	Short getCreateTime();

	/**
	 * 设置-创建时间
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setCreateTime(Short value);

	/**
	 * 获取-修改日期
	 * 
	 * @return 值
	 */
	@Override
	DateTime getUpdateDate();

	/**
	 * 设置-修改日期
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setUpdateDate(DateTime value);

	/**
	 * 获取-修改时间
	 * 
	 * @return 值
	 */
	@Override
	Short getUpdateTime();

	/**
	 * 设置-修改时间
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setUpdateTime(Short value);

	/**
	 * 获取-创建用户
	 * 
	 * @return 值
	 */
	@Override
	Integer getCreateUserSign();

	/**
	 * 设置-创建用户
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setCreateUserSign(Integer value);

	/**
	 * 获取-修改用户
	 * 
	 * @return 值
	 */
	@Override
	Integer getUpdateUserSign();

	/**
	 * 设置-修改用户
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setUpdateUserSign(Integer value);

	/**
	 * 获取-创建动作标识
	 * 
	 * @return 值
	 */
	@Override
	String getCreateActionId();

	/**
	 * 设置-创建动作标识
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setCreateActionId(String value);

	/**
	 * 获取-更新动作标识
	 * 
	 * @return 值
	 */
	@Override
	String getUpdateActionId();

	/**
	 * 设置-更新动作标识
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setUpdateActionId(String value);

	/**
	 * 获取-数据所有者
	 * 
	 * @return 值
	 */
	Integer getDataOwner();

	/**
	 * 设置-数据所有者
	 * 
	 * @param value
	 *            值
	 */
	void setDataOwner(Integer value);

	/**
	 * 获取-团队成员
	 * 
	 * @return 值
	 */
	String getTeamMembers();

	/**
	 * 设置-团队成员
	 * 
	 * @param value
	 *            值
	 */
	void setTeamMembers(String value);

	/**
	 * 获取-数据所属组织
	 * 
	 * @return 值
	 */
	String getOrganization();

	/**
	 * 设置-数据所属组织
	 * 
	 * @param value
	 *            值
	 */
	void setOrganization(String value);

	/**
	 * 获取-审批状态
	 * 
	 * @return 值
	 */
	emApprovalStatus getApprovalStatus();

	/**
	 * 设置-审批状态
	 * 
	 * @param value
	 *            值
	 */
	void setApprovalStatus(emApprovalStatus value);

	/**
	 * 获取-单据状态
	 * 
	 * @return 值
	 */
	@Override
	emDocumentStatus getDocumentStatus();

	/**
	 * 设置-单据状态
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setDocumentStatus(emDocumentStatus value);

	/**
	 * 获取-过账日期
	 * 
	 * @return 值
	 */
	@Override
	DateTime getPostingDate();

	/**
	 * 设置-过账日期
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setPostingDate(DateTime value);

	/**
	 * 获取-到期日
	 * 
	 * @return 值
	 */
	@Override
	DateTime getDeliveryDate();

	/**
	 * 设置-到期日
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setDeliveryDate(DateTime value);

	/**
	 * 获取-凭证日期
	 * 
	 * @return 值
	 */
	@Override
	DateTime getDocumentDate();

	/**
	 * 设置-凭证日期
	 * 
	 * @param value
	 *            值
	 */
	@Override
	void setDocumentDate(DateTime value);

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
	 * 获取-备注
	 * 
	 * @return 值
	 */
	String getRemarks();

	/**
	 * 设置-备注
	 * 
	 * @param value
	 *            值
	 */
	void setRemarks(String value);

	/**
	 * 获取-客户代码
	 * 
	 * @return 值
	 */
	String getCustomerCode();

	/**
	 * 设置-客户代码
	 * 
	 * @param value
	 *            值
	 */
	void setCustomerCode(String value);

	/**
	 * 获取-客户名称
	 * 
	 * @return 值
	 */
	String getCustomerName();

	/**
	 * 设置-客户名称
	 * 
	 * @param value
	 *            值
	 */
	void setCustomerName(String value);

	/**
	 * 获取-单据货币
	 * 
	 * @return 值
	 */
	String getDocumentCurrency();

	/**
	 * 设置-单据货币
	 * 
	 * @param value
	 *            值
	 */
	void setDocumentCurrency(String value);

	/**
	 * 获取-单据交换率
	 * 
	 * @return 值
	 */
	Decimal getDocumentRate();

	/**
	 * 设置-单据交换率
	 * 
	 * @param value
	 *            值
	 */
	void setDocumentRate(Decimal value);

	/**
	 * 设置-单据交换率
	 * 
	 * @param value
	 *            值
	 */
	void setDocumentRate(String value);

	/**
	 * 设置-单据交换率
	 * 
	 * @param value
	 *            值
	 */
	void setDocumentRate(int value);

	/**
	 * 设置-单据交换率
	 * 
	 * @param value
	 *            值
	 */
	void setDocumentRate(double value);

	/**
	 * 获取-单据总计
	 * 
	 * @return 值
	 */
	Decimal getDocumentTotal();

	/**
	 * 设置-单据总计
	 * 
	 * @param value
	 *            值
	 */
	void setDocumentTotal(Decimal value);

	/**
	 * 设置-单据总计
	 * 
	 * @param value
	 *            值
	 */
	void setDocumentTotal(String value);

	/**
	 * 设置-单据总计
	 * 
	 * @param value
	 *            值
	 */
	void setDocumentTotal(int value);

	/**
	 * 设置-单据总计
	 * 
	 * @param value
	 *            值
	 */
	void setDocumentTotal(double value);

	/**
	 * 获取-销售订单-行集合
	 * 
	 * @return 值
	 */
	ISalesOrderItems getSalesOrderItems();

	/**
	 * 设置-销售订单-行集合
	 * 
	 * @param value
	 *            值
	 */
	void setSalesOrderItems(ISalesOrderItems value);

	/**
	 * 获取-单据用户
	 * 
	 * @return 值
	 */
	IUser getDocumentUser();

	/**
	 * 设置-单据用户
	 * 
	 * @param value
	 *            值
	 */
	void setDocumentUser(IUser value);

	/**
	 * 获取-团队用户
	 * 
	 * @return 值
	 */
	IUser[] getTeamUsers();

	/**
	 * 设置-团队用户
	 * 
	 * @param value
	 *            值
	 */
	void setTeamUsers(IUser[] value);

	/**
	 * 获取-周期
	 * 
	 * @return 值
	 */
	Time getCycle();

	/**
	 * 设置-周期
	 * 
	 * @param value
	 *            值
	 */
	void setCycle(Time value);
}
