package org.colorcoding.ibas.bobas.organization;

import org.colorcoding.ibas.bobas.i18n.i18n;

/**
 * 未知的用户
 */
public class UnknownUser implements IUser {

	public static final int UNKNOWN_USER_SIGN = -1;

	@Override
	public int getId() {
		return UNKNOWN_USER_SIGN;
	}

	@Override
	public String toString() {
		return String.format("{User id = %s}", this.getId());
	}

	@Override
	public void checkAuthorization(String authorizationCode) throws InvalidAuthorizationException {
		throw new InvalidAuthorizationException(i18n.prop("msg_bobas_not_exist_user"));
	}
}
