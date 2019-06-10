package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CppIncludeTag {

	
//	static final String PP_TEMPLATE = "#includetemplate";
//	static final String PP_JAVASCRIPT = "#includejs";
//	static final String PP_CSS = "#includecss";
	
	public static final String CPP_TEMPLATE = "<cpp:baseTemplate";
	public static final String CPP_JAVASCRIPT = "<cpp:js";
	public static final String CPP_CSS = "<cpp:css";
	public static final String CPP_TPl_INCLUDE_END_TAG = "</cpp:include";
	public static final String CPP_TPl_INCLUDE_START_TAG = "<cpp:include";
	public static final String CPP_TPl_INCLUDE_HEADER = "<cpp:header";
	
	protected String includeLayoutTemplatePath;
	protected List<String> includeJs;
	protected List<String> includeJsLinks;
	protected List<String> includeCss;
	protected List<String> includeCssLinks;
	protected List<String> includeHeaders;
	
	public CppIncludeTag(String code, boolean cPreprocessorStyle) throws IOException {
		this.includeJs = new ArrayList<>();
		this.includeJsLinks = new ArrayList<>();
		this.includeCss = new ArrayList<>();
		this.includeCssLinks = new ArrayList<>();
		this.includeHeaders = new ArrayList<>();
		if(cPreprocessorStyle) {
			//parse(code);
			throw new RuntimeException("support for this syntax has been removed");
		} else {
			parse2(code);
		}
	}
	
	private void parse2(String code) throws IOException {
		String[] lines = code.split("\\r?\\n");
		
		for(String l:lines) {
			String l0=l.trim();
			if (l0.startsWith(CPP_TEMPLATE)) {
				int quotStart=l0.indexOf("name=\"",CPP_TEMPLATE.length());
				int quotEnd=l0.indexOf('"',quotStart+6);
				
				if (quotStart > CPP_TEMPLATE.length() && quotEnd > quotStart && includeLayoutTemplatePath == null) {
					includeLayoutTemplatePath ="layout_templates/" + l0.substring(quotStart+6,quotEnd) + ".html";
				} else {
					quotStart=l0.indexOf("src=\"",CPP_TEMPLATE.length());
					quotEnd=l0.indexOf('"',quotStart+5);
					
					if (quotStart > CPP_TEMPLATE.length() && quotEnd > quotStart && includeLayoutTemplatePath == null) {
						includeLayoutTemplatePath = l0.substring(quotStart+5,quotEnd) ;
						if(!includeLayoutTemplatePath.endsWith(".html")) {
							includeLayoutTemplatePath+=".html";
						}
						if(!includeLayoutTemplatePath.contains("/") && !includeLayoutTemplatePath.startsWith("layout_templates/")) {
							includeLayoutTemplatePath="layout_templates/" +includeLayoutTemplatePath;
						}
					}
					throw new IOException("syntax error");
				}
				
			} else if (l0.startsWith(CPP_JAVASCRIPT)) {
				int quotStart=l0.indexOf("includeType=\"",CPP_JAVASCRIPT.length());
				int quotEnd=l0.indexOf('"',quotStart+13);
				boolean inlineJs = true;
				if(quotStart > -1) {
					String includeType = l0.substring(quotStart+13,quotEnd);
					inlineJs = includeType.equals("inline");
				}
				
				quotStart=l0.indexOf("src=\"",CPP_JAVASCRIPT.length());
				quotEnd=l0.indexOf('"',quotStart+5);
				
				if (quotStart > CPP_JAVASCRIPT.length() && quotEnd > quotStart) {
					if(inlineJs) {
						includeJs.add( l0.substring(quotStart+5,quotEnd) );
					} else {
						includeJsLinks.add( l0.substring(quotStart+5,quotEnd) );
					}
					
				} else {
					throw new IOException("syntax error");
				}
			} else if (l0.startsWith(CPP_CSS)) {
				int quotStart=l0.indexOf("includeType=\"",CPP_JAVASCRIPT.length());
				int quotEnd=l0.indexOf('"',quotStart+13);
				boolean inlineCss = true;
				if(quotStart > -1) {
					String includeType = l0.substring(quotStart+13,quotEnd);
					inlineCss = includeType.equals("inline");
				}
				
				quotStart=l0.indexOf("src=\"",CPP_CSS.length());
				quotEnd=l0.indexOf('"',quotStart+5);
				
				if (quotStart > CPP_CSS.length() && quotEnd > quotStart) {
					if(inlineCss) {
						includeCss.add( l0.substring(quotStart+5,quotEnd) );
					} else {
						includeCssLinks.add( l0.substring(quotStart+5,quotEnd) );
					}
				} else {
					throw new IOException("syntax error");
				}
			} else if (l0.startsWith(CPP_TPl_INCLUDE_HEADER)) {
				int quotStart=l0.indexOf("file=\"",CPP_TPl_INCLUDE_HEADER.length());
				int quotEnd=l0.indexOf('"',quotStart+6);
				
				if (quotStart > CPP_TPl_INCLUDE_HEADER.length() && quotEnd > quotStart) {
					String h = l0.substring(quotStart+6,quotEnd);
					if(h.isEmpty()) {
						throw new IOException("header file path is empty");
					}
					includeHeaders.add( h );
				} else {
					throw new IOException("syntax error");
				}
			} 
		}
	}

	/*private void parse(String code) throws IOException {
		String[] lines = code.split("\\r?\\n");
		
		for(String l:lines) {
			String l0=l.trim();
			if (l0.startsWith(PP_TEMPLATE)) {
				int quotStart=l0.indexOf('"',PP_TEMPLATE.length());
				int quotEnd=l0.indexOf('"',quotStart+1);
				
				if (quotStart > PP_TEMPLATE.length() && quotEnd > quotStart && includeLayoutTemplatePath == null) {
					includeLayoutTemplatePath = l0.substring(quotStart+1,quotEnd) ;
				} else {
					throw new IOException("syntax error");
				}
				
			} else if (l0.startsWith(PP_JAVASCRIPT)) {
				int quotStart=l0.indexOf('"',PP_JAVASCRIPT.length());
				int quotEnd=l0.indexOf('"',quotStart+1);
				
				if (quotStart > PP_JAVASCRIPT.length() && quotEnd > quotStart) {
					includeJs.add( l0.substring(quotStart+1,quotEnd) );
				} else {
					throw new IOException("syntax error");
				}
			} else if (l0.startsWith(PP_CSS)) {
				int quotStart=l0.indexOf('"',PP_CSS.length());
				int quotEnd=l0.indexOf('"',quotStart+1);
				
				if (quotStart > PP_CSS.length() && quotEnd > quotStart) {
					includeCss.add( l0.substring(quotStart+1,quotEnd) );
				} else {
					throw new IOException("syntax error");
				}
			}
		}
	}*/

	public String getIncludeLayoutTemplatePath() {
		return includeLayoutTemplatePath;
	}
	
	public List<String> getIncludeCss() {
		return includeCss;
	}
	
	public List<String> getIncludeInlineJs() {
		return includeJs;
	}
	
	public List<String> getIncludeJsLinks() {
		return includeJsLinks;
	}
	
	public List<String> getIncludeCssLinks() {
		return includeCssLinks;
	}
	
	public List<String> getIncludeHeaders() {
		return includeHeaders;
	}

}
