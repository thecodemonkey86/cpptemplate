package model;

import java.io.IOException;

import config.TemplateConfig;

public class RenderSubtemplateAttrValue implements IAttrValueElement{

	CppRenderSubtemplateTag renderSubtemplate;
	
	public RenderSubtemplateAttrValue(CppRenderSubtemplateTag renderSubtemplate) throws IOException {
		this.renderSubtemplate = renderSubtemplate;
	}

	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		renderSubtemplate.toCppDoubleEscaped(out, directTextOutputBuffer, cfg);
	}
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		toCpp(out, directTextOutputBuffer, cfg);
	}
	@Override
	public void walkTree(WalkTreeAction action, ParserResult parserResult) throws IOException {
	}

	@Override
	public boolean stringOutput() {
		return false;
	}
}
