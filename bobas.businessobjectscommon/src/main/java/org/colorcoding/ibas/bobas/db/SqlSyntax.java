package org.colorcoding.ibas.bobas.db;

import javax.xml.bind.annotation.XmlRootElement;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.core.Serializable;

@XmlRootElement(name = "SqlSyntax", namespace = MyConfiguration.NAMESPACE_BOBAS_DB)
public class SqlSyntax extends Serializable {

	private static final long serialVersionUID = -7615697069051925759L;

	private String identifier;

	private String and;

	private String select;

	private String insert;

	private String update;

	private String delete;

	private String where;
}
