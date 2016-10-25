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

	private long task_id = -1;

	public BORepository4Cache() {
		// 创建清理缓存的任务，不使用也可以，对象CG时释放
		/*
		 * try { task_id = Daemon.register(new IDaemonTask() {
		 * 
		 * @Override public void run() { synchronized (cacheDatas) { for
		 * (BOCacheContainers cacheContainers : cacheDatas.values()) {
		 * cacheContainers.clearExpired(); } } }
		 * 
		 * @Override public String getName() { return "clear repository cache.";
		 * }
		 * 
		 * @Override public long getInterval() { return 30;// 每5秒释放缓存的数据 }
		 * 
		 * }); } catch (InvalidDaemonTask e) { RuntimeLog.log(e); }
		 */
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
	protected synchronized BOCacheContainers getCacheList(Class<? extends IBusinessObjectBase> boType) {
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
	public IOperationResult<?> fetchEx(ICriteria criteria, Class<? extends IBusinessObjectBase> boType) {
		return this.fetch(criteria, boType);
	}

	@Override
	public IOperationResult<?> fetchCopy(IBusinessObjectBase bo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IOperationResult<?> fetchCopyEx(IBusinessObjectBase bo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IOperationResult<?> fetch(ICriteria criteria, Class<? extends IBusinessObjectBase> boType) {
		OperationResult<?> operationResult = new OperationResult<Object>();
		try {
			BOCacheContainers cacheList = this.getCacheList(boType);
			if (cacheList == null) {
				return operationResult;
			}
			JudgmentLinks judgmentLinks = ExpressionFactory.create().createJudgmentLinks(criteria.getConditions());
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
