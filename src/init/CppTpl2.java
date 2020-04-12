package init;
import io.CppOutput;
import io.CssJsProcessor;
import io.SettingsIO;
import io.XmlCfgReader;
import io.parser.HtmlParser;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import config.TemplateConfig;
import settings.Settings;
import util.Util;
import util.exception.CancelException;
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

	
	private static void compileTemplate(TemplateConfig cfg, Path basePath, Path repositoryPath,Settings settings, String clsName, Path templatePath,  Path destBasePath, Set<String> collectInlineJs, Set<String> collectInlineCss, Set<String> collectCppHeaderIncludes,boolean debugMode,boolean nocache,SubtemplatesFunctions subtemplatesFunctions) throws IOException, CancelException {
		CssJsProcessor.setBasePath(basePath);
		CssJsProcessor.setRepositoryPath(repositoryPath);
		CssJsProcessor.setSettings(settings);
		CssJsProcessor.setNoCache(nocache);
		CssJsProcessor.setDebugMode(debugMode);
		CppRenderSubtemplateTag.setBasePath(basePath);
		CppOutput.setSettings(settings);
		HtmlParser.setLineWidth(settings.getLineWidth());
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
				rt.walkTree(new WalkTreeAction() {
					
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
				ParserResult layoutResult = p.parse(cfg,basePath.resolve(pp.getIncludeLayoutTemplatePath()),subtemplatesFunctions);
				result.setParentParserResult(layoutResult);
				System.out.println(layoutResult);
				layoutResult.getSimpleTemplate().walkTree(new WalkTreeAction() {
					
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
				result.getSimpleTemplate().walkTree(new WalkTreeAction() {
					
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
				throw new IOException();
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		try {
			
			URL u=ClassLoader.getSystemClassLoader().getResource("manifest.dat");
			if (u == null) {
				throw new Exception("missing manifest.dat");
			}
			Path manifestPath = Paths.get(u.toURI());
			Path execPath = manifestPath.getParent();
			Settings settings = null;
			List<String> linesManifest = Files.readAllLines(manifestPath);
			
			for (String line : linesManifest) {
				String[] lineParts = line.split("=");
				if (lineParts.length==2){
					String key = lineParts[0].trim();
					String val = lineParts[1].trim();
					if (key.equals("settingsPath")) {
						settings = SettingsIO.loadSettings(Util.getConfigurablePath(val, execPath).resolve("settings.dat"), execPath);
					}
				}
				
			}
			if (settings == null) {
				throw new IOException("settings not loaded");
			}
			
			String xmlFilePath = null;
			boolean debugMode = false;
			boolean nocache = false; 
			
			if(!args[args.length-1].endsWith(".xml")) {
				throw new IOException("path of xml config file missing");
			}
			
			for(int i=0;i<args.length-1;i++) {
				switch (args[i]) {
				case "--debug":
					debugMode = true;
					break;
				case "--force":
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
			//	HashMap<String, Instant> lastChanges = readLastChangesDatesFile(xmlDir);
				
				
				DefaultXMLReader.read(xmlFile, handler);
				
				List<TemplateConfig> xmlConfigs = handler.getXmlConfigs();
				LinkedHashSet<String> collectInlineJs = new LinkedHashSet<>();
				LinkedHashSet<String> collectInlineCss = new LinkedHashSet<>();
				LinkedHashSet<String> collectCppHeaderIncludes = new LinkedHashSet<>();
				LinkedHashSet<String> collectSubtemplateFunctionsCode = new LinkedHashSet<>();
				SubtemplatesFunctions subtemplatesFunctions = new SubtemplatesFunctions();
				for (TemplateConfig cfg : xmlConfigs) {
					Path basePath = TemplateConfig.getSrcPath();
					Path repositoryPath= basePath.resolve("repository");
					String clsName = cfg.getClsName();
					Path templatePath = cfg.getTmplPath();
					//String tplFilePath = templatePath.toString();
					//if(nocache || (!lastChanges.containsKey(tplFilePath)
					//		|| Files.getLastModifiedTime(templatePath).toInstant().isAfter(lastChanges.get(tplFilePath)))) {
					//Path cppFile = xmlConfig.getTplClsFile();
					compileTemplate(cfg, basePath, repositoryPath, settings, clsName, templatePath,  TemplateConfig.getDestPath(), collectInlineJs, collectInlineCss, collectCppHeaderIncludes, debugMode, nocache,subtemplatesFunctions);
					CppOutput.collectSubtemplatesCode(collectSubtemplateFunctionsCode, cfg, subtemplatesFunctions, null);
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
			
			
//			
//			result.toCpp(out); 

			
			
//			Files.write(Paths.get("D:\\Temp\\test.cpp"),out.toString().getBytes(Charset.forName("UTF-8"))  , StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}

}
