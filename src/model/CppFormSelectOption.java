package model;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;

public class CppFormSelectOption extends HtmlTag{

	public static final String TAG_NAME = "option" ;
	
	CppFormSelect parent;
	private String selectedValueExpression;
	
	public void setSelectedValueExpression(String selectedValueExpression) {
		this.selectedValueExpression = selectedValueExpression;
	}
	
	public CppFormSelectOption() {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}
	
	public void setParent(CppFormSelect parent) {
		this.parent = parent;
	}
	
	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		
		directTextOutputBuffer.append("<");
		directTextOutputBuffer.append(tagName);
		
		if (attrs!=null) {
			for(HtmlAttr a:attrs) {
				
				a.toCpp(out,directTextOutputBuffer,cfg);
				CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
				if(this.selectedValueExpression != null) {
					HtmlAttr optionValue = getAttrByName("value");
					
					CppFormSelect.ValueType valueType = CppFormSelect.ValueType.QString;
					
					if(parent.hasAttr("valueType")) {
						HtmlAttr optionValueType = parent.getAttrByName("valueType");
						switch (optionValueType.getStringValue()) {
						case "QString":
							valueType = CppFormSelect.ValueType.QString;
							break;
						case "int":
							valueType = CppFormSelect.ValueType.Int;
							break;
						case "bool":
							valueType = CppFormSelect.ValueType.Bool;
							break;
						default:
							break;
						}
					}
					
					out.append("if ").append(CodeUtil.parentheses(valueType.toCppCondition(selectedValueExpression,optionValue))).append(" {\n");
					
					directTextOutputBuffer.append(" selected=\"selected\"");
					CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
					out.append('\n');
					CodeUtil.writeLine(out, "}");
				}
			}
			
		}
		
		directTextOutputBuffer.append(">");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toCpp(out,directTextOutputBuffer,cfg);
			}
		}
		
		if (!isVoidTag(tagName) ) {
			directTextOutputBuffer.append("</").append(tagName).append('>');
		}
	}
}
