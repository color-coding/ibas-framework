package org.colorcoding.ibas.bobas.bo;

import java.util.ArrayList;

public class UserFieldInfoList extends ArrayList<UserFieldInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6320914335175298868L;

	@Override
	public boolean add(UserFieldInfo e) {
		for (UserFieldInfo item : this) {
			if (item.getName().equals(e.getName())) {
				return false;
			}
		}
		return super.add(e);
	}

}
