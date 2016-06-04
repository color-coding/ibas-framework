package org.colorcoding.bobas.repository;

import java.lang.reflect.Method;

import org.colorcoding.bobas.i18n.i18n;
import org.colorcoding.bobas.messages.RuntimeLog;
import org.colorcoding.bobas.ownership.IPermissionItem;
import org.colorcoding.bobas.ownership.NotConfiguredException;
import org.colorcoding.bobas.ownership.OwnershipException;
import org.colorcoding.bobas.ownership.Permission;
import org.colorcoding.bobas.ownership.PermissionGroup;
import org.colorcoding.bobas.ownership.PermissionItem;
import org.colorcoding.bobas.ownership.PermissionValue;
import org.colorcoding.bobas.ownership.UnauthorizedException;
import org.colorcoding.bobas.util.ArrayList;

/**
 * 业务仓库服务应用
 * 
 * 有方法权限交易
 * 
 * @author Niuren.Zhu
 *
 */
public class BORepositoryServiceApplication extends BORepositorySmartService implements IBORepositoryApplication {

	private String userToken = null;

	@Override
	public final String getUserToken() {
		return userToken;
	}

	@Override
	public final void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	/**
	 * 获取方法权限
	 * 
	 * @return
	 */
	protected IPermissionItem[] getMethodPermissions() {
		ArrayList<IPermissionItem> permissions = new ArrayList<IPermissionItem>();
		Method[] methods = this.getClass().getDeclaredMethods();
		if (methods != null) {
			String group = this.getClass().getName();
			PermissionGroup permissionGroup = this.getClass().getAnnotation(PermissionGroup.class);
			if (permissionGroup != null) {
				group = permissionGroup.value();
			}
			for (Method method : methods) {
				Permission permission = method.getAnnotation(Permission.class);
				if (permission != null) {
					PermissionItem permissionItem = new PermissionItem();
					permissionItem.setGroup(permission.group());
					permissionItem.setName(permission.name());
					permissionItem.setValue(permission.defaultValue());
					if (permissionItem.getGroup() == null || permissionItem.getGroup().equals("")) {
						permissionItem.setGroup(group);
					}
					if (permissionItem.getName() == null || permissionItem.getName().equals("")) {
						permissionItem.setName(method.getName());
					}
					permissions.add(permissionItem);
				}
			}
		}
		return permissions.toArray(new IPermissionItem[] {});
	}

	/**
	 * 检查调用方法权限
	 * 
	 * @throws OwnershipException
	 */
	protected void checkMethodPermissions() throws UnauthorizedException {
		StackTraceElement[] yste = Thread.currentThread().getStackTrace();
		if (yste == null || yste.length < 3) {
			throw new RuntimeException(i18n.prop("msg_bobas_not_found_method_name"));
		}
		this.checkMethodPermissions(yste[2].getMethodName());
	}

	/**
	 * 检查调用方法权限
	 * 
	 * @param name
	 *            方法名称
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws OwnershipException
	 */
	protected void checkMethodPermissions(String name) throws UnauthorizedException {
		if (this.getCurrentUser() == null) {
			throw new UnauthorizedException(i18n.prop("msg_bobas_invalid_user"));
		}
		if (this.getOwnershipJudge() != null) {
			Class<?> type = this.getClass();
			try {
				try {
					this.getOwnershipJudge().canCall(type.getName(), name, this.getCurrentUser());
				} catch (NotConfiguredException e) {
					// 没有配置权限，获取默认值
					Method[] methods = this.getClass().getDeclaredMethods();
					if (methods != null) {
						for (Method method : methods) {
							if (method.getName().equals(name)) {
								Permission permission = method.getAnnotation(Permission.class);
								if (permission != null) {
									if (permission.defaultValue() == PermissionValue.unavailable) {
										throw new UnauthorizedException();
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				RuntimeLog.log(RuntimeLog.MSG_PERMISSIONS_NOT_AUTHORIZED, this.getCurrentUser(), type.getName(), name);
				throw new UnauthorizedException(i18n.prop("msg_bobas_not_authorized_method", name));
			}
		}
	}
}
