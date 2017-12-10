package org.colorcoding.ibas.bobas.serialization;

import java.util.ArrayList;
import java.util.Collection;

public class SchemaElements extends ArrayList<SchemaElement> {

	private static final long serialVersionUID = 5525571009797394981L;

	public void add(SchemaElement[] items) {
		for (SchemaElement schemaElement : items) {
			this.add(schemaElement);
		}
	}

	public void add(Collection<SchemaElement> items) {
		for (SchemaElement schemaElement : items) {
			this.add(schemaElement);
		}
	}

	@Override
	public boolean add(SchemaElement e) {
		for (int i = 0; i < this.size(); i++) {
			SchemaElement item = this.get(i);
			if (item.getName().equals(e.getName())) {
				this.remove(i);
			}
		}
		return super.add(e);
	}

	public SchemaElement[] toArray() {
		return this.toArray(new SchemaElement[0]);
	}
}
