package org.colorcoding.ibas.bobas.test.common;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.OperationResult;

import junit.framework.TestCase;

public class testOperationResult extends TestCase {

	public void testToXML() throws Exception {
		OperationResult<?> operationResult = new OperationResult<Object>();
		operationResult.setResultCode(1);
		operationResult.setMessage("GOOD DAY!");
		operationResult.addInformations("NAME", "信息");
		System.out.println(operationResult.getError());
		operationResult = new OperationResult<Object>();
		operationResult.setError(new ClassNotFoundException(Criteria.class.getName()));
		System.out.println(operationResult.getMessage());

		Criteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias("ItemCode");
		condition.setValue("A00001");
		operationResult.addResultObjects(criteria);
		operationResult.addResultObjects(criteria.clone());

		JAXBContext context = JAXBContext.newInstance(operationResult.getClass(), Criteria.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// //编码格式
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 是否格式化生成的xml串
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
		StringWriter writer = new StringWriter();
		marshaller.marshal(operationResult, writer);
		System.out.println(writer.toString());
	}

}
