package utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

public final class Satoshi extends Number implements Comparable<Number> {

	private static final long serialVersionUID = 6066539222840486557L;
	
	private static final RoundingMode roundingMode = RoundingMode.HALF_UP;
	private static final MathContext MATH_CONTEXT = new MathContext(8, roundingMode);

	public static final Satoshi NaN = new Satoshi();
	public static final Satoshi ZERO = valueOf(0);
	public static final Satoshi ONE = valueOf(0.00000001);

	private final BigDecimal delegate;

	private Satoshi() {
		delegate = null;
	}

	private Satoshi(String val) {
		delegate = new BigDecimal(val, MATH_CONTEXT).setScale(8, roundingMode);
	}

	private Satoshi(short val) {
		delegate = new BigDecimal(val, MATH_CONTEXT).setScale(8, roundingMode);
	}

	private Satoshi(int val) {
		delegate = BigDecimal.valueOf(val).setScale(8, roundingMode);
	}

	private Satoshi(long val) {
		delegate = BigDecimal.valueOf(val).setScale(8, roundingMode);
	}

	private Satoshi(float val) {
		delegate = new BigDecimal(val, MATH_CONTEXT).setScale(8, roundingMode);
	}

	private Satoshi(double val) {
		delegate = BigDecimal.valueOf(val).setScale(8, roundingMode);
	}

	private Satoshi(BigDecimal val) {
		delegate = Objects.requireNonNull(val).setScale(8, roundingMode);
	}

	public BigDecimal getDelegate() {
		return delegate;
	}

	public Satoshi plus(Number augend) {
		if ((this == NaN) || (augend == NaN)) {
			return NaN;
		}
		return new Satoshi(delegate.add(Satoshi.valueOf(augend).delegate, MATH_CONTEXT));
	}

	public Satoshi minus(Number subtrahend) {
		if ((this == NaN) || (subtrahend == NaN)) {
			return NaN;
		}
		return new Satoshi(delegate.subtract(Satoshi.valueOf(subtrahend).delegate, MATH_CONTEXT));
	}

	public Satoshi multipliedBy(Number multiplicand) {
		if ((this == NaN) || (multiplicand == NaN)) {
			return NaN;
		}
		return new Satoshi(delegate.multiply(Satoshi.valueOf(multiplicand).delegate, MATH_CONTEXT));
	}

	public Satoshi dividedBy(Number divisor) {
		if ((this == NaN) || (divisor == NaN) || Satoshi.valueOf(divisor).isZero()) {
			return NaN;
		}
		return new Satoshi(delegate.divide(Satoshi.valueOf(divisor).delegate, MATH_CONTEXT));
	}

	public boolean isZero() {
		if (this == NaN) {
			return false;
		}
		return compareTo(ZERO) == 0;
	}

	public boolean isNaN() {
		return this == NaN;
	}

	public boolean isEqual(Number other) {
		if ((this == NaN) || (other == NaN)) {
			return false;
		}
		return compareTo(Satoshi.valueOf(other)) == 0;
	}

	public boolean isGreaterThan(Number other) {
		if ((this == NaN) || (other == NaN)) {
			return false;
		}
		return compareTo(Satoshi.valueOf(other)) > 0;
	}

	public boolean isGreaterThanOrEqual(Number other) {
		if ((this == NaN) || (other == NaN)) {
			return false;
		}
		return compareTo(Satoshi.valueOf(other)) > -1;
	}

	public boolean isLessThan(Number other) {
		if ((this == NaN) || (other == NaN)) {
			return false;
		}
		return compareTo(Satoshi.valueOf(other)) < 0;
	}

	public boolean isLessThanOrEqual(Number other) {
		if ((this == NaN) || (other == NaN)) {
			return false;
		}
		return compareTo(Satoshi.valueOf(other)) < 1;
	}

	public int compareTo(Number other) {
		if ((this == NaN) || (other == NaN)) {
			return 0;
		}
		return delegate.compareTo(Satoshi.valueOf(other).delegate);
	}

	public short shortValue() {
		if (this == NaN) {
			return 0;
		}
		return delegate.shortValue();
	}

	public int intValue() {
		if (this == NaN) {
			return 0;
		}
		return delegate.intValue();
	}

	public long longValue() {
		if (this == NaN) {
			return 0;
		}
		return delegate.longValue();
	}

	public float floatValue() {
		if (this == NaN) {
			return Float.NaN;
		}
		return delegate.floatValue();
	}

	public double doubleValue() {
		if (this == NaN) {
			return Double.NaN;
		}
		return delegate.doubleValue();
	}

	public static Satoshi valueOf(String val) {
		return val.equals("NaN") ? Satoshi.NaN : new Satoshi(val);
	}

	public static Satoshi valueOf(short val) {
		return new Satoshi(val);
	}

	public static Satoshi valueOf(int val) {
		return new Satoshi(val);
	}

	public static Satoshi valueOf(long val) {
		return new Satoshi(val);
	}

	public static Satoshi valueOf(float val) {
		if (val == Float.NaN) {
			return Satoshi.NaN;
		}
		return new Satoshi(val);
	}

	public static Satoshi valueOf(BigDecimal val) {
		return new Satoshi(val);
	}

	public static Satoshi valueOf(double val) {
		if (val == Double.NaN) {
			return Satoshi.NaN;
		}
		return new Satoshi(val);
	}

	public static Satoshi valueOf(Satoshi val) {
		return val;
	}

	public static Satoshi valueOf(Number val) {
		return new Satoshi(val.toString());
	}

	@Override
	public String toString() {
		if (this == NaN) {
			return "NaN";
		}
		return delegate.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(delegate);
	}
}