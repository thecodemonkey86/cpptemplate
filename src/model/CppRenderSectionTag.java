package model;

import io.parser.HtmlParser;

import java.io.IOException;

import config.TemplateConfig;

public class CppRenderSectionTag extends HtmlTag {

	public static final String TAG_NAME = "renderSection" ;

	CppSectionTag renderTmpl;
	
	public CppRenderSectionTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		if (this.renderTmpl == null) {
			throw new RuntimeException("illegal state");
		}
		if(hasAttr("args")) {
			renderTmpl.setPassedArgs(getAttrByName("args").getStringValue().split(","));
		}
		renderTmpl.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
	}
	
	public void setRenderTmpl(CppSectionTag renderTmpl) {
		this.renderTmpl = renderTmpl;
	}

}
