package org.colorcoding.ibas.bobas.bo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.BusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.core.Serializer;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.organization.IOrganizationManager;
import org.colorcoding.ibas.bobas.rules.BusinessRuleException;
import org.colorcoding.ibas.bobas.rules.BusinessRulesFactory;
import org.colorcoding.ibas.bobas.rules.IBusinessRule;
import org.colorcoding.ibas.bobas.rules.IBusinessRules;
import org.colorcoding.ibas.bobas.rules.IBusinessRulesManager;
import org.colorcoding.ibas.bobas.util.StringBuilder;

/*
 * 业务对象基础类型
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "BusinessObject", namespace = MyConsts.NAMESPACE_BOBAS_BO)
public abstract class BusinessObject<T extends IBusinessObject> extends BusinessObjectBase<T> {
    /**
     * 
     */
    private static final long serialVersionUID = -8485128824221654376L;

    public BusinessObject() {
        super();
        this.initializeRules();
        this.setLoading(true);
        this.initialize();
        this.setLoading(false);
    }

    /**
     * 初始化
     */
    protected void initialize() {
        if (this instanceof IBOUserFields) {
            // 继承了自定义字段，初始化列表
            if (this.userFields == null) {
                this.userFields = new UserFields(this.getClass());
                this.userFields.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        // 触发属性改变事件
                        markDirty();
                        firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getOldValue());
                    }
                });
            }
        }
    }

    /**
     * 获取自身查询条件
     * 
     * @return 自身的查询条件 ,新数据返回null
     */
    @Override
    public ICriteria getCriteria() {
        if (this.isNew()) {
            return null;
        }
        Criteria criteria = new Criteria();
        if (this instanceof IBOStorageTag) {
            IBOStorageTag tagBO = (IBOStorageTag) this;
            criteria.setBusinessObjectCode(tagBO.getObjectCode());
        }
        for (IFieldData item : this.getKeyFields()) {
            ICondition condition = criteria.getConditions().create();
            condition.setAlias(item.getName());
            condition.setCondVal(String.valueOf(item.getValue()));
        }
        return criteria;
    }

    /**
     * 获取识别码
     * 
     * @return
     */
    public String getIdentifiers() {
        String boCode = null;
        StringBuilder stringBuilder = new StringBuilder();
        if (this instanceof IBOStorageTag) {
            IBOStorageTag tagBO = (IBOStorageTag) this;
            boCode = tagBO.getObjectCode();
        }
        if (boCode == null || boCode.isEmpty()) {
            return super.toString();
        }
        stringBuilder.appendFormat("{");
        stringBuilder.append("[");
        stringBuilder.append(boCode);
        stringBuilder.append("]");
        stringBuilder.append(".");
        for (IFieldData item : this.getKeyFields()) {
            if (stringBuilder.length() > boCode.length() + 4) {
                stringBuilder.append("&");
            }
            stringBuilder.append("[");
            stringBuilder.append(item.getName());
            stringBuilder.append(" ");
            stringBuilder.append("=");
            stringBuilder.append(" ");
            stringBuilder.append(DataConvert.toString(item.getValue()));
            stringBuilder.append("]");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return this.getIdentifiers();
    }

    @Override
    public String toString(String type) {
        return Serializer.serializeString(type, this, false);
    }

    /*
     * 创建副本 可重载
     * 
     * @see
     * club.ibas.bobas.businessobjectscommon.core.BusinessObjectBase#clone()
     */
    @Override
    public T clone() {
        T nBO = (T) super.clone();
        if (nBO instanceof BusinessObject<?>) {
            BusinessObject<?> bo = (BusinessObject<?>) nBO;
            bo.resetStatus();
        }
        return nBO;
    }

    /**
     * 重置对象状态
     */
    protected void resetStatus() {
        this.markNew();
        // 重置对象存储标记
        if (this instanceof IBOStorageTag) {
            IBOStorageTag tagBO = (IBOStorageTag) this;
            tagBO.setLogInst(0);
            tagBO.setDataSource(null);
            tagBO.setCreateActionId(null);
            tagBO.setCreateDate(DateTime.minValue);
            tagBO.setCreateTime((short) 0);
            tagBO.setCreateUserSign(IOrganizationManager.UNKNOWN_USER_SIGN);
            tagBO.setUpdateActionId(null);
            tagBO.setUpdateDate(DateTime.minValue);
            tagBO.setUpdateTime((short) 0);
            tagBO.setUpdateUserSign(IOrganizationManager.UNKNOWN_USER_SIGN);
        }
        // 重置引用状态
        if (this instanceof IBOReferenced) {
            IBOReferenced tagBO = (IBOReferenced) this;
            tagBO.setReferenced(emYesNo.No);
        }
        // 重置删除状态
        if (this instanceof IBOTagDeleted) {
            IBOTagDeleted tagBO = (IBOTagDeleted) this;
            tagBO.setDeleted(emYesNo.No);
        }
        // 重置取消状态
        if (this instanceof IBOTagCanceled) {
            IBOTagCanceled tagBO = (IBOTagCanceled) this;
            tagBO.setCanceled(emYesNo.No);
        }
        // 重置对象状态
        if (this instanceof IBODocument) {
            IBODocument docBO = (IBODocument) this;
            docBO.setStatus(emBOStatus.Open);
            docBO.setDocumentStatus(emDocumentStatus.Planned);
            docBO.setDocNum(0);
            docBO.setPeriod(0);
        }
        if (this instanceof IBODocumentLine) {
            IBODocumentLine docLineBO = (IBODocumentLine) this;
            docLineBO.setStatus(emBOStatus.Open);
            docLineBO.setLineStatus(emDocumentStatus.Planned);
        }
        // 重置审批状态
        if (this instanceof IApprovalData) {
            IApprovalData apData = (IApprovalData) this;
            apData.setApprovalStatus(emApprovalStatus.Unaffected);
        }
        // 重置子项状态
        for (IFieldData fieldData : this.getFields()) {
            if (fieldData.getValue() instanceof BusinessObject<?>) {
                // 业务对象
                BusinessObject<?> bo = (BusinessObject<?>) fieldData.getValue();
                bo.resetStatus();
            } else if (fieldData.getValue() instanceof BusinessObjects<?, ?>) {
                // 业务对象集合
                BusinessObjects<?, ?> bos = (BusinessObjects<?, ?>) fieldData.getValue();
                for (IBusinessObject item : bos) {
                    if (item instanceof BusinessObject<?>) {
                        BusinessObject<?> bo = (BusinessObject<?>) item;
                        bo.resetStatus();
                    }
                }
            }
        }
    }

    @Override
    public final IFieldData[] getFields() {
        IFieldData[] boFieldDatas = super.getFields();
        if (!(this instanceof IBOUserFields) || this.userFields == null) {
            return boFieldDatas;
        }
        // 处理自定义字段
        IFieldData[] allFieldDatas = new IFieldData[boFieldDatas.length + this.userFields.size()];
        int i = 0;
        for (IFieldData iFieldData : boFieldDatas) {
            allFieldDatas[i] = iFieldData;
            i++;
        }
        for (IFieldData iFieldData : this.userFields.getFieldDatas()) {
            allFieldDatas[i] = iFieldData;
            i++;
        }
        return allFieldDatas;
    }

    @Override
    public final IFieldData getField(String name) {
        // 重写方法加入自定义字段处理
        if (name == null) {
            return null;
        }
        if (name.startsWith(UserField.USER_FIELD_PREFIX_SIGN) && (this instanceof IBOUserFields)
                && this.userFields != null) {
            for (IUserField userField : userFields) {
                if (userField.getName().equals(name) && userField instanceof UserField) {
                    return ((UserField) userField).getFieldData();
                }
            }
        } else {
            return super.getField(name);
        }
        return null;
    }

    private transient UserFields userFields = null;

    /**
     * 用户自定义字段
     * 
     * @return 自定义字段集合
     */
    public final UserFields getUserFields() {
        return this.userFields;
    }

    @XmlElementWrapper(name = "UserFields")
    @XmlElement(name = "UserField", type = UserFieldProxy.class, required = false)
    private UserFieldProxy[] getUserFieldProxys() {
        if (this.userFields == null) {
            return null;
        }
        return this.userFields.toProxyArray();
    }

    @SuppressWarnings("unused")
    private void setUserFieldProxys(UserFieldProxy[] value) {
        if (this.userFields == null || value == null) {
            return;
        }
        for (IUserField userField : value) {
            RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_USER_SET_FIELD_VALUE, userField.getName(),
                    userField.getValue(), userField.getValueType());
            IUserField has = this.userFields.get(userField.getName());
            if (has != null) {
                has.setValue(((UserFieldProxy) userField).convertValue(has.getValueType()));
            }
        }
    }

    /**
     * 删除数据
     */
    public void delete() {
        if (!this.isNew()) {
            // 非新建状态删除可用
            if (this instanceof IBOTagDeleted) {
                IBOTagDeleted tagBO = (IBOTagDeleted) this;
                if (tagBO.getReferenced() == emYesNo.Yes) {
                    // 被引用的数据，不允许删除
                    tagBO.setDeleted(emYesNo.Yes);
                    return;
                }
            }
            this.markDeleted(true);
        }
    }

    /**
     * 反序列化之前调用
     * 
     * @param parent
     *            所属父项
     */
    protected void beforeUnmarshal(Object parent) {
        this.setLoading(true);
    }

    /**
     * 反序列化之后调用
     * 
     * @param parent
     *            所属父项
     */
    protected void afterUnmarshal(Object parent) {
        this.setLoading(false);
    }

    /**
     * 序列化之前调用
     */
    protected void beforeMarshal() {

    }

    /**
     * 序列化之后调用
     */
    protected void afterMarshal() {

    }

    /**
     * （系统）回掉方法-反序列化之前
     * 
     * @param target
     * @param parent
     */
    final void beforeUnmarshal(Unmarshaller target, Object parent) {
        this.beforeUnmarshal(parent);
    }

    /**
     * （系统）回掉方法-反序列化之后
     * 
     * @param target
     * @param parent
     */
    final void afterUnmarshal(Unmarshaller target, Object parent) {
        this.afterUnmarshal(parent);
    }

    /**
     * （系统）回掉方法-序列化之前
     * 
     * @param target
     * @param parent
     */
    final void beforeMarshal(Marshaller marshaller) {
        this.beforeMarshal();
    }

    /**
     * （系统）回掉方法-序列化之后
     * 
     * @param target
     * @param parent
     */
    final void afterMarshal(Marshaller marshaller) {
        this.afterMarshal();
    }

    /**
     * 注册的业务规则
     */
    protected IBusinessRule[] registerRules() {
        return null;
    }

    /**
     * 初始化业务规则
     * 
     * @throws RuntimeException
     */
    private void initializeRules() throws RuntimeException {
        try {
            IBusinessRulesManager manager = BusinessRulesFactory.createManager();
            IBusinessRules rules = manager.getRules(this.getClass());
            if (rules != null && !rules.isInitialized()) {
                // 未初始化，则进行初始化
                rules.registerRules(this.registerRules());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置属性值之后的回掉方法
     */
    @Override
    protected <P> void afterSetProperty(IPropertyInfo<P> property) {
        // 触发业务逻辑运行
        if (!this.isLoading()) {
            // 读取数据时，不执行业务规则
            try {
                IBusinessRules rules = BusinessRulesFactory.createManager().getRules(this.getClass());
                if (rules != null) {
                    rules.execute(this, property);
                }
            } catch (BusinessRuleException e) {
                // 运行中，仅记录错误，以被调试。
                RuntimeLog.log(MessageLevel.ERROR, RuntimeLog.MSG_RULES_EXECUTING_FAILD, property.getName(),
                        e.getMessage());
            }
        }
    }
}
