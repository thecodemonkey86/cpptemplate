package model;

import io.CppOutput;
import io.parser.HtmlParser;
import model.debugger.DebuggerVariableList;


import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;

public class CppForTag extends HtmlTag {

	public static final String TAG_NAME = "for" ;

	
	public CppForTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult ) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		
		if(hasAttr("each")) {
			String as = getAttrByName("as").getStringValue().trim();
			
			out.append("for ")
			.append(CodeUtil.parentheses((!as.contains(" ") ? "const auto & "+as : as) + ':'+getAttrByName("each").getStringValue()))
			.append("{\n");
			if (childNodes != null) { 
				for(AbstractNode n:childNodes) {
					n.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
				}
			}
		} else {
		
			out.append("for ").append(CodeUtil.parentheses(getAttrByName("def").getStringValue()))
			.append("{\n");
			if (childNodes != null) { 
				for(AbstractNode n:childNodes) {
					n.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
				}
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("}\n");
		
	}
	
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult ) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		
		if(hasAttr("each")) {
			String as = getAttrByName("as").getStringValue().trim();
			
			out.append("for ")
			.append(CodeUtil.parentheses((!as.contains(" ") ? "const auto & "+as : as) + ':'+getAttrByName("each").getStringValue()))
			.append("{\n");
			if (childNodes != null) { 
				for(AbstractNode n:childNodes) {
					n.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);
				}
			}
		} else {
		
			out.append("for ").append(CodeUtil.parentheses(getAttrByName("def").getStringValue()))
			.append("{\n");
			if (childNodes != null) { 
				for(AbstractNode n:childNodes) {
					n.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);
				}
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("}\n");
		
	}
	
	@Override
	public void directRender(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException { 
		Integer count= variables.getIntAndIncrement("_"+getAttrByName("each").getStringValue()+"Count");
		if(count!=null) {
			for(int i=0;i<count;i++) {
				for(AbstractNode n:childNodes) {
					n.directRender(out, cfg, mainParserResult, variables);
				}
			}
		}
	}

}
