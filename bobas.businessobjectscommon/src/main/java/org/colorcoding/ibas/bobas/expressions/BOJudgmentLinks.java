package org.colorcoding.ibas.bobas.expressions;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.i18n.i18n;

/**
 * 业务对象的判断链
 * 
 * @author Niuren.Zhu
 *
 */
public class BOJudgmentLinks extends JudgmentLinks {
    /**
     * 创建属性操作者
     * 
     * @return
     */
    public IPropertyValueOperter createPropertyValueOperter() {
        return new IPropertyValueOperter() {
            private IManageFields value;
            private IFieldData field = null;

            private IFieldData getField() {
                if (this.field == null) {
                    this.field = this.value.getField(this.getPropertyName());
                }
                if (this.field == null) {
                    throw new JudgmentLinksException(i18n.prop("msg_bobas_not_found_bo_field", this.getPropertyName()));
                }
                return this.field;
            }

            @Override
            public void setValue(Object value) {
                if (value != null && !(value instanceof IManageFields)) {
                    throw new JudgmentLinksException(i18n.prop("msg_bobas_invaild_bo_type"));
                }
                this.value = (IManageFields) value;
                this.field = null;
            }

            @Override
            public Object getValue() {
                if (this.value == null) {
                    return null;
                }
                return this.getField().getValue();
            }

            @Override
            public Class<?> getValueClass() {
                if (this.value == null) {
                    return null;
                }
                return this.getField().getValueType();
            }

            private String propertyName;

            @Override
            public void setPropertyName(String value) {
                this.propertyName = value;
            }

            @Override
            public String getPropertyName() {
                return this.propertyName;
            }

            @Override
            public String toString() {
                return String.format("{property's value: %s}", this.getPropertyName());
            }
        };
    }

    /**
     * 判断
     * 
     * @param bo
     *            待判断的对象
     * @return true，满足条件；false，不满足
     * @throws JudmentOperationException
     */
    public boolean judge(IBusinessObjectBase bo) throws JudmentOperationException {
        return super.judge(bo);
    }
}
