package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;

public class BORepositoryServiceApplication extends BORepositoryService {

	/**
	 * 查询数据
	 * 
	 * @param <T>      对象类型
	 * @param criteria 查询条件
	 * @param boType   对象类型
	 * @return
	 */
	protected final <T extends IBusinessObject> IOperationResult<T> fetch(ICriteria criteria, Class<?> boType) {
		// 兼容性处理
		return this.fetch(boType, criteria);
	}
}
