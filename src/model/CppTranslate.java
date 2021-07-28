package model;

import java.io.IOException;

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
			if(hasAttr("args")) {
				CodeUtil.writeLine(out, String.format("FastCgiOutput::writeHtmlEncodedToBuffer(translations->%s().arg(%s), %s);", getAttrByName("key").getStringValue(),cfg.getRenderToQStringVariableName(),getAttrByName("args").getStringValue()));
			} else {
				CodeUtil.writeLine(out, String.format("FastCgiOutput::writeHtmlEncodedToBuffer(translations->%s(), %s);", getAttrByName("key").getStringValue(),cfg.getRenderToQStringVariableName()));
			}
		} else {
			CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
			if(hasAttr("args")) {
				CodeUtil.writeLine(out, "FastCgiOutput::writeHtmlEncoded"+CodeUtil.parentheses(String.format("translations->%s().arg(%s)", getAttrByName("key").getStringValue(),getAttrByName("args").getStringValue())+", out")+";");
			} else {
				CodeUtil.writeLine(out, "FastCgiOutput::writeHtmlEncoded"+CodeUtil.parentheses("translations->"+ getAttrByName("key").getStringValue()+"(), out")+";");
			}
		}
		
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		toCpp(out, directTextOutputBuffer, cfg, mainParserResult);
	}
	
	@Override
	public void walkTree(TemplateConfig tplCfg, WalkTreeAction action, ParserResult parserResult) throws IOException {
		tplCfg.setIncludeTranslations(true);
		super.walkTree(tplCfg, action, parserResult);
	}

}
