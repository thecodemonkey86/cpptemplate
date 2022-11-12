package model;

import java.io.IOException;
import config.TemplateConfig;
import model.debugger.DebuggerVariableList;


public class RenderTagAsAttrValue implements IAttrValueElement{

	ITemplateItem tag;
	@Override
	public void directRenderCollectVariables(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult) {
		throw new RuntimeException("not implemented");
		
	}
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
	public void walkTree(TemplateConfig cfg,WalkTreeAction action, ParserResult parserResult) throws IOException {
	}

	@Override
	public boolean stringOutput() {
		return false;
	}

	@Override
	public void directRender(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException {
		tag.directRenderDoubleEncoded(out, cfg, mainParserResult, variables);
	}
	
	@Override
	public void directRenderDoubleEncoded(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException {
		directRender(out, cfg, mainParserResult, variables);		
	}
}
