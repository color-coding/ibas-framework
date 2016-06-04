package org.colorcoding.bobas.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.colorcoding.bobas.MyConsts;

@XmlType(name = "Decimal", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
@XmlJavaTypeAdapter(DecimalSerializer.class)
public class Decimal extends BigDecimal {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3383909107151269118L;
	/**
	 * 运行时，保留小数位数
	 */
	public static int RESERVED_DECIMAL_PLACES_RUNTIME = 9;
	/**
	 * 存储时，保留小数位数
	 */
	public static int RESERVED_DECIMAL_PLACES_STORAGE = 6;

	final static Decimal DIVISOR_ONE = new Decimal("1");

	/**
	 * 截取小数
	 * 
	 * @param value
	 *            值
	 * @param scale
	 *            保留小数位
	 * @param mode
	 *            截取方式
	 * @return
	 */
	public static Decimal round(Decimal value, int scale, RoundingMode mode) {
		return new Decimal(value.divide(DIVISOR_ONE, scale, mode));
	}

	/**
	 * 四舍五入小数位
	 * 
	 * @param value
	 *            值
	 * @param scale
	 *            保留小数位数
	 * @return
	 */
	public static Decimal round(Decimal value, int scale) {
		return new Decimal(value.divide(DIVISOR_ONE, scale, RoundingMode.HALF_UP));// 仅保留6位小数
	}

	public static Decimal valueOf(double val) {
		return new Decimal(val);
	}

	public static Decimal valueOf(long val) {
		return new Decimal(val);
	}

	public static Decimal valueOf(String val) {
		return new Decimal(val);
	}

	public Decimal(BigDecimal val) {
		super(val.toString());
	}

	public Decimal(String val) {
		super(val);
	}

	public Decimal(BigInteger val) {
		super(val);
	}

	public Decimal(double val) {
		super(String.format("%." + RESERVED_DECIMAL_PLACES_RUNTIME + "f", val));
	}

	public Decimal(int val) {
		super(val);
	}

	public Decimal(long val) {
		super(val);
	}

	@Override
	public Decimal clone() {
		return new Decimal(this);
	}

	public Decimal divide(Decimal divisor) {
		return new Decimal(super.divide(divisor));
	}

	public Decimal divide(String divisor) {
		return new Decimal(super.divide(new Decimal(divisor)));
	}

	public Decimal divide(BigInteger divisor) {
		return new Decimal(super.divide(new Decimal(divisor)));
	}

	public Decimal divide(double divisor) {
		return new Decimal(super.divide(new Decimal(divisor)));
	}

	public Decimal divide(int divisor) {
		return new Decimal(super.divide(new Decimal(divisor)));
	}

	public Decimal divide(long divisor) {
		return new Decimal(super.divide(new Decimal(divisor)));
	}

	public Decimal multiply(Decimal multiplicand) {
		return new Decimal(super.multiply(multiplicand));
	}

	public Decimal multiply(String multiplicand) {
		return new Decimal(super.multiply(new Decimal(multiplicand)));
	}

	public Decimal multiply(BigInteger multiplicand) {
		return new Decimal(super.multiply(new Decimal(multiplicand)));
	}

	public Decimal multiply(double multiplicand) {
		return new Decimal(super.multiply(new Decimal(multiplicand)));
	}

	public Decimal multiply(int multiplicand) {
		return new Decimal(super.multiply(new Decimal(multiplicand)));
	}

	public Decimal multiply(long multiplicand) {
		return new Decimal(super.multiply(new Decimal(multiplicand)));
	}

}
