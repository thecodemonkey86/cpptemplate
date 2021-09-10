package model;

import java.io.IOException;

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
}
