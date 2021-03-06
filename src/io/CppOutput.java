package io;



import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.parser.HtmlParser;
import model.HtmlLinkTag;
import model.HtmlMetaTag;
import model.ParserResult;
import model.SubtemplateFunctionImpl;
import settings.Settings;
import util.FileUtil2;
import util.Pair;
import util.ParseUtil;
import util.StringUtil;
import util.Util;
import util.exception.CancelException;


public class CppOutput {

	protected static Settings settings;
	
	public static void clearDirectTextOutputBuffer(StringBuilder out, StringBuilder buffer,TemplateConfig cfg) {
		addOutputChunksPlainHtml(out, ParseUtil.dropWhitespaces(buffer.toString()), HtmlParser.getLineWidth(),cfg);
		buffer.setLength(0);
	}
	
	public static void setSettings(Settings settings) {
		CppOutput.settings = settings;
	}
	
	public static String getCppEscapedString(String s) {
		/*return StringUtil.replaceAll( s.replace( "\\", "\\\\").replace("\"", "\\\""),Arrays.asList(
				new Pair<String, String>("\r", "\\r"),
				new Pair<String, String>("\n", "\\n"),
				new Pair<String, String>("\t", "\\t")
			));*/
		
		String escapeSequences = StringUtil.replaceAll(s,Arrays.asList(
				new Pair<String, String>("\\", "\\\\"),
				new Pair<String, String>("\r", "\\r"),
				new Pair<String, String>("\n", "\\n"),
				new Pair<String, String>("\t", "\\t")
				
				
				));
		
		
		String escapeQuots = escapeSequences.replace("\"", "\\\""); 
		
		return escapeQuots;
	}
	
	public static String getFastCgiOutputMethodHtmlEncoded(String expression,TemplateConfig cfg) {
		if(cfg !=null && cfg.isRenderToString()) {
			return String.format("FastCgiOutput::writeHtmlEncodedToBuffer(%s, %s);\n",expression,cfg.getRenderToQStringVariableName());
		} else {
			return String.format("FastCgiOutput::writeHtmlEncoded(%s,out);\n",expression);				
		}
	}
	
	public static String getFastCgiOutputMethodHtmlDoubleEncoded(String expression,TemplateConfig cfg) {
		if(cfg !=null && cfg.isRenderToString()) {
			return String.format("FastCgiOutput::writeHtmlDoubleEncodedToBuffer(%s, %s);\n",expression,cfg.getRenderToQStringVariableName());
		} else {
			return String.format("FastCgiOutput::writeHtmlDoubleEncoded(%s,out);\n",expression);				
		}
	}
	
	public static String getFastCgiOutputMethod(String expression,TemplateConfig cfg) {
		if(cfg !=null && cfg.isRenderToString()) {
			return String.format("FastCgiOutput::writeToBuffer(%s, %s);\n",expression,cfg.getRenderToQStringVariableName());
		} else {
			return String.format("FastCgiOutput::write(%s,out);\n",expression);				
		}
	}
	
	public static void addOutput(StringBuilder out,String expression,TemplateConfig cfg) {
		out.append(getFastCgiOutputMethod(expression, cfg));
	}
	
	public static void addOutputHtmlEncoded(StringBuilder out,String expression,TemplateConfig cfg) {
		out.append(getFastCgiOutputMethodHtmlEncoded(expression, cfg));
	}
	
	public static void addOutputHtmlDoubleEncoded(StringBuilder out,String expression,TemplateConfig cfg) {
		out.append(getFastCgiOutputMethodHtmlDoubleEncoded(expression, cfg));
	}
		
	public static void addOutputChunksPlainHtml(StringBuilder out,String outLine,int lineWidth ,TemplateConfig cfg) {
		if (outLine.length()>lineWidth) {
			int i;
			for(i=0;i<=outLine.length()-lineWidth;i+=lineWidth) {
				int offset = 0;
				while(outLine.substring(i,i+lineWidth+offset).endsWith("\\")) {
					offset++;
				}
				if(cfg !=null && cfg.isRenderToString()) {
					out.append(String.format("%s += \"%s\";",cfg.getRenderToQStringVariableName(), getCppEscapedString(outLine.substring(i,i+lineWidth+offset))));
				} else {
					out.append("FastCgiOutput::write(\""+ getCppEscapedString(outLine.substring(i,i+lineWidth+offset))  + "\",out);");
				}
				if(offset>0) {
					i+=offset;
				}
				out.append('\n');
			}
			String lastChunk = outLine.substring(i);
			if (!lastChunk.isEmpty()) {
			
				if(cfg !=null && cfg.isRenderToString()) {
					out.append(String.format("%s += \"%s\";",cfg.getRenderToQStringVariableName(), getCppEscapedString(lastChunk)));
				} else {
					out.append("FastCgiOutput::write(\""+ getCppEscapedString(lastChunk)  + "\",out);");	
				}
				
				out.append('\n');
			}
		} else if (!outLine.isEmpty()){
			if(cfg !=null && cfg.isRenderToString()) {
				out.append(String.format("%s += \"%s\";", cfg.getRenderToQStringVariableName(),getCppEscapedString(outLine)));
			} else {
				out.append("FastCgiOutput::write(\""+ getCppEscapedString(outLine)  + "\",out);");
			}
			out.append('\n');
		}
		
	}

	
	protected static String getJsOrCssMethodName(String include) throws IOException {
		/*int start = 0;
		
		if (jsInclude.startsWith("https://")) {
			start = "https://".length();
		} else if  (jsInclude.startsWith("http://")) {
			start = "http://".length();
		}*/
		
		int start = include.lastIndexOf('/');
		if(start == -1) {
			start = include.lastIndexOf('\\');
		}
		if(start == -1) {
			if (include.startsWith("https://")) {
				start = "https://".length();
			} else if  (include.startsWith("http://")) {
				start = "http://".length();
			} else {
				start=0;
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i = start; i < include.length(); i++) {
			if (include.charAt(i) >= 'a' && include.charAt(i) <= 'z' ||
					include.charAt(i) >= 'A' && include.charAt(i) <= 'Z' ||
					include.charAt(i) >= '0' && include.charAt(i) <= '9'
					) {
				sb.append(include.charAt(i));
			} else if (include.charAt(i) == '.') {
				sb.append("dot");
			}
		}
		if (sb.length() <= 256)
			return sb.toString();
		else {
			try {
				MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
				return "js"+ DatatypeConverter.printHexBinary( sha256.digest(sb.toString().getBytes()));
			} catch (Exception e) {
				throw new IOException(e);
			}
			
		}
	}
	
	/*protected static String insertCss(String cppCode, String clsName, Set<String> inlineCss) throws IOException, CancelException {
		StringBuilder sbInlineCss = new StringBuilder();
		for(String cssSrc : inlineCss) {
			String css = new String(Files.readAllBytes(CssJsProcessor.loadCss(cssSrc)), UTF8 );
			CppOutput.addOutChunks(sbInlineCss, css, Settings.LINE_WIDTH);
			sbInlineCss.append('\n');
		}
		String templateClass = cppCode;
		int markerRenderInlineCssBeginIndex = templateClass.indexOf(BEGIN_COMPILED_TEMPLATE_INLINE_CSS)+BEGIN_COMPILED_TEMPLATE_INLINE_CSS.length();
		int markerRenderInlineCssEndIndex = templateClass.indexOf(END_COMPILED_TEMPLATE_INLINE_CSS);
		
		
		if (markerRenderInlineCssBeginIndex > -1 && markerRenderInlineCssEndIndex > -1) {
			templateClass = templateClass.substring(0,markerRenderInlineCssBeginIndex)+"\nprotected function renderInlineCss() {\n" + sbInlineCss.toString() +"\n}\n"+ templateClass.substring(markerRenderInlineCssEndIndex);
			
		}
		return templateClass;
	}
	
	protected static String insertTemplate(String cppCode, String clsName, ParserResult layoutParserResult) {
		StringBuilder out = new StringBuilder();
		StringBuilder directTextOutputBuffer = new StringBuilder();
		layoutParserResult.toCpp(out,directTextOutputBuffer,cfg);
		String templateClass = cppCode;
		int markerRenderInlineCssBeginIndex = templateClass.indexOf(BEGIN_COMPILED_TEMPLATE)+BEGIN_COMPILED_TEMPLATE.length();
		int markerRenderInlineCssEndIndex = templateClass.indexOf(END_COMPILED_TEMPLATE);
		
		
		if (markerRenderInlineCssBeginIndex > -1 && markerRenderInlineCssEndIndex > -1) {
			templateClass = templateClass.substring(0,markerRenderInlineCssBeginIndex)+"\nprotected function renderBody(ViewData" + clsName.substring(0,clsName.length()-4) +" $data) {\n" + out.toString() +"\n}\n"+ templateClass.substring(markerRenderInlineCssEndIndex);
			
		}
		return templateClass;
	}*/
	
	protected static String getJsAsCpp(String jsSrc, boolean enableInlineJsCppCodeInStrings) throws IOException, CancelException {
		StringBuilder sbInlineJs = new StringBuilder();
		String js = new String(Files.readAllBytes(CssJsProcessor.loadJs(jsSrc)), StandardCharsets.UTF_8 );
		
		if(enableInlineJsCppCodeInStrings) {
		
			boolean singleQuot = false;
			boolean doubleQuot = false;
			boolean escape = false;
			 
			int start=0;
			int i=0;
			while(i<js.length()-2) {
				if(escape) {
					escape = false;
				} else {
					switch (js.charAt(i)) {
					case '/':
						if(!singleQuot&&!doubleQuot) {
							if(js.charAt(i+1) == '/') {
								while(i<js.length()) {
									if( js.charAt(i) == '\n' || js.charAt(i) == '\r') {
										break;
									}
									i++;
								}
							} else if(js.charAt(i+1) == '*') {
								while(i<js.length()-1) {
									if( js.charAt(i) == '*' && js.charAt(i+1) == '/') {
										i++;
										break;
									}
									i++;
								}
							}
						}
						break;
					case '\\':
						escape = true;
						break;
					case '\"':
						if(!singleQuot)
							doubleQuot = !doubleQuot;
						break;
					case '\'':
						if(!doubleQuot)
							singleQuot = !singleQuot;
						
						break;
		
					case '{':
						if(singleQuot||doubleQuot) {
						if(js.charAt(i+1 ) == '{' && js.charAt(i+2 )=='{' ) {
							CppOutput.addOutputChunksPlainHtml(sbInlineJs, js.substring(start,i), settings.getLineWidth(),null);
							start = i+3;
							while(i<js.length()-2) {
								if(js.charAt(i) == '}' && js.charAt(i+1) == '}' && js.charAt(i+2) == '}') {
									sbInlineJs.append( String.format("FastCgiOutput::write(%s,out);\n",js.substring(start,i)));
									i+=2;
									start = i+1;
									break;
								}
								i++;
							}
						}
						}
						break;
					}
				}
				i++;
			}
		
			CppOutput.addOutputChunksPlainHtml(sbInlineJs, js.substring(start)+"\n", settings.getLineWidth(),null);
		} else {
			CppOutput.addOutputChunksPlainHtml(sbInlineJs, js+"\n", settings.getLineWidth(),null);
		}
		sbInlineJs.append('\n');
		return sbInlineJs.toString();
	}
	
	protected static String getCssAsCpp(String cssSrc) throws IOException, CancelException {
		StringBuilder sbInlineCss = new StringBuilder();
		String css = CssJsProcessor.getInlineCss(cssSrc);;
		CppOutput.addOutputChunksPlainHtml(sbInlineCss, css+"\n", settings.getLineWidth(),null);
		sbInlineCss.append('\n');
		return sbInlineCss.toString();
	}
	
	/*public static void collectSubtemplatesCode(LinkedHashSet<String> collectFunctions,
			TemplateConfig cfg, 
			SubtemplatesFunctions subtemplatesFunctions,
			ParserResult mainParserResult
			) throws IOException {
		
		List<Pair<Subtemplate, Boolean>> subtemplatesAsFunctions = subtemplatesFunctions.getSubtemplatesAsFunctions();
		
		while(!subtemplatesAsFunctions.isEmpty()) {
			StringBuilder sbSrc = new StringBuilder();
			StringBuilder directTextOutputBuffer = new StringBuilder();
			
			Pair<Subtemplate, Boolean> p = subtemplatesAsFunctions.remove(0);
			if(p.getValue2()) {
				p.getValue1().toCppDoubleEscaped(sbSrc, directTextOutputBuffer, cfg, mainParserResult);
			}else {
				p.getValue1().toCpp(sbSrc, directTextOutputBuffer, cfg, mainParserResult);
			}
			collectFunctions.add(sbSrc.toString());
		}
		
		
	}*/
	
	public static void writeCompiledTemplateFile2(ParserResult layoutResult,ParserResult result, Path directory, String clsName, TemplateConfig cfg) throws IOException, CancelException {
		
		
		String compiledTplClassName = clsName +"CompiledTemplate";
		
		LinkedHashSet<String> inlineCss = result.getAllCssIncludes();
		LinkedHashSet<String> cssLinks = result.getAllCssLinkIncludes();
		LinkedHashSet<String> inlineJs = result.getAllJsInlineIncludes();
		LinkedHashSet<String> jsLinks = result.getAllJsLinkIncludes();
		LinkedHashSet<String> fontLinks = result.getAllFontIncludes();
		LinkedHashSet<String> inlineHeaderFiles = result.getAllHeaderIncludes();
		LinkedHashSet<HtmlMetaTag> metaTags = result.getAllMetaTags();
		LinkedHashSet<HtmlLinkTag> linkTags = result.getAllLinkTags();
		StringBuilder sbSrc = new StringBuilder();
		StringBuilder sbHdr = new StringBuilder();

		CodeUtil.writeLine(sbHdr,"#pragma once");
		CodeUtil.writeLine(sbSrc, CodeUtil.sp("#include",CodeUtil.quote(compiledTplClassName.toLowerCase()+".h")));
		for(int i=0;i<cfg.getViewDataClsCount();i++)
			CodeUtil.writeLine(sbHdr, CodeUtil.sp("#include", CodeUtil.quote(cfg.getViewDataClsPath(i))));
		
		CodeUtil.writeLine(sbHdr, "#include <memory>");
		CodeUtil.writeLine(sbHdr, "#include \"core/fastcgioutput.h\"");
		CodeUtil.writeLine(sbSrc, "#include \"core/page/pagemanager.h\"");
		CodeUtil.writeLine(sbSrc, "#include \"view/compiledtemplate/inlinejsrenderer.h\"");
		CodeUtil.writeLine(sbSrc, "#include \"view/compiledtemplate/inlinecssrenderer.h\"");
		CodeUtil.writeLine(sbSrc, "#include \"view/compiledtemplate/compiledsubtemplates.h\"");
		CodeUtil.writeLine(sbHdr, "#include \"mvc/view/html/htmltemplate.h\"");
		
		StringBuilder out = new StringBuilder();
		StringBuilder directTextOutputBuffer = new StringBuilder();
		if(layoutResult.getSimpleTemplate() != null)
			layoutResult.getSimpleTemplate().toCpp(out,directTextOutputBuffer,cfg, result);
		
		if(cfg.isIncludeTranslations())
			CodeUtil.writeLine(sbHdr, "#include \"translations/compiled/translations.h\"");
		
		for(String includeHeader : inlineHeaderFiles) {
			CodeUtil.writeLine(sbHdr, "#include \""+includeHeader+"\"");
		}
		
		//CodeUtil.writeLine(sbSrc, "using namespace std;");
		
		if(!cfg.isRenderToString()) {
			if(!cfg.isRenderStatic()) {
				CodeUtil.writeLine(sbHdr, CodeUtil.sp("class",compiledTplClassName,":", "public", "HtmlTemplate", "{"));
				for(int i=0;i<cfg.getViewDataClsCount();i++ ) { 
					CodeUtil.writeLine(sbHdr, CodeUtil.sp("public:","void","renderBody",CodeUtil.parentheses(CodeUtil.sp("std::unique_ptr",CodeUtil.abr(cfg.getViewDataClsName(i)),"data")), ";"));
					CodeUtil.writeLine(sbHdr, CodeUtil.sp("public:","void","render",CodeUtil.parentheses(CodeUtil.sp("std::unique_ptr",CodeUtil.abr(cfg.getViewDataClsName(i)),"data")), ";"));
					
					
					CodeUtil.writeLine(sbSrc,  CodeUtil.sp("void",compiledTplClassName+ "::renderBody",CodeUtil.parentheses(CodeUtil.sp("std::unique_ptr",CodeUtil.abr(cfg.getViewDataClsName(i)),"data"))));
					CodeUtil.writeLine(sbSrc, "{");
					
					
					if(cfg.isIncludeTranslations()) {
						CodeUtil.writeLine(sbSrc, "auto translations = data->getTranslations();");
					}
					CodeUtil.writeLine(sbSrc, out.toString());
					CodeUtil.writeLine(sbSrc, "}");
					CodeUtil.writeLine(sbSrc,  CodeUtil.sp("void",compiledTplClassName+ "::render",CodeUtil.parentheses(CodeUtil.sp("std::unique_ptr",CodeUtil.abr(cfg.getViewDataClsName(i)),"data"))));
					CodeUtil.writeLine(sbSrc, "{");
					if(!jsLinks.isEmpty()) {
						for(String jsLink : jsLinks) {
							CodeUtil.writeLine(sbSrc, "addJsFile"+CodeUtil.parentheses(Util.qStringLiteral(jsLink))+";");
						}
					}
					if(!cssLinks.isEmpty()) {
						for(String cssLink : cssLinks) {
							CodeUtil.writeLine(sbSrc, "addCssFile"+CodeUtil.parentheses(Util.qStringLiteral(cssLink))+";");
						}
					}
					if(!fontLinks.isEmpty()) {
						for(String fontLink : fontLinks) {
							CodeUtil.writeLine(sbSrc, "addFont"+CodeUtil.parentheses(Util.qStringLiteral(fontLink))+";");
						}
					}
					
					if(!metaTags.isEmpty()) {
						for(HtmlMetaTag metaTag : metaTags) {
							CodeUtil.writeLine(sbSrc, "addMetaTag"+CodeUtil.parentheses(metaTag.toCppConstructor())+";");
						}
					}
					if(!linkTags.isEmpty()) {
						for(HtmlLinkTag linkTag : linkTags) {
							CodeUtil.writeLine(sbSrc, "addLinkTag"+CodeUtil.parentheses(linkTag.toCppConstructor())+";");
						}
					}
					CodeUtil.writeLine(sbSrc, "this->renderHeader();");
					CodeUtil.writeLine(sbSrc, "this->renderBody(std::move(data));");
					CodeUtil.writeLine(sbSrc, "this->renderFooter();");
					CodeUtil.writeLine(sbSrc, "}");
				}
				
				
				if(!inlineJs.isEmpty()) {
					CodeUtil.writeLine(sbHdr, "public: virtual void renderInlineJs() const override;");
					CodeUtil.writeLine(sbSrc, CodeUtil.sp("void", compiledTplClassName+"::renderInlineJs()", "const", "{"));
					
					
					//int i=0;
					for(String jsSrc : inlineJs) {
						CodeUtil.writeLine(sbSrc, "InlineJsRenderer::"+getJsOrCssMethodName(jsSrc)+"(out);" );
						//i++;
					}
					CodeUtil.writeLine(sbSrc, "}");
				}
				
				
				
				if(!inlineCss.isEmpty()) {
					CodeUtil.writeLine(sbHdr, "public: virtual void renderInlineCss() const override;");
					CodeUtil.writeLine(sbSrc, CodeUtil.sp("void", compiledTplClassName+"::renderInlineCss()", "const"));
					CodeUtil.writeLine(sbSrc, "{");
					for(String cssSrc : inlineCss) {
						CodeUtil.writeLine(sbSrc, "InlineCssRenderer::"+getJsOrCssMethodName(cssSrc)+"(out);" );
						//i++;
					}
					
					CodeUtil.writeLine(sbSrc, "}");
				}
			} else {
				CodeUtil.writeLine(sbHdr, CodeUtil.sp("class",compiledTplClassName));
				CodeUtil.writeLine(sbHdr, "{");
				/*if(subtemplatesFunctions.hasSubtemplatesAsFunction()) {
					List<Pair<Subtemplate, Boolean>> subtemplatesAsFunctions = subtemplatesFunctions.getSubtemplatesAsFunctions();
					int i=0;
					while(i<subtemplatesAsFunctions.size()) {
						Pair<Subtemplate, Boolean> p = subtemplatesAsFunctions.get(i++);
						if(p.getValue2())
							p.getValue1().toCppDoubleEscaped(sbSrc, directTextOutputBuffer, cfg, result);
						else
							p.getValue1().toCpp(sbSrc, directTextOutputBuffer, cfg, result);
						
					}
				}*/
				for(int i=0;i<cfg.getViewDataClsCount();i++ ) { 
					CodeUtil.writeLine(sbHdr, CodeUtil.sp("public:", "static", cfg.isRenderToString() ? "QString" : "void",  "renderBody", CodeUtil.parentheses(CodeUtil.sp("std::unique_ptr"+CodeUtil.abr(cfg.getViewDataClsName(i)),"data", cfg.isRenderToString()?null:",FCGX_Stream * out")),";"));
					CodeUtil.writeLine(sbSrc, CodeUtil.sp(cfg.isRenderToString() ? "QString" : "void", compiledTplClassName+ "::renderBody", CodeUtil.parentheses(CodeUtil.sp("std::unique_ptr"+CodeUtil.abr(cfg.getViewDataClsName(i)),"data", cfg.isRenderToString()?null:",FCGX_Stream*", "out")),"{"));
					if(cfg.isIncludeTranslations()) {
						CodeUtil.writeLine(sbSrc, "auto translations = data->getTranslations();");
					}
					
					CodeUtil.writeLine(sbSrc, out.toString());
					CodeUtil.writeLine(sbSrc, "}");
				}
			}
			
			
		} else {
			CodeUtil.writeLine(sbHdr,  CodeUtil.sp("class",compiledTplClassName));
			CodeUtil.writeLine(sbHdr, "{");
			
			for(int i=0;i<cfg.getViewDataClsCount();i++ ) { 
				CodeUtil.writeLine(sbHdr, CodeUtil.sp("public:", "static", cfg.isRenderToString() ? "QString" : "void" , "renderBody",CodeUtil.parentheses(CodeUtil.sp("std::unique_ptr", CodeUtil.abr(cfg.getViewDataClsName(i)),"data",cfg.isRenderToString()?null:",FCGX_Stream* out")),";"));
				CodeUtil.writeLine(sbSrc, CodeUtil.sp( cfg.isRenderToString() ? "QString" : "void" , compiledTplClassName+"::renderBody",CodeUtil.parentheses(CodeUtil.sp("std::unique_ptr", CodeUtil.abr(cfg.getViewDataClsName(i)),"data",cfg.isRenderToString()?null:",FCGX_Stream* out"))));
				CodeUtil.writeLine(sbSrc, "{");
				if(cfg.isIncludeTranslations()) {
					CodeUtil.writeLine(sbSrc, "auto translations = data->getTranslations();");
				}
				CodeUtil.writeLine(sbSrc, CodeUtil.sp("QString",cfg.getRenderToQStringVariableName(),";"));
				
			
				
				CodeUtil.writeLine(sbSrc, out.toString());
				CodeUtil.writeLine(sbSrc, CodeUtil.sp("return",cfg.getRenderToQStringVariableName(),";"));
				CodeUtil.writeLine(sbSrc, "}");
			}
			if(!jsLinks.isEmpty()) {
				CodeUtil.writeLine(sbHdr, CodeUtil.sp("public:", "static", "void", "addExternalJs", CodeUtil.parentheses(CodeUtil.sp("HtmlTemplate*", "htmlTemplate")),";"));
				CodeUtil.writeLine(sbSrc, CodeUtil.sp("void", "addExternalJs", CodeUtil.parentheses(CodeUtil.sp("HtmlTemplate*", "htmlTemplate")),"{"));
				for(String jsLink : jsLinks) {
					CodeUtil.writeLine(sbSrc, "htmlTemplate->addJsFile"+CodeUtil.parentheses(CodeUtil.quote(jsLink))+";");
				}
				CodeUtil.writeLine(sbSrc, "}");
			}
			if(!fontLinks.isEmpty()) {
				CodeUtil.writeLine(sbHdr, CodeUtil.sp("public:", "static", "void", "addFonts", CodeUtil.parentheses(CodeUtil.sp("HtmlTemplate*", "htmlTemplate")),";"));
				CodeUtil.writeLine(sbSrc, CodeUtil.sp("void", "addFonts", CodeUtil.parentheses(CodeUtil.sp("HtmlTemplate*", "htmlTemplate")),"{"));
				for(String fontLink : fontLinks) {
					CodeUtil.writeLine(sbSrc, "htmlTemplate->addFont"+CodeUtil.parentheses(CodeUtil.quote(fontLink))+";");
				}
				CodeUtil.writeLine(sbSrc, "}");
			}
			
			if(!inlineJs.isEmpty()) {
				CodeUtil.writeLine(sbHdr, CodeUtil.sp("public:", "static", "void", "renderInlineJs", CodeUtil.parentheses(CodeUtil.sp("FCGX_Stream*", "out")),";"));
				CodeUtil.writeLine(sbSrc, CodeUtil.sp("void", "renderInlineJs", CodeUtil.parentheses(CodeUtil.sp("FCGX_Stream*", "out")),"{"));
				
				int i=0;
				for(String jsSrc : inlineJs) {
					CodeUtil.writeLine(sbSrc, CodeUtil.sp("InlineJsRenderer::"+getJsOrCssMethodName(jsSrc)+"(out);", i==inlineJs.size()-1 ? null : "\\"));
					i++;
				}
				CodeUtil.writeLine(sbSrc, "}");
			}
			
			if(!cssLinks.isEmpty()) {
				CodeUtil.writeLine(sbHdr, CodeUtil.sp("public:", "static", "void", "addExternalCss", CodeUtil.parentheses(CodeUtil.sp("HtmlTemplate*", "htmlTemplate")),";"));
				CodeUtil.writeLine(sbSrc, CodeUtil.sp("void", "addExternalCss", CodeUtil.parentheses(CodeUtil.sp("HtmlTemplate*", "htmlTemplate")),"{"));
				
				for(String cssLink : cssLinks) {
					CodeUtil.writeLine(sbSrc, "htmlTemplate->addCssFile"+CodeUtil.parentheses(CodeUtil.quote(cssLink))+";");
				}
				CodeUtil.writeLine(sbSrc, "}");
			}
			
			if(!inlineCss.isEmpty()) {
				CodeUtil.writeLine(sbHdr, CodeUtil.sp("public:", "static", "void", "renderInlineCss", CodeUtil.parentheses(CodeUtil.sp("FCGX_Stream*", "out")),";"));
				CodeUtil.writeLine(sbSrc, CodeUtil.sp("void", "renderInlineCss", CodeUtil.parentheses(CodeUtil.sp("FCGX_Stream*", "out"))));
				CodeUtil.writeLine(sbSrc, "{");
				int i=0;
				for(String cssSrc : inlineCss) {
					CodeUtil.writeLine(sbSrc, CodeUtil.sp("InlineCssRenderer::"+getJsOrCssMethodName(cssSrc)+"(out);",  i==inlineCss.size()-1 ? null : "\\"));
					i++;
				}
				
				CodeUtil.writeLine(sbSrc, "}");
			}
		}
		
		CodeUtil.writeLine(sbHdr, "};");
		 
		String filenameHdr = clsName.toLowerCase()+ "compiledtemplate.h";
		String filenameSrc = clsName.toLowerCase()+ "compiledtemplate.cpp";
		
		FileUtil2.writeFileIfContentChangedUtf8(directory.resolve(filenameHdr),  sbHdr.toString(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		System.out.println("Written "+directory.resolve(filenameHdr));
		FileUtil2.writeFileIfContentChangedUtf8(directory.resolve(filenameSrc),  sbSrc.toString(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		System.out.println("Written "+directory.resolve(filenameSrc));
	}
	
	
	
	public static void writeJsCppFile(Path directory, Set<String> inlineJs,LinkedHashSet<String> includeHeaders) throws IOException, CancelException {
	
		
		StringBuilder sbHeader = new StringBuilder("#pragma once\n");
		sbHeader.append("#include  \"core/fastcgioutput.h\"\n\n");
		

		
		sbHeader.append("\nclass InlineJsRenderer {\n");
			
		
		
		StringBuilder sbSource = new StringBuilder("#include \"inlinejsrenderer.h\"\n\n");
		
		if(includeHeaders!=null)
		for(String h : includeHeaders) {
			CodeUtil.writeLine(sbSource,String.format("#include \"%s\"", h));
		}
		HashSet<String> methodNames = new HashSet<>();
		
		for(String jsSrc : inlineJs) {
			String methodname = getJsOrCssMethodName(jsSrc);
			if(!methodNames.contains(methodname)) {
				methodNames.add(methodname);
				
				sbHeader.append("public: static void ")
				.append(getJsOrCssMethodName(jsSrc)).append("(FCGX_Stream * out);\n");
				
				sbSource.append("void InlineJsRenderer::")
				.append(methodname).append("(FCGX_Stream * out) {\n")
				.append(getJsAsCpp(jsSrc,includeHeaders!=null))
				.append("\n}\n");
				;
			
			}
		}
sbHeader.append("};\n\n") ;
		String headerFilename = "inlinejsrenderer.h";
		String sourceFileName = "inlinejsrenderer.cpp";
		
		FileUtil2.writeFileIfContentChangedUtf8(directory.resolve(headerFilename), sbHeader.toString());
	 
		FileUtil2.writeFileIfContentChangedUtf8(directory.resolve(sourceFileName), sbSource.toString());
	}
	
	public static void writeCssCppFile(Path directory, Set<String> inlineCss) throws IOException, CancelException {
		
		if (!Files.exists(directory))
			Files.createDirectories(directory);
		
		HashSet<String> methodNames = new HashSet<>();
		
		StringBuilder sbHeader = new StringBuilder("#pragma once\n");
		sbHeader.append("#include  \"core/fastcgioutput.h\"\n\n")
				.append("class InlineCssRenderer {\n");
			
		
		
		StringBuilder sbSource = new StringBuilder("#include \"inlinecssrenderer.h\"\n\n");
		
		for(String cssSrc : inlineCss) {
			String methodname = getJsOrCssMethodName(cssSrc);
			if(!methodNames.contains(methodname)) {
				methodNames.add(methodname);
				
				sbHeader.append("public: static void ")
				.append(getJsOrCssMethodName(cssSrc)).append("(FCGX_Stream * out);\n");
				
				sbSource.append("void InlineCssRenderer::")
				.append(methodname).append("(FCGX_Stream * out) {\n")
				.append(getCssAsCpp(cssSrc))
				.append("\n}\n");
				;
			}
		}
		
		sbHeader.append("};\n\n");
		
		String headerFilename = "inlinecssrenderer.h";
		String sourceFileName = "inlinecssrenderer.cpp";
		
		FileUtil2.writeFileIfContentChangedUtf8(directory.resolve(headerFilename), sbHeader.toString(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		FileUtil2.writeFileIfContentChangedUtf8(directory.resolve(sourceFileName), sbSource.toString(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		
	}

	public static void writeSubtemplatesClass(Path directory, LinkedHashSet<SubtemplateFunctionImpl> subtemplateFunctions,LinkedHashSet<String> inlineHeaderFiles) throws IOException {
		String clsName = "CompiledSubtemplates";
		StringBuilder sbHdr = new StringBuilder();		
		String headerFileName = clsName.toLowerCase()+ ".h";
		String srcFileName = clsName.toLowerCase()+ ".cpp";
		StringBuilder sbSrc = new StringBuilder("#include \""+headerFileName+".h\"");
		CodeUtil.writeLine(sbHdr,CodeUtil.sp("#pragma once"));
		for(String includeHeader : inlineHeaderFiles) {
			CodeUtil.writeLine(sbSrc, "#include \""+includeHeader+"\"");
		}
		CodeUtil.writeLine(sbHdr, "class "+clsName+" {");
		CodeUtil.writeLine(sbHdr, "public:");
		
		for(SubtemplateFunctionImpl func:subtemplateFunctions) {
			CodeUtil.writeLine(sbHdr, func.toCppHeader());
			CodeUtil.writeLine(sbSrc, func.toCpp());
		}
		CodeUtil.writeLine(sbHdr, "};");
		
		
		FileUtil2.writeFileIfContentChangedUtf8(directory.resolve(headerFileName), sbHdr.toString(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		FileUtil2.writeFileIfContentChangedUtf8(directory.resolve(srcFileName), sbSrc.toString(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
	}
}