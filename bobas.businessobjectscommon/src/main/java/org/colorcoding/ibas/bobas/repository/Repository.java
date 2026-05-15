package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;

/**
 * 仓库基类，提供用户认证功能
 *
 * @author Niuren.Zhu
 *
 */
public class Repository implements AutoCloseable {

	private IUser currentUser;

	/**
	 * 获取当前用户，未设置时返回UNKNOWN_USER
	 *
	 * @return 当前用户，永不为null
	 */
	public final IUser getCurrentUser() {
		if (this.currentUser == null) {
			return OrganizationFactory.UNKNOWN_USER;
		}
		return currentUser;
	}

	final void setCurrentUser(IUser user) {
		this.currentUser = user;
	}

	/**
	 * 获取当前用户口令，未设置用户时返回空字符串
	 *
	 * @return 用户口令
	 */
	public final String getUserToken() {
		IUser user = this.currentUser;
		if (user == null) {
			return Strings.VALUE_EMPTY;
		}
		return user.getToken();
	}

	/**
	 * 通过用户对象设置当前用户
	 *
	 * @param user 用户对象
	 * @throws InvalidAuthorizationException 口令无效时抛出
	 */
	public final void setUserToken(IUser user) throws InvalidAuthorizationException {
		this.setUserToken(user.getToken());
	}

	/**
	 * 通过口令设置当前用户，与当前口令相同时不做处理，口令为空或无匹配用户时抛出异常
	 *
	 * @param token 用户口令，不可为空
	 * @throws InvalidAuthorizationException 口令为空或无匹配用户
	 */
	public final void setUserToken(String token) throws InvalidAuthorizationException {
		if (Strings.isNullOrEmpty(token)) {
			throw new InvalidAuthorizationException(I18N.prop("msg_bobas_no_user_match_the_token"));
		}
		IUser user = this.getCurrentUser();
		// 与当前的口令相同，不做处理
		if (user != null && Strings.equalsIgnoreCase(user.getToken(), token)) {
			return;
		}
		// 加载组织
		user = OrganizationFactory.createManager().getUser(token);
		// 没有用户匹配次口令
		if (user == null || user == OrganizationFactory.UNKNOWN_USER) {
			throw new InvalidAuthorizationException(I18N.prop("msg_bobas_no_user_match_the_token"));
		}
		this.setCurrentUser(user);
	}

	@Override
	protected void finalize() throws Throwable {
		this.currentUser = null;
		super.finalize();
	}

	@Override
	public void close() throws Exception {
	}
}