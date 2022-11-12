package model;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;
import model.debugger.DebuggerVariableList;
import util.TemplateCodeUtil;

public class CppDebugTag extends HtmlTag{
	public static final String TAG_NAME = "debug" ;
	
	public CppDebugTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}
	
	@Override
	public void directRenderCollectVariables(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult) {
		CodeUtil.writeLine(out, "#ifdef QT_DEBUG");
		TemplateCodeUtil.cppDirectRenderAddVariable(out, "_QT_DEBUG","1");
		CodeUtil.writeLine(out,"#else");
		TemplateCodeUtil.cppDirectRenderAddVariable(out, "_QT_DEBUG","0");
		CodeUtil.writeLine(out,"#endif");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.directRenderCollectVariables(out, cfg, mainParserResult);
			}
		}
		
	}
	
	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult ) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out, "#ifdef QT_DEBUG");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out,"#endif");
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult ) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out, "#ifdef QT_DEBUG");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out,"#endif");
	}
	
	@Override
	public void directRender(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		if(variables.getBoolAndIncrement("_QT_DEBUG")) {
			if (childNodes != null) { 
				for(AbstractNode n:childNodes) {
					n.directRender(out, cfg, mainParserResult, variables);
				}
			}
		}
	}
	
	@Override
	public void directRenderDoubleEncoded(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		if(variables.getBoolAndIncrement("_QT_DEBUG")) {
			if (childNodes != null) { 
				for(AbstractNode n:childNodes) {
					n.directRenderDoubleEncoded(out, cfg, mainParserResult, variables);
				}
			}
		}
	}
}
