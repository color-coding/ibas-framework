package org.colorcoding.ibas.btulz.models;

import org.colorcoding.ibas.btulz.data.emPropertyType;

public class PropertyModel extends Property implements IPropertyModel {

	private IModel model;

	@Override
	public IModel getModel() {
		return this.model;
	}

	@Override
	public void setModel(IModel model) {
		this.model = model;
	}

	@Override
	public emPropertyType getPropertyType() {
		return emPropertyType.pt_Model;
	}
}
