package model;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;

public class CppTranslate extends HtmlTag {
	public static final String TAG_NAME = "translate" ;
	
	public CppTranslate() {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}
	
	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("output").append(CodeUtil.parentheses("Translation::"+ getAttrByName("key").getStringValue()+"();"));
	
	}

}
