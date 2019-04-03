package model;

import java.io.IOException;

import config.TemplateConfig;
import io.CppOutput;

public class QStringHtmlEscapedOutputSection extends AbstractNode implements IAttrValueElement {

	String expression;
	
	public QStringHtmlEscapedOutputSection(String expression) throws IOException {
		this.expression = expression.trim();
		if (this.expression.length() == 0) {
			throw new IOException("syntax error");
		}
	}
	
	@Override
	public void addChildNode(AbstractNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return String.format("{%s}", expression) ;
	}
	

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer,cfg);
		if(cfg.isRenderToQString()) {
			out.append( String.format("%s += %s.toHtmlEscaped();\n",cfg.getRenderToQStringVariableName(), expression.startsWith("\"") ? String.format("QStringLiteral(%s)", expression) : expression));
		} else {
			int inlineIfThenIndex = -1;
			int inlineIfElseIndex = -1;
			boolean quot = false;
			boolean escape = false;
			for(int i=0;i<expression.length();i++) {
				if(escape) {
					continue;
				} else if(quot && expression.charAt(i) == '\\') {
					escape = true;
				} else if(expression.charAt(i) == '"') {
					quot = !quot;
				} else if(expression.charAt(i) == '?') {
					inlineIfThenIndex = i;
				} else if(inlineIfThenIndex > -1 && expression.charAt(i) == ':') {
					if(i+1<expression.length() && expression.charAt(i+1) == ':') {
						i++;
						continue;
					}
					inlineIfElseIndex = i;
					break;
				}
			}
			if(inlineIfThenIndex == -1) {
				out.append( String.format("FastCgiCout::write(%s.toHtmlEscaped());\n",expression));
			} else if(inlineIfElseIndex > -1){
				String conditionExpression = expression.substring(0,inlineIfThenIndex).trim();
				String thenExpression = expression.substring(inlineIfThenIndex+1,inlineIfElseIndex).trim();
				String elseExpression = expression.substring(inlineIfElseIndex+1).trim();
				
				if(thenExpression.startsWith("\"")) {
					thenExpression = String.format("QStringLiteral(%s)", thenExpression);
				}
				if(elseExpression.startsWith("\"")) {
					elseExpression = String.format("QStringLiteral(%s)", elseExpression);
				}
				
				out.append( String.format("if(%s)\n{\nFastCgiCout::write(%s.toHtmlEscaped());\n}\nelse\n{\nFastCgiCout::write(%s.toHtmlEscaped());\n}\n",
						conditionExpression,thenExpression,elseExpression));
			} else {
				String conditionExpression = expression.substring(0,inlineIfThenIndex).trim();
				String thenExpression = expression.substring(inlineIfThenIndex+1).trim();
				
				if(thenExpression.startsWith("\"")) {
					thenExpression = String.format("QStringLiteral(%s)", thenExpression);
				}
				
				out.append( String.format("if(%s)\n{\nFastCgiCout::write(%s.toHtmlEscaped());\n}\n",
						conditionExpression,thenExpression));
			}
				
		}
		
		
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer,cfg);
		if(cfg.isRenderToQString()) {
			out.append( String.format("%s += %s.toHtmlEscaped().toHtmlEscaped();\n",cfg.getRenderToQStringVariableName(), expression.startsWith("\"") ? String.format("QStringLiteral(%s)", expression) : expression));
		} else {
			int inlineIfThenIndex = -1;
			int inlineIfElseIndex = -1;
			boolean quot = false;
			boolean escape = false;
			for(int i=0;i<expression.length();i++) {
				if(escape) {
					continue;
				} else if(quot && expression.charAt(i) == '\\') {
					escape = true;
				} else if(expression.charAt(i) == '"') {
					quot = !quot;
				} else if(expression.charAt(i) == '?') {
					inlineIfThenIndex = i;
				} else if(inlineIfThenIndex > -1 && expression.charAt(i) == ':') {
					if(i+1<expression.length() && expression.charAt(i+1) == ':') {
						i++;
						continue;
					}
					inlineIfElseIndex = i;
					break;
				}
			}
			if(inlineIfThenIndex == -1) {
				out.append( String.format("FastCgiCout::write(%s.toHtmlEscaped().toHtmlEscaped());\n",expression));
			} else if(inlineIfElseIndex > -1){
				String conditionExpression = expression.substring(0,inlineIfThenIndex).trim();
				String thenExpression = expression.substring(inlineIfThenIndex+1,inlineIfElseIndex).trim();
				String elseExpression = expression.substring(inlineIfElseIndex+1).trim();
				
				if(thenExpression.startsWith("\"")) {
					thenExpression = String.format("QStringLiteral(%s)", thenExpression);
				}
				if(elseExpression.startsWith("\"")) {
					elseExpression = String.format("QStringLiteral(%s)", elseExpression);
				}
				
				out.append( String.format("if(%s)\n{\nFastCgiCout::write(%s.toHtmlEscaped().toHtmlEscaped());\n}\nelse\n{\nFastCgiCout::write(%s.toHtmlEscaped().toHtmlEscaped());\n}\n",
						conditionExpression,thenExpression,elseExpression));
			} else {
				String conditionExpression = expression.substring(0,inlineIfThenIndex).trim();
				String thenExpression = expression.substring(inlineIfThenIndex+1).trim();
				
				if(thenExpression.startsWith("\"")) {
					thenExpression = String.format("QStringLiteral(%s)", thenExpression);
				}
				
				out.append( String.format("if(%s)\n{\nFastCgiCout::write(%s.toHtmlEscaped().toHtmlEscaped());\n}\n",
						conditionExpression,thenExpression));
			}
				
		}
		
		
	}

	@Override
	public boolean stringOutput() {
		return false;
	}
}
