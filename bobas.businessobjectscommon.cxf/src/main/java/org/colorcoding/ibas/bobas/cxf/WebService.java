package org.colorcoding.ibas.bobas.cxf;

public class WebService {

	public WebService() {
	}

	public WebService(Object implementor) {
		this.setImplementor(implementor);
	}

	public WebService(Object implementor, String address) {
		this.setImplementor(implementor);
		this.setAddress(address);
	}

	private String name;

	public final String getName() {
		if (this.name == null || this.name.isEmpty()) {
			this.name = "unkown";
		}
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	private String address;

	public final String getAddress() {
		if (this.address == null) {
			this.address = "";
		}
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

	@Override
	public String toString() {
		return String.format("{server: %s %s}", this.getName(), this.getAddress());
	}
}
