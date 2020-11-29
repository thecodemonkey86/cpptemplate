package model;

import codegen.CodeUtil;
import util.Pair;

public class SubtemplateArg extends Pair<String,String> {

	public SubtemplateArg(String type, String name) {
		super(type, name);
	}
	
	public String getType() {
		return value1;
	}
	public String getName() {
		return value2;
	}
	
	@Override
	public String toString() {
		return CodeUtil.sp(getType(),getName());
	}

}
