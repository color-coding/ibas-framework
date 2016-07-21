package org.colorcoding.ibas.bobas.ownership;

/**
 * 团队数据所有权
 */
public interface ITeamDataOwnership extends IDataOwnership {
	/**
	 * 获取-团队成员
	 * 
	 * @return
	 */
	Integer[] getTeamUsers();
}
