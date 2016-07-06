package org.colorcoding.ibas.bobas.repository;

/**
 * 业务逻辑运行错误
 * @author Niuren.Zhu
 *
 */
public class ActionsEventException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8963564858229856377L;

	public ActionsEventException() {
		super();
	}

	public ActionsEventException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public ActionsEventException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ActionsEventException(String arg0) {
		super(arg0);
	}

	public ActionsEventException(Throwable arg0) {
		super(arg0);
	}

}
