package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.CppOutput;
import util.FileUtil;
import util.StringUtil;

public class Subtemplate {
	protected String subtemplateFilePath;
	protected ParserResult parserResult;
	protected String[] arguments;
	protected static List<CppSubtemplateTag> subtemplatesFunctionsHeaders; // if subtemplates have arguments their rendering must be extracted as function
	
	
	public Subtemplate(String subtemplateFilePath,String[] arguments ,ParserResult parserResult) {
		this.subtemplateFilePath = subtemplateFilePath;
		this.arguments = arguments;
		this.parserResult = parserResult;
	}
	
	public static void addSubtemplatesFunctionHeader(CppSubtemplateTag subtemplateHeaderTag) {
		if(Subtemplate.subtemplatesFunctionsHeaders==null) {
			Subtemplate.subtemplatesFunctionsHeaders = new ArrayList<>();
		}
		
		boolean found=false;
		for(CppSubtemplateTag t : Subtemplate.subtemplatesFunctionsHeaders) {
			if(t.getSubtemplateIdentifier().equals(subtemplateHeaderTag.getSubtemplateIdentifier())) {
				found = true;
			}
		}
		if(!found) {
			Subtemplate.subtemplatesFunctionsHeaders.add(subtemplateHeaderTag);
		}
	}
	
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) throws IOException {
		CppSubtemplateTag subtemplatesFunctionHeader = getSubtemplatesFunctionHeaderByIdentifier(getSubtemplateIdentifier());
		
		int argCount = subtemplatesFunctionHeader.getArgumentCount();
		String[] templateArgsDef = new String[argCount];
		String[] templateArgsDecl = new String[argCount];
		
		for(int i=0;i<subtemplatesFunctionHeader.getArgumentCount();i++) {
			String type="A"+i;
			templateArgsDef[i] = "class "+type;
			templateArgsDecl[i] = CodeUtil.sp(type,subtemplatesFunctionHeader.getArgument(i));
		}
		
		if(cfg.isRenderToString()) {
			CodeUtil.writeLine(out,CodeUtil.sp("template", CodeUtil.abr( CodeUtil.commaSep(templateArgsDef)), "static","QString",getCppMethodName(getSubtemplateIdentifier(), false),CodeUtil.parentheses( CodeUtil.commaSep(templateArgsDecl)),"{"));
			CodeUtil.writeLine(out, "QString _html;");
		} else {
			CodeUtil.writeLine(out,CodeUtil.sp("template", CodeUtil.abr( CodeUtil.commaSep(templateArgsDef)), "static","void",getCppMethodName(getSubtemplateIdentifier(), false),CodeUtil.parentheses( CodeUtil.commaSep(templateArgsDecl)+",FCGX_Stream * out"),"{"));
			
		}
		
		parserResult.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		
		if(cfg.isRenderToString()) {
			CodeUtil.writeLine(out, "return _html;");
		}
		CodeUtil.writeLine(out, "}");
	}
	
	public static String getCppMethodName(String subtemplateName,boolean doubleEncode) {
		return "subtemplate"+subtemplateName+(doubleEncode?"DoubleEncode":"");
	}
	
	public String getSubtemplateFilePath() {
		return subtemplateFilePath;
	}


	public static CppSubtemplateTag getSubtemplatesFunctionHeaderByIdentifier(String identifier) throws IOException {
		if(subtemplatesFunctionsHeaders != null) {
			for(CppSubtemplateTag t : subtemplatesFunctionsHeaders) {
				if(t.getSubtemplateIdentifier().equals(identifier)) {
					return t;
				}
			}
		}
		throw new IOException("no such subtemplate function: "+identifier);
	}
	
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) throws IOException {
		
		
		CppSubtemplateTag subtemplatesFunctionHeader = getSubtemplatesFunctionHeaderByIdentifier(getSubtemplateIdentifier());
		int argCount = subtemplatesFunctionHeader.getArgumentCount();
		String[] templateArgsDef = new String[argCount];
		String[] templateArgsDecl = new String[argCount];
		for(int i=0;i<argCount;i++) {
			String type="A"+i;
			templateArgsDef[i] = "class "+type;
			templateArgsDecl[i] = CodeUtil.sp(type,subtemplatesFunctionHeader.getArgument(i));
		}
		
		if(cfg.isRenderToString()) {
			CodeUtil.writeLine(out,CodeUtil.sp("template", CodeUtil.abr( CodeUtil.commaSep(templateArgsDef)), "static","QString",getCppMethodName(getSubtemplateIdentifier(), true),CodeUtil.parentheses( CodeUtil.commaSep(templateArgsDecl)),"{"));
			CodeUtil.writeLine(out, "QString _html;");
		} else {
			CodeUtil.writeLine(out,CodeUtil.sp("template", CodeUtil.abr( CodeUtil.commaSep(templateArgsDef)), "void",getCppMethodName(getSubtemplateIdentifier(), true),CodeUtil.parentheses( CodeUtil.commaSep(templateArgsDecl)),"{"));
			
		}
		parserResult.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);
		
		
		CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		
		if(cfg.isRenderToString()) {
			CodeUtil.writeLine(out, "return _html;");
		}
		CodeUtil.writeLine(out, "}");
		
	}
	
	public String getSubtemplateIdentifier() {
		return StringUtil.dropAll(FileUtil.dropExtension( subtemplateFilePath),'\\','/').toLowerCase();
	}
}
