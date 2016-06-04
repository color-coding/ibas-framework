package org.colorcoding.bobas.core;

/**
 * 类没有定义业务对象编码
 * 
 * @author Niuren.Zhu
 *
 */
public class ClassNotDefinedBOCode extends BOFactoryException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7466593944696537181L;

	public ClassNotDefinedBOCode() {
	}

	public ClassNotDefinedBOCode(String message) {
		super(message);
	}

	public ClassNotDefinedBOCode(String message, Throwable exception) {
		super(message, exception);
	}

	public ClassNotDefinedBOCode(Throwable exception) {
		super(exception);
	}

}
