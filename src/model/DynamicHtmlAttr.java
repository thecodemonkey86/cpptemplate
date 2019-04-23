package model;

import java.io.IOException;

import config.TemplateConfig;

public class DynamicHtmlAttr extends HtmlAttr{

	RawOutputSection code;
	
	public DynamicHtmlAttr(String code) throws IOException {
		super(null, null, '\"');
		this.code = new RawOutputSection(code);
	}

	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		directTextOutputBuffer.append(" ");
		code.toCpp(out, directTextOutputBuffer, cfg);
	}
}