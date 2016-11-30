package org.colorcoding.ibas.bobas.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.colorcoding.ibas.bobas.MyConsts;

@XmlType(name = "Decimal", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
@XmlJavaTypeAdapter(DecimalSerializer.class)
public class Decimal extends BigDecimal {

    private static final long serialVersionUID = -3383909107151269118L;
    /**
     * 存储时，保留小数位数
     */
    public static int RESERVED_DECIMAL_PLACES_STORAGE = 6;

    /**
     * 运行时，保留小数位数
     */
    public static int RESERVED_DECIMAL_PLACES_RUNNING = 9;

    /**
     * 运行时，截取小数方式
     */
    public static RoundingMode ROUNDING_MODE_RUNNING = RoundingMode.CEILING;

    private final static String DECIMAL_TEMPLATE = "%s."
            + String.format("%0" + RESERVED_DECIMAL_PLACES_STORAGE + "d", 0);

    /**
     * 数值1
     */
    public final static Decimal ONE = new Decimal(String.format(DECIMAL_TEMPLATE, 1));

    /**
     * 数值0
     */
    public final static Decimal ZERO = new Decimal(String.format(DECIMAL_TEMPLATE, 0));

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
    public static Decimal round(BigDecimal value, int scale, RoundingMode mode) {
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
    public static Decimal round(BigDecimal value, int scale) {
        return new Decimal(value.divide(ONE, scale, RoundingMode.CEILING));
    }

    /**
     * 四舍五入小数位
     * 
     * @param value
     *            值
     * @return
     */
    public static Decimal round(BigDecimal value) {
        return new Decimal(value.divide(ONE, RESERVED_DECIMAL_PLACES_STORAGE, RoundingMode.CEILING));
    }

    public static Decimal valueOf(double val) {
        if (val == 0d) {
            return ZERO;
        } else if (val == 1d) {
            return ONE;
        }
        return new Decimal(val);
    }

    public static Decimal valueOf(long val) {
        if (val == 0l) {
            return ZERO;
        } else if (val == 1l) {
            return ONE;
        }
        return new Decimal(val);
    }

    public static Decimal valueOf(int val) {
        if (val == 0) {
            return ZERO;
        } else if (val == 1) {
            return ONE;
        }
        return new Decimal(val);
    }

    public static Decimal valueOf(String val) {
        if (val != null && !val.isEmpty()) {
            if (val.equals("0")) {
                return ZERO;
            } else if (val.equals("1")) {
                return ONE;
            }
        }
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
        super(val);
        // super(String.format("%."+RESERVED_DECIMAL_PLACES_STORAGE+"f",val));
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
        return new Decimal(super.divide(divisor, RESERVED_DECIMAL_PLACES_RUNNING, ROUNDING_MODE_RUNNING));
    }

    public Decimal divide(String divisor) {
        return this.divide(new Decimal(divisor));
    }

    public Decimal divide(BigInteger divisor) {
        return this.divide(new Decimal(divisor));
    }

    public Decimal divide(double divisor) {
        return this.divide(new Decimal(divisor));
    }

    public Decimal divide(int divisor) {
        return this.divide(new Decimal(divisor));
    }

    public Decimal divide(long divisor) {
        return this.divide(new Decimal(divisor));
    }

    public Decimal multiply(Decimal multiplicand) {
        return new Decimal(super.multiply(multiplicand));
    }

    public Decimal multiply(String multiplicand) {
        return this.multiply(new Decimal(multiplicand));
    }

    public Decimal multiply(BigInteger multiplicand) {
        return this.multiply(new Decimal(multiplicand));
    }

    public Decimal multiply(double multiplicand) {
        return this.multiply(new Decimal(multiplicand));
    }

    public Decimal multiply(int multiplicand) {
        return this.multiply(new Decimal(multiplicand));
    }

    public Decimal multiply(long multiplicand) {
        return this.multiply(new Decimal(multiplicand));
    }

    public Decimal add(Decimal augend) {
        return new Decimal(super.add(augend));
    }

    public Decimal subtract(Decimal subtrahend) {
        return new Decimal(super.subtract(subtrahend));
    }

    public Decimal divide(Decimal divisor, int scale, int roundingMode) {
        return new Decimal(super.divide(divisor, scale, roundingMode));
    }

    public Decimal divide(Decimal divisor, int scale, RoundingMode roundingMode) {
        return this.divide(divisor, scale, roundingMode.ordinal());
    }

    public Decimal divide(Decimal divisor, int roundingMode) {
        return this.divide(divisor, RESERVED_DECIMAL_PLACES_RUNNING, roundingMode);
    }

    public Decimal divide(Decimal divisor, RoundingMode roundingMode) {
        return this.divide(divisor, RESERVED_DECIMAL_PLACES_RUNNING, roundingMode);
    }

    public Decimal divideToIntegralValue(Decimal divisor) {
        return new Decimal(super.divideToIntegralValue(divisor));
    }

    public Decimal remainder(Decimal divisor) {
        return new Decimal(super.remainder(divisor));
    }

    public Decimal[] divideAndRemainder(Decimal divisor) {
        BigDecimal[] bValues = super.divideAndRemainder(divisor);
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
    public Decimal abs() {
        return new Decimal(super.abs());
    }

    @Override
    public Decimal negate() {
        return new Decimal(super.negate());
    }

    @Override
    public Decimal plus() {
        return new Decimal(super.plus());
    }

    public Decimal round() {
        return round(this, RESERVED_DECIMAL_PLACES_STORAGE);
    }

    @Override
    public Decimal setScale(int newScale, int roundingMode) {
        return new Decimal(super.setScale(newScale, roundingMode));
    }

    @Override
    public Decimal setScale(int newScale, RoundingMode roundingMode) {
        return this.setScale(newScale, roundingMode.ordinal());
    }

    @Override
    public Decimal setScale(int newScale) {
        return this.setScale(newScale, ROUNDING_MODE_RUNNING);
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
