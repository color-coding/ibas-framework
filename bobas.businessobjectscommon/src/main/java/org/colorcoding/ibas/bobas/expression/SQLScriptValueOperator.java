package org.colorcoding.ibas.bobas.expression;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.db.DbTransaction;
import org.colorcoding.ibas.bobas.db.SqlStatement;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * SQL脚本值操作
 * 
 * @author Niuren.Zhu
 *
 */
public class SQLScriptValueOperator implements IPropertyValueOperator {

	public SQLScriptValueOperator(DbTransaction dbTransaction) {
		this.dbTransaction = dbTransaction;
	}

	private DbTransaction dbTransaction;

	private Object value;

	@Override
	public Object getValue() {
		return this.value;
	}

	private IBusinessObject bo;

	@Override
	public void setValue(Object value) {
		try {
			this.value = null;
			if (value instanceof IBusinessObject) {
				this.bo = (IBusinessObject) value;
				Objects.requireNonNull(this.dbTransaction);
				if (this.propertyName == null || this.propertyName.isEmpty()) {
					// 此时propertyName为查询命令
					throw new RuntimeException(I18N.prop("msg_bobas_invalid_sql_query"));
				}
				String query = this.propertyName;
				// 替换查询中的变量
				Matcher matcher = Pattern.compile(MyConfiguration.VARIABLE_PATTERN).matcher(query);
				while (matcher.find()) {
					// 带格式名称${}
					String vName = matcher.group(0);
					String tName = vName.substring(2, vName.length() - 1);
					String vValue = Strings.valueOf(BOUtilities.propertyValue(this.bo, tName));
					if (vValue != null) {
						query = query.replace(vName, vValue.replace("'", "''"));
					}
				}
				IDataTable table = this.dbTransaction.fetch(new SqlStatement(query));
				if (table != null && !table.getColumns().isEmpty() && !table.getRows().isEmpty()) {
					this.value = table.getRows().get(0).getValue(0);
				}
			}
		} catch (Exception e) {
			throw new ExpressionException(e);
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
		return String
				.format("{sql operator: %s}",
						this.propertyName != null
								? this.propertyName.length() > 10 ? this.propertyName.substring(0, 10)
										: this.propertyName.substring(0, this.propertyName.length())
								: Strings.VALUE_EMPTY);
	}
}
