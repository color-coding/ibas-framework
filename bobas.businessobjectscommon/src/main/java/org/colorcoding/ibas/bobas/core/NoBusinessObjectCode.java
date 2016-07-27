package org.colorcoding.ibas.bobas.core;

/**
 * 类没有定义业务对象编码
 * 
 * @author Niuren.Zhu
 *
 */
public class NoBusinessObjectCode extends BOFactoryException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7466593944696537181L;

	public NoBusinessObjectCode() {
	}

	public NoBusinessObjectCode(String message) {
		super(message);
	}

	public NoBusinessObjectCode(String message, Throwable exception) {
		super(message, exception);
	}

	public NoBusinessObjectCode(Throwable exception) {
		super(exception);
	}

}
