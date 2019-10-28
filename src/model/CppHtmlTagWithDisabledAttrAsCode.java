package model;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;

public class CppHtmlTagWithDisabledAttrAsCode extends HtmlTag {
	public CppHtmlTagWithDisabledAttrAsCode(String tagName) throws IOException {
		super(tagName);
		setNs(HtmlParser.CPP_NS);
	}

	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		directTextOutputBuffer.append("<");
		directTextOutputBuffer.append(tagName);

		if (attrs != null) {
			for (HtmlAttr a : attrs) {
				if(a.getName()!=null) {
					if(!a.getName().equals("disabled")) {
						a.toCpp(out, directTextOutputBuffer, cfg, mainParserResult);	
					} else {
						CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
						CodeUtil.writeLine(out, "if("+ a.getStringValue()+") {");
						directTextOutputBuffer.append(" disabled");
						CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
						CodeUtil.writeLine(out, "}");
					}
				}
			}
		}

		directTextOutputBuffer.append(">");
		if (childNodes != null) {
			for (AbstractNode n : childNodes) {
				n.toCpp(out, directTextOutputBuffer, cfg, mainParserResult);
			}
		}

		directTextOutputBuffer.append("</").append(tagName).append('>');
	}
}
