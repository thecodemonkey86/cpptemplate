package model;

import config.TemplateConfig;
import io.parser.HtmlParser;

public class CppStringFormatTag extends HtmlTag {
	public static final String TAG_NAME = "string" ;
	
	public CppStringFormatTag() {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}
	
	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		/*CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		
		if(childNodes.size() == 1) {
			if(childNodes.get(0) instanceof TextNode) {
				TextNode t = (TextNode) childNodes.get(0);
				CodeUtil.writeLine(out, "QStringLiteral" + CodeUtil.quote(CodeUtil.parentheses(t.getText()))+".args"
			}
		}
		
		CodeUtil.writeLine(out, "FastCgiOutput::write"+CodeUtil.parentheses("translations->"+ getAttrByName("key").getStringValue()+"().toHtmlEscaped(), out")+";");
		cfg.setIncludeTranslations(true);*/
	}

}
