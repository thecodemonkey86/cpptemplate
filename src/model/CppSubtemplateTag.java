package model;

import config.TemplateConfig;
import io.parser.HtmlParser;
import util.FileUtil;
import util.StringUtil;

public class CppSubtemplateTag extends HtmlTag {

	public static final String TAG_NAME = "subtemplate" ;
	
	String subtemplateFilePath;
	
	public CppSubtemplateTag(String subtemplateFilePath) {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
		this.subtemplateFilePath = subtemplateFilePath;
	}
 

	public String getSubtemplateFilePath() {
		return subtemplateFilePath;
	}
	
	public String getSubtemplateIdentifier() {
		return StringUtil.dropAll(FileUtil.dropExtension( subtemplateFilePath),'\\','/').toLowerCase();
	}


/*	public String getArgument(int i) {
		return getAttrStringValue("args").split(",")[i];
	}*/
	
	@Override
	public void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);
			}
		}
	}


	/*public int getArgumentCount() {
		return  getAttrStringValue("args").split(",").length;
	}*/
}
