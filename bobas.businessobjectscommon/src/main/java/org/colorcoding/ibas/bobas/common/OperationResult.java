package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.ArrayList;

/**
 * 操作消息结果
 * 
 * @author Niuren.Zhu
 *
 * @param <P> 结果类型
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "OperationResult", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "OperationResult", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class OperationResult<P> extends OperationMessage implements IOperationResult<P> {

	private static final long serialVersionUID = 3450498667313147437L;

	public OperationResult() {
		super();
	}

	public OperationResult(int resultCode, String message) {
		super(resultCode, message);
	}

	public OperationResult(Exception exception) {
		super(exception);
	}

	public OperationResult(IOperationResult<?> result) {
		this();
		this.copy(result);
	}

	private ArrayList<P> resultObjects = null;

	@Override
	@XmlElementWrapper(name = "ResultObjects")
	@XmlElement(name = "ResultObject")
	public final ArrayList<P> getResultObjects() {
		if (this.resultObjects == null) {
			this.resultObjects = new ArrayList<P>();
		}
		return this.resultObjects;
	}

	public final IOperationResult<P> addResultObjects(Iterable<?> values) {
		if (values != null) {
			for (Object value : values) {
				this.addResultObjects(value);
			}
		}
		return this;
	}

	public final IOperationResult<P> addResultObjects(Object[] values) {
		if (values != null) {
			for (Object value : values) {
				this.addResultObjects(value);
			}
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public final IOperationResult<P> addResultObjects(Object value) {
		if (value != null) {
			this.getResultObjects().add((P) value);
		}
		return this;
	}

	private ArrayList<IOperationInformation> informations = null;

	@Override
	@XmlElementWrapper(name = "Informations")
	@XmlElement(name = "Information", type = OperationInformation.class)
	public final ArrayList<IOperationInformation> getInformations() {
		if (this.informations == null) {
			this.informations = new ArrayList<IOperationInformation>();
		}
		return this.informations;
	}

	public final IOperationResult<P> addInformations(String name, String content, String tag) {
		this.getInformations().add(new OperationInformation(name, content, tag));
		return this;
	}

	public final IOperationResult<P> addInformations(String name, String content) {
		this.getInformations().add(new OperationInformation(name, content));
		return this;
	}

	public final IOperationResult<P> addInformations(IOperationInformation value) {
		if (value != null) {
			this.getInformations().add(value);
		}
		return this;
	}

	public final IOperationResult<P> addInformations(Iterable<IOperationInformation> values) {
		if (values != null) {
			for (IOperationInformation value : values) {
				this.addInformations(value);
			}
		}
		return this;
	}

	public final IOperationResult<P> addInformations(IOperationInformation[] values) {
		if (values != null) {
			for (IOperationInformation value : values) {
				this.addInformations(value);
			}
		}
		return this;
	}

	/**
	 * 复制数据
	 * 
	 * @param content 复制内容
	 * @return 当前实例
	 */
	@Override
	public IOperationResult<P> copy(IOperationResult<?> content) {
		if (content != null) {
			super.copy(content);
			this.addResultObjects(content.getResultObjects());
			this.addInformations(content.getInformations());
		}
		return this;
	}

	@Override
	public String toString() {
		return String.format("{operation result: %s - %s}", this.getResultCode(), this.getMessage());
	}
}
