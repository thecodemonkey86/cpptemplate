package model;

import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;

import config.TemplateConfig;

public class EmptyHtmlAttr extends HtmlAttr{

	public EmptyHtmlAttr(String name) throws IOException {
		super(name, null);
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg,ParserResult mainParserResult) {
		directTextOutputBuffer.append(" ");
		directTextOutputBuffer.append(name) ;
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg,ParserResult mainParserResult) {
		toCpp(out, directTextOutputBuffer, cfg, mainParserResult);
	}
	
	
	public void directRender(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult) throws IOException {
		out.append(name);
	}
	
	
	public void directRenderDoubleEncoded(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult) throws IOException {
		
		out.append(StringEscapeUtils.escapeHtml4(name));
	}
}
