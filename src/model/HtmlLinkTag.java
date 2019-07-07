package model;

import java.io.IOException;

import codegen.CodeUtil;
import util.StringUtil;
import util.Util;

public class HtmlLinkTag extends HtmlTag {

	public HtmlLinkTag() throws IOException {
		super("link");
		setSelfClosing();
	}

	public String toCppConstructor() {
		
		StringBuilder sb = new StringBuilder("LinkTag()");
		for(HtmlAttr a : attrs) {
			sb.append(".set"+StringUtil.ucfirst(a.getName())+CodeUtil.parentheses(Util.qStringLiteral(a.getStringValue())));
		}
		
		
		return sb.toString();
	}

}
