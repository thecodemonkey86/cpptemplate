package model;

import io.CppOutput;
import io.parser.HtmlParser;
import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;

public class CppElseIfTag extends HtmlTag {

	public static final String TAG_NAME = "elseif" ;

	private boolean hasThenTag() {
		boolean result = false;
		for(AbstractNode n : childNodes) {
			if (n instanceof TextNode) {
				continue;
			} else if (n instanceof CppThenTag) {
				result = true;
			} else if (result && !(n instanceof CppElseTag)){
				throw new RuntimeException("syntax error");
			}
		}
		return result;
	}
	
	public CppElseIfTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("else if ").append(CodeUtil.parentheses(getAttrByName("cond").getStringValue()));
	
		if (childNodes != null && childNodes.size() > 0) { 
			if (!hasThenTag()) {
				out.append("{\n");
			}
			for(AbstractNode n:childNodes) {
				n.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		} else {
			out.append("{\n");
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		if (!hasThenTag()) {
			out.append("}\n");
		}
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("else if ").append(CodeUtil.parentheses(getAttrByName("cond").getStringValue()));
	
		if (childNodes != null && childNodes.size() > 0) { 
			if (!hasThenTag()) {
				out.append("{\n");
			}
			for(AbstractNode n:childNodes) {
				n.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		} else {
			out.append("{\n");
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		if (!hasThenTag()) {
			out.append("}\n");
		}
	}
	
	
}
