package model;

import io.CppOutput;
import io.parser.HtmlParser;
import util.StringUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import codegen.CodeUtil;
import config.TemplateConfig;

public class CppRenderSubtemplateTag extends HtmlTag {

	public static final String TAG_NAME = "renderSubtemplate" ;

	protected static Path basePath;
	protected static final Charset UTF8 = Charset.forName("UTF-8");	
	
	public CppRenderSubtemplateTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}

	public static void setBasePath(Path basePath) {
		CppRenderSubtemplateTag.basePath = basePath;
	}
	
	private void invokeSubTemplateMethod(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult,ParserResult subTemplateResult, String subtemplateName, boolean doubleEncode) throws IOException {
		String[] strArgs = getAttrStringValue("args").split(",");
		SubtemplateArg[] args = new SubtemplateArg[strArgs.length];
		for(int i=0;i<strArgs.length;i++) {
			String[] parts = strArgs[i].split(" ", 2);
			if(parts.length == 2) {
				String type = parts[0].trim();
				String name=parts[1].trim();
				if(type.isEmpty() || name.isEmpty()) {
					throw new IOException("subtemplate args now must have a concrete type. Missing for subtemplate "+subtemplateName);
				}
				args[i] = new SubtemplateArg(type, name);
			} else {
				throw new IOException("subtemplate args now must have a concrete type. Missing for subtemplate "+subtemplateName);
			}
		}
		
		mainParserResult.addSubtemplatesImpl(new SubtemplateFunctionImpl(  cfg, mainParserResult, new Subtemplate(subtemplateName, args,subTemplateResult),doubleEncode));
		 
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		
		if(cfg.isRenderToString()) {
			CodeUtil.writeLine(out,"_html += CompiledSubtemplates::"+ Subtemplate.getCppMethodName(StringUtil.dropAll(getAttrStringValue("name"),'\\','/').toLowerCase(), doubleEncode)+CodeUtil.parentheses(getAttrStringValue("args"))+";");
		//} else if(cfg.isRenderStatic()){
		//	CodeUtil.writeLine(out,"CompiledSubtemplates::"+Subtemplate.getCppMethodName(StringUtil.dropAll(getAttrStringValue("name"),'\\','/').toLowerCase(), doubleEncode)+CodeUtil.parentheses(getAttrStringValue("args")+",out")+";");
		} else {
			CodeUtil.writeLine(out,"CompiledSubtemplates::"+Subtemplate.getCppMethodName(StringUtil.dropAll(getAttrStringValue("name"),'\\','/').toLowerCase(), doubleEncode)+CodeUtil.parentheses("out,"+getAttrStringValue("args"))+";");
		}
		
	}
	
	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		HtmlParser p = new HtmlParser();
		try {
			String subtemplateName = getAttrStringValue("name");
			ParserResult result = p.parse(cfg,basePath.resolve(TemplateConfig.DIR_SUBTEMPLATES).resolve(subtemplateName +".html"));
			
			if(hasAttr("args")) {
				invokeSubTemplateMethod(out, directTextOutputBuffer, cfg, mainParserResult, result, subtemplateName,false);
			} else {
				result.toCpp(out, directTextOutputBuffer,cfg, mainParserResult);
			}
			
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		HtmlParser p = new HtmlParser();
		try {
			String subtemplateName = getAttrStringValue("name");
			ParserResult result = p.parse(cfg,basePath.resolve(TemplateConfig.DIR_SUBTEMPLATES).resolve(subtemplateName +".html"));
			
			if(hasAttr("args")) {
				invokeSubTemplateMethod(out, directTextOutputBuffer, cfg, mainParserResult, result, subtemplateName,true);
			} else {
				result.toCppDoubleEscaped(out, directTextOutputBuffer,cfg, mainParserResult);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
}
