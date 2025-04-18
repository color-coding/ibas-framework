package org.colorcoding.ibas.bobas.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 日期类型
 */
@XmlJavaTypeAdapter(DateTimeSerializer.class)
@XmlType(name = "DateTime", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class DateTime extends Date implements Serializable {

	private static final long serialVersionUID = 9197442988053540497L;

	/**
	 * 日期格式，默认yyyy-MM-dd
	 */
	public static String FORMAT_DATE = "yyyy-MM-dd";
	/**
	 * 时间格式，默HH:mm:ss
	 */
	public static String FORMAT_TIME = "HH:mm:ss";
	/**
	 * 日期时间格式，默认yyyy-MM-dd'T'HH:mm:ss
	 */
	public static String FORMAT_DATETIME = String.format("%s'T'%s", FORMAT_DATE, FORMAT_TIME);

	public DateTime() {
		super();
	}

	public DateTime(long date) {
		super(date);
	}

	public String toString() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this);
		if (calendar.get(Calendar.SECOND) > 0 || calendar.get(Calendar.MINUTE) > 0
				|| calendar.get(Calendar.HOUR_OF_DAY) > 0) {
			return this.toString(FORMAT_DATETIME);
		}
		return this.toString(FORMAT_DATE);
	}

	/**
	 * 输出字符串
	 * 
	 * @param format 字符串格式（yyyy-MM-dd'T'HH:mm:ss）
	 * @return
	 */
	public String toString(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(this);
	}

	public DateTime addDays(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return new DateTime(calendar.getTimeInMillis());
	}

	public DateTime addMonths(int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this);
		calendar.add(Calendar.MONTH, months);
		return new DateTime(calendar.getTimeInMillis());
	}

	public DateTime addYears(int years) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this);
		calendar.add(Calendar.YEAR, years);
		return new DateTime(calendar.getTimeInMillis());
	}
}
