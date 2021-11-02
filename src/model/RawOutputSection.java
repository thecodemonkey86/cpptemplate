package model;

import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import util.Util;

public class RawOutputSection extends AbstractNode implements IAttrValueElement {

	String expression;

	public RawOutputSection(String expression) throws IOException {
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
		return String.format("{{%s}}", expression);
	}

	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
if(expression.contains("doubleEncode") ) {
	System.out.println();
}
		int inlineIfThenIndex = -1;
		int inlineIfElseIndex = -1;
		boolean quot = false;
		boolean escape = false;
		int parenthesisCounter=0;
		for (int i = 0; i < expression.length(); i++) {
			if (escape) {
				escape = false;
				continue;
			} else if (quot && expression.charAt(i) == '\\') {
				escape = true;
			} else if (expression.charAt(i) == '"') {
				quot = !quot;
			} else if (expression.charAt(i) == '?' && parenthesisCounter==0) {
				inlineIfThenIndex = i;
			} else if (expression.charAt(i) == '(') {
				parenthesisCounter++;
			} else if (expression.charAt(i) == ')') {
				parenthesisCounter--;
			} else if (!quot && inlineIfThenIndex > -1 && expression.charAt(i) == ':' && parenthesisCounter==0) {
				if (i + 1 < expression.length() && expression.charAt(i + 1) == ':') {
					i++;
					continue;
				}
				inlineIfElseIndex = i;
				break;
			}
		}
		if (inlineIfThenIndex == -1) {
			CppOutput.addOutput(out, expression, cfg);
		} else if (inlineIfElseIndex > -1) {
			String conditionExpression = expression.substring(0, inlineIfThenIndex).trim();
			String thenExpression = expression.substring(inlineIfThenIndex + 1, inlineIfElseIndex).trim();
			String elseExpression = expression.substring(inlineIfElseIndex + 1).trim();

			out.append(String.format("if(%s)\n{\n%s}\nelse\n{\n%s}\n", conditionExpression,
					CppOutput.getFastCgiOutputMethod(thenExpression, cfg),
					CppOutput.getFastCgiOutputMethod(elseExpression, cfg)));
		} else {
			String conditionExpression = expression.substring(0, inlineIfThenIndex).trim();
			String thenExpression = expression.substring(inlineIfThenIndex + 1).trim();

			if (thenExpression.startsWith("\"")) {
				thenExpression = String.format("%s(%s)", Util.getQStringLiteralConstructor(thenExpression, false),
						thenExpression);
			}
			if (cfg.isRenderToString()) {
				out.append(String.format("if(%s)\n{\n%s += %s;\n}\n", conditionExpression,
						cfg.getRenderToQStringVariableName(), thenExpression));
			} else {
				out.append(String.format("if(%s)\n{\n%s}\n", conditionExpression,
						CppOutput.getFastCgiOutputMethod(thenExpression, cfg)));
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
		int parenthesisCounter=0;
		for(int i=0;i<expression.length();i++) {
			if(escape) {
				escape = false;
				continue;
			} else if(quot && expression.charAt(i) == '\\') {
				escape = true;
			} else if(expression.charAt(i) == '"') {
				quot = !quot;
			} else if(expression.charAt(i) == '?'&& parenthesisCounter==0) {
				inlineIfThenIndex = i;
			} else if (expression.charAt(i) == '(') {
				parenthesisCounter++;
			} else if (expression.charAt(i) == ')') {
				parenthesisCounter--;
			} else if(!quot && inlineIfThenIndex > -1 && expression.charAt(i) == ':' && parenthesisCounter==0) {
				if(i+1<expression.length() && expression.charAt(i+1) == ':') {
					i++;
					continue;
				}
				inlineIfElseIndex = i;
				break;
			}
		}
		if(inlineIfThenIndex == -1) {
			CppOutput.addOutput(out, expression, cfg);
		} else if(inlineIfElseIndex > -1){
			String conditionExpression = expression.substring(0,inlineIfThenIndex).trim();
			String thenExpression = expression.substring(inlineIfThenIndex+1,inlineIfElseIndex).trim();
			String elseExpression = expression.substring(inlineIfElseIndex+1).trim();
			if(elseExpression.startsWith("\"")) {
				elseExpression = String.format("%s(%s)",Util.getQStringLiteralConstructor(elseExpression,false),CodeUtil.quote( StringEscapeUtils.escapeHtml4(elseExpression.substring(1, elseExpression.length()-1)))).replace("\\&quot;", "&quot;");
			}
			if(thenExpression.startsWith("\"")) {
				thenExpression = String.format("%s(%s)",Util.getQStringLiteralConstructor(thenExpression,false),CodeUtil.quote( StringEscapeUtils.escapeHtml4(thenExpression.substring(1, thenExpression.length()-1)))).replace("\\&quot;", "&quot;");
			}
			
			out.append( String.format("if(%s)\n{\n%s}\nelse\n{\n%s}\n",
					conditionExpression,CppOutput.getFastCgiOutputMethod(thenExpression, cfg),CppOutput.getFastCgiOutputMethod(elseExpression, cfg)));
		} else {
			String conditionExpression = expression.substring(0,inlineIfThenIndex).trim();
			String thenExpression = expression.substring(inlineIfThenIndex+1).trim();
			
			if(thenExpression.startsWith("\"")) {
				thenExpression = String.format("%s(%s)",Util.getQStringLiteralConstructor(thenExpression,false),CodeUtil.quote( StringEscapeUtils.escapeHtml4(thenExpression.substring(1, thenExpression.length()-1)))).replace("\\&quot;", "&quot;");
			}
			if(cfg.isRenderToString()) {
				out.append( String.format("if(%s)\n{\n%s += %s;\n}\n",
						conditionExpression,cfg.getRenderToQStringVariableName(),thenExpression));
			} else {
				out.append( String.format("if(%s)\n{\n%s}\n",
						conditionExpression,CppOutput.getFastCgiOutputMethod(thenExpression, cfg)));
			}
			
			
		}
	}

	@Override
	public boolean stringOutput() {
		return false;
	}
}
