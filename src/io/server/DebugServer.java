package io.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import config.TemplateConfig;
import io.parser.HtmlParser;
import model.AbstractNode;
import model.CppIncludeTag;
import model.CppRenderSectionTag;
import model.CppSectionTag;
import model.ParserResult;
import model.SubtemplatesFunctions;
import model.WalkTreeAction;
import model.debugger.DebuggerVariableList;
import model.debugger.Variable;


public class DebugServer {
	public static void serve(List<TemplateConfig> xmlConfigs) throws IOException {
		final HashMap<String, TemplateConfig> cfgs=new HashMap<String, TemplateConfig>();
		for(TemplateConfig cfg:xmlConfigs) {
			cfgs.put(cfg.getSubDir()!=null? cfg.getSubDir()+"/"+cfg.getClsName():cfg.getClsName(), cfg);
		}
		HttpServer server = HttpServer.create(new InetSocketAddress(9000), 0);
        server.createContext("/test", new HttpHandler() {

        	 
        	
			@Override
			public void handle(HttpExchange t) throws IOException {
				try {
					InputStream in= t.getRequestBody();
					InputStreamReader rd = new InputStreamReader(in);
				 
		            Gson gson = new Gson();
		            DebuggerVariableList variables = new DebuggerVariableList( gson.fromJson(rd, Variable[].class));
					
					StringBuilder response = new StringBuilder("<!DOCTYPE html><html><body>");
					 HtmlParser p=new HtmlParser();
			          TemplateConfig cfg = cfgs.get("TestView");
			            SubtemplatesFunctions subtemplatesFunctions=new SubtemplatesFunctions();
					ParserResult result=	p.parse(cfg, cfg.getTmplPath(), subtemplatesFunctions);
						
							if(result.isSimpleTemplate()) {
								result.getSimpleTemplate().directRender(response, cfg, result,variables);
							} else {
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
										Path layoutTplPath=TemplateConfig.getSrcPath().resolve(pp.getIncludeLayoutTemplatePath());
										ParserResult layoutResult = null;
										
										
										//if(basetemplateCache.containsKey(layoutTplPath.toString())) {
										//	layoutResult = basetemplateCache.get(layoutTplPath.toString()) ;
										//} else {
											layoutResult = p.parse(cfg,layoutTplPath,subtemplatesFunctions);
										//	basetemplateCache.put(layoutTplPath.toString(), layoutResult);
										//}
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
										layoutResult.getSimpleTemplate().directRender(response, cfg, layoutResult, variables);
									}
									
							}
						}
						
						response.append("</body></html>");
				  t.getRequestHeaders().put("Content-Type", Collections.singletonList("text/html"));
		            t.sendResponseHeaders(200, response.length());
		            OutputStream os = t.getResponseBody();
		            os.write(response.toString().getBytes());
		            os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
        	
        });
        server.setExecutor(null); // creates a default executor
        server.start();
	}
}
