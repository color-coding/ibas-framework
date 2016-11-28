package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

/**
 * 操作信息
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "OperationInformation", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "OperationInformation", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public class OperationInformation implements IOperationInformation {
    public OperationInformation() {

    }

    public OperationInformation(String name) {
        this.setName(name);
    }

    public OperationInformation(String name, String contents) {
        this(name);
        this.setContents(contents);
    }

    public OperationInformation(String name, String tag, String contents) {
        this(name);
        this.setTag(tag);
        this.setContents(contents);
    }

    // 名称
    private String name;

    @Override
    @XmlElement(name = "Name")
    public final String getName() {
        return this.name;
    }

    public final void setName(String value) {
        this.name = value;
    }

    // 内容
    private String contents;

    @Override
    @XmlElement(name = "Contents")
    public final String getContents() {
        return this.contents;
    }

    public final void setContents(String value) {
        this.contents = value;
    }

    // 标签
    private String tag;

    @Override
    @XmlElement(name = "Tag")
    public final String getTag() {
        return this.tag;
    }

    public final void setTag(String value) {
        this.tag = value;
    }

    @Override
    public String toString() {
        return String.format("{operation information: %s - %s}", this.getName(), this.getContents());
    }

}
