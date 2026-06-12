package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.file.FileItem;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 文件项目判断链
 * 
 * @author Niuren.Zhu
 *
 */
public class FileItemJudgmentLink extends FileJudgmentLink {

	public boolean judge(FileItem file) throws JudgmentOperationException {
		return super.judge(file);
	}

	/**
	 * 创建属性操作者
	 * 
	 * @return
	 */
	public IPropertyValueOperator createPropertyValueOperator() {
		return new IPropertyValueOperator() {

			private FileItem value;

			@Override
			public Object getValue() {
				if (CRITERIA_CONDITION_ALIAS_FILE_NAME.equals(this.getPropertyName())) {
					return value.getName();
				} else if (CRITERIA_CONDITION_ALIAS_FILE_FOLDER.equals(this.getPropertyName())) {
					if (value.isFolder()) {
						return value.getPath();
					} else if (value.isFile()) {
						return value.getPath().substring(0, value.getPath().lastIndexOf(value.getName()));
					}
				} else if (CRITERIA_CONDITION_ALIAS_FILE_PATH.equals(this.getPropertyName())) {
					return value.getPath();
				} else if (CRITERIA_CONDITION_ALIAS_MODIFIED_TIME.equals(this.getPropertyName())) {
					return value.getModifyTime();
				} else if (CRITERIA_CONDITION_ALIAS_FILE_TYPE.equals(this.getPropertyName())) {
					if (value.isFile()) {
						return CRITERIA_CONDITION_VALUE_FILE;
					} else if (value.isFolder()) {
						return CRITERIA_CONDITION_VALUE_FOLDER;
					} else {
						return Strings.VALUE_EMPTY;
					}
				}
				return value;
			}

			@Override
			public void setValue(Object value) {
				if (!(value instanceof FileItem)) {
					throw new ExpressionException(I18N.prop("msg_bobas_invalid_data"));
				}
				this.setValue((FileItem) value);
			}

			public void setValue(FileItem value) {
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
}
