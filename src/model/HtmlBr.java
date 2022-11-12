package model;

import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;

import config.TemplateConfig;

public class HtmlBr extends HtmlTag{

	public static final String TAG_NAME = "br";
	
	public HtmlBr() {
		super(TAG_NAME);
	}
	
	@Override
	public void addAttr(HtmlAttr a) {
		throw new RuntimeException("br tags don't have attributes");
	}

	@Override
	public String toString() {
		return String.format("<%s>", TAG_NAME
			);
	}
	
	public void directRender(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult) throws IOException {
		out.append(toString());
	}
	
	
	public void directRenderDoubleEncoded(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult) throws IOException {
		
		out.append(StringEscapeUtils.escapeHtml4(toString()));
	}
}
