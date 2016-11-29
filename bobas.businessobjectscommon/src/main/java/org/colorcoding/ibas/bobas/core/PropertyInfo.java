package org.colorcoding.ibas.bobas.core;

import java.lang.annotation.Annotation;

import org.colorcoding.ibas.bobas.util.ArrayList;

public class PropertyInfo<P> implements IPropertyInfo<P> {

    public PropertyInfo(String name, Class<P> type) {
        this.setName(name);
        this.setValueType(type);
    }

    private String name = "";

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private int index = -1;

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int value) {
        this.index = value;
    }

    private Class<P> valueType = null;

    public Class<P> getValueType() {
        return this.valueType;
    }

    public void setValueType(Class<P> value) {
        this.valueType = value;
    }

    private P defaultValue;

    public P getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(P value) {
        this.defaultValue = value;
    }

    private ArrayList<Annotation> annotations = null;

    public Annotation[] getAnnotations() {
        if (this.annotations == null) {
            this.annotations = new ArrayList<Annotation>();
        }
        return this.annotations.toArray(new Annotation[] {});
    }

    public Annotation getAnnotation(Class<?> type) {
        if (this.annotations == null) {
            return null;
        }
        for (Annotation annotation : this.annotations) {
            if (annotation.annotationType() == type) {
                return annotation;
            }
        }
        return null;
    }

    public void addAnnotation(Annotation item) {
        if (item == null) {
            return;
        }
        if (this.annotations == null) {
            this.annotations = new ArrayList<Annotation>();
        }
        this.annotations.add(item);
    }

    public void addAnnotation(Annotation[] items) {
        if (items == null) {
            return;
        }
        for (Annotation item : items) {
            this.addAnnotation(item);
        }
    }

    @Override
    public String toString() {
        return String.format("{property info: %s}", this.getName());
    }
}
