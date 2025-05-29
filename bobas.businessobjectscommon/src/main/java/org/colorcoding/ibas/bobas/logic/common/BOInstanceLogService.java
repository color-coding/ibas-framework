package org.colorcoding.ibas.bobas.logic.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOMasterDataLine;
import org.colorcoding.ibas.bobas.bo.IBOSimple;
import org.colorcoding.ibas.bobas.bo.IBOSimpleLine;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.Booleans;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManagedFields;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.logging.LoggingLevel;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.LogicContract;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializationFactory;

@LogicContract(IBOInstanceLogContract.class)
public class BOInstanceLogService extends BusinessLogic<IBOInstanceLogContract, BOLogst> {

	public static final Map<String, Boolean> BO_LOGST_SETTING;

	static {
		BO_LOGST_SETTING = new ConcurrentHashMap<>(64);
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
		// boLogst.setBOKeys(contract.getHost().toString());
		StringBuilder builder = new StringBuilder();
		if (contract.getHost() instanceof IBOMasterData) {
			IBOMasterData data = (IBOMasterData) contract.getHost();
			builder.append(IBOMasterData.MASTER_PRIMARY_KEY_NAME);
			builder.append(" = ");
			builder.append(data.getCode());
		} else if (contract.getHost() instanceof IBOMasterDataLine) {
			IBOMasterDataLine data = (IBOMasterDataLine) contract.getHost();
			builder.append(IBOMasterDataLine.MASTER_PRIMARY_KEY_NAME);
			builder.append(" = ");
			builder.append(data.getCode());
			builder.append(" & ");
			builder.append(IBOMasterDataLine.SECONDARY_PRIMARY_KEY_NAME);
			builder.append(" = ");
			builder.append(data.getLineId());
		} else if (contract.getHost() instanceof IBODocument) {
			IBODocument data = (IBODocument) contract.getHost();
			builder.append(IBODocument.MASTER_PRIMARY_KEY_NAME);
			builder.append(" = ");
			builder.append(data.getDocEntry());
		} else if (contract.getHost() instanceof IBODocumentLine) {
			IBODocumentLine data = (IBODocumentLine) contract.getHost();
			builder.append(IBODocumentLine.MASTER_PRIMARY_KEY_NAME);
			builder.append(" = ");
			builder.append(data.getDocEntry());
			builder.append(" & ");
			builder.append(IBODocumentLine.SECONDARY_PRIMARY_KEY_NAME);
			builder.append(" = ");
			builder.append(data.getLineId());
		} else if (contract.getHost() instanceof IBOSimple) {
			IBOSimple data = (IBOSimple) contract.getHost();
			builder.append(IBOSimple.MASTER_PRIMARY_KEY_NAME);
			builder.append(" = ");
			builder.append(data.getObjectKey());
		} else if (contract.getHost() instanceof IBOSimpleLine) {
			IBOSimpleLine data = (IBOSimpleLine) contract.getHost();
			builder.append(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			builder.append(" = ");
			builder.append(data.getObjectKey());
			builder.append(" & ");
			builder.append(IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME);
			builder.append(" = ");
			builder.append(data.getLineId());
		} else if (contract.getHost() instanceof IManagedFields) {
			for (IFieldData item : ((IManagedFields) contract.getHost()).getFields(c -> c.isPrimaryKey())) {
				if (builder.length() > 0) {
					builder.append(" & ");
				}
				builder.append(item.getName());
				builder.append(" = ");
				builder.append(item.getValue());
			}
			if (builder.length() == 0) {
				builder.append(contract.getHost().toString());
			}
		} else {
			builder.append(contract.getHost().toString());
		}
		boLogst.setBOKeys(builder.toString());
		boLogst.setLogInst(contract.getHost().getLogInst());
		return boLogst;
	}

	@Override
	protected void impact(IBOInstanceLogContract contract) {
	}

	@Override
	protected void revoke(IBOInstanceLogContract contract) {
		BOLogst boLogst = this.getBeAffected();
		boLogst.setModifyDate(DateTimes.today());
		boLogst.setModifyTime(DateTimes.time());
		boLogst.setModifyUser(this.getUser().getId());
		boLogst.setTransationId(this.getTransaction().getId());

		ISerializer serializer = SerializationFactory.createManager().create(SerializationFactory.TYPE_JSON);
		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			serializer.serialize(contract.getHost(), writer, false);
			boLogst.setContent(writer.toString());
		} catch (IOException e) {
			Logger.log(e);
		}
		boLogst.setSavable(true);
	}

}
