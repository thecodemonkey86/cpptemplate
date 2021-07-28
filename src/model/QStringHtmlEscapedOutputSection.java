package model;

import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;

public class QStringHtmlEscapedOutputSection extends AbstractNode implements IAttrValueElement {

	private String expression;
	
	public String getExpression() {
		return expression;
	}
	
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
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer,cfg);
		int inlineIfThenIndex = -1;
		int inlineIfElseIndex = -1;
		boolean quot = false;
		boolean escape = false;
		int parenthesisCount = 0;
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
				if(parenthesisCount > 0 || (i+1<expression.length() && expression.charAt(i+1) == ':')) {
					i++;
					continue;
				}
				inlineIfElseIndex = i;
				break;
			} else if(inlineIfThenIndex == -1 && expression.charAt(i) == '(') {
				parenthesisCount++;
			} else if(inlineIfThenIndex == -1 && expression.charAt(i) == ')') {
				parenthesisCount--;
			}
		}
		if( (inlineIfThenIndex > 0 && inlineIfElseIndex > 0) || parenthesisCount > 0  || inlineIfThenIndex == -1) {
			CppOutput.addOutputHtmlEncoded(out, expression, cfg);
		} else if(inlineIfElseIndex > -1){
			String conditionExpression = expression.substring(0,inlineIfThenIndex).trim();
			String thenExpression = expression.substring(inlineIfThenIndex+1,inlineIfElseIndex).trim();
			String elseExpression = expression.substring(inlineIfElseIndex+1).trim();
			
			if(thenExpression.startsWith("\"")) {
				String htmlEncoded = StringEscapeUtils.escapeHtml4(thenExpression.substring(1,thenExpression.length()-1));
				
				out.append( String.format("if(%s)\n{\n%s}",
					 conditionExpression,CppOutput.getFastCgiOutputMethod(CodeUtil.quote(htmlEncoded),cfg)));
			} else {
				
				
				out.append( String.format("if(%s)\n{\n%s}",
						conditionExpression,CppOutput.getFastCgiOutputMethodHtmlEncoded(thenExpression,cfg)));
			}
			
		
			
			if(!elseExpression.equals("\"\"")) {
				if(elseExpression.startsWith("\"")) {
					String htmlEncoded =  StringEscapeUtils.escapeHtml4(elseExpression.substring(1,elseExpression.length()-1));
					
					out.append( String.format("\nelse\n{\n%s}\n",CppOutput.getFastCgiOutputMethod(CodeUtil.quote(htmlEncoded),cfg)));
				} else {
					out.append( String.format("\nelse\n{\n%s}\n",CppOutput.getFastCgiOutputMethodHtmlEncoded(elseExpression,cfg)));
				}
				
			} else {
				out.append("\n"); 
			}
			
		} else {
			String conditionExpression = expression.substring(0,inlineIfThenIndex).trim();
			String thenExpression = expression.substring(inlineIfThenIndex+1).trim();
			
			if(thenExpression.startsWith("\"")) {
				String htmlEncoded =StringEscapeUtils.escapeHtml4(thenExpression.substring(1,thenExpression.length()-1));
				
				out.append( String.format("if(%s)\n{\n%s}\n",
						conditionExpression,CppOutput.getFastCgiOutputMethod(CodeUtil.quote(htmlEncoded),cfg)));
			} else {
			
				out.append( String.format("if(%s)\n{\n%s}\n",
						conditionExpression,CppOutput.getFastCgiOutputMethodHtmlEncoded(thenExpression,cfg)));
			}
		}
		
		
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer,cfg);
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
			CppOutput.addOutputHtmlDoubleEncoded(out, expression, cfg);
		} else if(inlineIfElseIndex > -1){
			String conditionExpression = expression.substring(0,inlineIfThenIndex).trim();
			String thenExpression = expression.substring(inlineIfThenIndex+1,inlineIfElseIndex).trim();
			String elseExpression = expression.substring(inlineIfElseIndex+1).trim();
			
			if(thenExpression.startsWith("\"")) {
				String htmlEncoded = StringEscapeUtils.escapeHtml4(StringEscapeUtils.escapeHtml4(thenExpression.substring(1,thenExpression.length()-1)));
				
				out.append( String.format("if(%s)\n{\n%s}",
					 conditionExpression,CppOutput.getFastCgiOutputMethod(CodeUtil.quote(htmlEncoded),cfg)));
			} else {
				
				
				out.append( String.format("if(%s)\n{\n%s}",
						conditionExpression,CppOutput.getFastCgiOutputMethodHtmlDoubleEncoded(thenExpression,cfg)));
			}
			
		
			
			if(!elseExpression.equals("\"\"")) {
				if(elseExpression.startsWith("\"")) {
					String htmlEncoded = StringEscapeUtils.escapeHtml4(StringEscapeUtils.escapeHtml4(elseExpression.substring(1,elseExpression.length()-1)));
					
					out.append( String.format("\nelse\n{\n%s}\n",CppOutput.getFastCgiOutputMethod(CodeUtil.quote(htmlEncoded),cfg)));
				} else {
					out.append( String.format("\nelse\n{\n%s}\n",CppOutput.getFastCgiOutputMethodHtmlDoubleEncoded(elseExpression,cfg)));
				}
				
			} else {
				out.append("\n"); 
			}
			
		} else {
			String conditionExpression = expression.substring(0,inlineIfThenIndex).trim();
			String thenExpression = expression.substring(inlineIfThenIndex+1).trim();
			
			if(thenExpression.startsWith("\"")) {
				String htmlEncoded = StringEscapeUtils.escapeHtml4(StringEscapeUtils.escapeHtml4(thenExpression.substring(1,thenExpression.length()-1)));
				
				out.append( String.format("if(%s)\n{\n%s}\n",
						conditionExpression,CppOutput.getFastCgiOutputMethod(CodeUtil.quote(htmlEncoded),cfg)));
			} else {
			
				out.append( String.format("if(%s)\n{\n%s}\n",
						conditionExpression,CppOutput.getFastCgiOutputMethodHtmlDoubleEncoded(thenExpression,cfg)));
			}
		}
		
		
	}

	@Override
	public boolean stringOutput() {
		return false;
	}
}
