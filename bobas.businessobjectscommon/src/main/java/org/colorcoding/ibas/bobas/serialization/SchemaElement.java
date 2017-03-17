package org.colorcoding.ibas.bobas.serialization;

public class SchemaElement implements Comparable<SchemaElement> {

	public SchemaElement() {

	}

	public SchemaElement(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}

	public SchemaElement(String name, String wrapper, Class<?> type) {
		this(name, type);
		this.wrapper = wrapper;
	}

	private String name;

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	private String wrapper;

	public final String getWrapper() {
		return wrapper;
	}

	public final void setWrapper(String wrapper) {
		this.wrapper = wrapper;
	}

	private Class<?> type;

	public final Class<?> getType() {
		return type;
	}

	public final void setType(Class<?> type) {
		this.type = type;
	}

	@Override
	public int compareTo(SchemaElement target) {
		String sName = this.getWrapper();
		if (sName == null || sName.isEmpty()) {
			sName = this.getName();
		}
		String tName = target.getWrapper();
		if (tName == null || tName.isEmpty()) {
			tName = target.getName();
		}
		if (Character.isUpperCase(tName.charAt(0)) == Character.isUpperCase(sName.charAt(0))) {
			return sName.compareTo(tName);
		} else {
			if (Character.isUpperCase(tName.charAt(0))) {
				return -1;
			}
			return 1;
		}
	}

}
