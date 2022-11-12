package init;
import io.CppOutput;
import io.CssJsProcessor;
import io.SettingsIO;
import io.XmlCfgReader;
import io.parser.HtmlParser;
import io.server.DebugServer;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import config.TemplateConfig;
import cryptography.MD5;
import settings.Settings;
import util.Util;
import util.exception.CancelException;
import util.ref.BoolByRef;
import xml.reader.DefaultXMLReader;
import model.AbstractNode;
import model.CppSectionTag;
import model.CppRenderSubtemplateTag;
import model.CppRenderSectionTag;
import model.ParserResult;
import model.SubtemplatesFunctions;
import model.CppIncludeTag;
import model.WalkTreeAction;

public class CppTpl2 {

	static HashMap<String, ParserResult> basetemplateCache=new HashMap<String, ParserResult>();
	
	private static ParserResult compileTemplate(TemplateConfig cfg, Path basePath, Path repositoryPath,Settings settings, String clsName, Path templatePath,  Path destBasePath, Set<String> collectInlineJs, Set<String> collectInlineCss, Set<String> collectCppHeaderIncludes,boolean debugMode,boolean nocache,SubtemplatesFunctions subtemplatesFunctions) throws IOException, CancelException {
		HtmlParser p=new HtmlParser();
		ParserResult result = p.parse(cfg,templatePath,subtemplatesFunctions);
		Path compiledTemplateDir = TemplateConfig.getDestPath().resolve("compiledtemplate");
		
		
		if(cfg.getSubDir() != null && !cfg.getSubDir().isEmpty()) {
			compiledTemplateDir = compiledTemplateDir.resolve(cfg.getSubDir());
		}
		
		if (!Files.exists(compiledTemplateDir))
			Files.createDirectories(compiledTemplateDir);
		
		if (result.hasLayoutTemplate()) {
			
			for(CppSectionTag rt: result.getTemplateRegionTags()) {
				rt.walkTree(cfg,new WalkTreeAction() {
					
					@Override
					public void currentNode(AbstractNode node, ParserResult parserResult) throws IOException {
						if (node instanceof CppRenderSectionTag) {
							CppRenderSectionTag tpl = (CppRenderSectionTag) node;
							tpl.setRenderTmpl(result.getTemplateByName(tpl.getAttrByName("name").getStringValue()));
						} 
						
					}
				}, result);	
			}
			
			for(CppIncludeTag pp: result.getPreprocessorTags()) {
				Path layoutTplPath=basePath.resolve(pp.getIncludeLayoutTemplatePath());
				ParserResult layoutResult = null;
				
				
				if(basetemplateCache.containsKey(layoutTplPath.toString())) {
					layoutResult = basetemplateCache.get(layoutTplPath.toString()) ;
				} else {
					layoutResult = p.parse(cfg,layoutTplPath,subtemplatesFunctions);
					basetemplateCache.put(layoutTplPath.toString(), layoutResult);
				}
				result.setParentParserResult(layoutResult);
				layoutResult.getSimpleTemplate().walkTree(cfg,new WalkTreeAction() {
					
					@Override
					public void currentNode(AbstractNode node, ParserResult parserResult) throws IOException {
						if (node instanceof CppRenderSectionTag) {
							CppRenderSectionTag tpl = (CppRenderSectionTag) node;
							tpl.setRenderTmpl(result.getTemplateByName(tpl.getAttrByName("name").getStringValue()));
						}
						
					}
				}, layoutResult);				
				
				collectInlineJs.addAll(result.getAllJsInlineIncludes());
				collectInlineCss.addAll(result.getAllCssIncludes());
				collectCppHeaderIncludes.addAll(result.getAllHeaderIncludes());
				
				CppOutput.writeCompiledTemplateFile2(layoutResult,result,compiledTemplateDir , clsName, cfg);
			}
			
		} else {
			if(result.getSimpleTemplate()!=null ) {
				result.getSimpleTemplate().walkTree(cfg,new WalkTreeAction() {
					
					@Override
					public void currentNode(AbstractNode node, ParserResult parserResult) throws IOException {
						if (node instanceof CppRenderSectionTag) {
							CppRenderSectionTag tpl = (CppRenderSectionTag) node;
							tpl.setRenderTmpl(result.getTemplateByName(tpl.getAttrByName("name").getStringValue()));
						} 
						
					}
				}, result);	
				
				collectInlineJs.addAll(result.getAllJsInlineIncludes());
				collectInlineCss.addAll(result.getAllCssIncludes());
				collectCppHeaderIncludes.addAll(result.getAllHeaderIncludes());
				//CppOutput.insertCode(clsName, cppFile, result, result.getAllCssIncludes(), result.getAllJsIncludes());
				CppOutput.writeCompiledTemplateFile2(result,result, compiledTemplateDir, clsName, cfg);
			} else {
				// empty templates
				CppOutput.writeCompiledTemplateFile2(result,result, compiledTemplateDir, clsName, cfg);
			}
		}
		return result;
	}
	
	
	
	public static void main(String[] args) {
		try {
			
			URL u=ClassLoader.getSystemClassLoader().getResource("manifest.dat");
			if (u == null) {
				throw new Exception("missing manifest.dat");
			}
			Path manifestPath = Paths.get(u.toURI());
			Path execPath = manifestPath.getParent();
			Path settingsPath = null;
			Settings settings = null;
			List<String> linesManifest = Files.readAllLines(manifestPath);
			
			for (String line : linesManifest) {
				String[] lineParts = line.split("=");
				if (lineParts.length==2){
					String key = lineParts[0].trim();
					String val = lineParts[1].trim();
					if (key.equals("settingsPath")) {
						settingsPath =Util.getConfigurablePath(val, execPath);
						settings = SettingsIO.loadSettings(settingsPath.resolve("settings.dat"), execPath);
					}
				}
				
			}
			if (settings == null) {
				throw new IOException("settings not loaded");
			}
			HtmlParser.setLineWidth(settings.getLineWidth());
			String xmlFilePath = null;
			boolean debugMode = false;
			boolean debugServerMode = false;
			boolean nocache = false; 
			boolean force = false;
			
			if(!args[args.length-1].endsWith(".xml")) {
				throw new IOException("path of xml config file missing");
			}
			
			for(int i=0;i<args.length-1;i++) {
				switch (args[i]) {
				case "--debug":
					debugMode = true;
					break;
				case "--debugServer":
					debugServerMode = true;
					break;
				case "--force":
					force = true;
					break;
				case "--nocache":
					nocache = true;
					break;
				default:
					throw new IOException("invalid option " +args[i]);
				}
				
			}
			xmlFilePath = args[args.length-1];
			if (xmlFilePath != null) {
				Path xmlFile = Paths.get(xmlFilePath);
				Path xmlDir = xmlFile.getParent();
				XmlCfgReader handler = new XmlCfgReader(xmlDir);
				
				
				DefaultXMLReader.read(xmlFile, handler);
				
				Path basePath = TemplateConfig.getSrcPath();
				Path repositoryPath= basePath.resolve("repository");
				CssJsProcessor.setBasePath(basePath);
				CssJsProcessor.setRepositoryPath(repositoryPath);
				CssJsProcessor.setSettings(settings);
				CssJsProcessor.setNoCache(nocache);
				CssJsProcessor.setDebugMode(debugMode);
				CppRenderSubtemplateTag.setBasePath(basePath);
				CppOutput.setSettings(settings);
				
				List<TemplateConfig> xmlConfigs = handler.getXmlConfigs();
				if(debugServerMode) {
					DebugServer.serve(xmlConfigs);
				} else {
					final BoolByRef run=new BoolByRef();
					run.val = false;
					if(!force) {
						if(settingsPath!=null) {
						
							Path lastRunFlagFile=settingsPath.resolve(MD5.getHexString(xmlFilePath)+"_last.txt" );
							if( Files.exists(lastRunFlagFile)) {
								final Instant lastRun = Instant.parse(new String(Files.readAllBytes(lastRunFlagFile), StandardCharsets.US_ASCII));
								 Files.walkFileTree(basePath, new FileVisitor<Path>() {
	
									@Override
									public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
											throws IOException {
										// TODO Auto-generated method stub
										return FileVisitResult.CONTINUE;
									}
	
									@Override
									public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
											throws IOException {
										// TODO Auto-generated method stub
										if(Files.getLastModifiedTime(file).toInstant().compareTo(lastRun)==1) {
											run.val = true;
											return FileVisitResult.TERMINATE;
										}
										return FileVisitResult.CONTINUE;
									}
	
									@Override
									public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
										// TODO Auto-generated method stub
										return FileVisitResult.CONTINUE;
									}
	
									@Override
									public FileVisitResult postVisitDirectory(Path dir, IOException exc)
											throws IOException {
										// TODO Auto-generated method stub
										return FileVisitResult.CONTINUE;
									}
								});
								
								
							}
							Files.write(lastRunFlagFile, Instant.now().toString().getBytes(StandardCharsets.US_ASCII) );
						}
						if(!run.val) {
							return; 
						}
					}
					LinkedHashSet<String> collectInlineJs = new LinkedHashSet<>();
					LinkedHashSet<String> collectInlineCss = new LinkedHashSet<>();
					LinkedHashSet<String> collectCppHeaderIncludes = new LinkedHashSet<>();
					LinkedHashSet<String> collectSubtemplateFunctionsCode = new LinkedHashSet<>();
					SubtemplatesFunctions subtemplatesFunctions = new SubtemplatesFunctions();
					for (TemplateConfig cfg : xmlConfigs) {
						
						String clsName = cfg.getClsName();
						Path templatePath = cfg.getTmplPath();
						//String tplFilePath = templatePath.toString();
						//if(nocache || (!lastChanges.containsKey(tplFilePath)
						//		|| Files.getLastModifiedTime(templatePath).toInstant().isAfter(lastChanges.get(tplFilePath)))) {
						//Path cppFile = xmlConfig.getTplClsFile();
						ParserResult result= compileTemplate(cfg, basePath, repositoryPath, settings, clsName, templatePath,  TemplateConfig.getDestPath(), collectInlineJs, collectInlineCss, collectCppHeaderIncludes, debugMode, nocache,subtemplatesFunctions);
						CppOutput.collectSubtemplatesCode(collectSubtemplateFunctionsCode, cfg, subtemplatesFunctions, result);
						//lastChanges.put(tplFilePath, Files.getLastModifiedTime(templatePath).toInstant());
						//}
					}
					CppOutput.writeSubtemplatesFile( TemplateConfig.getDestPath().resolve("compiledtemplate"),collectSubtemplateFunctionsCode,collectCppHeaderIncludes);
	//				writeLastChangesDatesFile(xmlDir,lastChanges);
					Path pathCompiledTemplate = TemplateConfig.getDestPath().resolve("compiledtemplate");
					LinkedHashSet<String> inlineJsRendererHeaderIncludes =   handler.getInlineJsRendererHeaderIncludes();
					//inlineJsRendererHeaderIncludes.addAll(collectCppHeaderIncludes)	;			
					
					CppOutput.writeJsCppFile(pathCompiledTemplate, collectInlineJs, inlineJsRendererHeaderIncludes);
					CppOutput.writeCssCppFile(pathCompiledTemplate, collectInlineCss);
				}
				
				
			}
			
			
//			
//			result.toCpp(out); 

			
			
//			Files.write(Paths.get("D:\\Temp\\test.cpp"),out.toString().getBytes(Charset.forName("UTF-8"))  , StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}

}
