package model;

import java.io.IOException;
import org.apache.commons.text.StringEscapeUtils;

import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;
import model.debugger.DebuggerVariableList;
import util.ParseUtil;


public class TextAttrValueElement implements IAttrValueElement{
	String chars;
	@Override
	public void directRenderCollectVariables(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult) {
		throw new RuntimeException("not implemented");
		
	}
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
	public void walkTree(TemplateConfig cfg,WalkTreeAction action, ParserResult parserResult) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void directRender(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException {
		out.append(chars);
		
	}

	@Override
	public void directRenderDoubleEncoded(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException {
		out.append(StringEscapeUtils.escapeHtml4(chars));
		
	}

}
