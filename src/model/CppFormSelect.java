package model;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;

public class CppFormSelect extends HtmlTag{
	public static final String TAG_NAME = "select" ;
	public CppFormSelect() {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}
	
	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		directTextOutputBuffer.append("<");
		directTextOutputBuffer.append(tagName);
		
		if (attrs!=null) {
			for(HtmlAttr a:attrs) {
				if(!a.getName().equals("value") && !a.getName().equals("options")) {
					a.toCpp(out,directTextOutputBuffer,cfg);	
				}
				
			}
		}
		
		directTextOutputBuffer.append(">");
		HtmlAttr value = hasAttr("value") ? getAttrByName("value") : null;
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		if(childNodes != null && !childNodes.isEmpty()) {
			
			if(value != null) {
				for (AbstractNode node : childNodes) {
					try {
						node.walkTree(new WalkTreeAction() {
							
							@Override
							public void currentNode(AbstractNode node, ParserResult parserResult) throws IOException {
								if(node instanceof CppFormSelectOption) {
									CppFormSelectOption opt = (CppFormSelectOption) node;
									opt.setSelectedValueExpression(value.getStringValue());
								}
								
							}
						}, null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			for (AbstractNode node : childNodes) {
				if(node instanceof TextNode) {
					continue;
				}
				node.toCpp(out, directTextOutputBuffer, cfg);
			}
			
		
		} else {
			String var = "_selectkey";
			String varKey =var+".first";
			String varValue =var+".second";
			if(hasAttr("options")) {
				HtmlAttr options = getAttrByName("options");
				out.append("for (const auto & ").append(var+" : "+options.getStringValue()).append(") {\n");
				
				directTextOutputBuffer.append("<option value=\"");
				CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
				CodeUtil.writeLine(out,"FastCgiCout::write("+varKey+ ");");
				
				if(value != null) {
					CodeUtil.writeLine(out,"FastCgiCout::write('\"');");
					out.append("if ").append(CodeUtil.parentheses(value.getStringValue()+" == "+varKey)).append("{\n");
					CodeUtil.writeLine(out,"FastCgiCout::write(\"selected=\\\"selected\\\");");
					CodeUtil.writeLine(out, "}");
					CodeUtil.writeLine(out,"FastCgiCout::write('>');");
				} else {
					CodeUtil.writeLine(out,"FastCgiCout::write(\"\\\">\");");
				}
				
				CodeUtil.writeLine(out,"FastCgiCout::write("+varValue+");");
				CodeUtil.writeLine(out,"FastCgiCout::write(\"</option>\");");
				out.append('\n');
				CodeUtil.writeLine(out, "}");
			}
		}
		
		directTextOutputBuffer.append("</").append(tagName).append('>');
		
		
		
		
	}

}