package org.colorcoding.ibas.bobas.approval;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDb;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.expressions.IPropertyValueOperator;
import org.colorcoding.ibas.bobas.i18n.i18n;
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

	private IBusinessObjectBase bo;

	@Override
	public void setValue(Object value) {
		this.value = null;
		if (value instanceof IBusinessObjectBase) {
			this.bo = (IBusinessObjectBase) value;
			if (this.repository == null) {
				throw new RuntimeException(i18n.prop("msg_bobas_invaild_bo_repository"));
			}
			if (this.propertyName == null || this.propertyName.isEmpty()) {
				// 此时propertyName为查询命令
				throw new RuntimeException(i18n.prop("msg_bobas_invalid_sql_query"));
			}
			String query = this.propertyName;
			// 替换查询中的变量
			if (this.bo instanceof IManageFields) {
				IManageFields boFields = (IManageFields) this.bo;
				String pattern = "\\$\\{([\\!a-zA-Z].*?)\\}";
				Matcher matcher = Pattern.compile(pattern).matcher(query);
				while (matcher.find()) {
					String vName = matcher.group(0).replace("${", "").replace("}", "");
					IFieldDataDb fieldData = null;
					for (IFieldData item : boFields.getFields()) {
						if (item instanceof IFieldDataDb) {
							IFieldDataDb dbItem = (IFieldDataDb) item;
							if (dbItem.getDbField().equals(vName)) {
								fieldData = dbItem;
								break;
							}
						}
					}
					if (fieldData == null) {
						throw new RuntimeException(i18n.prop("msg_bobas_not_found_bo_field", vName));
					}
					query = query.replace(String.format(MyConsts.VARIABLE_NAMING_TEMPLATE, fieldData.getDbField()),
							String.valueOf(fieldData.getValue()));
				}
			}
			IOperationResult<IDataTable> opRslt = this.repository.query(new SqlQuery(query));
			if (opRslt.getError() != null) {
				throw new RuntimeException(opRslt.getError());
			}
			if (opRslt.getResultCode() != 0) {
				throw new RuntimeException(opRslt.getMessage());
			}
			IDataTable table = opRslt.getResultObjects().firstOrDefault();
			if (table != null && table.getColumns().size() > 0 && table.getRows().size() > 0) {
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
		return String.format("{property's value: %s}", this.propertyName);
	}
}
