package org.colorcoding.ibas.bobas.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.colorcoding.ibas.bobas.MyConsts;

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

    /**
     * 数值1
     */
    public final static Decimal ONE = new Decimal("1");
    /**
     * 数值0
     */
    public final static Decimal ZERO = new Decimal("0");

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
        return new Decimal(value.divide(ONE, scale, mode));
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
        return new Decimal(value.divide(ONE, scale, RoundingMode.HALF_UP));// 仅保留6位小数
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

    public Decimal add(Decimal augend) {
        return new Decimal(super.add(augend));
    }

    public Decimal add(Decimal augend, MathContext mc) {
        return new Decimal(super.add(augend, mc));
    }

    public Decimal subtract(Decimal subtrahend) {
        return new Decimal(super.subtract(subtrahend));
    }

    public Decimal subtract(Decimal subtrahend, MathContext mc) {
        return new Decimal(super.subtract(subtrahend, mc));
    }

    public Decimal multiply(Decimal multiplicand, MathContext mc) {
        return new Decimal(super.multiply(multiplicand, mc));
    }

    public Decimal divide(Decimal divisor, int scale, int roundingMode) {
        return new Decimal(super.divide(divisor, scale, roundingMode));
    }

    public Decimal divide(Decimal divisor, int scale, RoundingMode roundingMode) {
        return new Decimal(super.divide(divisor, scale, roundingMode));
    }

    public Decimal divide(Decimal divisor, int roundingMode) {
        return new Decimal(super.divide(divisor, roundingMode));
    }

    public Decimal divide(Decimal divisor, RoundingMode roundingMode) {
        return new Decimal(super.divide(divisor, roundingMode));
    }

    public Decimal divide(Decimal divisor, MathContext mc) {
        return new Decimal(super.divide(divisor, mc));
    }

    public Decimal divideToIntegralValue(Decimal divisor) {
        return new Decimal(super.divideToIntegralValue(divisor));
    }

    public Decimal divideToIntegralValue(Decimal divisor, MathContext mc) {
        return new Decimal(super.divideToIntegralValue(divisor, mc));
    }

    public Decimal remainder(Decimal divisor) {
        return new Decimal(super.remainder(divisor));
    }

    public Decimal remainder(Decimal divisor, MathContext mc) {
        return new Decimal(super.remainder(divisor, mc));
    }

    public Decimal[] divideAndRemainder(Decimal divisor) {
        BigDecimal[] bValues = super.divideAndRemainder(divisor);
        Decimal[] values = new Decimal[bValues.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = new Decimal(bValues[i]);
        }
        return values;
    }

    public Decimal[] divideAndRemainder(Decimal divisor, MathContext mc) {
        BigDecimal[] bValues = super.divideAndRemainder(divisor, mc);
        Decimal[] values = new Decimal[bValues.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = new Decimal(bValues[i]);
        }
        return values;
    }

    @Override
    public Decimal pow(int n) {
        return new Decimal(super.pow(n));
    }

    @Override
    public Decimal pow(int n, MathContext mc) {
        return new Decimal(super.pow(n, mc));
    }

    @Override
    public Decimal abs() {
        return new Decimal(super.abs());
    }

    @Override
    public Decimal abs(MathContext mc) {
        return new Decimal(super.abs(mc));
    }

    @Override
    public Decimal negate() {
        return new Decimal(super.negate());
    }

    @Override
    public Decimal negate(MathContext mc) {
        return new Decimal(super.negate(mc));
    }

    @Override
    public Decimal plus() {
        return new Decimal(super.plus());
    }

    @Override
    public Decimal plus(MathContext mc) {
        return new Decimal(super.plus(mc));
    }

    @Override
    public Decimal round(MathContext mc) {
        return new Decimal(super.round(mc));
    }

    @Override
    public Decimal setScale(int newScale, RoundingMode roundingMode) {
        return new Decimal(super.setScale(newScale, roundingMode));
    }

    @Override
    public Decimal setScale(int newScale, int roundingMode) {
        return new Decimal(super.setScale(newScale, roundingMode));
    }

    @Override
    public Decimal setScale(int newScale) {
        return new Decimal(super.setScale(newScale));
    }

    @Override
    public Decimal movePointLeft(int n) {
        return new Decimal(super.movePointLeft(n));
    }

    @Override
    public Decimal movePointRight(int n) {
        return new Decimal(super.movePointRight(n));
    }

    @Override
    public Decimal scaleByPowerOfTen(int n) {
        return new Decimal(super.scaleByPowerOfTen(n));
    }

    @Override
    public Decimal stripTrailingZeros() {
        return new Decimal(super.stripTrailingZeros());
    }

    public Decimal min(Decimal val) {
        return new Decimal(super.min(val));
    }

    public Decimal max(Decimal val) {
        return new Decimal(super.max(val));
    }

    @Override
    public BigDecimal ulp() {
        return new Decimal(super.ulp());
    }

}
