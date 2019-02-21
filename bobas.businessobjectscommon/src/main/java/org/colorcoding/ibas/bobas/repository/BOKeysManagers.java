package org.colorcoding.ibas.bobas.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.BOException;
import org.colorcoding.ibas.bobas.bo.IBOCustomKey;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOKeysManager;
import org.colorcoding.ibas.bobas.bo.IBOLine;
import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOMasterDataLine;
import org.colorcoding.ibas.bobas.bo.IBOMaxValueKey;
import org.colorcoding.ibas.bobas.bo.IBOSeriesKey;
import org.colorcoding.ibas.bobas.bo.IBOSimple;
import org.colorcoding.ibas.bobas.bo.IBOSimpleLine;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectsBase;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDb;
import org.colorcoding.ibas.bobas.core.fields.IManagedFields;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.db.BOAdapter;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter;
import org.colorcoding.ibas.bobas.db.IDbCommand;
import org.colorcoding.ibas.bobas.db.IDbConnection;
import org.colorcoding.ibas.bobas.db.IDbDataReader;
import org.colorcoding.ibas.bobas.db.ISqlScripts;
import org.colorcoding.ibas.bobas.db.ParsingException;
import org.colorcoding.ibas.bobas.db.SqlScriptException;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

class FileKeysManager implements IBOKeysManager {

	private String workFolder;

	public final String getWorkFolder() {
		return this.workFolder;
	}

	public final void setWorkFolder(String workFolder) {
		this.workFolder = workFolder;
	}

	private String transactionId;

	public final String getTransactionId() {
		return transactionId;
	}

	public final void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public KeyValue[] usePrimaryKeys(IBusinessObjectBase bo) {
		try {
			KeyValue[] keys = null;
			if (bo instanceof IBOStorageTag) {
				IBOStorageTag tagBO = (IBOStorageTag) bo;
				StringBuilder builder = new StringBuilder();
				builder.append(this.getWorkFolder());
				builder.append(File.separator);
				builder.append(MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_COMPANY, "CC").toLowerCase());
				builder.append("_sys");
				builder.append(File.separator);
				builder.append("bo_keys.properties");
				File file = new File(builder.toString());
				if (!file.exists()) {
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				Properties props = new Properties();
				props.load(new FileInputStream(file));
				String value = props.getProperty(tagBO.getObjectCode());
				if (value == null || value.isEmpty()) {
					value = "1";
				}
				int key = 1, nextKey = 1;
				key = Integer.parseInt(value);
				nextKey = key + 1;
				if (bo instanceof IBODocument) {
					IBODocument item = (IBODocument) bo;
					item.setDocEntry(key);
					keys = new KeyValue[] { new KeyValue("DocEntry", key) };
				} else if (bo instanceof IBOMasterData) {
					IBOMasterData item = (IBOMasterData) bo;
					item.setDocEntry(key);
					keys = new KeyValue[] { new KeyValue("DocEntry", key) };
				} else if (bo instanceof IBOSimple) {
					IBOSimple item = (IBOSimple) bo;
					item.setObjectKey(key);
					keys = new KeyValue[] { new KeyValue("ObjectKey", key) };
				}
				OutputStream fos = new FileOutputStream(file);
				props.setProperty(tagBO.getObjectCode(), String.valueOf(nextKey));
				props.store(fos, String.format("fixed by transaction [%s].", this.getTransactionId()));
				fos.close();
			}
			return keys;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void applyPrimaryKeys(IBusinessObjectBase bo, KeyValue[] keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void applySeriesKey(IBusinessObjectBase bo, KeyValue key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public KeyValue[] usePrimaryKeys(IBusinessObjectBase[] bos) {
		KeyValue[] keyValues = null;
		for (IBusinessObjectBase item : bos) {
			keyValues = this.usePrimaryKeys(item);
		}
		return keyValues;
	}

	@Override
	public KeyValue[] usePrimaryKeys(IBusinessObjectsBase<?> bos) throws Exception {
		KeyValue[] keyValues = null;
		for (IBusinessObjectBase item : bos) {
			keyValues = this.usePrimaryKeys(item);
		}
		return keyValues;
	}

	@Override
	public KeyValue useSeriesKey(IBusinessObjectBase bo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public KeyValue useSeriesKey(IBusinessObjectBase[] bos) {
		throw new UnsupportedOperationException();
	}

	@Override
	public KeyValue useSeriesKey(IBusinessObjectsBase<?> bos) throws Exception {
		throw new UnsupportedOperationException();
	}
}

class DbKeysManager implements IBOKeysManager {

	private IDbConnection dbConnection;

	public final IDbConnection getDbConnection() {
		return dbConnection;
	}

	public final void setDbConnection(IDbConnection dbConnection) {
		this.dbConnection = dbConnection;
	}

	private BOAdapter adapter;

	public final BOAdapter getAdapter() {
		return adapter;
	}

	public final void setAdapter(IBOAdapter adapter) {
		this.adapter = (BOAdapter) adapter;
	}

	@Override
	public void applyPrimaryKeys(IBusinessObjectBase bo, KeyValue[] keys) {
		if (bo instanceof IBODocument) {
			IBODocument boKey = (IBODocument) bo;
			for (KeyValue key : keys) {
				if (key.getValue() == null) {
					continue;
				}
				if (IBODocument.MASTER_PRIMARY_KEY_NAME.equals(key.getKey())) {
					boKey.setDocEntry((int) (key.getValue()));
				}
			}
		} else if (bo instanceof IBODocumentLine) {
			IBODocumentLine boKey = (IBODocumentLine) bo;
			for (KeyValue key : keys) {
				if (key.getValue() == null) {
					continue;
				}
				if (IBODocumentLine.MASTER_PRIMARY_KEY_NAME.equals(key.getKey())) {
					boKey.setDocEntry((int) (key.getValue()));
				} else if (IBODocumentLine.SECONDARY_PRIMARY_KEY_NAME.equals(key.getKey())) {
					boKey.setLineId((int) (key.getValue()));
				}
			}
		} else if (bo instanceof IBOMasterData) {
			IBOMasterData boKey = (IBOMasterData) bo;
			for (KeyValue key : keys) {
				if (key.getValue() == null) {
					continue;
				}
				if (IBOMasterData.SERIAL_NUMBER_KEY_NAME.equals(key.getKey())) {
					boKey.setDocEntry((int) (key.getValue()));
				} else if (IBOMasterData.MASTER_PRIMARY_KEY_NAME.equals(key.getKey())) {
					boKey.setCode(String.valueOf(key.getValue()));
				}
			}
		} else if (bo instanceof IBOMasterDataLine) {
			IBOMasterDataLine boKey = (IBOMasterDataLine) bo;
			for (KeyValue key : keys) {
				if (key.getValue() == null) {
					continue;
				}
				if (IBOMasterDataLine.SECONDARY_PRIMARY_KEY_NAME.equals(key.getKey())) {
					boKey.setLineId((int) (key.getValue()));
				} else if (IBOMasterDataLine.MASTER_PRIMARY_KEY_NAME.equals(key.getKey())) {
					boKey.setCode(String.valueOf(key.getValue()));
				}
			}
		} else if (bo instanceof IBOSimple) {
			IBOSimple boKey = (IBOSimple) bo;
			for (KeyValue key : keys) {
				if (key.getValue() == null) {
					continue;
				}
				if (IBOSimple.MASTER_PRIMARY_KEY_NAME.equals(key.getKey())) {
					boKey.setObjectKey((int) (key.getValue()));
				}
			}
		} else if (bo instanceof IBOSimpleLine) {
			IBOSimpleLine boKey = (IBOSimpleLine) bo;
			for (KeyValue key : keys) {
				if (key.getValue() == null) {
					continue;
				}
				if (IBOSimpleLine.MASTER_PRIMARY_KEY_NAME.equals(key.getKey())) {
					boKey.setObjectKey((int) (key.getValue()));
				} else if (IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME.equals(key.getKey())) {
					boKey.setLineId((int) (key.getValue()));
				}
			}
		} else if (bo instanceof IBOCustomKey) {
			// 自定义主键
			if (bo instanceof IBOMaxValueKey) {
				IBOMaxValueKey maxValueKey = (IBOMaxValueKey) bo;
				IFieldDataDb dbField = maxValueKey.getMaxValueField();
				for (KeyValue key : keys) {
					if (key.getValue() == null) {
						continue;
					}
					if (dbField.getName().equals(key.getKey())) {
						dbField.setValue(key.getValue());
					}
				}
			}
		}
	}

	@Override
	public KeyValue[] usePrimaryKeys(IBusinessObjectBase bo) throws Exception {
		// 获取主键
		KeyValue[] keys = this.parsePrimaryKeys(bo);
		if ((keys == null || keys.length == 0) && !(bo instanceof IBOCustomKey)) {
			throw new Exception(I18N.prop("msg_bobas_not_found_bo_primary_key", bo.getClass().getName()));
		}
		// 主键赋值
		this.applyPrimaryKeys(bo, keys);
		// 更新主键
		this.updatePrimaryKeyRecords(bo);
		return keys;
	}

	@Override
	public KeyValue[] usePrimaryKeys(IBusinessObjectBase[] bos) throws Exception {
		// 获取主键
		KeyValue[] keys = null;// 主键信息
		int keyUsedCount = 0;// 主键使用的个数
		for (IBusinessObjectBase bo : bos) {
			if (bo == null)
				continue;
			if (!bo.isDirty() || !bo.isNew() || !bo.isSavable())
				continue;
			if (keys == null) {
				// 初始化主键
				keys = this.parsePrimaryKeys(bo);
			}
			// 设置主键
			this.applyPrimaryKeys(bo, keys);
			// 主键值增加
			for (KeyValue key : keys) {
				if (bo instanceof IBOLine) {
					if (!key.getKey().equals(IBOLine.SECONDARY_PRIMARY_KEY_NAME)) {
						continue;
					}
				}
				if (key.getValue() instanceof Integer) {
					key.setValue(Integer.sum((int) key.getValue(), 1));
				} else if (key.getValue() instanceof Long) {
					key.setValue(Long.sum((long) key.getValue(), 1));
				}
			}
			keyUsedCount++;// 使用了主键
		}
		// 更新主键
		if (keyUsedCount > 0)
			this.updatePrimaryKeyRecords(bos[0], keyUsedCount);
		return keys;
	}

	@Override
	public KeyValue[] usePrimaryKeys(IBusinessObjectsBase<?> bos) throws Exception {
		return this.usePrimaryKeys(bos.toArray(new IBusinessObjectBase[bos.size()]));
	}

	@Override
	public void applySeriesKey(IBusinessObjectBase bo, KeyValue key) {
		if (bo instanceof IBOSeriesKey) {
			IBOSeriesKey seriesKey = (IBOSeriesKey) bo;
			if (key.getKey() != null && !key.getKey().isEmpty()) {
				// 存在模块，则格式化编号
				seriesKey.setSeriesValue(String.format(key.getKey(), key.getValue()));
			} else {
				// 直接赋值编号
				seriesKey.setSeriesValue(key.getValue());
			}
		}
	}

	@Override
	public KeyValue useSeriesKey(IBusinessObjectBase bo) throws Exception {
		if (!(bo instanceof IBOSeriesKey))
			return null;
		IBOSeriesKey seriesKey = (IBOSeriesKey) bo;
		KeyValue key = this.parseSeriesKey(seriesKey);
		if (key == null) {
			return null;
		}
		this.updateSeriesKeyRecords(seriesKey, 1);
		this.applySeriesKey(bo, key);
		return key;
	}

	@Override
	public KeyValue useSeriesKey(IBusinessObjectBase[] bos) throws Exception {
		KeyValue key = null;
		int keyUsedCount = 0;// 使用的个数
		IBOSeriesKey seriesKey = null;
		for (IBusinessObjectBase bo : bos) {
			if (bo == null)
				continue;
			if (!bo.isDirty() || !bo.isNew() || !bo.isSavable())
				continue;
			if (!(bo instanceof IBOSeriesKey))
				continue;
			seriesKey = (IBOSeriesKey) bo;
			if (key == null) {
				// 初始化系列号
				key = this.parseSeriesKey(seriesKey);
			}
			if (key == null) {
				continue;
			}
			// 应用键值
			this.applySeriesKey(bo, key);
			// 键值增加
			if (key.getValue() instanceof Integer) {
				key.setValue(Integer.sum((int) key.getValue(), 1));
			} else if (key.getValue() instanceof Long) {
				key.setValue(Long.sum((long) key.getValue(), 1));
			}
			keyUsedCount++;// 使用了键值
		}
		// 更新键值
		if (keyUsedCount > 0)
			this.updateSeriesKeyRecords(seriesKey, keyUsedCount);
		return key;
	}

	@Override
	public KeyValue useSeriesKey(IBusinessObjectsBase<?> bos) throws Exception {
		return this.useSeriesKey(bos.toArray(new IBusinessObjectBase[bos.size()]));
	}

	protected KeyValue[] parsePrimaryKeys(IBusinessObjectBase bo)
			throws DbException, BOException, ParsingException, SqlScriptException {
		IDbCommand command = null;
		IDbDataReader reader = null;
		try {
			ISqlScripts sqlScripts = this.getAdapter().getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
			}
			command = this.getDbConnection().createCommand();
			ArrayList<KeyValue> keys = new ArrayList<KeyValue>();
			if (bo instanceof IBODocument) {
				// 业务单据主键
				IBODocument item = (IBODocument) bo;
				reader = command.executeReader(sqlScripts.getPrimaryKeyQuery(item.getObjectCode()));
				if (reader.next()) {
					keys.add(new KeyValue(IBODocument.MASTER_PRIMARY_KEY_NAME, reader.getInt(1)));
				} else {
					throw new BOException(
							I18N.prop("msg_bobas_not_found_bo_primary_key", bo.getClass().getSimpleName()));
				}
			} else if (bo instanceof IBODocumentLine) {
				// 业务单据行主键
				IBODocumentLine item = (IBODocumentLine) bo;
				ICriteria criteria = new Criteria();
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(IBODocumentLine.MASTER_PRIMARY_KEY_NAME);
				condition.setAliasDataType(DbFieldType.NUMERIC);
				condition.setValue(item.getDocEntry().toString());
				String table = String.format(sqlScripts.getDbObjectSign(),
						this.getAdapter().getMasterTable((IManagedFields) bo));
				String field = String.format(sqlScripts.getDbObjectSign(), IBODocumentLine.SECONDARY_PRIMARY_KEY_NAME);
				String where = this.getAdapter().parseSqlQuery(criteria.getConditions()).getQueryString();
				reader = command.executeReader(sqlScripts.groupMaxValueQuery(field, table, where));
				if (reader.next()) {
					keys.add(new KeyValue(IBODocumentLine.MASTER_PRIMARY_KEY_NAME, item.getDocEntry()));
					keys.add(new KeyValue(IBODocumentLine.SECONDARY_PRIMARY_KEY_NAME, reader.getInt(1) + 1));
				} else {
					throw new BOException(
							I18N.prop("msg_bobas_not_found_bo_primary_key", bo.getClass().getSimpleName()));
				}
			} else if (bo instanceof IBOMasterData) {
				// 主数据主键
				IBOMasterData item = (IBOMasterData) bo;
				reader = command.executeReader(sqlScripts.getPrimaryKeyQuery(item.getObjectCode()));
				if (reader.next()) {
					keys.add(new KeyValue(IBOMasterData.MASTER_PRIMARY_KEY_NAME, item.getCode()));
					keys.add(new KeyValue(IBOMasterData.SERIAL_NUMBER_KEY_NAME, reader.getInt(1)));
				} else {
					throw new BOException(
							I18N.prop("msg_bobas_not_found_bo_primary_key", bo.getClass().getSimpleName()));
				}
			} else if (bo instanceof IBOMasterDataLine) {
				// 主数据行主键
				IBOMasterDataLine item = (IBOMasterDataLine) bo;
				ICriteria criteria = new Criteria();
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(IBOMasterDataLine.MASTER_PRIMARY_KEY_NAME);
				condition.setValue(item.getCode());
				String table = String.format(sqlScripts.getDbObjectSign(),
						this.getAdapter().getMasterTable((IManagedFields) bo));
				String field = String.format(sqlScripts.getDbObjectSign(),
						IBOMasterDataLine.SECONDARY_PRIMARY_KEY_NAME);
				String where = this.getAdapter().parseSqlQuery(criteria.getConditions()).getQueryString();
				reader = command.executeReader(sqlScripts.groupMaxValueQuery(field, table, where));
				if (reader.next()) {
					keys.add(new KeyValue(IBOMasterDataLine.MASTER_PRIMARY_KEY_NAME, item.getCode()));
					keys.add(new KeyValue(IBOMasterDataLine.SECONDARY_PRIMARY_KEY_NAME, reader.getInt(1) + 1));
				} else {
					throw new BOException(
							I18N.prop("msg_bobas_not_found_bo_primary_key", bo.getClass().getSimpleName()));
				}
			} else if (bo instanceof IBOSimple) {
				// 简单对象主键
				IBOSimple item = (IBOSimple) bo;
				reader = command.executeReader(sqlScripts.getPrimaryKeyQuery(item.getObjectCode()));
				if (reader.next()) {
					keys.add(new KeyValue(IBOSimple.MASTER_PRIMARY_KEY_NAME, reader.getInt(1)));
				} else {
					throw new BOException(
							I18N.prop("msg_bobas_not_found_bo_primary_key", bo.getClass().getSimpleName()));
				}
			} else if (bo instanceof IBOSimpleLine) {
				// 简单对象行主键
				IBOSimpleLine item = (IBOSimpleLine) bo;
				ICriteria criteria = new Criteria();
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
				condition.setAliasDataType(DbFieldType.NUMERIC);
				condition.setValue(item.getObjectKey());
				String table = String.format(sqlScripts.getDbObjectSign(),
						this.getAdapter().getMasterTable((IManagedFields) bo));
				String field = String.format(sqlScripts.getDbObjectSign(), IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME);
				String where = this.getAdapter().parseSqlQuery(criteria.getConditions()).getQueryString();
				reader = command.executeReader(sqlScripts.groupMaxValueQuery(field, table, where));
				if (reader.next()) {
					keys.add(new KeyValue(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME, item.getObjectKey()));
					keys.add(new KeyValue(IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME, reader.getInt(1) + 1));
				} else {
					throw new BOException(
							I18N.prop("msg_bobas_not_found_bo_primary_key", bo.getClass().getSimpleName()));
				}

			} else {
				// 没有指定主键的获取方式
				/*
				 * throw new SqlScriptsException( i18n.prop(
				 * "msg_bobas_not_specify_primary_keys_obtaining_method",
				 * bo.getClass().getName()));
				 */
			}
			if (reader != null) {
				reader.close();
				reader = null;
			}
			// 额外主键获取
			if (bo instanceof IBOCustomKey) {
				// 自定义主键
				if (bo instanceof IBOMaxValueKey) {
					// 字段最大值
					IBOMaxValueKey maxValueKey = (IBOMaxValueKey) bo;
					IFieldDataDb dbField = maxValueKey.getMaxValueField();
					String table = String.format(sqlScripts.getDbObjectSign(), dbField.getDbTable());
					String field = String.format(sqlScripts.getDbObjectSign(), dbField.getDbField());
					ICondition[] tmpConditions = maxValueKey.getMaxValueConditions();
					ICriteria criteria = new Criteria();
					if (tmpConditions != null) {
						for (ICondition item : tmpConditions) {
							criteria.getConditions().add(item);
						}
					}
					criteria.check(bo.getClass());
					String where = this.getAdapter().parseSqlQuery(criteria.getConditions()).getQueryString();
					reader = command.executeReader(sqlScripts.groupMaxValueQuery(field, table, where));
					if (reader.next()) {
						keys.add(new KeyValue(dbField.getName(), reader.getInt(1) + 1));
					} else {
						throw new BOException(
								I18N.prop("msg_bobas_not_found_bo_primary_key", bo.getClass().getSimpleName()));
					}
				}
			}
			return keys.toArray(new KeyValue[] {});
		} finally {
			if (reader != null) {
				reader.close();
				reader = null;
			}
			if (command != null) {
				command.close();
				command = null;
			}
		}
	}

	protected void updatePrimaryKeyRecords(IBusinessObjectBase bo)
			throws ParsingException, BOException, DbException, SqlScriptException {
		this.updatePrimaryKeyRecords(bo, 1);
	}

	protected void updatePrimaryKeyRecords(IBusinessObjectBase bo, int addValue)
			throws ParsingException, BOException, DbException, SqlScriptException {
		IDbCommand command = null;
		try {
			if (bo instanceof IBOLine) {
				// 对象行，不做处理
				return;
			}
			if (bo instanceof IBOCustomKey) {
				// 自定义主键，不做处理
				return;
			}
			ISqlScripts sqlScripts = this.getAdapter().getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
			}
			int nextValue = 0;
			String boCode = null;
			if (bo instanceof IBODocument) {
				// 业务单据主键
				IBODocument item = (IBODocument) bo;
				nextValue = item.getDocEntry() + addValue;
				boCode = item.getObjectCode();
			} else if (bo instanceof IBOMasterData) {
				// 主数据主键
				IBOMasterData item = (IBOMasterData) bo;
				nextValue = item.getDocEntry() + addValue;
				boCode = item.getObjectCode();
			} else if (bo instanceof IBOSimple) {
				// 简单对象主键
				IBOSimple item = (IBOSimple) bo;
				nextValue = item.getObjectKey() + addValue;
				boCode = item.getObjectCode();
			}
			if (boCode == null || nextValue == 0) {
				// 未能有效解析
				throw new ParsingException(
						I18N.prop("msg_bobas_not_specify_primary_keys_obtaining_method", bo.toString()));
			}
			// 更新数据记录
			command = this.getDbConnection().createCommand();
			command.executeUpdate(sqlScripts.getUpdatePrimaryKeyScript(boCode, addValue));
		} finally {
			if (command != null) {
				command.close();
				command = null;
			}
		}
	}

	protected void updateSeriesKeyRecords(IBOSeriesKey bo, int addValue)
			throws BOException, DbException, SqlScriptException {
		IDbCommand command = null;
		try {
			ISqlScripts sqlScripts = this.getAdapter().getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
			}
			// 更新数据记录
			command = this.getDbConnection().createCommand();
			command.executeUpdate(sqlScripts.getUpdateSeriesKeyScript(bo.getObjectCode(), bo.getSeries(), addValue));
		} finally {
			if (command != null) {
				command.close();
				command = null;
			}
		}
	}

	protected KeyValue parseSeriesKey(IBOSeriesKey bo) throws BOException, DbException, SqlScriptException {
		IDbCommand command = null;
		IDbDataReader reader = null;
		try {
			ISqlScripts sqlScripts = this.getAdapter().getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
			}
			KeyValue key = null;
			command = this.getDbConnection().createCommand();
			if (bo.getSeries() > 0) {
				// 设置了系列
				reader = command.executeReader(sqlScripts.getSeriesKeyQuery(bo.getObjectCode(), bo.getSeries()));
				if (reader.next()) {
					key = new KeyValue(reader.getString(2), reader.getInt(1));
				} else {
					throw new BOException(
							I18N.prop("msg_bobas_not_found_bo_series_key", bo.getClass().getSimpleName()));
				}
			}
			return key;
		} finally {
			if (reader != null) {
				reader.close();
				reader = null;
			}
			if (command != null) {
				command.close();
				command = null;
			}
		}
	}

}