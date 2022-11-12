package model;

import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;

import config.TemplateConfig;
import model.debugger.DebuggerVariableList;

public class DynamicHtmlAttr extends HtmlAttr{

	RawOutputSection code;
	
	public DynamicHtmlAttr(String code) throws IOException {
		super(null, null);
		this.code = new RawOutputSection(code);
	}

	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		directTextOutputBuffer.append(" ");
		code.toCpp(out, directTextOutputBuffer, cfg, mainParserResult);
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		directTextOutputBuffer.append(" ");
		code.toCppDoubleEscaped(out, directTextOutputBuffer, cfg, mainParserResult);
	}
	
	@Override
	public void directRender(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
	  out.append(StringEscapeUtils.escapeHtml4(variables.getStringAndIncrement("_dynamicAttr"))); 
	}
	
	@Override
	public void directRenderDoubleEncoded(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		out.append(StringEscapeUtils.escapeHtml4(StringEscapeUtils.escapeHtml4(variables.getStringAndIncrement("_dynamicAttr")))); 
	}
}
