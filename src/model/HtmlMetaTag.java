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
		StringBuilder sb = new StringBuilder("MetaTag()");
		if(this.hasAttr("name")) {
			sb.append(".setName"+CodeUtil.parentheses(Util.qStringLiteral(getAttrStringValue("name"))));
		}
		sb.append(".setContent"+ CodeUtil.parentheses(Util.qStringLiteral(getAttrStringValue("content"))));
		if(this.hasAttr("http-equiv")) {
			sb.append(".setHttpEquiv"+ CodeUtil.parentheses(Util.qStringLiteral(getAttrStringValue("http-equiv"))));
		}
		return sb.toString();
	}

}
