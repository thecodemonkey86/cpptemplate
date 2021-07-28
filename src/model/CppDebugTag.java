package model;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import io.parser.HtmlParser;

public class CppDebugTag extends HtmlTag{
	public static final String TAG_NAME = "debug" ;
	
	public CppDebugTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}
	
	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult ) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out, "#ifdef QT_DEBUG");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out,"#endif");
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult ) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out, "#ifdef QT_DEBUG");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out,"#endif");
	}
}
