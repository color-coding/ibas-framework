package org.colorcoding.ibas.bobas.serialization;

import java.util.ArrayList;
import java.util.Collection;

public class SerializationElements extends ArrayList<SerializationElement> {

	private static final long serialVersionUID = 5525571009797394981L;

	public void add(SerializationElement[] items) {
		for (SerializationElement schemaElement : items) {
			this.add(schemaElement);
		}
	}

	public void add(Collection<SerializationElement> items) {
		for (SerializationElement schemaElement : items) {
			this.add(schemaElement);
		}
	}

	@Override
	public boolean add(SerializationElement e) {
		for (int i = 0; i < this.size(); i++) {
			SerializationElement item = this.get(i);
			if (item.getName().equals(e.getName())) {
				this.remove(i);
			}
		}
		return super.add(e);
	}

}
