package model;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;

public class CppCodeCheckedAttr extends HtmlAttr {

	public static final String NAME ="checked";
	
	public CppCodeCheckedAttr(String name, AttrValue value) {
		super(name, value);
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
		// TODO Auto-generated method stub
		toCpp(out, directTextOutputBuffer, cfg, mainParserResult);
	}
}
