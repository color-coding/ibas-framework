package org.colorcoding.ibas.bobas.rule.common;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCollection;

/**
 * 业务规则-集合元素状态
 * 
 * 子项状态完全一致，则使用此状态
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleDocumentStatus extends BusinessRuleCollection {

	protected BusinessRuleDocumentStatus() {
		this.setName(I18N.prop("msg_bobas_business_rule_element_status"));
	}

	/**
	 * 构造方法
	 * 
	 * @param affectedProperty   属性-被影响
	 * @param collectionProperty 属性-集合
	 * @param statusProperty     属性-取值
	 */
	public BusinessRuleDocumentStatus(IPropertyInfo<emDocumentStatus> affectedProperty,
			IPropertyInfo<?> collectionProperty, IPropertyInfo<emDocumentStatus> statusProperty) {
		this();
		this.setCollection(collectionProperty);
		this.setResult(affectedProperty);
		this.setStatus(statusProperty);
		// 要输入的参数
		this.getInputProperties().add(this.getStatus());
		// 结果
		this.getAffectedProperties().add(this.getResult());
	}

	private IPropertyInfo<emDocumentStatus> result;

	public final IPropertyInfo<emDocumentStatus> getResult() {
		return result;
	}

	public final void setResult(IPropertyInfo<emDocumentStatus> result) {
		this.result = result;
	}

	private IPropertyInfo<emDocumentStatus> status;

	public final IPropertyInfo<emDocumentStatus> getStatus() {
		return status;
	}

	public final void setStatus(IPropertyInfo<emDocumentStatus> status) {
		this.status = status;
	}

	@Override
	protected void execute(BusinessRuleContext context) throws Exception {
		emDocumentStatus result = emDocumentStatus.PLANNED;
		Object[] values = context.getInputValues().get(this.getStatus());
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				Object value = values[i];
				if (value instanceof emDocumentStatus) {
					emDocumentStatus tmp = (emDocumentStatus) value;
					if (result == emDocumentStatus.PLANNED) {
						result = tmp;
					} else if (result != tmp) {
						// 退出，并不修改状态
						result = emDocumentStatus.PLANNED;
						break;
					}
				}
			}
		}
		if (result != emDocumentStatus.PLANNED) {
			context.getOutputValues().put(this.getResult(), result);
		}
	}

}
