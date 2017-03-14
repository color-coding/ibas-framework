package org.colorcoding.ibas.bobas.test.commands;

import org.colorcoding.ibas.bobas.commands.Argument;
import org.colorcoding.ibas.bobas.commands.Command;

public class Command4Test extends Command<Command4Test> {

	/**
	 * 命令符
	 */
	public final static String COMMAND_PROMPT = "test";

	@Override
	protected Argument[] createArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isRequiredArguments() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int run(Argument[] arguments) {
		// TODO Auto-generated method stub
		return 0;
	}

}
