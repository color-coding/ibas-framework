package org.colorcoding.ibas.bobas.repository;

import java.util.HashMap;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.BORepositoryBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.expressions.ExpressionFactory;
import org.colorcoding.ibas.bobas.expressions.JudgmentLinks;

/**
 * 业务对象仓库-缓存
 */
public class BORepository4Cache extends BORepositoryBase implements IBORepository4Cache {

	public BORepository4Cache() {
	}

	@Override
	public DateTime getServerTime() {
		return DateTime.getNow();
	}

	/**
	 * 数据缓存集合
	 */
	private HashMap<Class<? extends IBusinessObjectBase>, BOCacheContainers> cacheDatas = new HashMap<Class<? extends IBusinessObjectBase>, BOCacheContainers>();

	/**
	 * 获取对象的缓存集合
	 * 
	 * @param boType
	 *            对象类型
	 * 
	 * @return 对象的缓存集合
	 */
	protected BOCacheContainers getCacheList(Class<? extends IBusinessObjectBase> boType) {
		if (this.cacheDatas == null) {
			cacheDatas = new HashMap<Class<? extends IBusinessObjectBase>, BOCacheContainers>();
		}
		if (this.cacheDatas.containsKey(boType)) {
			return this.cacheDatas.get(boType);
		} else {
			synchronized (cacheDatas) {
				BOCacheContainers cacheList = new BOCacheContainers();
				cacheDatas.put(boType, cacheList);
				return cacheList;
			}
		}
	}

	/**
	 * 缓存数据
	 * 
	 * @param datas
	 *            数据
	 */
	@Override
	public void cacheData(Iterable<? extends IBusinessObjectBase> datas) {
		if (datas != null) {
			BOCacheContainers cacheList = null;
			for (IBusinessObjectBase item : datas) {
				if (cacheList == null) {
					cacheList = this.getCacheList(item.getClass());
				}
				synchronized (cacheList) {
					cacheList.add(RepositoryFactory.createFatory().createCacheContainer(item));
				}
			}
		}
	}

	/**
	 * 缓存数据
	 * 
	 * @param datas
	 *            数据
	 */
	@Override
	public void cacheData(IBusinessObjectBase data) {
		if (data == null) {
			return;
		}
		BOCacheContainers cacheList = this.getCacheList(data.getClass());
		synchronized (cacheList) {
			cacheList.add(RepositoryFactory.createFatory().createCacheContainer(data));
		}
	}

	@Override
	public void dispose() throws RepositoryException {
		this.cacheDatas = null;
		// Daemon.unRegister(task_id);
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ICriteria criteria, Class<T> boType) {
		return this.fetch(criteria, boType);
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetchCopy(T bo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetchCopyEx(T bo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetch(ICriteria criteria, Class<T> boType) {
		OperationResult<T> operationResult = new OperationResult<>();
		try {
			BOCacheContainers cacheList = this.getCacheList(boType);
			if (cacheList == null) {
				return operationResult;
			}
			JudgmentLinks judgmentLinks = ExpressionFactory.create().createBOJudgmentLinks(criteria.getConditions());
			synchronized (cacheList) {
				for (IBOCacheContainer boContainer : cacheList) {
					if (judgmentLinks.judge(boContainer.getData())) {
						operationResult.addResultObjects(boContainer.getData());
					}
				}
			}
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}

	@Override
	public void clearData(IBusinessObjectBase bo) {
		if (bo == null) {
			return;
		}
		BOCacheContainers cacheList = this.getCacheList(bo.getClass());
		synchronized (cacheList) {
			cacheList.remove(bo);
		}
	}

}
