package org.colorcoding.ibas.bobas.i18n;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "LanguageItems", namespace = MyConsts.NAMESPACE_BOBAS_I18N)
@XmlRootElement(name = "LanguageItems", namespace = MyConsts.NAMESPACE_BOBAS_I18N)
public class LanguageItems implements ILanguageItems {

	private List<ILanguageItem> items;

	@XmlElementWrapper(name = "LanguageItemList")
	@XmlElement(name = "LanguageItem", type = LanguageItem.class)
	public List<ILanguageItem> getItems() {
		if (items == null) {
			items = new ArrayList<ILanguageItem>();
		}
		return items;
	}

	@Override
	public Iterator<ILanguageItem> iterator() {
		return getItems().iterator();
	}

	@Override
	public ILanguageItem create() {
		LanguageItem item = new LanguageItem();
		getItems().add(item);
		return item;
	}

}
