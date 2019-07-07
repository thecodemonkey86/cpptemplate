package model;

import java.io.IOException;

import codegen.CodeUtil;
import util.Util;

public class HtmlMetaTag extends HtmlTag {

	public HtmlMetaTag() throws IOException {
		super("meta");
		setSelfClosing();
	}

	public String toCppConstructor() {
		return "MetaTag"+CodeUtil.parentheses(CodeUtil.commaSep(Util.qStringLiteral(getAttrStringValue("name")),Util.qStringLiteral(getAttrStringValue("content"))));
	}

}
