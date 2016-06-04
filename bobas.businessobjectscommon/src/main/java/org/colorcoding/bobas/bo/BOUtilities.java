package org.colorcoding.bobas.bo;

import org.colorcoding.bobas.core.fields.IFieldData;
import org.colorcoding.bobas.core.fields.IManageFields;
import org.colorcoding.bobas.i18n.i18n;
import org.colorcoding.bobas.util.ArrayList;

/**
 * 业务对象的实用工具
 * 
 * @author Niuren.Zhu
 *
 */
public class BOUtilities {
	/**
	 * 查找BO的差异
	 * 
	 * @param bo01
	 *            数据1（基点）
	 * @param bo02
	 *            数据2
	 * @return 差异内容
	 */
	public static String[] findDiscrepancies(Object bo01, Object bo02) {
		ArrayList<String> difs = new ArrayList<String>();
		try {
			Class<?> b1Type = bo01.getClass();
			Class<?> b2Type = bo02.getClass();
			if (!b1Type.equals(b2Type)) {
				difs.add(i18n.prop("msg_bobas_utilities_different_class", b1Type.getName(), b2Type.getName()));
				return difs.toArray(new String[] {});
			}
			IFieldData[] fields01 = ((IManageFields) bo01).getFields();
			IFieldData[] fields02 = ((IManageFields) bo02).getFields();
			if (fields01.length != fields02.length) {
				difs.add(i18n.prop("msg_bobas_utilities_different_fields_count", b1Type.getName(), b2Type.getName()));
				return difs.toArray(new String[] {});
			}
			// 比较每个字段
			for (int i = 0; i < fields01.length; i++) {
				IFieldData fd01 = fields01[i];
				IFieldData fd02 = fields02[i];
				if (fd01.getValue() instanceof IBusinessObject && fd02.getValue() instanceof IBusinessObject) {
					// 属性是对象
					difs.addAll(findDiscrepancies(fd01.getValue(), fd02.getValue()));
				} else if (fd01.getValue() instanceof IBusinessObjects<?, ?>
						&& fd02.getValue() instanceof IBusinessObjects<?, ?>) {
					// 属性是集合
					IBusinessObjects<?, ?> child01 = (IBusinessObjects<?, ?>) fd01.getValue();
					IBusinessObjects<?, ?> child02 = (IBusinessObjects<?, ?>) fd02.getValue();
					// 集合数量不同
					if (child01.size() != child02.size()) {
						difs.add(i18n.prop("msg_bobas_utilities_different_field_children_count", fd01.getName()));
					}
					// 比较每个集合的元素
					for (int j = 0; j < child01.size(); j++) {
						if (j > child02.size()) {
							// 超过对比项数组
							break;// 退出元素比较
						}
						difs.addAll(findDiscrepancies(child01.get(j), child02.get(j)));
					}
				} else {
					if ((fd01.getValue() == null && fd02.getValue() != null)
							|| (fd01.getValue() != null && fd02.getValue() == null)) {
						// 字段比较不同或无法比较
						difs.add(i18n.prop("msg_bobas_utilities_different_field_value", fd01.getName()));
					} else if (fd01.getValue() != null && fd02.getValue() != null
							&& !fd01.getValue().toString().equals(fd02.getValue().toString())) {
						// 字符串比较不相同
						difs.add(i18n.prop("msg_bobas_utilities_different_field_value", fd01.getName()));
					}
				}
			}
		} catch (Exception e) {
			difs.add(i18n.prop("msg_bobas_utilities_unable_to_complete_task"));
		}
		return difs.toArray(new String[] {});
	}

}
