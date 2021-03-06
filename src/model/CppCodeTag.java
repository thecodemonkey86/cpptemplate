package model;

import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;

public class CppCodeTag extends AbstractNode implements IAttrValueElement {
	protected String code;
	
	public CppCodeTag(String code) {
		setCode(code);
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(HtmlParser.CPP_CODE_TAG);
		sb.append(' ')
			.append(this.code)
			.append(HtmlParser.CPP_CODE_END_TAG);
		return sb.toString();
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		String[] lines = code.split("\n");
		StringBuilder sbTrimmed = new StringBuilder(code.length());
		if(lines.length > 0) {
			for(int i = 0; i < lines.length; i++) {
				String trim = lines[i].trim();
				if (trim.length() > 0) {
					sbTrimmed.append(trim).append('\n');
				}
			}			
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer,cfg);
		String trimmed = sbTrimmed.toString().trim();
		if(trimmed.length()>0)
			out.append(trimmed).append('\n');
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		toCpp(out, directTextOutputBuffer, cfg, mainParserResult);
	}

	@Override
	public boolean stringOutput() {
		return false;
	}

}
