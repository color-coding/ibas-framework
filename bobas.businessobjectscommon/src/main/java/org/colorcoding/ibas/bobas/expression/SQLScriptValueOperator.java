package org.colorcoding.ibas.bobas.expression;

import java.util.Iterator;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDb;
import org.colorcoding.ibas.bobas.core.fields.IManagedFields;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.data.IKeyText;
import org.colorcoding.ibas.bobas.i18n.I18N;
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
			if (this.bo instanceof IManagedFields) {
				IManagedFields boFields = (IManagedFields) this.bo;
				query = MyConfiguration.applyVariables(query, new Iterator<IKeyText>() {
					IFieldData[] fieldDatas = boFields.getFields(c -> c instanceof IFieldDataDb);
					int index;

					@Override
					public IKeyText next() {
						IFieldData fieldData = fieldDatas[index];
						IKeyText next = new IKeyText() {

							@Override
							public void setText(String value) {
							}

							@Override
							public void setKey(String value) {
							}

							@Override
							public String getText() {
								return fieldData.getValue() == null ? null : fieldData.getValue().toString();
							}

							@Override
							public String getKey() {
								return ((IFieldDataDb) fieldData).getDbField();
							}

							@Override
							public String toString() {
								return String.format("{key text: %s %s}", this.getKey(), this.getText());
							}

						};
						index++;
						return next;
					}

					@Override
					public boolean hasNext() {
						return index < fieldDatas.length ? true : false;
					}
				});
				;
			}
			IOperationResult<IDataTable> opRslt = this.repository.query(new SqlQuery(query, true, false));
			if (opRslt.getError() != null) {
				throw new RuntimeException(opRslt.getError());
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
