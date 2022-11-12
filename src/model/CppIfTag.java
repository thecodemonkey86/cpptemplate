package model;

import io.CppOutput;
import io.parser.HtmlParser;
import model.debugger.DebuggerVariableList;
import util.TemplateCodeUtil;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;

public class CppIfTag extends HtmlTag {

	public static final String TAG_NAME = "if" ;

	private boolean hasThenTag() {
		boolean result = false;
		for(AbstractNode n : childNodes) {
			if (n instanceof TextNode) {
				continue;
			} else if (n instanceof CppThenTag) {
				result = true;
			} else if (result && !(n instanceof CppElseTag) && !(n instanceof CppElseIfTag)){
				throw new RuntimeException("syntax error");
			}
		}
		return result;
	}
	
	public CppIfTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}

	
	@Override
	public void directRenderCollectVariables(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult) {
		TemplateCodeUtil.cppDirectRenderAddVariableBool(out, getAttrByName("cond").getStringValue());
		out.append("if ").append(CodeUtil.parentheses(getAttrByName("cond").getStringValue()));
		
		if (childNodes != null && childNodes.size() > 0) { 
			if (!hasThenTag()) {
				out.append("{\n");
			}
			for(AbstractNode n:childNodes) {
				n.directRenderCollectVariables(out, cfg, mainParserResult);
			}
		} else {
			out.append("{\n");
		}
		if (!hasThenTag()) {
			out.append("}\n");
		}
	}
	
	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("if ").append(CodeUtil.parentheses(getAttrByName("cond").getStringValue()));
	
		if (childNodes != null && childNodes.size() > 0) { 
			if (!hasThenTag()) {
				out.append("{\n");
			}
			for(AbstractNode n:childNodes) {
				n.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		} else {
			out.append("{\n");
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		if (!hasThenTag()) {
			out.append("}\n");
		}
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("if ").append(CodeUtil.parentheses(getAttrByName("cond").getStringValue()));
	
		if (childNodes != null && childNodes.size() > 0) { 
			if (!hasThenTag()) {
				out.append("{\n");
			}
			for(AbstractNode n:childNodes) {
				n.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		} else {
			out.append("{\n");
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		if (!hasThenTag()) {
			out.append("}\n");
		}
	}
	
	@Override
	public void directRender(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		if (childNodes != null && childNodes.size() > 0) { 
			boolean conditionMatched=variables.getBoolAndIncrement(getAttrByName("cond").getStringValue());
			
			if(conditionMatched) {
				if(!hasThenTag()) {

					for(AbstractNode n:childNodes) {
						n.directRender(out, cfg, mainParserResult, variables);
					}
					
				} else {
					for(AbstractNode n:childNodes) {
						if(n instanceof CppThenTag) {
							n.directRender(out, cfg, mainParserResult, variables);
						}
					}
				}
			} else {
				for(AbstractNode n:childNodes) {
					if(n instanceof CppElseIfTag) {
						conditionMatched=variables.getBoolAndIncrement(((CppElseIfTag) n).getAttrByName("cond").getStringValue());
						if(conditionMatched) {
							n.directRender(out, cfg, mainParserResult, variables);
							break;
						}
					
					}
				}
				if(!conditionMatched) {
					for(AbstractNode n:childNodes) {
						if(n instanceof CppElseTag) {
							n.directRender(out, cfg, mainParserResult, variables);
						
						}
					}
				}
			}
		
		}
	}
	
}
