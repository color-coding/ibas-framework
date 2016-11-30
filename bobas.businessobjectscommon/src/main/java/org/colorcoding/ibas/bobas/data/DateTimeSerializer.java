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
	public final static String DATETIME_FORMAT_DATA = "yyyy-MM-dd";
	public final static String DATETIME_FORMAT_DATA_TIME = "yyyy-MM-dd'T'HH:mm:ss";
	public final static String DATETIME_FORMAT_DATA_SLASH = "yyyy/MM/dd";
	public final static String DATETIME_FORMAT_DATA_TIME_SLASH = "yyyy/MM/dd'T'HH:mm:ss";

	@Override
	public String marshal(DateTime data) throws Exception {
		if (DateTime.minValue.equals(data) || data == null) {
			// 最小日期返回null
			return null;
		}
		return data.toString(DATETIME_FORMAT_DATA_TIME);
	}

	@Override
	public DateTime unmarshal(String data) throws Exception {
		if (data.indexOf("/") > 0) {
			// “/”分隔的日期
			if (data.indexOf("'T'") > 0) {
				return DateTime.valueOf(data, DATETIME_FORMAT_DATA_TIME_SLASH);
			}
			return DateTime.valueOf(data, DATETIME_FORMAT_DATA_SLASH);
		}
		// 默认分隔符
		if (data.indexOf("'T'") > 0) {
			return DateTime.valueOf(data, DATETIME_FORMAT_DATA_TIME);
		}
		return DateTime.valueOf(data, DATETIME_FORMAT_DATA);
	}

}
