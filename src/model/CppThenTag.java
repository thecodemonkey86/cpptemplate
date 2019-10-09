package model;

import io.CppOutput;
import io.parser.HtmlParser;

import java.io.IOException;

import config.TemplateConfig;

public class CppThenTag extends HtmlTag {

	public static final String TAG_NAME = "then" ;

	
	public CppThenTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("{\n");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("}\n");
	}
	

}
