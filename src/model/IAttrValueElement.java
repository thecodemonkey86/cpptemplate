package model;

import java.io.IOException;
import config.TemplateConfig;
import model.debugger.DebuggerVariableList;


public interface IAttrValueElement extends ITemplateItem {
	String toString();
	boolean stringOutput();
	void directRender(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException;
	void directRenderDoubleEncoded(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException;
}
