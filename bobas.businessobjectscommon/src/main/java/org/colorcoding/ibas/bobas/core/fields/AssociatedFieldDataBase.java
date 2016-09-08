package org.colorcoding.ibas.bobas.core.fields;

import org.colorcoding.ibas.bobas.mapping.AssociatedField;
import org.colorcoding.ibas.bobas.mapping.AssociationMode;
import org.colorcoding.ibas.bobas.mapping.Associations;

/**
 * 关联字段基类
 * 
 * @author niuren.zhu
 *
 * @param <T>
 */
public abstract class AssociatedFieldDataBase<T> extends FieldDataBase<T> {
	/**
	 * 
	 * @param assoCount
	 *            关联字段数量
	 */
	public AssociatedFieldDataBase(int assoCount) {
		this.associations = new FieldRelation[assoCount];
	}

	FieldRelation[] associations = null;

	public FieldRelation[] getAssociations() {
		return this.associations;
	}

	public void addAssociations(String field, String mapped) {
		FieldRelation fieldRelation = new FieldRelation();
		fieldRelation.associatedField = field;
		fieldRelation.mappedField = mapped;
		for (int i = 0; i < this.associations.length; i++) {
			if (this.associations[i] == null) {
				this.associations[i] = fieldRelation;
				break;
			}
		}
	}

	AssociationMode associationMode;

	public final AssociationMode getAssociationMode() {
		return associationMode;
	}

	public final void setAssociationMode(AssociationMode associationMode) {
		this.associationMode = associationMode;
	}

	boolean autoLoading;

	public final boolean isAutoLoading() {
		return autoLoading;
	}

	public final void setAutoLoading(boolean autoLoading) {
		this.autoLoading = autoLoading;
	}

	public void mapping(Associations mapping) {
		this.setAssociationMode(mapping.mode());
		this.setSavable(mapping.isSavable());
		this.setAutoLoading(mapping.autoLoading());
		this.setValueType(mapping.type());
		for (AssociatedField assoField : mapping.value()) {
			this.addAssociations(assoField.field(),
					assoField.mapped().isEmpty() ? assoField.field() : assoField.mapped());
		}
	}
}
