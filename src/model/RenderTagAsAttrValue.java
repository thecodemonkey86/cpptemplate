package model;

import java.io.IOException;

import config.TemplateConfig;

public class RenderTagAsAttrValue implements IAttrValueElement{

	ITemplateItem tag;
	
	public RenderTagAsAttrValue(ITemplateItem renderSubtemplate) throws IOException {
		this.tag = renderSubtemplate;
	}

	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		tag.toCppDoubleEscaped(out, directTextOutputBuffer, cfg, mainParserResult);
	}
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		toCpp(out, directTextOutputBuffer, cfg,mainParserResult);
	}
	@Override
	public void walkTree(WalkTreeAction action, ParserResult parserResult) throws IOException {
	}

	@Override
	public boolean stringOutput() {
		return false;
	}
}
