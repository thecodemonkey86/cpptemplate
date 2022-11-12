package model;

import java.io.IOException;

import config.TemplateConfig;
import model.debugger.DebuggerVariableList;


public interface ITemplateItem {
	void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) ;
	void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) ;
	void walkTree(TemplateConfig cfg,WalkTreeAction action,ParserResult parserResult) throws IOException;
	void directRenderCollectVariables(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult);
	void directRender(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException;
	void directRenderDoubleEncoded(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException;
}
