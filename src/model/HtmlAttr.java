package model;


import org.apache.commons.text.StringEscapeUtils;

import config.TemplateConfig;
import io.CppOutput;

public class HtmlAttr implements ITemplateItem{

	String name;
	AttrValue value;
	//char valueSeparatorChar;
	
	
	public HtmlAttr(String name, AttrValue value
			//, char valueSeparatorChar
			) {
		super();
		this.name = name;
		this.value = value;
		//this.valueSeparatorChar = valueSeparatorChar;
	}
	
	public String getName() {
		return name;
	}
	
	public AttrValue getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		char valueSeparatorChar = '"';
		StringBuilder sb=new StringBuilder(name);
		sb.append('=');
		sb.append(valueSeparatorChar);
		if (value!=null)
			sb.append(value.toString());
		sb.append(valueSeparatorChar);
		return sb.toString();
	}

//	public char getValueSeparatorChar() {
//		return valueSeparatorChar;
//	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		char valueSeparatorChar = '"';
		
		directTextOutputBuffer.append(" ");
		directTextOutputBuffer.append(name)
			.append('=')
			.append(valueSeparatorChar);
		
		for(IAttrValueElement e: value.getElements()) {
			if (e.stringOutput()) {
				directTextOutputBuffer.append(e.toString() );
			} else {
				CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer,cfg);
				e.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
		directTextOutputBuffer.append(valueSeparatorChar);
		
		
	}

	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		directTextOutputBuffer.append(" ");
		directTextOutputBuffer.append(StringEscapeUtils.escapeHtml4(name) )
			.append('=')
			.append("&quot;");
		 
		for(IAttrValueElement e: value.getElements()) {
			if (e.stringOutput()) {
				directTextOutputBuffer.append(StringEscapeUtils.escapeHtml4(e.toString()) );
			} else {
				CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer,cfg);
				e.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
		directTextOutputBuffer.append("&quot;");
	}
	
	@Override
	public void walkTree(TemplateConfig cfg, WalkTreeAction action, ParserResult parserResult) {
		
	}

	public String getStringValue() {
		StringBuilder sb=new StringBuilder();
		for(IAttrValueElement e: value.getElements()) {
			sb.append(e.toString());
		}
		return sb.toString();
	}

	public void preProcessAttr(HtmlTag tag) {
		
	}
}
