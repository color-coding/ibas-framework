package org.colorcoding.ibas.bobas.data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.measurement.emTimeUnit;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 日期类型
 */
@XmlType(name = "DateTime", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
@XmlJavaTypeAdapter(DateTimeSerializer.class)
public class DateTime extends Date implements Serializable {

	private static final long serialVersionUID = 9197442988053540497L;
	/**
	 * 日期格式，默认yyyy-MM-dd
	 */
	public static String FORMAT_DATE = "yyyy-MM-dd";
	/**
	 * 日期时间格式，默认yyyy-MM-dd'T'HH:mm:ss
	 */
	public static String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";

	/**
	 * 转换值
	 * 
	 * @param date
	 *            日期
	 * @return 日期
	 */
	public static DateTime valueOf(Date date) {
		return new DateTime(date.getTime());
	}

	/**
	 * 转换值
	 * 
	 * @param date
	 *            日期的字符串
	 * @return 日期
	 */
	public static DateTime valueOf(String date) {
		return valueOf(date, FORMAT_DATE);
	}

	/**
	 * 转换值
	 * 
	 * @param date
	 *            日期字符串
	 * @param format
	 *            字符串格式
	 * @return 日期
	 */
	public static DateTime valueOf(String date, String format) {
		try {
			if (date == null || format == null) {
				return DateTime.minValue;
			}
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			Date dateValue = simpleDateFormat.parse(date);
			return new DateTime(dateValue.getTime());
		} catch (ParseException e) {
			return DateTime.minValue;
		}
	}

	/**
	 * 转换值
	 * 
	 * @param date
	 *            日期值
	 * @return 日期
	 */
	public static DateTime valueOf(long date) {
		return new DateTime(date);
	}

	/**
	 * 最小日期
	 */
	public static DateTime getMinValue() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(1899, 11, 1);
		return new DateTime(calendar.getTimeInMillis());
	}

	/**
	 * 最大日期
	 */
	public static DateTime getMaxValue() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(2099, 11, 31);
		return new DateTime(calendar.getTimeInMillis());
	}

	/**
	 * 当前时间
	 */
	public static DateTime getNow() {
		Calendar calendar = Calendar.getInstance();
		return new DateTime(calendar.getTimeInMillis());
	}

	/**
	 * 当天
	 */
	public static DateTime getToday() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.clear();
		calendar.set(year, month, day, 0, 0, 0);
		return new DateTime(calendar.getTimeInMillis());
	}

	/**
	 * 最小日期
	 */
	public static final DateTime minValue = getMinValue();

	/**
	 * 最大日期
	 */
	public static final DateTime maxValue = getMaxValue();

	/**
	 * 计算间隔时间
	 * 
	 * @param fromTime
	 *            起始时间
	 * 
	 * @param unit
	 *            间隔的时间单位
	 * 
	 * @return 返回日期间的间隔
	 */
	public static long interval(DateTime fromTime, emTimeUnit unit) throws ComputeException {
		return interval(fromTime, DateTime.getNow(), unit);
	}

	/**
	 * 计算间隔时间
	 * 
	 * @param fromTime
	 *            起始时间
	 * @param toTime
	 *            截止时间
	 * @param unit
	 *            间隔的时间单位
	 * 
	 * @return 返回日期间的间隔
	 */
	public static long interval(DateTime fromTime, DateTime toTime, emTimeUnit unit) throws ComputeException {
		long interval = toTime.getTime() - fromTime.getTime();
		if (unit == emTimeUnit.HOUR) {
			return interval / (1000 * 60 * 60);
		} else if (unit == emTimeUnit.MINUTE) {
			return interval / (1000 * 60);
		} else if (unit == emTimeUnit.SECOND) {
			return interval / (1000);
		}
		/*
		 * else if (unit == emTimeUnit.day) { return interval / (1000 * 60 * 60 * 24); }
		 */
		throw new ComputeException(I18N.prop("msg_bobas_not_support_the_compute"));
	}

	public DateTime() {
		super();
	}

	public DateTime(long date) {
		super(date);
	}

	@Override
	public String toString() {
		return this.toString(FORMAT_DATE);
	}

	/**
	 * 输出字符串
	 * 
	 * @param format
	 *            字符串格式（yyyy-MM-dd'T'HH:mm:ss）
	 * @return
	 */
	public String toString(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(this);
	}
}
