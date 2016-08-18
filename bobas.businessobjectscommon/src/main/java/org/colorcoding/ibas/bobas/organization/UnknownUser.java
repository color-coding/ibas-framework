package org.colorcoding.ibas.bobas.organization;

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
	public String getBelong() {
		return null;
	}

	@Override
	public String toString() {
		return String.format("{User id = %s}", this.getId());
	}

	@Override
	public String getToken() {
		return null;
	}

	@Override
	public void checkAuthorization(String authorizationCode) throws InvalidAuthorizationException {
		// throw new
		// InvalidAuthorizationException(i18n.prop("msg_bobas_not_exist_user"));
	}

}
