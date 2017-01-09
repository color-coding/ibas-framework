package org.colorcoding.ibas.bobas.cxf;

public class Server {

	public Server(String address, Object implementor) {
		this.setAddress(address);
		this.setImplementor(implementor);
	}

	private String address;

	public final String getAddress() {
		return address;
	}

	public final void setAddress(String address) {
		this.address = address;
	}

	public Object implementor;

	public final Object getImplementor() {
		return implementor;
	}

	public final void setImplementor(Object implementor) {
		this.implementor = implementor;
	}

}
