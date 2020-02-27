package model;

import org.apache.commons.text.StringEscapeUtils;

import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;
import util.ParseUtil;

public class TextAttrValueElement implements IAttrValueElement{
	String chars;
	
	public TextAttrValueElement(String chars) {
		this.chars = chars;
	}
	
	@Override
	public String toString() {
		return chars;
	}

	@Override
	public boolean stringOutput() {
		return true;
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.addOutputChunksPlainHtml(out, ParseUtil.dropWhitespaces(chars), HtmlParser.getLineWidth(),cfg);
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.addOutputChunksPlainHtml(out, ParseUtil.dropWhitespaces(StringEscapeUtils.escapeHtml4(chars)), HtmlParser.getLineWidth(),cfg);
	}
	@Override
	public void walkTree(WalkTreeAction action, ParserResult parserResult) {
		// TODO Auto-generated method stub
		
	}

}
