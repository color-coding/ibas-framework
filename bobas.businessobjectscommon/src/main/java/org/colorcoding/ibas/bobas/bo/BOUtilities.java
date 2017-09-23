package org.colorcoding.ibas.bobas.bo;

import java.lang.reflect.Array;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;
import org.colorcoding.ibas.bobas.core.ITrackStatus;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.util.ArrayList;

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
				difs.add(I18N.prop("msg_bobas_utilities_different_class", b1Type.getName(), b2Type.getName()));
				return difs.toArray(new String[] {});
			}
			IFieldData[] fields01 = ((IManageFields) bo01).getFields();
			IFieldData[] fields02 = ((IManageFields) bo02).getFields();
			if (fields01.length != fields02.length) {
				difs.add(I18N.prop("msg_bobas_utilities_different_fields_count", b1Type.getName(), b2Type.getName()));
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
						difs.add(I18N.prop("msg_bobas_utilities_different_field_children_count", fd01.getName()));
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
						difs.add(I18N.prop("msg_bobas_utilities_different_field_value", fd01.getName()));
					} else if (fd01.getValue() != null && fd02.getValue() != null
							&& !fd01.getValue().toString().equals(fd02.getValue().toString())) {
						// 字符串比较不相同
						difs.add(I18N.prop("msg_bobas_utilities_different_field_value", fd01.getName()));
					}
				}
			}
		} catch (Exception e) {
			difs.add(I18N.prop("msg_bobas_utilities_unable_to_complete_task"));
		}
		return difs.toArray(new String[] {});
	}

	/**
	 * 获取业务对象的属性值
	 * 
	 * @param bo
	 *            对象
	 * @param path
	 *            属性路径，例如：Customer;SalesOrderLine.ItemCode
	 * @param index
	 *            数组类属性的索引
	 * @return
	 * @throws BOException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public static Object getPropertyValue(IBusinessObject bo, String path, int... indexs) throws BOException {
		try {
			if (bo instanceof IManageFields) {
				IManageFields boFields = (IManageFields) bo;
				String property = path;
				if (path.indexOf(".") > 1) {
					property = path.split("\\.")[0];
				}
				for (int i = 0; i < boFields.getFields().length; i++) {
					IFieldData fieldData = boFields.getFields()[i];
					if (fieldData.getName().equals(property)) {
						if (path.indexOf(".") > 1) {
							// 路径继续
							String cPath = path.substring(property.length() + 1, path.length());
							int[] cIndex = null;
							if (indexs != null && indexs.length > 0) {
								cIndex = new int[indexs.length - 1];
								// 复制数组未使用部分
								for (int j = 1; j < indexs.length; j++) {
									cIndex[j - 1] = indexs[j];
								}
							}
							IBusinessObjects<?, ?> boValues = (IBusinessObjects<?, ?>) fieldData.getValue();
							return getPropertyValue((IBusinessObject) boValues.get(indexs[0]), cPath, cIndex);
						} else {
							// 路径终止
							return fieldData.getValue();
						}
					}
				}
			}
			return null;
		} catch (Exception e) {
			throw new BOException(I18N.prop("msg_bobas_not_found_property_path_value", path), e);
		}
	}

	/**
	 * 移除业务对象的标记删除数据
	 * 
	 * @param bo
	 */
	public static void removeDeleted(IBusinessObjectBase bo) {
		if (bo instanceof IManageFields) {
			IManageFields boFields = (IManageFields) bo;
			for (IFieldData item : boFields.getFields()) {
				Object data = item.getValue();
				if (data == null) {
					continue;
				}
				if (data instanceof ITrackStatus) {
					// 值是业务对象
					if (((ITrackStatus) data).isDeleted()) {
						item.setValue(null);
					}
				} else if (data instanceof IBusinessObjectListBase<?>) {
					// 值是业务对象列表
					IBusinessObjectListBase<?> boList = (IBusinessObjectListBase<?>) data;
					for (int i = boList.size() - 1; i >= 0; i--) {
						IBusinessObjectBase childItem = boList.get(i);
						if (childItem.isDeleted()) {
							boList.remove(childItem);
						}
					}
				} else if (data.getClass().isArray()) {
					// 值是数组
					int length = Array.getLength(data);
					for (int i = 0; i < length; i++) {
						Object childItem = Array.get(data, i);
						if (childItem instanceof ITrackStatus) {
							if (((ITrackStatus) childItem).isDeleted()) {
								Array.set(data, i, null);
							}
						}
					}
				}
			}
		}
	}
}
