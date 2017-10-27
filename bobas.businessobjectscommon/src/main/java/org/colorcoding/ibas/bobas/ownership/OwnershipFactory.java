package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 所有权管理员工厂
 */
public class OwnershipFactory extends ConfigurableFactory<IOwnershipJudger> {

	private OwnershipFactory() {
	}

	private volatile static OwnershipFactory instance;

	public synchronized static OwnershipFactory create() {
		if (instance == null) {
			synchronized (OwnershipFactory.class) {
				if (instance == null) {
					instance = new OwnershipFactory();
				}
			}
		}
		return instance;
	}

	private volatile static IOwnershipJudger ownershipJudger = null;

	public synchronized IOwnershipJudger createJudger() {
		if (ownershipJudger == null) {
			ownershipJudger = this.create(MyConfiguration.CONFIG_ITEM_OWNERSHIP_WAY, "OwnershipJudger");
		}
		return ownershipJudger;
	}

	@Override
	protected IOwnershipJudger createDefault(String typeName) {
		return new IOwnershipJudger() {

			@Override
			public boolean canRead(IDataOwnership bo, IUser user) {
				return true;
			}

			@Override
			public boolean canRead(IDataOwnership bo, IUser user, boolean throwError) throws UnauthorizedException {
				return this.canRead(bo, user);
			}

			@Override
			public boolean canSave(IDataOwnership bo, IUser user) {
				return true;
			}

			@Override
			public boolean canSave(IDataOwnership bo, IUser user, boolean throwError) throws UnauthorizedException {
				return this.canSave(bo, user);
			}

		};
	}

}
