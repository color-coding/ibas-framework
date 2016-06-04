package org.colorcoding.bobas.test.logics;

import org.colorcoding.bobas.data.Decimal;
import org.colorcoding.bobas.logics.IBusinessLogicContract;

public interface IMaterialsIssueContract extends IBusinessLogicContract {
	String getItemCode();

	Decimal getQuantity();
}
