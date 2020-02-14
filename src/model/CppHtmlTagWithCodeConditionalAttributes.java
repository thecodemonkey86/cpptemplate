package model;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;

public class CppHtmlTagWithCodeConditionalAttributes extends HtmlTag {
	public CppHtmlTagWithCodeConditionalAttributes(String tagName) throws IOException {
		super(tagName);
		setNs(HtmlParser.CPP_NS);
	}

	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		directTextOutputBuffer.append("<");
		directTextOutputBuffer.append(tagName);

		if (attrs != null) {
			/*for (HtmlAttr a : attrs) {
				if(a.getName()!=null) {
					if(a.getName().equals("hidden")) {
						HtmlAttr attrClass;
						if(hasAttr("class")) {
							attrClass = getAttrByName("class");
						} else {
							attrClass=new HtmlAttr("class",new AttrValue());
							addAttr(attrClass);
						}
						try {
							attrClass.getValue().addElement(new RawOutputSection( a.getStringValue()+" ? "+CodeUtil.quote("d-none")) );
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}*/
			for (HtmlAttr a : attrs) {
				if(a.getName()!=null) {
					if(a.getName().equals("disabled")) {
						CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
						CodeUtil.writeLine(out, "if("+ a.getStringValue()+") {");
						directTextOutputBuffer.append(" disabled");
						CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
						CodeUtil.writeLine(out, "}");
					} else {
						a.toCpp(out, directTextOutputBuffer, cfg, mainParserResult);	
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
