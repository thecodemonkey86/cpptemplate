package model;

import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;
import util.TemplateCodeUtil;
import util.Util;

public class CppFormSelect extends HtmlTag{
	public static enum ValueType{QString, Bool,Int;

	public String toCppCondition(String selectedValueExpression, HtmlAttr optionValue) {
		if(optionValue.getValue().getElements().size()==1 && optionValue.getValue().getElements().get(0) instanceof QStringHtmlEscapedOutputSection) {
			return selectedValueExpression+" == "+ ((QStringHtmlEscapedOutputSection)optionValue.getValue().getElements().get(0)).getExpression();
		} else {
			switch(this) {
			case QString:
				return selectedValueExpression+" == "+Util.qStringLiteral( optionValue.getStringValue());
			case Bool:
				return optionValue.getStringValue().equals("1") ? selectedValueExpression : "!"+selectedValueExpression;
			case Int:
				return selectedValueExpression+" == "+ optionValue.getStringValue();
			}
		}
		
		
		throw new IllegalArgumentException();
	}} 
	public static final String TAG_NAME = "select" ;
	public CppFormSelect() {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}
	
	@Override
	public void addChildNode(AbstractNode node) {
		if(node instanceof CppFormSelectOption) {
			((CppFormSelectOption)node).setParent(this);
		} else if(node instanceof CppForTag) {
			for(AbstractNode n:node.getChildNodes()) {
				if(n instanceof CppFormSelectOption) {
					((CppFormSelectOption)n).setParent(this);
				}
			}
		}
		super.addChildNode(node);
	}
	
	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		directTextOutputBuffer.append("<");
		directTextOutputBuffer.append(tagName);
		
		if (attrs!=null) {
			for(HtmlAttr a:attrs) {
				if(a.getName()!=null) {
					if(!a.getName().equals("value") && !a.getName().equals("options")) {
						a.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);	
					}
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
						node.walkTree(cfg,new WalkTreeAction() {
							
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
				node.toCpp(out, directTextOutputBuffer, cfg, mainParserResult);
			}
			
		
		} else {
			
			if(hasAttr("options")) {
				String var;
				String varValue;
				String varKey;
				if(hasAttr("valueGetter") && hasAttr("displayGetter")) {
					HtmlAttr valueGetter = getAttrByName("valueGetter");
					HtmlAttr displayGetter = getAttrByName("displayGetter");
					var = "_option";
					varKey =var+"->"+valueGetter.getStringValue();
					varValue =var+"->"+displayGetter.getStringValue();
				} else {
					var = "_selectkey";
					varKey =var+".first";
					varValue =var+".second";
				}
				HtmlAttr options = getAttrByName("options");
				out.append("for (const auto & ").append(var+" : "+options.getStringValue()).append(") {\n");
				
				directTextOutputBuffer.append("<option value=\"");
				CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
				TemplateCodeUtil.writeExpression(out,varKey,cfg);
				
				if(value != null) {
					TemplateCodeUtil.writeExpression(out,"'\"'",cfg);
					out.append("if ").append(CodeUtil.parentheses(value.getStringValue()+" == "+varKey)).append("{\n");
					directTextOutputBuffer.append(" selected=\"selected\"");
					CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
					CodeUtil.writeLine(out, "}");
					TemplateCodeUtil.writeExpression(out,"'>'",cfg);
				} else {
					TemplateCodeUtil.writeExpression(out,"\"\\\">\"",cfg);;
				}
				
				TemplateCodeUtil.writeExpression(out,varValue,cfg);
				TemplateCodeUtil.writeExpression(out,"\"</option>\"",cfg);
				out.append('\n');
				CodeUtil.writeLine(out, "}");
			} 
			
		 
		}
		
		directTextOutputBuffer.append("</").append(tagName).append('>');
		
		
		
		
	}

	
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		directTextOutputBuffer.append(StringEscapeUtils.escapeHtml4("<"));
		directTextOutputBuffer.append(tagName);
		
		if (attrs!=null) {
			for(HtmlAttr a:attrs) {
				if(a.getName()!=null) {
					if(!a.getName().equals("value") && !a.getName().equals("options")) {
						a.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);	
					}
				}
				
				
			}
		}
		
		directTextOutputBuffer.append(StringEscapeUtils.escapeHtml4(">"));
		HtmlAttr value = hasAttr("value") ? getAttrByName("value") : null;
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		if(childNodes != null && !childNodes.isEmpty()) {
			
			if(value != null) {
				for (AbstractNode node : childNodes) {
					try {
						node.walkTree(cfg,new WalkTreeAction() {
							
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
				node.toCppDoubleEscaped(out, directTextOutputBuffer, cfg, mainParserResult);
			}
			
		
		} else {
			
			if(hasAttr("options")) {
				String var;
				String varValue;
				String varKey;
				if(hasAttr("valueGetter") && hasAttr("displayGetter")) {
					HtmlAttr valueGetter = getAttrByName("valueGetter");
					HtmlAttr displayGetter = getAttrByName("displayGetter");
					var = "_option";
					varKey =var+"->"+valueGetter.getStringValue();
					varValue =var+"->"+displayGetter.getStringValue();
				} else {
					var = "_selectkey";
					varKey =var+".first";
					varValue =var+".second";
				}
				HtmlAttr options = getAttrByName("options");
				out.append("for (const auto & ").append(var+" : "+options.getStringValue()).append(") {\n");
				
				directTextOutputBuffer.append( StringEscapeUtils.escapeHtml4("<option value=\""));
				CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
				TemplateCodeUtil.writeExpression(out,varKey,cfg);
				
				if(value != null) {
					TemplateCodeUtil.writeExpression(out,Util.qStringLiteral(StringEscapeUtils.escapeHtml4("\""),true),cfg);
					out.append("if ").append(CodeUtil.parentheses(value.getStringValue()+" == "+varKey)).append("{\n");
					directTextOutputBuffer.append(StringEscapeUtils.escapeHtml4(" selected=\"selected\""));
					CodeUtil.writeLine(out, "}");
					TemplateCodeUtil.writeExpression(out,Util.qStringLiteral(StringEscapeUtils.escapeHtml4(">"),true),cfg);
				} else {
					TemplateCodeUtil.writeExpression(out,Util.qStringLiteral(StringEscapeUtils.escapeHtml4("\">"),true),cfg);;
				}
				
				TemplateCodeUtil.writeExpression(out,varValue,cfg);
				TemplateCodeUtil.writeExpression(out,Util.qStringLiteral(StringEscapeUtils.escapeHtml4("</option>"),true),cfg);
				out.append('\n');
				CodeUtil.writeLine(out, "}");
			} 
			
		 
		}
		
		directTextOutputBuffer.append(StringEscapeUtils.escapeHtml4("</")).append(tagName).append(StringEscapeUtils.escapeHtml4(">"));
		
		
		
		
	}

}
