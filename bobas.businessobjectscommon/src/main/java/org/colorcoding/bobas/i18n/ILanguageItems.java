package org.colorcoding.bobas.i18n;

public interface ILanguageItems extends Iterable<ILanguageItem> {
	/**
	 * 创建内容实例
	 * 
	 * @return 返回实例
	 */
	ILanguageItem create();

}
