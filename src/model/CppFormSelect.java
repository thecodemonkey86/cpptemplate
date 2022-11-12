package model;

import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;
import model.debugger.DebuggerVariableList;
import model.debugger.Variable;
import util.TemplateCodeUtil;
import util.Util;

public class CppFormSelect extends HtmlTag{
	public static enum ValueType{QString, Bool,Int;

	public String toCppCondition(String selectedValueExpression, HtmlAttr optionValue) {
		if(optionValue.getValue().getElements().size()==1 && optionValue.getValue().getElements().get(0) instanceof QStringHtmlEscapedOutputSection) {
			if(this == Bool) {
				return ((QStringHtmlEscapedOutputSection)optionValue.getValue().getElements().get(0)).getExpression(); 
			}
			
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
	public void directRenderCollectVariables(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult) {
		if(hasAttr("options")) {
			CodeUtil.writeLine(out, "size_t count=0;");
			
			HtmlAttr value = hasAttr("value") ? getAttrByName("value") : null;

			if(hasAttr("options")) {
				String var;
				String varDisplay;
				String varVal;
				if(hasAttr("valueGetter") && hasAttr("displayGetter")) {
					HtmlAttr valueGetter = getAttrByName("valueGetter");
					HtmlAttr displayGetter = getAttrByName("displayGetter");
					var = "_option";
					varVal =var+"->"+valueGetter.getStringValue();
					varDisplay =var+"->"+displayGetter.getStringValue();
				} else {
					var = "_selectkey";
					varVal =var+".first";
					varDisplay =var+".second";
				}
				HtmlAttr options = getAttrByName("options");
				

				if(value != null) {
					TemplateCodeUtil.cppDirectRenderAddVariable(out, value.getStringValue());
				}
				
				out.append("for (const auto & ").append(var+" : "+options.getStringValue()).append(") {\n");
				TemplateCodeUtil.cppDirectRenderAddVariable(out, "_optionValue"+value.getStringValue(),varVal);
				TemplateCodeUtil.cppDirectRenderAddVariable(out, "_optionDisplay"+value.getStringValue(),varDisplay);
				CodeUtil.writeLine(out, "}");
			} else {
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
					node.directRenderCollectVariables(out, cfg, mainParserResult);
				}
			}
			
			/*String val=variables.getStringAndIncrement("_optionValue"+value.getStringValue());
					out.append("<option");
					if(value != null ) {
						out.append(" value=\"").append(val)
						.append('\"');
						if( Variable.eq(val, variables.getStringAndIncrement(value.getStringValue()))) {
							out.append(" selected=\"selected\"");
						}
					}
					*/
			
		}
		
	}
	
	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		directTextOutputBuffer.append("<");
		directTextOutputBuffer.append(tagName);
		
		if (attrs!=null) {
			for(HtmlAttr a:attrs) {
				if(a.getName()!=null) {
					if(!a.getName().equals("value") && !a.getName().equals("options") && !a.getName().equals("valueType")&& !a.getName().equals("valueGetter")&& !a.getName().equals("displayGetter")) {
						a.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);	
					}
				}
				
				
			}
		}
		
		directTextOutputBuffer.append(">");
		HtmlAttr value = hasAttr("value") ? getAttrByName("value") : null;
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);

		if(hasAttr("options")) {
			String var;
			String varDisplay;
			String varVal;
			if(hasAttr("valueGetter") && hasAttr("displayGetter")) {
				HtmlAttr valueGetter = getAttrByName("valueGetter");
				HtmlAttr displayGetter = getAttrByName("displayGetter");
				var = "_option";
				varVal =var+"->"+valueGetter.getStringValue();
				varDisplay =var+"->"+displayGetter.getStringValue();
			} else {
				var = "_selectkey";
				varVal =var+".first";
				varDisplay =var+".second";
			}
			HtmlAttr options = getAttrByName("options");
			out.append("for (const auto & ").append(var+" : "+options.getStringValue()).append(") {\n");
			
			directTextOutputBuffer.append("<option value=\"");
			CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
			CodeUtil.writeLine(out, CppOutput.getFastCgiOutputMethodHtmlEncoded(varVal,cfg)); 
			
			if(value != null) {
				TemplateCodeUtil.writeExpression(out,"'\"'",cfg);
				out.append("if ").append(CodeUtil.parentheses(value.getStringValue()+" == "+varVal)).append("{\n");
				directTextOutputBuffer.append(" selected=\"selected\"");
				CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
				CodeUtil.writeLine(out, "}");
				TemplateCodeUtil.writeExpression(out,"'>'",cfg);
			} else {
				TemplateCodeUtil.writeExpression(out,"\"\\\">\"",cfg);;
			}
			
			CodeUtil.writeLine(out, CppOutput.getFastCgiOutputMethodHtmlEncoded(varDisplay,cfg)); 
			TemplateCodeUtil.writeExpression(out,"\"</option>\"",cfg);
			out.append('\n');
			CodeUtil.writeLine(out, "}");
		} else {
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
					if(!a.getName().equals("value") && !a.getName().equals("options") && !a.getName().equals("valueType")) {
						a.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);	
					}
				}
				
				
			}
		}
		
		directTextOutputBuffer.append(StringEscapeUtils.escapeHtml4(">"));
		HtmlAttr value = hasAttr("value") ? getAttrByName("value") : null;
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		
		if(hasAttr("options")) {
			String var;
			String varDisplay;
			String varVal;
			if(hasAttr("valueGetter") && hasAttr("displayGetter")) {
				HtmlAttr valueGetter = getAttrByName("valueGetter");
				HtmlAttr displayGetter = getAttrByName("displayGetter");
				var = "_option";
				varVal =var+"->"+valueGetter.getStringValue();
				varDisplay =var+"->"+displayGetter.getStringValue();
			} else {
				var = "_selectkey";
				varVal =var+".first";
				varDisplay =var+".second";
			}
			HtmlAttr options = getAttrByName("options");
			out.append("for (const auto & ").append(var+" : "+options.getStringValue()).append(") {\n");
			
			directTextOutputBuffer.append( StringEscapeUtils.escapeHtml4("<option value=\""));
			CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
			CodeUtil.writeLine(out, CppOutput.getFastCgiOutputMethodHtmlEncoded(varVal,cfg)); 
			
			if(value != null) {
				TemplateCodeUtil.writeExpression(out,CodeUtil.quote(StringEscapeUtils.escapeHtml4("\"")),cfg);
				out.append("if ").append(CodeUtil.parentheses(value.getStringValue()+" == "+varVal)).append("{\n");
				directTextOutputBuffer.append(StringEscapeUtils.escapeHtml4(" selected=\"selected\""));
				CodeUtil.writeLine(out, "}");
				TemplateCodeUtil.writeExpression(out,CodeUtil.quote(StringEscapeUtils.escapeHtml4(">")),cfg);
			} else {
				TemplateCodeUtil.writeExpression(out,CodeUtil.quote(StringEscapeUtils.escapeHtml4("\">")),cfg);;
			}
			
			CodeUtil.writeLine(out, CppOutput.getFastCgiOutputMethodHtmlEncoded(varDisplay,cfg)); 
			TemplateCodeUtil.writeExpression(out,CodeUtil.quote(StringEscapeUtils.escapeHtml4("</option>")),cfg);
			out.append('\n');
			CodeUtil.writeLine(out, "}");
		} else {
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
			
		}
		
		directTextOutputBuffer.append(StringEscapeUtils.escapeHtml4("</")).append(tagName).append(StringEscapeUtils.escapeHtml4(">"));
		
		
		
		
	}

	
	@Override
	public void directRender(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		out.append("<");
		out.append(tagName);
		
		if (attrs!=null) {
			for(HtmlAttr a:attrs) {
				if(a.getName()!=null) {
					if(!a.getName().equals("value") && !a.getName().equals("options") && !a.getName().equals("valueType")&& !a.getName().equals("valueGetter")&& !a.getName().equals("displayGetter")) {
						a.directRender(out, cfg, mainParserResult, variables);
					}
				}
				
				
			}
		}
		
		out.append(">");
		HtmlAttr value = hasAttr("value") ? getAttrByName("value") : null;
		String valToSelect=variables.getStringAndIncrement(value.getStringValue());
		if(hasAttr("options")) {
			Integer count= variables.getIntAndIncrement("_"+value.getStringValue()+"Count");
			if(count!=null) {
				for(int i=0;i<count;i++) {
					String val=variables.getStringAndIncrement("_optionValue"+value.getStringValue());
					out.append("<option");
					if(value != null ) {
						out.append(" value=\"").append(val)
						.append('\"');
						if( Variable.eq(val,valToSelect)) {
							out.append(" selected=\"selected\"");
						}
					}
					
					out.append('>')
					.append(variables.getStringAndIncrement("_optionDisplay"+value.getStringValue()))
					.append("</option>");
					
				}
			}
		}	
			
			/*String var;
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
			
			
			CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
			CodeUtil.writeLine(out, CppOutput.getFastCgiOutputMethodHtmlEncoded(varKey,cfg)); 
			
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
			
			CodeUtil.writeLine(out, CppOutput.getFastCgiOutputMethodHtmlEncoded(varValue,cfg)); 
			TemplateCodeUtil.writeExpression(out,"\"</option>\"",cfg);
			out.append('\n');
			CodeUtil.writeLine(out, "}");
		} else {
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
		}
		
		directTextOutputBuffer.append("</").append(tagName).append('>');*/
		
	}
}
