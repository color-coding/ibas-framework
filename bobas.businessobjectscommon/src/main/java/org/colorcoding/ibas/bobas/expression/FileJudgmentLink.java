package org.colorcoding.ibas.bobas.expression;

import java.io.File;

import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 文件判断链
 * 
 * @author Niuren.Zhu
 *
 */

public class FileJudgmentLink extends JudgmentLink {

	/**
	 * 检索条件项目：文件所属文件夹（比较对象是文件夹时是自身）
	 */
	public static final String CRITERIA_CONDITION_ALIAS_FILE_FOLDER = "FileFolder";
	/**
	 * 检索条件项目：文件名称
	 */
	public static final String CRITERIA_CONDITION_ALIAS_FILE_NAME = "FileName";
	/**
	 * 检索条件项目：文件路径（文件夹+名称）
	 */
	public static final String CRITERIA_CONDITION_ALIAS_FILE_PATH = "FilePath";
	/**
	 * 检索条件项目：最后修改时间（文件时间）
	 */
	public static final String CRITERIA_CONDITION_ALIAS_MODIFIED_TIME = "ModifiedTime";
	/**
	 * 检索条件项目：文件夹(Folder)或文件(File)
	 */
	public static final String CRITERIA_CONDITION_ALIAS_FILE_TYPE = "FileType";
	/**
	 * 查询条件值-文件
	 */
	public static final String CRITERIA_CONDITION_VALUE_FILE = "File";
	/**
	 * 查询条件值-文件夹
	 */
	public static final String CRITERIA_CONDITION_VALUE_FOLDER = "Folder";

	private String maskFolder;

	/**
	 * 文件夹比较时的掩码（剔除部分）
	 * 
	 * @return
	 */
	public String getMaskFolder() {
		return maskFolder;
	}

	public void setMaskFolder(String maskFolder) {
		this.maskFolder = maskFolder;
	}

	/**
	 * 创建属性操作者
	 * 
	 * @return
	 */
	public IPropertyValueOperator createPropertyValueOperator() {
		return new IPropertyValueOperator() {

			private File value;

			@Override
			public Object getValue() {
				if (CRITERIA_CONDITION_ALIAS_FILE_NAME.equals(this.getPropertyName())) {
					return value.getName();
				} else if (CRITERIA_CONDITION_ALIAS_FILE_FOLDER.equals(this.getPropertyName())) {
					if (value.isDirectory()) {
						return Files.maskingPath(value, FileJudgmentLink.this.getMaskFolder());
					} else if (value.isFile()) {
						return Files.maskingPath(value.getParentFile(), FileJudgmentLink.this.getMaskFolder());
					}
				} else if (CRITERIA_CONDITION_ALIAS_FILE_PATH.equals(this.getPropertyName())) {
					return Files.maskingPath(value, FileJudgmentLink.this.getMaskFolder());
				} else if (CRITERIA_CONDITION_ALIAS_MODIFIED_TIME.equals(this.getPropertyName())) {
					return value.lastModified();
				} else if (CRITERIA_CONDITION_ALIAS_FILE_TYPE.equals(this.getPropertyName())) {
					if (value.isFile()) {
						return CRITERIA_CONDITION_VALUE_FILE;
					} else if (value.isDirectory()) {
						return CRITERIA_CONDITION_VALUE_FOLDER;
					} else {
						return Strings.VALUE_EMPTY;
					}
				}
				return value;
			}

			@Override
			public void setValue(Object value) {
				if (!(value instanceof File)) {
					throw new ExpressionException(I18N.prop("msg_bobas_invalid_data"));
				}
				this.setValue((File) value);
			}

			public void setValue(File value) {
				this.value = value;
			}

			private String propertyName;

			@Override
			public String getPropertyName() {
				return this.propertyName;
			}

			@Override
			public void setPropertyName(String value) {
				this.propertyName = value;
			}

			@Override
			public Class<?> getValueClass() {
				if (CRITERIA_CONDITION_ALIAS_FILE_NAME.equals(this.getPropertyName())
						|| CRITERIA_CONDITION_ALIAS_FILE_FOLDER.equals(this.getPropertyName())
						|| CRITERIA_CONDITION_ALIAS_FILE_PATH.equals(this.getPropertyName())
						|| CRITERIA_CONDITION_ALIAS_FILE_TYPE.equals(this.getPropertyName())) {
					return String.class;
				} else if (CRITERIA_CONDITION_ALIAS_MODIFIED_TIME.equals(this.getPropertyName())) {
					return Long.class;
				}
				throw new RuntimeException(I18N.prop("msg_bobas_not_support_type_expression", this.getPropertyName()));
			}

			@Override
			public String toString() {
				if (CRITERIA_CONDITION_ALIAS_FILE_NAME.equals(this.getPropertyName())
						|| CRITERIA_CONDITION_ALIAS_FILE_FOLDER.equals(this.getPropertyName())
						|| CRITERIA_CONDITION_ALIAS_FILE_PATH.equals(this.getPropertyName())
						|| CRITERIA_CONDITION_ALIAS_FILE_TYPE.equals(this.getPropertyName())) {
					return String.format("{file's value: %s}", this.getValue());
				} else if (CRITERIA_CONDITION_ALIAS_MODIFIED_TIME.equals(this.getPropertyName())) {
					return String.format("{file's value: %s}", this.getValue());
				}
				return "{file's value: ???}";
			}
		};
	}

	public boolean judge(File file) throws JudmentOperationException {
		return super.judge(file);
	}

	/**
	 * 初始化判断条件
	 * 
	 * @param conditions
	 */
	public void parsingConditions(Iterable<ICondition> conditions) {
		// 判断无条件
		if (conditions == null || conditions.toString().equals("[]")) {
			return;
		}
		ArrayList<JudgmentLinkItem> jLinkItems = new ArrayList<JudgmentLinkItem>();
		for (ICondition item : conditions) {
			JudgmentLinkItem jItem = new JudgmentLinkItem();
			jItem.setOpenBracket(item.getBracketOpen());
			jItem.setCloseBracket(item.getBracketClose());
			if (item.getRelationship() == ConditionRelationship.NONE) {
				jItem.setRelationship(JudmentOperation.AND);
			} else {
				jItem.setRelationship(JudmentOperation.valueOf(item.getRelationship()));
			}
			jItem.setOperation(JudmentOperation.valueOf(item.getOperation()));
			// 左边取值
			IPropertyValueOperator propertyValueOperator = this.createPropertyValueOperator();
			propertyValueOperator.setPropertyName(item.getAlias());
			jItem.setLeftOperter(propertyValueOperator);
			// 右边取值
			IValueOperator ValueOperator = this.createValueOperator();
			ValueOperator.setValue(item.getValue());
			jItem.setRightOperter(ValueOperator);
			jLinkItems.add(jItem);
		}
		if (jLinkItems.isEmpty()) {
			throw new ExpressionException(I18N.prop("msg_bobas_invaild_judgment_link_conditions"));
		}
		super.setJudgmentItems(jLinkItems.toArray(new JudgmentLinkItem[jLinkItems.size()]));
	}

}