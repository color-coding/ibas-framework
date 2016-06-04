package org.colorcoding.ibas.btulz.models;

import org.colorcoding.ibas.btulz.data.emDataType;
import org.colorcoding.ibas.btulz.data.emPropertyType;
import org.colorcoding.ibas.btulz.data.emYesNo;
import org.colorcoding.ibas.btulz.models.Domain;
import org.colorcoding.ibas.btulz.models.IDomain;
import org.colorcoding.ibas.btulz.models.IModel;
import org.colorcoding.ibas.btulz.models.IProperty;
import org.colorcoding.ibas.btulz.models.IPropertyData;
import org.colorcoding.ibas.btulz.models.IPropertyModel;
import org.colorcoding.ibas.btulz.models.IPropertyModels;

import junit.framework.TestCase;

public class testModels extends TestCase {

	public void testDomainModels() {
		IDomain domain = new Domain();
		domain.setName("TrainingTesting");
		domain.setShortName("TT");
		domain.setDescription("培训&测试");

		IProperty property;
		IPropertyData propertyData;
		IPropertyModel propertyModel;
		IPropertyModels propertyModels;

		IModel model = domain.getModels().create();
		model.setName("SalesOrder");
		model.setDescription("销售订单");
		model.setShortName("CC_TT_SO");
		model.setMapped("CC_TT_ORDR");
		property = model.getProperties().create(emPropertyType.pt_Data);
		property.setName("DocumentEntry");
		property.setDescription("单据编号");
		propertyData = (IPropertyData) property;
		propertyData.setDataType(emDataType.dt_Numeric);
		propertyData.setPrimaryKey(emYesNo.Yes);
		propertyData.setMapped("DocEntry");

		IModel userModel = domain.getModels().create();
		userModel.setName("User");
		userModel.setDescription("用户");
		userModel.setShortName("CC_TT_US");
		userModel.setMapped("CC_TT_OUSR");
		property = userModel.getProperties().create(emPropertyType.pt_Data);
		property.setName("UserCode");
		property.setDescription("用户编码");
		propertyData = (IPropertyData) property;
		propertyData.setDataType(emDataType.dt_Alphanumeric);
		propertyData.setPrimaryKey(emYesNo.Yes);
		propertyData.setMapped("Code");

		property = model.getProperties().create(emPropertyType.pt_Model);
		property.setName("DocumentUser");
		property.setDescription("单据创建人");
		propertyModel = (IPropertyModel) property;
		propertyModel.setModel(userModel);

		IModel modelLine = domain.getModels().create();
		modelLine.setName("SalesOrderLine");
		modelLine.setDescription("销售订单行");
		modelLine.setShortName("CC_TT_SOLINE");
		modelLine.setMapped("CC_TT_RDR1");
		property = modelLine.getProperties().create(emPropertyType.pt_Data);
		property.setName("DocumentEntry");
		property.setDescription("单据编号");
		propertyData = (IPropertyData) property;
		propertyData.setDataType(emDataType.dt_Numeric);
		propertyData.setPrimaryKey(emYesNo.Yes);
		propertyData.setMapped("DocEntry");
		property = modelLine.getProperties().create(emPropertyType.pt_Data);
		property.setName("DocumentLine");
		property.setDescription("单据行号");
		propertyData = (IPropertyData) property;
		propertyData.setDataType(emDataType.dt_Numeric);
		propertyData.setPrimaryKey(emYesNo.Yes);
		propertyData.setMapped("LineId");

		property = model.getProperties().create(emPropertyType.pt_Models);
		property.setName("Lines");
		property.setDescription("单据行集合");
		propertyModels = (IPropertyModels) property;
		propertyModels.setModel(modelLine);

		System.out.println(domain.toString());

		for (IModel mItem : domain.getModels()) {
			this.outPrint(mItem, 1);
		}
	}

	private void outPrint(IModel model, int index) {
		String space = "";
		for (int i = 0; i < index; i++) {
			space += " ";
		}
		System.out.println(space + model.toString());
		space += " ";
		for (IProperty pItem : model.getProperties()) {
			System.out.println(space + pItem.toString());
			if (pItem instanceof IPropertyModel) {
				IPropertyModel nModel = (IPropertyModel) pItem;
				this.outPrint(nModel.getModel(), index + 2);
			} else if (pItem instanceof IPropertyModels) {
				IPropertyModels nModel = (IPropertyModels) pItem;
				this.outPrint(nModel.getModel(), index + 2);
			}
		}
	}

}
