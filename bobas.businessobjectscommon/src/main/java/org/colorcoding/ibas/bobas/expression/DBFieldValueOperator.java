package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 数据库字段值操作
 * 
 * @author Niuren.Zhu
 *
 */
public class DBFieldValueOperator extends FieldValueOperator {

	public IPropertyInfo<?> getProperty() {
		if (this.property == null) {
			DbField dbField;
			for (IPropertyInfo<?> item : this.dataObject.properties()) {
				dbField = item.getAnnotation(DbField.class);
				if (dbField == null) {
					continue;
				}
				if (Strings.equals(dbField.name(), this.getPropertyName())) {
					this.property = item;
					break;
				}
			}
		}
		if (this.property == null) {
			throw new ExpressionException(I18N.prop("msg_bobas_not_found_property", this.getPropertyName()));
		}
		return this.property;
	}

}
