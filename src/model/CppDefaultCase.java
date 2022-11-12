package model;

import io.CppOutput;
import io.parser.HtmlParser;
import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;

public class CppDefaultCase extends HtmlTag {

	
	@Override
	public void directRenderCollectVariables(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult) {
		out.append("default:\n");
		CodeUtil.writeLine(out, "{");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.directRenderCollectVariables(out, cfg, mainParserResult);			}
		}
		CodeUtil.writeLine(out, "}");
		CodeUtil.writeLine(out, "break;");
		
	}
	
	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {

			CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
			out.append("default:\n");
			CodeUtil.writeLine(out, "{");
			if (childNodes != null) { 
				for(AbstractNode n:childNodes) {
					n.toCpp(out,directTextOutputBuffer,cfg,mainParserResult);
				}
			}
			CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
			CodeUtil.writeLine(out, "}");
			CodeUtil.writeLine(out, "break;");
		
	}

	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("default:\n");
		CodeUtil.writeLine(out, "{");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toCppDoubleEscaped(out,directTextOutputBuffer,cfg,mainParserResult);
			}
		}
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		CodeUtil.writeLine(out, "}");
		CodeUtil.writeLine(out, "break;");
	}

	public static final String TAG_NAME = "default" ;
	
	public CppDefaultCase() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}

}
