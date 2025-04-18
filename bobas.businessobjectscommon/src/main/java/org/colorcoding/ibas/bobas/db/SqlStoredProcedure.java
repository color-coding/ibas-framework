package org.colorcoding.ibas.bobas.db;

import java.util.ArrayList;
import java.util.List;

import org.colorcoding.ibas.bobas.core.Serializable;
import org.colorcoding.ibas.bobas.data.KeyValue;

/**
 * 数据库存储过程
 * 
 * @author Niuren.Zhu
 *
 */
public class SqlStoredProcedure extends Serializable implements ISqlStatement {

	private static final long serialVersionUID = 8400088898134597897L;

	public SqlStoredProcedure() {

	}

	public SqlStoredProcedure(String name) {
		this();
		this.setName(name);
	}

	private String name;

	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		this.name = value;
	}

	private List<KeyValue> parameters;

	public List<KeyValue> getParameters() {
		if (this.parameters == null) {
			this.parameters = new ArrayList<>();
		}
		return this.parameters;
	}

	public void addParameters(String name, Object value) {
		this.getParameters().add(new KeyValue(name, value));
	}

	@Override
	public String getContent() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.getName());
		if (!this.getParameters().isEmpty()) {
			stringBuilder.append(" ");
			for (int i = 0; i < this.getParameters().size(); i++) {
				if (i > 0) {
					stringBuilder.append(", ");
				}
				stringBuilder.append(this.getParameters().get(i).getKey());
				stringBuilder.append(" = ");
				stringBuilder.append(this.getParameters().get(i).getValue());
			}
		}
		return stringBuilder.toString();
	}

	@Override
	public void setContent(String value) {
		throw new RuntimeException("please input by parameters.");
	}

}
