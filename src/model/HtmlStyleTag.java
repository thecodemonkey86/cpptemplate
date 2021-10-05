package model;

import config.TemplateConfig;

public class HtmlStyleTag extends HtmlTag{

	public static final String TAG_NAME = "style";
	
	String css;
	
	public HtmlStyleTag() {
		super(TAG_NAME);
	}
	
	public void setCss(String css) {
		this.css = css;
	}
	
	@Override
	public void addChildNode(AbstractNode node) {
		throw new RuntimeException("cannot add nodes to css style tag"); 
	}
	
	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		directTextOutputBuffer.append("<");
		directTextOutputBuffer.append(tagName);
		
		if (attrs!=null) {
			for(HtmlAttr a:attrs) {
				a.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
		
		directTextOutputBuffer.append(">");
		directTextOutputBuffer.append(css);
		directTextOutputBuffer.append("</").append(tagName).append('>');
	 }
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		directTextOutputBuffer.append("<");
		directTextOutputBuffer.append(tagName);
		
		if (attrs!=null) {
			for(HtmlAttr a:attrs) {
				a.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
		
		directTextOutputBuffer.append(">");
		directTextOutputBuffer.append(css);
		directTextOutputBuffer.append("</").append(tagName).append('>');
	}

	@Override
	public String toString() {
		return css;
	}
}
