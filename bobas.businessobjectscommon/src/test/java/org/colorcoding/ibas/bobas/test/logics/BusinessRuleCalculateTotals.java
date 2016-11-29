package org.colorcoding.ibas.bobas.test.logics;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.rules.BusinessRule;
import org.colorcoding.ibas.bobas.rules.BusinessRuleContext;

/**
 * 业务规则-最小值检查
 * 
 * @author Niuren.Zhu
 *
 * @param <T>
 *            值类型，需要实现Comparable
 */
public class BusinessRuleCalculateTotals extends BusinessRule {

    /**
     * 构造
     * 
     * @param maxValue
     *            最小值
     * @param propertyInfos
     *            要求值的属性数组
     */
    public BusinessRuleCalculateTotals(IPropertyInfo<Decimal> propertyPrice, IPropertyInfo<Decimal> propertyQuantity,
            IPropertyInfo<Decimal> propertyTotal) {
        // 输入字段
        this.propertyPrice = propertyPrice;
        this.getInputProperties().add(this.propertyPrice);
        this.propertyQuantity = propertyQuantity;
        this.getInputProperties().add(this.propertyQuantity);
        // 输出字段
        this.propertyTotal = propertyTotal;
        this.getAffectedProperties().add(this.propertyTotal);
    }

    private IPropertyInfo<Decimal> propertyPrice;
    private IPropertyInfo<Decimal> propertyQuantity;
    private IPropertyInfo<Decimal> propertyTotal;

    @Override
    protected String getName() {
        return "计算总计金额";
    }

    @Override
    protected void execute(BusinessRuleContext context) throws Exception {
        Decimal price = (Decimal) context.getInputPropertyValues().get(this.propertyPrice);
        Decimal quantity = (Decimal) context.getInputPropertyValues().get(this.propertyQuantity);
        Decimal total = price.multiply(quantity);
        // 输出结果
        context.getOutputPropertyValues().put(this.propertyTotal, total);
    }

}
