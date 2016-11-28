package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.mapping.DbFieldType;

public class UserFieldInfo {

    public UserFieldInfo() {

    }

    public UserFieldInfo(String name, DbFieldType type) {
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

    private DbFieldType valueType = null;

    public DbFieldType getValueType() {
        return this.valueType;
    }

    public void setValueType(DbFieldType value) {
        this.valueType = value;
    }

    @Override
    public String toString() {
        return String.format("{user field info: %s %s}", this.getName(), this.getValueType());
    }
}
