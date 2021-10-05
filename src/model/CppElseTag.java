package model;

import io.CppOutput;
import io.parser.HtmlParser;

import java.io.IOException;

import config.TemplateConfig;

public class CppElseTag extends HtmlTag {

	public static final String TAG_NAME = "else" ;

	
	public CppElseTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}

	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("else {\n");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("}\n");
	}
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("else {\n");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("}\n");
	}

}
