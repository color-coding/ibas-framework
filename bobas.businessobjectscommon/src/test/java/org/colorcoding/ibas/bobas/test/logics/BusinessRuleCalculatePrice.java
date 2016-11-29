package org.colorcoding.ibas.bobas.test.logics;

import java.math.RoundingMode;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.rules.BusinessRule;
import org.colorcoding.ibas.bobas.rules.BusinessRuleContext;

/**
 * 业务规则-计算价格
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleCalculatePrice extends BusinessRule {

    public BusinessRuleCalculatePrice(IPropertyInfo<Decimal> propertyTotal, IPropertyInfo<Decimal> propertyQuantity,
            IPropertyInfo<Decimal> propertyPrice) {
        // 输出字段
        this.propertyPrice = propertyPrice;
        this.getAffectedProperties().add(this.propertyPrice);
        // 输入字段
        this.propertyQuantity = propertyQuantity;
        this.getInputProperties().add(this.propertyQuantity);
        this.propertyTotal = propertyTotal;
        this.getInputProperties().add(this.propertyTotal);
    }

    private IPropertyInfo<Decimal> propertyPrice;
    private IPropertyInfo<Decimal> propertyQuantity;
    private IPropertyInfo<Decimal> propertyTotal;

    @Override
    protected String getName() {
        return "计算价格";
    }

    @Override
    protected void execute(BusinessRuleContext context) throws Exception {
        Decimal total = (Decimal) context.getInputPropertyValues().get(this.propertyTotal);
        Decimal quantity = (Decimal) context.getInputPropertyValues().get(this.propertyQuantity);
        Decimal price = total.divide(quantity, RoundingMode.CEILING);// 注意四舍五入
        // 输出结果
        context.getOutputPropertyValues().put(this.propertyPrice, price);
    }

}
