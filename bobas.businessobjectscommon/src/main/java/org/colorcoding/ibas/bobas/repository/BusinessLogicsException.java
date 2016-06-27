package org.colorcoding.ibas.bobas.repository;

/**
 * 业务逻辑运行错误
 * @author Niuren.Zhu
 *
 */
public class BusinessLogicsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8963564858229856377L;

	public BusinessLogicsException() {
		super();
	}

	public BusinessLogicsException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public BusinessLogicsException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BusinessLogicsException(String arg0) {
		super(arg0);
	}

	public BusinessLogicsException(Throwable arg0) {
		super(arg0);
	}

}
