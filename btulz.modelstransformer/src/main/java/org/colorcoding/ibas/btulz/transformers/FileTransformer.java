package org.colorcoding.ibas.btulz.transformers;

import org.colorcoding.ibas.btulz.models.IDomain;

public class FileTransformer extends Transformer implements IFileTransformer {

	private boolean groupingFile;

	@Override
	public boolean isGroupingFile() {
		return this.groupingFile;
	}

	@Override
	public void setGroupingFile(boolean value) {
		this.groupingFile = value;
	}

	protected void clearResults() {
		if (!this.isKeepResults()) {

		}
	}

	@Override
	public void input(String filePath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void input(String[] filePathes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void input(IDomain domain) {
		// TODO Auto-generated method stub

	}

	@Override
	public void transform() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getWorkFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWorkFolder(String foder) {
		// TODO Auto-generated method stub

	}

	@Override
	public IDomain[] getDomainModels() {
		// TODO Auto-generated method stub
		return null;
	}

}
