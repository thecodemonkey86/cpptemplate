package model;

import io.CppOutput;
import io.parser.HtmlParser;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;

public class CppSwitchTag extends HtmlTag {

	public static final String TAG_NAME = "switch" ;

	
	public CppSwitchTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg,
			ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("switch ").append(CodeUtil.parentheses(getAttrByName("cond").getStringValue())).append("{\n");
	
		if (childNodes != null && childNodes.size() > 0) { 
			for(AbstractNode n:childNodes) {
				n.toCpp(out,directTextOutputBuffer,cfg,mainParserResult);
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out, "}");
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("switch ").append(CodeUtil.parentheses(getAttrByName("cond").getStringValue())).append("{\n");
	
		if (childNodes != null && childNodes.size() > 0) { 
			for(AbstractNode n:childNodes) {
				n.toCppDoubleEscaped(out,directTextOutputBuffer,cfg,mainParserResult);
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out, "}");
	}

}
