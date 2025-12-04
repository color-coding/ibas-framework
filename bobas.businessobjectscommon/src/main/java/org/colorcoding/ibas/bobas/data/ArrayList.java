package org.colorcoding.ibas.bobas.data;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Decimals;

/**
 * 列表
 * 
 * @author Niuren.Zhu
 *
 * @param <E> 元素类型
 */
@XmlType(name = "ArrayList", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
@XmlRootElement(name = "ArrayList", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class ArrayList<E> extends java.util.ArrayList<E> implements List<E> {

	private static final long serialVersionUID = 721283937680328856L;

	/**
	 * 创建列表
	 * 
	 * @param <E>    元素类型
	 * @param values 初始值（空值跳过）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> ArrayList<E> create(Object... values) {
		if (values == null) {
			return new ArrayList<>(0);
		}
		ArrayList<E> results = new ArrayList<>(values.length);
		for (Object value : values) {
			if (value == null) {
				continue;
			}
			if (value.getClass().isArray()) {
				int size = Array.getLength(value);
				for (int i = 0; i < size; i++) {
					results.add((E) Array.get(value, i));
				}
			} else if (value instanceof Iterable) {
				Iterable<?> iterable = (Iterable<?>) value;
				for (Object item : iterable) {
					results.add((E) item);
				}
			} else {
				results.add((E) value);
			}
		}
		return results;
	}

	public ArrayList() {
	}

	public ArrayList(Collection<? extends E> c) {
		super(c);
	}

	public ArrayList(int size) {
		super(size);
	}

	@Override
	public final E firstOrDefault() {
		if (!this.isEmpty()) {
			return this.get(0);
		}
		return null;
	}

	@Override
	public final E lastOrDefault() {
		if (!this.isEmpty()) {
			return this.get(this.size() - 1);
		}
		return null;
	}

	@Override
	public E firstOrDefault(E defalutValue) {
		E value = this.firstOrDefault();
		if (value == null) {
			return defalutValue;
		}
		return value;
	}

	@Override
	public E lastOrDefault(E defalutValue) {
		E value = this.lastOrDefault();
		if (value == null) {
			return defalutValue;
		}
		return value;
	}

	@Override
	public final E firstOrDefault(Predicate<? super E> filter) {
		Objects.requireNonNull(filter);
		E item;
		for (int i = 0; i < this.size(); i++) {
			item = this.get(i);
			if (item == null) {
				continue;
			}
			if (filter.test(item)) {
				return item;
			}
		}
		return null;
	}

	@Override
	public final E lastOrDefault(Predicate<? super E> filter) {
		Objects.requireNonNull(filter);
		E item;
		for (int i = this.size() - 1; i >= 0; i--) {
			item = this.get(i);
			if (item == null) {
				continue;
			}
			if (filter.test(item)) {
				return item;
			}
		}
		return null;
	}

	@Override
	public E firstOrDefault(Predicate<? super E> filter, E defalutValue) {
		E value = this.firstOrDefault(filter);
		if (value == null) {
			return defalutValue;
		}
		return value;
	}

	@Override
	public E lastOrDefault(Predicate<? super E> filter, E defalutValue) {
		E value = this.lastOrDefault(filter);
		if (value == null) {
			return defalutValue;
		}
		return value;
	}

	@Override
	public boolean addAll(E[] c) {
		if (c != null) {
			return this.addAll(Arrays.asList(c));
		}
		return false;
	}

	@Override
	public boolean addAll(Iterable<? extends E> c) {
		if (c != null) {
			for (E item : c) {
				this.add(item);
			}
			return true;
		}
		return false;
	}

	@Override
	public List<E> where(Predicate<? super E> filter) {
		Objects.requireNonNull(filter);
		List<E> results = new ArrayList<E>(this.size());
		for (E item : this) {
			if (item == null) {
				continue;
			}
			if (filter.test(item)) {
				results.add(item);
			}
		}
		return results;
	}

	@Override
	public boolean contains(Predicate<? super E> filter) {
		Objects.requireNonNull(filter);
		for (E item : this) {
			if (item == null) {
				continue;
			}
			if (filter.test(item)) {
				return true;
			}
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Number> T sum(Function<E, T> function) {
		Objects.requireNonNull(function);
		T value;
		Class<?> valueType = null;
		BigDecimal total = Decimals.VALUE_ZERO;
		for (E item : this) {
			if (item == null) {
				continue;
			}
			value = function.apply(item);
			if (value == null) {
				continue;
			}
			if (valueType == null) {
				valueType = value.getClass();
			}
			total = Decimals.add(total, DataConvert.convert(BigDecimal.class, value));
		}
		return (T) DataConvert.convert(valueType, total);
	}

	@Override
	public <T extends Comparable<T>> T max(Function<E, T> function) {
		Objects.requireNonNull(function);
		T value = null;
		T maxValue = null;
		for (E item : this) {
			if (item == null) {
				continue;
			}
			value = function.apply(item);
			if (value == null) {
				continue;
			}
			if (maxValue == null) {
				maxValue = value;
			}
			if (value.compareTo(maxValue) > 0) {
				maxValue = value;
			}
		}
		return maxValue;
	}

	@Override
	public <T extends Comparable<T>> T min(Function<E, T> function) {
		Objects.requireNonNull(function);
		T value = null;
		T minValue = null;
		for (E item : this) {
			if (item == null) {
				continue;
			}
			value = function.apply(item);
			if (value == null) {
				continue;
			}
			if (minValue == null) {
				minValue = value;
			}
			if (value.compareTo(minValue) < 0) {
				minValue = value;
			}
		}
		return minValue;
	}
}
