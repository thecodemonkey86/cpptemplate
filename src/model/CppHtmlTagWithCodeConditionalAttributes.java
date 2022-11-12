package model;

import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;
import model.debugger.DebuggerVariableList;

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
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		directTextOutputBuffer.append(StringEscapeUtils.escapeHtml4("<"));
		directTextOutputBuffer.append(tagName);

		if (attrs != null) {
			
			for (HtmlAttr a : attrs) {
				if(a.getName()!=null) {
					if(a.getName().equals("disabled")) {
						CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
						CodeUtil.writeLine(out, "if("+ a.getStringValue()+") {");
						directTextOutputBuffer.append(" disabled");
						CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
						CodeUtil.writeLine(out, "}");
					} else {
						a.toCppDoubleEscaped(out, directTextOutputBuffer, cfg, mainParserResult);	
					}
				}
			}
		}

		directTextOutputBuffer.append(StringEscapeUtils.escapeHtml4(">"));
		if (childNodes != null) {
			for (AbstractNode n : childNodes) {
				n.toCppDoubleEscaped(out, directTextOutputBuffer, cfg, mainParserResult);
			}
		}

		directTextOutputBuffer.append(StringEscapeUtils.escapeHtml4("</")).append(tagName).append(StringEscapeUtils.escapeHtml4(">"));
	}
	
	@Override
	public void directRender(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		out.append("<");
		out.append(tagName);

		if (attrs != null) {
			
			for (HtmlAttr a : attrs) {
				if(a.getName()!=null) {
					if(a.getName().equals("disabled")) {
						if(variables.getStringAndIncrement(a.getStringValue()).equals("1")) {
							out.append(" disabled");
						}
					} else {
						a.directRender(out, cfg, mainParserResult, variables);					}
				}
			}
		}

		out.append(">");
		if (childNodes != null) {
			for (AbstractNode n : childNodes) {
				n.directRender(out, cfg, mainParserResult, variables);	
			}
		}
		out.append("</").append(tagName).append('>');
	}
	
	@Override
	public void directRenderDoubleEncoded(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		out.append(StringEscapeUtils.escapeHtml4("<"));
		out.append(tagName);

		if (attrs != null) {
			
			for (HtmlAttr a : attrs) {
				if(a.getName()!=null) {
					if(a.getName().equals("disabled")) {
						if(variables.getStringAndIncrement(a.getStringValue()).equals("1")) {
							out.append(" disabled");
						}
					} else {
						a.directRender(out, cfg, mainParserResult, variables);					}
				}
			}
		}

		out.append(StringEscapeUtils.escapeHtml4(">"));
		if (childNodes != null) {
			for (AbstractNode n : childNodes) {
				n.directRender(out, cfg, mainParserResult, variables);	
			}
		}
		out.append(StringEscapeUtils.escapeHtml4("</")).append(tagName).append(StringEscapeUtils.escapeHtml4(">"));
	}
}
