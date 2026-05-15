package org.colorcoding.ibas.bobas.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.DateTimes;

/**
 * 日期类型
 */
@XmlJavaTypeAdapter(DateTimeSerializer.class)
@XmlType(name = "DateTime", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class DateTime extends Date {

	private static final long serialVersionUID = 9197442988053540497L;

	/**
	 * 当前时间
	 */
	@Deprecated
	public static DateTime getNow() {
		return DateTimes.now();
	}

	/**
	 * 当天
	 */
	@Deprecated
	public static DateTime getToday() {
		return DateTimes.today();
	}

	/**
	 * 将毫秒值转为日期
	 *
	 * @param value 毫秒值
	 * @return 日期对象
	 */
	public static DateTime valueOf(long value) {
		return DateTimes.valueOf(value);
	}

	/**
	 * 将字符串转为日期
	 *
	 * @param value 日期字符串
	 * @return 日期对象
	 */
	public static DateTime valueOf(String value) {
		return DateTimes.valueOf(value);
	}

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
			return this.toString(DateTimes.FORMAT_DATETIME);
		}
		return this.toString(DateTimes.FORMAT_DATE);
	}

	/**
	 * 输出字符串
	 *
	 * @param format 日期格式（如yyyy-MM-dd'T'HH:mm:ss）
	 * @return 格式化后的日期字符串
	 */
	public String toString(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(this);
	}

	/**
	 * 日期增加天数（负数表示减少）
	 *
	 * @param days 天数
	 * @return 新的日期对象
	 */
	public DateTime addDays(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return new DateTime(calendar.getTimeInMillis());
	}

	/**
	 * 日期增加月数（负数表示减少）
	 *
	 * @param months 月数
	 * @return 新的日期对象
	 */
	public DateTime addMonths(int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this);
		calendar.add(Calendar.MONTH, months);
		return new DateTime(calendar.getTimeInMillis());
	}

	/**
	 * 日期增加年数（负数表示减少）
	 *
	 * @param years 年数
	 * @return 新的日期对象
	 */
	public DateTime addYears(int years) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this);
		calendar.add(Calendar.YEAR, years);
		return new DateTime(calendar.getTimeInMillis());
	}
}
