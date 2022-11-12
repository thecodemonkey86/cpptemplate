package model;

import io.parser.HtmlParser;
import model.debugger.DebuggerVariableList;


import java.io.IOException;
import codegen.CodeUtil;
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
		CodeUtil.writeLine(out,  "{");
		renderTmpl.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
		CodeUtil.writeLine(out, "}");
	}
	
	public void setRenderTmpl(CppSectionTag renderTmpl) {
		this.renderTmpl = renderTmpl;
	}
	
	@Override
	public void directRender(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException {
		renderTmpl.directRender(out, cfg, mainParserResult, variables);
	}
	@Override
	public void directRenderDoubleEncoded(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException {
		renderTmpl.directRenderDoubleEncoded(out, cfg, mainParserResult, variables);
	}
}
