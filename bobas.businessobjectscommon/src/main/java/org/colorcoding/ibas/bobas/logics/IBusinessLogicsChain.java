package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

/**
 * 业务逻辑链
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogicsChain {
	/**
	 * 设置-标记
	 * 
	 * @param value
	 */
	void setId(String value);

	/**
	 * 获取-标记
	 * 
	 * @param value
	 */
	String getId();

	/**
	 * 改变业务对象仓库
	 * 
	 * @param boRepository
	 *            仓库
	 * @return 成功，true；失败，false
	 */
	void useRepository(IBORepository boRepository);

	/**
	 * 运行
	 * 
	 * @param bo
	 *            契约数据
	 */
	void run(IBusinessObjectBase bo);
}
