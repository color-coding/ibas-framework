package org.colorcoding.ibas.bobas.expressions;

import java.io.File;

import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.core.fields.NotSupportTypeException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.util.ArrayList;

/**
 * 文件判断链
 * 
 * @author Niuren.Zhu
 *
 */

public class FileJudgmentLinks extends JudgmentLinks {

    /**
     * 检索条件项目：文件名称。如：ibas.*.jar，条件仅可等于，其他忽略。
     */
    public static final String CRITERIA_CONDITION_ALIAS_FILE_NAME = "FileName";

    /**
     * 检索条件项目：最后修改时间（文件时间）。如：1479965348，条件可等于，大小等于。
     */
    public static final String CRITERIA_CONDITION_ALIAS_MODIFIED_TIME = "ModifiedTime";

    /**
     * 创建属性操作者
     * 
     * @return
     */
    public IPropertyValueOperter createPropertyValueOperter() {
        return new IPropertyValueOperter() {

            private File value;

            @Override
            public Object getValue() {
                if (CRITERIA_CONDITION_ALIAS_FILE_NAME.equals(this.getPropertyName())) {
                    return value.getName();
                } else if (CRITERIA_CONDITION_ALIAS_MODIFIED_TIME.equals(this.getPropertyName())) {
                    return value.lastModified();
                }
                return value;
            }

            @Override
            public void setValue(Object value) {
                if (!(value instanceof File)) {
                    throw new NotSupportTypeException();
                }
                this.setValue((File) value);
            }

            public void setValue(File value) {
                this.value = value;
            }

            private String propertyName;

            @Override
            public String getPropertyName() {
                return this.propertyName;
            }

            @Override
            public void setPropertyName(String value) {
                this.propertyName = value;
            }

            @Override
            public Class<?> getValueClass() {
                if (CRITERIA_CONDITION_ALIAS_FILE_NAME.equals(this.getPropertyName())) {
                    return String.class;
                } else if (CRITERIA_CONDITION_ALIAS_MODIFIED_TIME.equals(this.getPropertyName())) {
                    return Long.class;
                }
                return null;
            }

            @Override
            public String toString() {
                if (CRITERIA_CONDITION_ALIAS_FILE_NAME.equals(this.getPropertyName())) {
                    return String.format("{file's value: %s}", this.getValue());
                } else if (CRITERIA_CONDITION_ALIAS_MODIFIED_TIME.equals(this.getPropertyName())) {
                    return String.format("{file's value: %s}", this.getValue());
                }
                return "{file's value: ???}";
            }
        };
    }

    public boolean judge(File file) throws JudmentOperationException {
        return super.judge(file);
    }

    /**
     * 初始化判断条件
     * 
     * @param conditions
     */
    public void parsingConditions(Iterable<ICondition> conditions) {
        // 判断无条件
        if (conditions != null && conditions.toString().equals("[]")) {
            return;
        }
        ArrayList<JudgmentLinkItem> jLinkItems = new ArrayList<JudgmentLinkItem>();
        for (ICondition item : conditions) {
            JudgmentLinkItem jItem = new JudgmentLinkItem();
            jItem.setOpenBracket(item.getBracketOpenNum());
            jItem.setCloseBracket(item.getBracketCloseNum());
            if (item.getRelationship() == ConditionRelationship.cr_NONE) {
                jItem.setRelationship(JudmentOperations.AND);
            } else {
                jItem.setRelationship(JudmentOperations.valueOf(item.getRelationship()));
            }
            jItem.setOperation(JudmentOperations.valueOf(item.getOperation()));
            // 左边取值
            IPropertyValueOperter propertyValueOperter = this.createPropertyValueOperter();
            propertyValueOperter.setPropertyName(item.getAlias());
            jItem.setLeftOperter(propertyValueOperter);
            // 右边取值
            IValueOperter valueOperter = this.createValueOperter();
            valueOperter.setValue(item.getCondVal());
            jItem.setRightOperter(valueOperter);
            jLinkItems.add(jItem);
        }
        if (jLinkItems.size() == 0) {
            throw new JudgmentLinksException(i18n.prop("msg_bobas_invaild_judgment_link_conditions"));
        }
        super.setJudgmentItems(jLinkItems.toArray(new JudgmentLinkItem[] {}));
    }

}