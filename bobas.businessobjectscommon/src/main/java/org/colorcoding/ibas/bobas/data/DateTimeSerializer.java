package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 日期类型序列化
 * 
 * @author Niuren.Zhu
 *
 */
class DateTimeSerializer extends XmlAdapter<String, DateTime> {

	/**
	 * 日期格式模板
	 */
	public static String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	@Override
	public String marshal(DateTime arg0) throws Exception {
		if (DateTime.getMinValue().equals(arg0) || arg0 == null) {
			// 最小日期返回null
			return null;
		}
		return arg0.toString(DATETIME_FORMAT);
	}

	@Override
	public DateTime unmarshal(String arg0) throws Exception {
		return DateTime.valueOf(arg0, DATETIME_FORMAT);
	}

}
