package org.colorcoding.ibas.bobas.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emTimeUnit;
import org.colorcoding.ibas.bobas.i18n.I18N;

public class DateTimes {

	private DateTimes() {
	}

	/**
	 * 日期格式，默认yyyy-MM-dd
	 */
	public static final String FORMAT_DATE = "yyyy-MM-dd";

	/**
	 * 时间格式，默HH:mm:ss
	 */
	public static final String FORMAT_TIME = "HH:mm:ss";

	/**
	 * 日期时间格式，默认yyyy-MM-dd'T'HH:mm:ss
	 */
	public static final String FORMAT_DATETIME = String.format("%s'T'%s", FORMAT_DATE, FORMAT_TIME);

	/**
	 * 最小日期
	 */
	public static final DateTime VALUE_MIN = valueOf(1900, 1, 1);

	/**
	 * 最大日期
	 */
	public static final DateTime VALUE_MAX = valueOf(2099, 12, 31);

	/**
	 * 类型默认值（VALUE_MIN，1900-01-01）
	 *
	 * @return 最小日期
	 */
	public static DateTime defaultValue() {
		return VALUE_MIN;
	}

	/**
	 * 转换值（时间戳）
	 *
	 * @param value 时间戳（毫秒）
	 * @return 日期
	 */
	public static DateTime valueOf(long value) {
		return new DateTime(value);
	}

	/**
	 * 转换值（Date对象）
	 *
	 * @param value Date对象；null返回VALUE_MIN
	 * @return 日期
	 */
	public static DateTime valueOf(Date value) {
		if (value == null) {
			return VALUE_MIN;
		}
		long time = value.getTime();
		if (VALUE_MIN.getTime() == time) {
			return VALUE_MIN;
		} else if (VALUE_MAX.getTime() == time) {
			return VALUE_MAX;
		}
		return valueOf(time);
	}

	/**
	 * 转换值（字符串，默认日期格式）
	 *
	 * @param value 日期字符串；解析失败返回VALUE_MIN
	 * @return 日期
	 */
	public static DateTime valueOf(String value) {
		return valueOf(value, DateTimes.FORMAT_DATE);
	}

	/**
	 * 转换值（字符串，指定格式）
	 *
	 * @param value  日期字符串；解析失败返回VALUE_MIN
	 * @param format 格式模板
	 * @return 日期
	 */
	public static DateTime valueOf(String value, String format) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			Date dateValue = simpleDateFormat.parse(value);
			return valueOf(dateValue.getTime());
		} catch (ParseException e) {
			return VALUE_MIN;
		}
	}

	/**
	 * 转换值（年月日，月份1-based自动转换）
	 *
	 * @param year  年
	 * @param month 月（1-12，内部自动减1）
	 * @param day   日
	 * @return 日期
	 */
	public static DateTime valueOf(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month - 1, day);
		return valueOf(calendar.getTimeInMillis());
	}

	public static String toString(DateTime value, String format) {
		if (value == null) {
			return null;
		}
		return value.toString(format);
	}

	/**
	 * 当前时间
	 */
	public static DateTime now() {
		return valueOf(Calendar.getInstance().getTimeInMillis());
	}

	/**
	 * 当前时间（天部分）
	 */
	public static DateTime today() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.clear();
		calendar.set(year, month, day, 0, 0, 0);
		return valueOf(calendar.getTimeInMillis());
	}

	/**
	 * 当前时间（时间部分）
	 */
	public static short time() {
		return Short.valueOf(now().toString("HHmm"));
	}

	/**
	 * 计算间隔时间
	 *
	 * @param fromTime 起始时间
	 * @param toTime   截止时间
	 * @param unit     间隔的时间单位（仅支持HOUR/MINUTE/SECOND）
	 *
	 * @return 间隔值；不支持的单位抛出ArithmeticException
	 */
	public static long interval(DateTime fromTime, DateTime toTime, emTimeUnit unit) {
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
		throw new ArithmeticException(I18N.prop("msg_bobas_not_support_the_compute"));
	}

	/**
	 * 是否相等
	 *
	 * @param a 日期a；null返回false
	 * @param b 日期b；null返回false
	 * @return 相等返回true；任一为null返回false
	 */
	public static boolean equals(DateTime a, DateTime b) {
		if (a == null || b == null) {
			return false;
		}
		if (a == b) {
			return true;
		}
		if (a.compareTo(b) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 是否大于(a > b)
	 *
	 * @param a 日期a；null返回false
	 * @param b 日期b；null返回false
	 * @return a大于b返回true
	 */
	public static boolean greaterThan(DateTime a, DateTime b) {
		if (a == null || b == null) {
			return false;
		}
		return a.compareTo(b) > 0;
	}

	/**
	 * 是否大于等于(a >= b)
	 *
	 * @param a 日期a；null返回false
	 * @param b 日期b；null返回false
	 * @return a大于等于b返回true
	 */
	public static boolean greaterEqual(DateTime a, DateTime b) {
		if (a == null || b == null) {
			return false;
		}
		return a.compareTo(b) >= 0;
	}

	/**
	 * 是否小于(a < b)
	 *
	 * @param a 日期a；null返回false
	 * @param b 日期b；null返回false
	 * @return a小于b返回true
	 */
	public static boolean lessThan(DateTime a, DateTime b) {
		if (a == null || b == null) {
			return false;
		}
		return a.compareTo(b) < 0;
	}

	/**
	 * 是否小于等于(a <= b)
	 *
	 * @param a 日期a；null返回false
	 * @param b 日期b；null返回false
	 * @return a小于等于b返回true
	 */
	public static boolean lessEqual(DateTime a, DateTime b) {
		if (a == null || b == null) {
			return false;
		}
		return a.compareTo(b) <= 0;
	}

	/**
	 * 日期加天数（负数表示减少）
	 *
	 * @param value 日期；null返回VALUE_MIN
	 * @param days  天数
	 * @return 新日期
	 */
	public static DateTime addDays(DateTime value, int days) {
		if (value == null) {
			return VALUE_MIN;
		}
		return value.addDays(days);
	}

	/**
	 * 日期加月数（负数表示减少）
	 *
	 * @param value 日期；null返回VALUE_MIN
	 * @param months 月数
	 * @return 新日期
	 */
	public static DateTime addMonths(DateTime value, int months) {
		if (value == null) {
			return VALUE_MIN;
		}
		return value.addMonths(months);
	}

	/**
	 * 日期加年数（负数表示减少）
	 *
	 * @param value 日期；null返回VALUE_MIN
	 * @param years 年数
	 * @return 新日期
	 */
	public static DateTime addYears(DateTime value, int years) {
		if (value == null) {
			return VALUE_MIN;
		}
		return value.addYears(years);
	}

	/**
	 * 日期加小时（负数表示减少）
	 *
	 * @param value  日期；null返回VALUE_MIN
	 * @param hours  小时数
	 * @return 新日期
	 */
	public static DateTime addHours(DateTime value, int hours) {
		if (value == null) {
			return VALUE_MIN;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		return valueOf(calendar.getTimeInMillis());
	}

	/**
	 * 日期加分钟（负数表示减少）
	 *
	 * @param value   日期；null返回VALUE_MIN
	 * @param minutes 分钟数
	 * @return 新日期
	 */
	public static DateTime addMinutes(DateTime value, int minutes) {
		if (value == null) {
			return VALUE_MIN;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		calendar.add(Calendar.MINUTE, minutes);
		return valueOf(calendar.getTimeInMillis());
	}

	/**
	 * 日期加秒数（负数表示减少）
	 *
	 * @param value   日期；null返回VALUE_MIN
	 * @param seconds 秒数
	 * @return 新日期
	 */
	public static DateTime addSeconds(DateTime value, int seconds) {
		if (value == null) {
			return VALUE_MIN;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		calendar.add(Calendar.SECOND, seconds);
		return valueOf(calendar.getTimeInMillis());
	}

	/**
	 * 计算两个日期间隔天数（toTime - fromTime）
	 *
	 * @param fromTime 起始时间；null视为VALUE_MIN
	 * @param toTime   截止时间；null视为VALUE_MIN
	 * @return 间隔天数（不足一天按整天算，如1天23小时=1天）
	 */
	public static long diffDays(DateTime fromTime, DateTime toTime) {
		if (fromTime == null) {
			fromTime = VALUE_MIN;
		}
		if (toTime == null) {
			toTime = VALUE_MIN;
		}
		long diff = toTime.getTime() - fromTime.getTime();
		return diff / (1000 * 60 * 60 * 24);
	}

	/**
	 * 计算两个日期间隔月数（toTime - fromTime）
	 *
	 * @param fromTime 起始时间；null视为VALUE_MIN
	 * @param toTime   截止时间；null视为VALUE_MIN
	 * @return 间隔月数
	 */
	public static int diffMonths(DateTime fromTime, DateTime toTime) {
		if (fromTime == null) {
			fromTime = VALUE_MIN;
		}
		if (toTime == null) {
			toTime = VALUE_MIN;
		}
		Calendar from = Calendar.getInstance();
		from.setTime(fromTime);
		Calendar to = Calendar.getInstance();
		to.setTime(toTime);
		return (to.get(Calendar.YEAR) - from.get(Calendar.YEAR)) * 12
				+ (to.get(Calendar.MONTH) - from.get(Calendar.MONTH));
	}

	/**
	 * 计算两个日期间隔年数（toTime - fromTime）
	 *
	 * @param fromTime 起始时间；null视为VALUE_MIN
	 * @param toTime   截止时间；null视为VALUE_MIN
	 * @return 间隔年数
	 */
	public static int diffYears(DateTime fromTime, DateTime toTime) {
		return diffMonths(fromTime, toTime) / 12;
	}

	/**
	 * 取日期的年份
	 *
	 * @param value 日期；null返回0
	 * @return 年份
	 */
	public static int getYear(DateTime value) {
		if (value == null) {
			return 0;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 取日期的月份（1-12）
	 *
	 * @param value 日期；null返回0
	 * @return 月份（1-based）
	 */
	public static int getMonth(DateTime value) {
		if (value == null) {
			return 0;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 取日期的天数（当月第几天）
	 *
	 * @param value 日期；null返回0
	 * @return 日（1-31）
	 */
	public static int getDay(DateTime value) {
		if (value == null) {
			return 0;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 取日期的小时
	 *
	 * @param value 日期；null返回0
	 * @return 小时（0-23）
	 */
	public static int getHour(DateTime value) {
		if (value == null) {
			return 0;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 取日期的分钟
	 *
	 * @param value 日期；null返回0
	 * @return 分钟（0-59）
	 */
	public static int getMinute(DateTime value) {
		if (value == null) {
			return 0;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		return calendar.get(Calendar.MINUTE);
	}

	/**
	 * 取日期的秒数
	 *
	 * @param value 日期；null返回0
	 * @return 秒（0-59）
	 */
	public static int getSecond(DateTime value) {
		if (value == null) {
			return 0;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		return calendar.get(Calendar.SECOND);
	}

	/**
	 * 取日期是星期几（1=周一，7=周日，ISO标准）
	 *
	 * @param value 日期；null返回0
	 * @return 星期几（1-7）
	 */
	public static int getDayOfWeek(DateTime value) {
		if (value == null) {
			return 0;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		// Calendar: 1=Sunday ... 7=Saturday → ISO: 1=Monday ... 7=Sunday
		return day == Calendar.SUNDAY ? 7 : day - 1;
	}

	/**
	 * 取日期所在月的最后一天
	 *
	 * @param value 日期；null返回VALUE_MIN
	 * @return 当月最后一天的日期（时间部分清零）
	 */
	public static DateTime getEndOfMonth(DateTime value) {
		if (value == null) {
			return VALUE_MIN;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return valueOf(calendar.getTimeInMillis());
	}

	/**
	 * 取日期所在月的第一天
	 *
	 * @param value 日期；null返回VALUE_MIN
	 * @return 当月第一天的日期（时间部分清零）
	 */
	public static DateTime getBeginOfMonth(DateTime value) {
		if (value == null) {
			return VALUE_MIN;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return valueOf(calendar.getTimeInMillis());
	}

	/**
	 * 取日期所在年的第一天
	 *
	 * @param value 日期；null返回VALUE_MIN
	 * @return 当年第一天的日期（时间部分清零）
	 */
	public static DateTime getBeginOfYear(DateTime value) {
		if (value == null) {
			return VALUE_MIN;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return valueOf(calendar.getTimeInMillis());
	}

	/**
	 * 取日期所在年的最后一天
	 *
	 * @param value 日期；null返回VALUE_MIN
	 * @return 当年最后一天的日期（时间部分清零）
	 */
	public static DateTime getEndOfYear(DateTime value) {
		if (value == null) {
			return VALUE_MIN;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return valueOf(calendar.getTimeInMillis());
	}

	/**
	 * 取日期的日期部分（时间清零）
	 *
	 * @param value 日期；null返回VALUE_MIN
	 * @return 仅保留年月日的日期
	 */
	public static DateTime getDateOnly(DateTime value) {
		if (value == null) {
			return VALUE_MIN;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return valueOf(calendar.getTimeInMillis());
	}

	/**
	 * 取最大日期
	 *
	 * @param values 日期数组；null跳过，全null返回VALUE_MIN
	 * @return 最大日期
	 */
	public static DateTime max(DateTime... values) {
		if (values == null || values.length == 0) {
			return VALUE_MIN;
		}
		DateTime result = null;
		for (DateTime value : values) {
			if (value == null) {
				continue;
			}
			if (result == null || value.compareTo(result) > 0) {
				result = value;
			}
		}
		return result != null ? result : VALUE_MIN;
	}

	/**
	 * 取最小日期
	 *
	 * @param values 日期数组；null跳过，全null返回VALUE_MIN
	 * @return 最小日期
	 */
	public static DateTime min(DateTime... values) {
		if (values == null || values.length == 0) {
			return VALUE_MIN;
		}
		DateTime result = null;
		for (DateTime value : values) {
			if (value == null) {
				continue;
			}
			if (result == null || value.compareTo(result) < 0) {
				result = value;
			}
		}
		return result != null ? result : VALUE_MIN;
	}

	/**
	 * 判断是否在范围内（from <= value <= to）
	 *
	 * @param value 日期
	 * @param from  起始日期（含）；null不限制
	 * @param to    截止日期（含）；null不限制
	 * @return 在范围内返回true
	 */
	public static boolean between(DateTime value, DateTime from, DateTime to) {
		if (value == null) {
			return false;
		}
		if (from != null && value.compareTo(from) < 0) {
			return false;
		}
		if (to != null && value.compareTo(to) > 0) {
			return false;
		}
		return true;
	}

}
