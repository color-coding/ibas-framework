package org.colorcoding.ibas.bobas.test.logics;

import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.logics.IBusinessLogicContract;

public interface IMaterialsIssueContract extends IBusinessLogicContract {
	String getItemCode();

	Decimal getQuantity();
}
