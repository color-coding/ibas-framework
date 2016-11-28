package org.colorcoding.ibas.bobas.data.measurement;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.KeyText;
import org.colorcoding.ibas.bobas.mapping.DbValue;
import org.colorcoding.ibas.bobas.util.ArrayList;

/**
 * 度量
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Measurement", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public abstract class Measurement<V, U> extends Number implements IMeasurement<V, U>, Comparable<IMeasurement<V, U>> {
    /**
     * 
     */
    private static final long serialVersionUID = 3951630803024284525L;

    transient private PropertyChangeSupport propertyChangeisteners = new PropertyChangeSupport(this);

    @Override
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeisteners.addPropertyChangeListener(listener);
    }

    @Override
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeisteners.removePropertyChangeListener(listener);
    }

    protected final void firePropertyChange(String name, Object oldValue, Object newValue) {
        this.propertyChangeisteners.firePropertyChange(name, oldValue, newValue);
    }

    /**
     * 显示的字符串 值 + 单位
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (this.getUnit() == null) {
            // 无单位
            return String.format("{measurement: %s}", this.getValue());
        } else {
            // 有单位
            KeyText[] characters = this.getUnitCharacters();
            if (characters != null) {
                for (KeyText keyText : characters) {
                    if (keyText.key.equalsIgnoreCase(this.getUnit().toString())) {
                        return String.format("{measurement: %s %s}", this.getValue(), keyText.text);
                    }
                }
            }
        }
        return String.format("{measurement: %s %s}", this.getValue(), this.getUnit());
    }

    /**
     * 单位字符缓存
     */
    protected KeyText[] getUnitCharacters() {
        return null;
    }

    /**
     * 解析单位的显示字符，通过@DbValue注释
     */
    public static KeyText[] parseUnitCharacters(Class<?> type) {
        if (type == null || !type.isEnum()) {
            return null;
        }
        ArrayList<KeyText> keyTexts = new ArrayList<KeyText>();
        for (Field field : type.getDeclaredFields()) {
            Annotation annotation = field.getAnnotation(DbValue.class);
            if (annotation != null) {
                DbValue dbValue = (DbValue) annotation;
                KeyText keyText = new KeyText();
                keyText.key = field.getName();
                keyText.text = dbValue.value();
                keyTexts.add(keyText);
            }
        }
        return keyTexts.toArray(new KeyText[] {});
    }

    /**
     * 默认计算值
     * 
     * @see org.colorcoding.ibas.bobas.data.measurement.IMeasurement# toValue()
     */
    @Override
    public V toValue() {
        return this.getValue();
    }
}
