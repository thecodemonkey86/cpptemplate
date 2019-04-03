package model;

import java.io.IOException;

import config.TemplateConfig;
import io.CppOutput;

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
		return String.format("{{%s}}", expression) ;
	}
	

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer,cfg);
		if(cfg.isRenderToQString()) {
			out.append( String.format("%s += QString::number(%s);\n",cfg.getRenderToQStringVariableName(), expression));
		} else {
			out.append( String.format("FastCgiCout::write(%s);\n",expression));
		}
	}
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		toCpp(out, directTextOutputBuffer, cfg);
	}
	@Override
	public boolean stringOutput() {
		return false;
	}
}
