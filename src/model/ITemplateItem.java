package model;

import java.io.IOException;

import config.TemplateConfig;

public interface ITemplateItem {
	void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) ;
	void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) ;
	void walkTree(WalkTreeAction action,ParserResult parserResult) throws IOException;
}
