package org.colorcoding.ibas.bobas.logic.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.Booleans;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.ITrackable;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.logging.LoggingLevel;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.LogicContract;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializationFactory;

@LogicContract(IBOInstanceLogContract.class)
public class BOInstanceLogService extends BusinessLogic<IBOInstanceLogContract, BOLogst> {

	public static final Map<String, Boolean> BO_LOGST_SETTING;

	private static final ISerializer SERIALIZER;

	static {
		BO_LOGST_SETTING = new ConcurrentHashMap<>(64);
		SERIALIZER = SerializationFactory.createManager().create(SerializationFactory.TYPE_JSON);
	}

	@Override
	protected boolean checkDataStatus(Object data) {
		if (data instanceof IBusinessObject && data == this.getHost()) {
			IBusinessObject boData = (IBusinessObject) data;
			if (boData.isSavable() == false) {
				Logger.log(LoggingLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(), "isSavable",
						"false");
				return false;
			}
			if (boData instanceof IBOStorageTag) {
				IBOStorageTag boTag = (IBOStorageTag) boData;
				Boolean enabled = BO_LOGST_SETTING.get(boTag.getObjectCode());
				if (enabled == null) {
					enabled = false;
					if (MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_ENABLE_BO_LOGST, false)) {
						enabled = true;
						if (!MyConfiguration.getConfigValue(Strings.format("%s|%s",
								MyConfiguration.CONFIG_ITEM_ENABLE_BO_LOGST, boTag.getObjectCode()), false)) {
							enabled = false;
						}
					}
					BO_LOGST_SETTING.put(boTag.getObjectCode(), enabled);
				}
				if (!Booleans.equals(enabled, true)) {
					Logger.log(LoggingLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
							Strings.format("isLogst|%s", boTag.getObjectCode()), "false");
					return false;
				}
			}
		}
		return true;
	}

	@Override
	protected BOLogst fetchBeAffected(IBOInstanceLogContract contract) {
		BOLogst boLogst = new BOLogst();
		boLogst.setSavable(false);
		boLogst.setBOCode(contract.getHost().getObjectCode());
		boLogst.setBOKeys(contract.getHost().toString());
		boLogst.setLogInst(contract.getHost().getLogInst());
		return boLogst;
	}

	@Override
	protected void impact(IBOInstanceLogContract contract) {
		BOLogst boLogst = this.getBeAffected();
		boLogst.setModifyDate(DateTimes.today());
		boLogst.setModifyTime(DateTimes.time());
		boLogst.setModifyUser(this.getUser().getId());
		boLogst.setTransationId(this.getTransaction().getId());

		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			SERIALIZER.serialize(contract.getHost(), writer, false);
			boLogst.setContent(writer.toString());
		} catch (IOException e) {
			Logger.log(e);
		}
		boLogst.setSavable(true);
	}

	@Override
	protected void revoke(IBOInstanceLogContract contract) {
		if (this.getTrigger() instanceof ITrackable) {
			ITrackable trackable = (ITrackable) this.getTrigger();
			// 记录删除时的状态
			if (trackable.isDeleted()) {
				BOLogst boLogst = this.getBeAffected();
				boLogst.setModifyDate(DateTimes.today());
				boLogst.setModifyTime(DateTimes.time());
				boLogst.setModifyUser(this.getUser().getId());
				boLogst.setTransationId(this.getTransaction().getId());
				// 负数表示删除
				boLogst.setLogInst(-(int) boLogst.getLogInst());

				try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
					SERIALIZER.serialize(contract.getHost(), writer, false);
					boLogst.setContent(writer.toString());
				} catch (IOException e) {
					Logger.log(e);
				}
				boLogst.setSavable(true);
			}
		}
	}

}
