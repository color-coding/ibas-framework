package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.colorcoding.ibas.bobas.common.DateTimes;

/**
 * 日期类型序列化
 * 
 * @author Niuren.Zhu
 *
 */
class DateTimeSerializer extends XmlAdapter<String, DateTime> {

	public final static String DATETIME_FORMAT_DATA = DateTimes.FORMAT_DATE;
	public final static String DATETIME_FORMAT_DATA_TIME = DateTimes.FORMAT_DATETIME;
	public final static String DATETIME_FORMAT_DATA_SLASH = "yyyy/MM/dd";
	public final static String DATETIME_FORMAT_DATA_TIME_SLASH = "yyyy/MM/dd'T'HH:mm:ss";

	/**
	 * 序列化DateTime为字符串
	 *
	 * @param data 日期值，为null或最小日期时返回null
	 * @return 格式化后的日期时间字符串，或null
	 */
	@Override
	public String marshal(DateTime data) throws Exception {
		if (DateTimes.VALUE_MIN.equals(data) || data == null) {
			return null;
		}
		return data.toString(DATETIME_FORMAT_DATA_TIME);
	}

	/**
	 * 反序列化字符串为DateTime
	 *
	 * @param data 日期字符串，支持"-"或"/"分隔，可选带时间部分"T"
	 * @return 解析后的DateTime
	 */
	@Override
	public DateTime unmarshal(String data) throws Exception {
		if (data.indexOf("/") > 0) {
			// “/”分隔的日期
			if (data.indexOf("T") > 0) {
				return DateTimes.valueOf(data, DATETIME_FORMAT_DATA_TIME_SLASH);
			}
			return DateTimes.valueOf(data, DATETIME_FORMAT_DATA_SLASH);
		}
		// 默认分隔符
		if (data.indexOf("T") > 0) {
			return DateTimes.valueOf(data, DATETIME_FORMAT_DATA_TIME);
		}
		return DateTimes.valueOf(data, DATETIME_FORMAT_DATA);
	}

}
