package org.colorcoding.ibas.bobas.rule.common;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-数值小数位舍入
 * 
 * @author Niuren.Zhu
 *
 * @param <T> 值类型，需要实现Comparable
 */
public class BusinessRuleRoundingOff extends BusinessRuleCommon {

	/**
	 * 配置项目-默认小数修正位数
	 */
	public final static String CONFIG_ITEM_DEFAULT_DECIMAL_PLACES_ROUNDING_OFF = "defaultRoundingOff";

	protected BusinessRuleRoundingOff() {
		this.setName(I18N.prop("msg_bobas_business_rule_rounding_off"));
	}

	public BusinessRuleRoundingOff(IPropertyInfo<BigDecimal> rounding, IPropertyInfo<BigDecimal> original,
			IPropertyInfo<emYesNo> enabled) {
		this();
		this.setRounding(rounding);
		this.setOriginal(original);
		this.setEnabled(enabled);
		this.getInputProperties().add(this.getOriginal());
		this.getInputProperties().add(this.getRounding());
		this.getInputProperties().add(this.getEnabled());
		this.getAffectedProperties().add(this.getRounding());
		this.getAffectedProperties().add(this.getOriginal());
	}

	public BusinessRuleRoundingOff(IPropertyInfo<BigDecimal> rounding, IPropertyInfo<BigDecimal> original,
			IPropertyInfo<emYesNo> enabled, int place) {
		this(rounding, original, enabled);
		this.setPlace(place);
	}

	private Integer place;

	public final Integer getPlace() {
		return place;
	}

	public final void setPlace(Integer place) {
		this.place = place;
	}

	private IPropertyInfo<BigDecimal> rounding;

	public final IPropertyInfo<BigDecimal> getRounding() {
		return rounding;
	}

	public final void setRounding(IPropertyInfo<BigDecimal> rounding) {
		this.rounding = rounding;
	}

	private IPropertyInfo<BigDecimal> original;

	public final IPropertyInfo<BigDecimal> getOriginal() {
		return original;
	}

	public final void setOriginal(IPropertyInfo<BigDecimal> original) {
		this.original = original;
	}

	private IPropertyInfo<emYesNo> enabled;

	public final IPropertyInfo<emYesNo> getEnabled() {
		return enabled;
	}

	public final void setEnabled(IPropertyInfo<emYesNo> enabled) {
		this.enabled = enabled;
	}

	@Override
	protected void execute(BusinessRuleContext context) throws Exception {
		boolean enabled = false;
		if (context.getInputValues().get(this.getEnabled()) == emYesNo.YES) {
			enabled = true;
		}
		if (!enabled) {
			return;
		}
		BigDecimal original = (BigDecimal) context.getInputValues().get(this.getOriginal());
		if (original == null) {
			return;
		}
		Integer place = this.getPlace();
		BigDecimal rounding = (BigDecimal) context.getInputValues().get(this.getRounding());
		// 未设置小数取舍位数
		if (place == null) {
			// 参考取舍值位数
			if (rounding != null && !Decimal.isZero(rounding)) {
				// 舍入非0时，使用其舍入位数，即0值个数
				String sValue = rounding.toString();
				int index = sValue.indexOf(".");
				if (index > 0) {
					place = 0;
					sValue = sValue.substring(index + 1);
					for (char item : sValue.toCharArray()) {
						if (item != '0') {
							break;
						}
						place = place + 1;
					}
					if (place == 0) {
						place = MyConfiguration.getConfigValue(CONFIG_ITEM_DEFAULT_DECIMAL_PLACES_ROUNDING_OFF, 2);
					}
					this.setPlace(place);
					// 获取到了进取位数，不在需要输入
					this.getInputProperties().remove(this.getRounding());
				}
			}
		}
		if (place == null) {
			place = MyConfiguration.getConfigValue(CONFIG_ITEM_DEFAULT_DECIMAL_PLACES_ROUNDING_OFF, 2);
		}
		if (place > Decimal.DECIMAL_PLACES_STORAGE) {
			place = Decimal.DECIMAL_PLACES_STORAGE;
		}
		BigDecimal dValue = Decimal.round(original, place);
		if (dValue.compareTo(original) != 0) {
			context.getOutputValues().put(this.getOriginal(), dValue);
			context.getOutputValues().put(this.getRounding(), Decimal.subtract(dValue, original));
		} else {
			context.getOutputValues().put(this.getRounding(), Decimal.ZERO);
		}
	}

}
