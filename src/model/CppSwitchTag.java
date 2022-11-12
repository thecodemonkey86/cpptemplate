package model;

import io.CppOutput;
import io.parser.HtmlParser;
import model.debugger.DebuggerVariableList;
import model.debugger.Variable;


import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;

public class CppSwitchTag extends HtmlTag {

	public static final String TAG_NAME = "switch" ;

	
	public CppSwitchTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg,
			ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("switch ").append(CodeUtil.parentheses(getAttrByName("cond").getStringValue())).append("{\n");
	
		if (childNodes != null && childNodes.size() > 0) { 
			for(AbstractNode n:childNodes) {
				n.toCpp(out,directTextOutputBuffer,cfg,mainParserResult);
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out, "}");
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("switch ").append(CodeUtil.parentheses(getAttrByName("cond").getStringValue())).append("{\n");
	
		if (childNodes != null && childNodes.size() > 0) { 
			for(AbstractNode n:childNodes) {
				n.toCppDoubleEscaped(out,directTextOutputBuffer,cfg,mainParserResult);
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out, "}");
	}

	@Override
	public void directRender(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
//		
//		super.directRender(out, cfg, mainParserResult, variables+1);
		String cond = variables.getStringAndIncrement(getAttrByName("cond").getStringValue());
		if (childNodes != null) { 
			boolean caseMatched=false;
			for(AbstractNode n:childNodes) {
				if(n instanceof CppCaseTag) {
					if(Variable.eq(cond,variables.getStringAndIncrement(((CppCaseTag)n).getAttrByName("value").getStringValue()))) {
						n.directRender(out, cfg, mainParserResult, variables);
						caseMatched = true;
						break;
					}
				}
			}
			
			if(!caseMatched) {
				for(AbstractNode n:childNodes) {
					if(n instanceof CppDefaultCase) {
						n.directRender(out, cfg, mainParserResult, variables);
					}
				}
			}
		}
	
		
	}
	
	@Override
	public void directRenderDoubleEncoded(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList  variables) throws IOException {
		if (childNodes != null) { 
			boolean caseMatched=false;
			for(AbstractNode n:childNodes) {
				if(n instanceof CppCaseTag) {
					if(Variable.eq(variables.getStringAndIncrement(getAttrByName("cond").getStringValue()),variables.getStringAndIncrement(((CppCaseTag)n).getAttrByName("value").getStringValue()))) {
						n.directRenderDoubleEncoded(out, cfg, mainParserResult, variables);
						break;
					}
				}
			}
			
			if(!caseMatched) {
				for(AbstractNode n:childNodes) {
					if(n instanceof CppDefaultCase) {
						n.directRenderDoubleEncoded(out, cfg, mainParserResult, variables);
					}
				}
			}
		}
	}
}
