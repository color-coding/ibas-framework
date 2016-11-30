package org.colorcoding.ibas.bobas.test.logics;

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
        if (Decimal.ZERO.compareTo(total) != 0) {// 等于判断，不可以用equals()
            // 总计，不为0
            Decimal quantity = (Decimal) context.getInputPropertyValues().get(this.propertyQuantity);
            Decimal price = total.divide(quantity);// 注意四舍五入
            // 输出结果
            context.getOutputPropertyValues().put(this.propertyPrice, price);
        } else {
            // 输出结果
            context.getOutputPropertyValues().put(this.propertyPrice, Decimal.ZERO);
        }
    }

}
