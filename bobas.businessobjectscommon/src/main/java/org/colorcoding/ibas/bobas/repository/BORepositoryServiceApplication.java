package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.organization.IOrganizationManager;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;

public class BORepositoryServiceApplication extends BORepositoryService {

	/**
	 * 设置当前用户
	 * 
	 * @param token
	 */
	public final void setCurrentUser(String token) {
		if (this.getCurrentUser() != null && Strings.equalsIgnoreCase(this.getCurrentUser().getToken(), token)) {
			// 与当前的口令相同，不做处理
			return;
		}
		IOrganizationManager orgManager = OrganizationFactory.create().createManager();
		IUser user = orgManager.getUser(token);
		if (user == null) {
			// 没有用户匹配次口令
			throw new InvalidAuthorizationException(I18N.prop("msg_bobas_no_user_match_the_token"));
		}
		this.setCurrentUser(user);
	}

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
