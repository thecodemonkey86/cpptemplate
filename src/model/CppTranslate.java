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
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out, "FastCgiOutput::writeHtmlEncoded"+CodeUtil.parentheses("translations->"+ getAttrByName("key").getStringValue()+"(), out")+";");
		cfg.setIncludeTranslations(true);
	}

}
