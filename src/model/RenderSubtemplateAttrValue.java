package model;

import java.io.IOException;

import config.TemplateConfig;

public class RenderSubtemplateAttrValue implements IAttrValueElement{

	CppRenderSubtemplateTag renderSubtemplate;
	
	public RenderSubtemplateAttrValue(CppRenderSubtemplateTag renderSubtemplate) throws IOException {
		this.renderSubtemplate = renderSubtemplate;
	}

	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		renderSubtemplate.toCppDoubleEscaped(out, directTextOutputBuffer, cfg, mainParserResult);
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
