package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import config.TemplateConfig;
import io.CppOutput;

public class ParserResult {

	protected List<TplPreprocessorTag> preprocessorTags;
	protected List<CppSectionTag> templateRegionTags; // if layout template referencing multiple template regions
	protected Template simpleTemplate;  // if simple template
//	protected List<CppSectionTag> sectionTags;
	protected ParserResult parentParserResult;
	protected boolean hasLayoutTemplate;
	
	public ParserResult() {
		preprocessorTags = new ArrayList<>();
		templateRegionTags = null;
	}
	
	public void addPreprocessorTag(TplPreprocessorTag ppTag) {
		this.preprocessorTags.add(ppTag);
	}
	
	public List<TplPreprocessorTag> getPreprocessorTags() {
		return preprocessorTags;
	}
	
	public List<CppSectionTag> getTemplateRegionTags() {
		return templateRegionTags;
	}
	
	public void addTemplateTag(CppSectionTag tpl) throws IOException {
//		if (simpleTemplate != null) {
//			throw new IOException("illegal state");
//		}
		if (this.templateRegionTags == null) {
			this.templateRegionTags = new ArrayList<>();
		}
		this.templateRegionTags.add(tpl);
	}
	
	public void setSimpleTemplate(Template simpleTemplate) throws IOException {
		if (hasLayoutTemplate) {
			throw new IOException("illegal state");
		}
		this.simpleTemplate = simpleTemplate;
	}
	
	public Template getSimpleTemplate() {
		return simpleTemplate;
	}
	
//	public boolean isSimpleTemplate() {
//		return simpleTemplate != null;
//	}
//	
//	public boolean isMultiTemplate() {
//		return templateRegionTags != null;
//	}

	public void setHasLayoutTemplate() throws IOException {
		hasLayoutTemplate = true;		
	}
	
	public boolean isSimpleTemplate() {
		return !hasLayoutTemplate;
	}
	
	public boolean hasLayoutTemplate() {
		return hasLayoutTemplate;
	}
	
	public void addNode(AbstractNode node) throws IOException {
		if (isSimpleTemplate()) {
			this.simpleTemplate.addTag(node);
		} else {
			if (this.templateRegionTags == null || this.templateRegionTags.size() == 0) {
				throw new IOException("illegal state");
			}
			this.templateRegionTags.get(this.templateRegionTags.size()-1).addChildNode(node);
		}
	}
	
	public void setParentParserResult(ParserResult parentParserResult) {
		this.parentParserResult = parentParserResult;
	}
	
	public ParserResult getParentParserResult() {
		return parentParserResult;
	}

	public CppSectionTag getTemplateByName(String name) throws IOException {
		for(CppSectionTag t : templateRegionTags) {
			if (t.getAttrByName("name").getStringValue().equals(name)) {
				return t;
			}
		}
		throw new IOException("no such element: "+name);
	}

	
//	public void addNode(AbstractNode tag) throws IOException {
//		if (templateTags != null) {
//			throw new IOException("illegal state");
//		}
//		if (this.nodes == null) {
//			this.nodes = new ArrayList<>();
//		}
//		this.nodes.add(tag);
//	}
//
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		if(isSimpleTemplate()) {
			simpleTemplate.toCpp(out,directTextOutputBuffer,cfg);
			CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer,cfg);
		} else {
			
		}
		
	}

	public LinkedHashSet<String> getAllCssIncludes() {
		LinkedHashSet<String> includeCss = null;
		if(parentParserResult == null) {
			includeCss = new LinkedHashSet<>();
		} else {
			includeCss = parentParserResult.getAllCssIncludes();
		}
		for(TplPreprocessorTag t : preprocessorTags) {
			for(String css : t.getIncludeCss()) {
				includeCss.add(css.replace('\\', '/'));
			}
		}
		return includeCss;
	}
	
	public LinkedHashSet<String> getAllJsIncludes() {
		LinkedHashSet<String> includeJs = null;
		if(parentParserResult == null) {
			includeJs = new LinkedHashSet<>();
		} else {
			includeJs = parentParserResult.getAllJsIncludes();
		}
		for(TplPreprocessorTag t : preprocessorTags) {
			for(String js : t.getIncludeJs()) {
				includeJs.add(js.replace('\\', '/'));
			}
		}
		return includeJs;
	}

/*	public void addSectionTag(CppSectionTag section) {
		if(	this.sectionTags==null)
			this.sectionTags = new ArrayList<>();
		this.sectionTags.add(section);
	}
	
	public CppSectionTag getSection(String name) {
		for(CppSectionTag s : sectionTags) {
			if(s.getAttrByName("name").getStringValue().equals(name)) {
				return s;
			}
		}
		if(parentParserResult!=null) {
			return parentParserResult.getSection(name);
		}
		return null;
	}*/

}
