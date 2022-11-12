package model;

import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;

import config.TemplateConfig;
import model.debugger.DebuggerVariableList;
import util.ParseUtil;

public class TextNode extends AbstractNode {

	protected String text;
	
	public TextNode(String text) throws IOException {
		if (text.contains("<")) {
			throw new IOException("unexpected char < | " +text);
		} else
		if (text.contains(">")) {
			throw new IOException("unexpected char > | " +text);
		}
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return text;
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		if (text.trim().length() > 0) {
			directTextOutputBuffer.append(ParseUtil.dropWhitespaces(text) );
		} else if (text.length()==1) {
			directTextOutputBuffer.append(" ");
		}
	}
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		if (text.trim().length() > 0) {
			directTextOutputBuffer.append(ParseUtil.dropWhitespaces(StringEscapeUtils.escapeHtml4(text)) );
		} else if (text.length()==1) {
			directTextOutputBuffer.append(" ");
		}
	}
	
	@Override
	public void directRender(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		if (text.trim().length() > 0) {
			out.append(ParseUtil.dropWhitespaces(text) );
		} else if (text.length()==1) {
			out.append(" ");
		}
	}
	
	@Override
	public void directRenderDoubleEncoded(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		if (text.trim().length() > 0) {
			out.append(ParseUtil.dropWhitespaces(StringEscapeUtils.escapeHtml4(text) ));
		} else if (text.length()==1) {
			out.append(" ");
		}
	}

	@Override
	public void directRenderCollectVariables(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult) {
		// TODO Auto-generated method stub
		
	}
}
