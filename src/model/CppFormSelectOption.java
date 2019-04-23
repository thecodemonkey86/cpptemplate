package model;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;

public class CppFormSelectOption extends HtmlTag{

	public static final String TAG_NAME = "option" ;
	private String selectedValueExpression;
	
	public void setSelectedValueExpression(String selectedValueExpression) {
		this.selectedValueExpression = selectedValueExpression;
	}
	
	public CppFormSelectOption() {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
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
					out.append("if ").append(CodeUtil.parentheses(selectedValueExpression+" == '"+ optionValue.getStringValue()+'\'')).append(" {\n");
					CodeUtil.writeLine(out,"echo ' selected=\"selected\"';");
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
