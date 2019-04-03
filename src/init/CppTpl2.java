package init;
import io.CppOutput;
import io.CssJsProcessor;
import io.SettingsIO;
import io.XmlCfgReader;
import io.parser.HtmlParser;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
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
import model.TplPreprocessorTag;
import model.WalkTreeAction;

public class CppTpl2 {

	private static String readUtf8(Path p) throws IOException {
		return new String(Files.readAllBytes(p),Charset.forName("UTF-8"));
	}
	
	private static void compileTemplate(TemplateConfig cfg, Path basePath, Path repositoryPath,Settings settings, String clsName, Path templatePath,  Path destBasePath, Set<String> collectInlineJs, Set<String> collectInlineCss,boolean debugMode,boolean nocache) throws IOException, CancelException {
		CssJsProcessor.setBasePath(basePath);
		CssJsProcessor.setRepositoryPath(repositoryPath);
		CssJsProcessor.setSettings(settings);
		CssJsProcessor.setNoCache(nocache);
		CssJsProcessor.setDebugMode(debugMode);
		CppRenderSubtemplateTag.setBasePath(basePath);
		CppOutput.setSettings(settings);
		HtmlParser.setLineWidth(settings.getLineWidth());
		HtmlParser p=new HtmlParser();
		ParserResult result = p.parse(readUtf8(templatePath));
		Path compiledTemplateDir = TemplateConfig.getDestPath().resolve("compiledtemplate");
		
		
		if(cfg.getSubDir() != null && !cfg.getSubDir().isEmpty()) {
			compiledTemplateDir = compiledTemplateDir.resolve(cfg.getSubDir());
		}
		
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
			
			for(TplPreprocessorTag pp: result.getPreprocessorTags()) {
				ParserResult layoutResult = p.parse(readUtf8(basePath.resolve(pp.getIncludeLayoutTemplatePath())));
				result.setParentParserResult(layoutResult);
				System.out.println(layoutResult);
				layoutResult.getSimpleTemplate().walkTree(new WalkTreeAction() {
					
					@Override
					public void currentNode(AbstractNode node, ParserResult parserResult) throws IOException {
						if (node instanceof CppRenderSectionTag) {
							CppRenderSectionTag tpl = (CppRenderSectionTag) node;
							tpl.setRenderTmpl(result.getTemplateByName(tpl.getAttrByName("name").getStringValue()));
//						} else if(node instanceof CppRenderSectionTag) {
//							CppRenderSectionTag section = (CppRenderSectionTag) node;
//							section.setSection(parserResult.getSection(section.getAttrByName("name").getStringValue()));
						}
						
					}
				}, layoutResult);				
				
				collectInlineJs.addAll(result.getAllJsIncludes());
				collectInlineCss.addAll(result.getAllCssIncludes());
			//	CppOutput.insertCode(clsName, cppFile, layoutResult, result.getAllCssIncludes(), allJsIncludes);
				
				CppOutput.writeCompiledTemplateFile(layoutResult,result,compiledTemplateDir , clsName, cfg);
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
				
				collectInlineJs.addAll(result.getAllJsIncludes());
				collectInlineCss.addAll(result.getAllCssIncludes());
				//CppOutput.insertCode(clsName, cppFile, result, result.getAllCssIncludes(), result.getAllJsIncludes());
				CppOutput.writeCompiledTemplateFile(result,result, compiledTemplateDir, clsName, cfg);
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			
			//Path basePath = Paths.get("D:\\Bernhard\\netbeans_workspace\\marketplace\\public_html");
			//Path templatePath = basePath.resolve("templates\\product\\ProductList.html");
			//Path templatePath = basePath.resolve("templates\\product\\Test.html");
			//Path cppFile = Paths.get("D:\\Temp\\test.cpp");
			
			URL u=ClassLoader.getSystemClassLoader().getResource("manifest.dat");
			if (u == null) {
				throw new Exception("missing manifest.dat");
			}
			Path manifestPath = Paths.get(u.toURI());
			Path execPath = manifestPath.getParent();
			Settings settings = null;
			List<String> linesManifest = Files.readAllLines(manifestPath);
			
			//String clsName = "ProductList";
			
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
			boolean forceReloadJsCss = false; 
			
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
					forceReloadJsCss = true;
					break;
				default:
					throw new IOException("invalid option " +args[i]);
				}
				
			}
			xmlFilePath = args[args.length-1];
			
			
			if (xmlFilePath != null) {
				Path xmlFile = Paths.get(xmlFilePath);
				XmlCfgReader handler = new XmlCfgReader(xmlFile.getParent());
				DefaultXMLReader.read(xmlFile, handler);
				
				List<TemplateConfig> xmlConfigs = handler.getXmlConfigs();
				LinkedHashSet<String> collectInlineJs = new LinkedHashSet<>();
				LinkedHashSet<String> collectInlineCss = new LinkedHashSet<>();
				for (TemplateConfig cfg : xmlConfigs) {
					Path basePath = TemplateConfig.getSrcPath();
					Path repositoryPath= basePath.resolve("repository");
					String clsName = cfg.getClsName();
					Path templatePath = cfg.getTmplPath();
					//Path cppFile = xmlConfig.getTplClsFile();
					compileTemplate(cfg, basePath, repositoryPath, settings, clsName, templatePath,  TemplateConfig.getDestPath(), collectInlineJs,collectInlineCss, debugMode, forceReloadJsCss);
				}
				CppOutput.writeJsCppFile(TemplateConfig.getDestPath().resolve("compiledtemplate"), collectInlineJs);
				CppOutput.writeCssCppFile(TemplateConfig.getDestPath().resolve("compiledtemplate"), collectInlineCss);
			}
			
			
//			
//			result.toCpp(out); 

			
			
//			Files.write(Paths.get("D:\\Temp\\test.cpp"),out.toString().getBytes(Charset.forName("UTF-8"))  , StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
