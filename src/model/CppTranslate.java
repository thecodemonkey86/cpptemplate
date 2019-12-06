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
		if(cfg.isRenderToString()) {
			CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
			CodeUtil.writeLine(out, String.format("FastCgiOutput::writeHtmlEncodedToBuffer(translations->%s(), %s);", getAttrByName("key").getStringValue(),cfg.getRenderToQStringVariableName()));
			cfg.setIncludeTranslations(true);
		} else {
			CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
			CodeUtil.writeLine(out, "FastCgiOutput::writeHtmlEncoded"+CodeUtil.parentheses("translations->"+ getAttrByName("key").getStringValue()+"(), out")+";");
			cfg.setIncludeTranslations(true);
		}
		
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		toCpp(out, directTextOutputBuffer, cfg, mainParserResult);
	}

}
