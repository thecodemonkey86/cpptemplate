package model;

import io.parser.HtmlParser;
import model.debugger.DebuggerVariableList;

import java.io.IOException;

import config.TemplateConfig;

public class CppCommentTag extends HtmlTag {

	public static final String TAG_NAME = "comment" ;

	
	public CppCommentTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}
	
	@Override
	public void directRenderCollectVariables(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult) {
		// Do nothing
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
//		out.append("/*");
//		super.toCpp(out);
//		out.append("*/");
	}
	

	@Override
	public void directRender(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
	}
	
	@Override
	public void directRenderDoubleEncoded(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
	}
}
