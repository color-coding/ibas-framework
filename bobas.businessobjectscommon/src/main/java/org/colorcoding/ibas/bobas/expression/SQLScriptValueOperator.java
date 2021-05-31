package org.colorcoding.ibas.bobas.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDb;
import org.colorcoding.ibas.bobas.core.fields.IManagedFields;
import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.repository.IBORepository4DbReadonly;

/**
 * SQL脚本值操作
 * 
 * @author Niuren.Zhu
 *
 */
public class SQLScriptValueOperator implements IPropertyValueOperator {

	public SQLScriptValueOperator() {

	}

	public SQLScriptValueOperator(IBORepository4DbReadonly repository) {
		this.repository = repository;
	}

	private IBORepository4DbReadonly repository;

	private Object value;

	@Override
	public Object getValue() {
		return this.value;
	}

	private IBusinessObject bo;

	@Override
	public void setValue(Object value) {
		this.value = null;
		if (value instanceof IBusinessObject) {
			this.bo = (IBusinessObject) value;
			if (this.repository == null) {
				throw new RuntimeException(I18N.prop("msg_bobas_invaild_bo_repository"));
			}
			if (this.propertyName == null || this.propertyName.isEmpty()) {
				// 此时propertyName为查询命令
				throw new RuntimeException(I18N.prop("msg_bobas_invalid_sql_query"));
			}
			String query = this.propertyName;
			// 替换查询中的变量
			Matcher matcher = Pattern.compile(MyConfiguration.VARIABLE_PATTERN).matcher(query);
			while (matcher.find()) {
				// 带格式名称${}
				try {
					String vName = matcher.group(0);
					String tName = vName.substring(2, vName.length() - 1);
					String vValue = this.getPropertyValues(this.bo, tName);
					if (vValue != null) {
						query = query.replace(vName, vValue.replace("'", "''"));
					}
				} catch (Exception e) {
					Logger.log(e);
				}
			}
			IOperationResult<IDataTable> opRslt = this.repository.query(new SqlQuery(query, true, false));
			if (opRslt.getError() != null) {
				throw new RuntimeException(opRslt.getError());
			}
			IDataTable table = opRslt.getResultObjects().firstOrDefault();
			if (table != null && !table.getColumns().isEmpty() && !table.getRows().isEmpty()) {
				this.value = table.getRows().get(0).getValue(0);
			}
		}
	}

	@Override
	public Class<?> getValueClass() {
		if (this.value != null) {
			return this.value.getClass();
		}
		// 默认字符串比较
		return String.class;
	}

	private String propertyName;

	@Override
	public String getPropertyName() {
		return this.propertyName;
	}

	@Override
	public void setPropertyName(String value) {
		this.propertyName = value;
	}

	@Override
	public String toString() {
		return String.format("{sql operator: %s}",
				this.propertyName != null
						? this.propertyName.length() > 10 ? this.propertyName.substring(0, 10)
								: this.propertyName.substring(0, this.propertyName.length())
						: DataConvert.STRING_VALUE_EMPTY);
	}

	protected String getPropertyValues(IBusinessObject bo, String path) throws JudmentOperationException {
		try {
			if (bo instanceof IManagedFields) {
				IManagedFields boFields = (IManagedFields) bo;
				String property = path;
				if (path.indexOf(".") > 1) {
					property = path.split("\\.")[0];
				}
				for (IFieldData fieldData : boFields.getFields()) {
					if (fieldData instanceof IFieldDataDb) {
						IFieldDataDb dbField = (IFieldDataDb) fieldData;
						if (!dbField.getName().equalsIgnoreCase(property)
								&& !dbField.getDbField().equalsIgnoreCase(property)) {
							continue;
						}
					} else {
						if (!fieldData.getName().equalsIgnoreCase(property)) {
							continue;
						}
					}
					Object value = fieldData.getValue();
					if (value == null) {
						return DataConvert.STRING_VALUE_EMPTY;
					}
					if (value instanceof Iterable) {
						if (path.indexOf(".", property.length()) > 1) {
							String cPath = path.substring(property.length() + 1, path.length());
							StringBuilder stringBuilder = new StringBuilder();
							boolean done = false;
							for (Object item : (Iterable<?>) value) {
								if (done) {
									stringBuilder.append(", ");
								}
								if (!(item instanceof IBusinessObject)) {
									continue;
								}
								stringBuilder.append(this.getPropertyValues((IBusinessObject) item, cPath));
								done = true;
							}
							return stringBuilder.toString();
						} else {
							StringBuilder stringBuilder = new StringBuilder();
							boolean done = false;
							for (Object item : (Iterable<?>) value) {
								if (done) {
									stringBuilder.append(", ");
								}
								if (item == null) {
									stringBuilder.append(DataConvert.STRING_VALUE_EMPTY);
								} else {
									stringBuilder.append(String.valueOf(item));
								}
								done = true;
							}
							return stringBuilder.toString();
						}
					}
					return String.valueOf(value);
				}
			}
			return null;
		} catch (Exception e) {
			throw new JudmentOperationException(I18N.prop("msg_bobas_not_found_property_path_value", path), e);
		}
	}
}
