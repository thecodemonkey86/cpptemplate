package model;

import io.parser.HtmlParser;

import java.io.IOException;

import config.TemplateConfig;

public class CppCommentTag extends HtmlTag {

	public static final String TAG_NAME = "comment" ;

	
	public CppCommentTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
//		out.append("/*");
//		super.toCpp(out);
//		out.append("*/");
	}
	

}
