package util;

import codegen.CodeUtil;
import config.TemplateConfig;

public class TemplateCodeUtil {
	public static void writeExpression(StringBuilder out, String expression, TemplateConfig cfg) {
		if(cfg.isRenderToString()) {
			CodeUtil.writeLine(out, String.format("%s += %s;",cfg.getRenderToQStringVariableName(), expression));
		} else {
			CodeUtil.writeLine(out,String.format("FastCgiOutput::write(%s,out);",expression));
		}
	}
	public static void writeString(StringBuilder out, String string, TemplateConfig cfg) {
		if(cfg.isRenderToString()) {
			CodeUtil.writeLine(out, String.format("%s += %s;",cfg.getRenderToQStringVariableName(), Util.getQStringLiteralConstructor(string, false)));
		} else {
			CodeUtil.writeLine(out,String.format("FastCgiOutput::write(%s,out);",Util.getQStringLiteralConstructor(string, false)));
		}
	}
	
	public static void cppDirectRenderAddVariable(StringBuilder out,String var) {
		out.append(String.format("_vars += qMakePair(%s, %s);\n", CodeUtil.quote(var),var));
	}
	
	public static void cppDirectRenderAddVariable(StringBuilder out,String varName, String value) {
		out.append(String.format("_vars += qMakePair(%s, %s);\n", CodeUtil.quote(varName),value));
	}
	
	public static void cppDirectRenderAddVariableBool(StringBuilder out,String var) {
		out.append(String.format("_vars += qMakePair(%s, %s);\n", CodeUtil.quote(var),String.format("%s ? \"1\" : \"0\"",var)));
	}
	
}
