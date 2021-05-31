package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDb;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 数据库字段值操作
 * 
 * @author Niuren.Zhu
 *
 */
public class DBFieldValueOperator extends FieldValueOperator {
	protected IFieldData getField() {
		if (this.field == null) {
			for (IFieldData item : value.getFields()) {
				if (item instanceof IFieldDataDb) {
					IFieldDataDb dbField = (IFieldDataDb) item;
					if (dbField.getDbField().equalsIgnoreCase(this.getPropertyName())) {
						this.field = dbField;
						break;
					}
				}
			}
		}
		if (this.field == null) {
			throw new JudgmentLinkException(I18N.prop("msg_bobas_not_found_bo_field", this.getPropertyName()));
		}
		return this.field;
	}

}
