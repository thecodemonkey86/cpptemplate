package model;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import model.debugger.DebuggerVariableList;
import util.TemplateCodeUtil;

public class CppCodeCheckedAttr extends HtmlAttr {

	public static final String NAME ="checked";
	
	public CppCodeCheckedAttr(String name, AttrValue value) {
		super(name, value);
	}
	
	@Override
	public void directRenderCollectVariables(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult) {
		TemplateCodeUtil.cppDirectRenderAddVariableBool(out, getStringValue());
		
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out, "if("+ getStringValue()+") {");
		directTextOutputBuffer.append(" ").append(NAME);
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out, "}");
		
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		toCpp(out, directTextOutputBuffer, cfg, mainParserResult);
	}
	
	@Override
	public void directRender(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		if(variables.getStringAndIncrement(getStringValue()).equals("1")) {
			out.append(" ").append(NAME);
		}
	}
	
	@Override
	public void directRenderDoubleEncoded(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		directRender(out, cfg, mainParserResult, variables);
	}
}
