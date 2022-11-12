package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.debugger.DebuggerVariableList;

import config.TemplateConfig;
import io.CppOutput;

public class Template implements ITemplateItem {
	protected List<ITemplateItem> tags;
	
	public Template() {
		tags = new ArrayList<>();
	}
	
	public void addTag(ITemplateItem tag) {
		this.tags.add(tag);
	}
	@Override
	public void directRenderCollectVariables(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult) {
		throw new RuntimeException("not implemented");
		
	}
//	public void replaceRenderTags(Template tmpl, String name) throws IOException {
//		for(int i=0;i<tags.size();i++) {
//			if (tags.get(i) instanceof CppRenderTemplateTag) {
//				if ( ((CppRenderTemplateTag)tags.get(i)).getAttrByName("name").equals(name)) {
//					tags.set(i, tmpl);
//					return;
//				}
//				//
//			}
//		}
//	}
	
	public void walkTree(TemplateConfig cfg,WalkTreeAction action,ParserResult parserResult) throws IOException {
		if (this.tags != null) {
			for(ITemplateItem n:tags) {
				n.walkTree(cfg,action, parserResult);
			}
		}
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		if (this.tags != null) {
			for(ITemplateItem n:tags) {
				n.toCpp(out,directTextOutputBuffer,cfg, mainParserResult);
			}
			CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		}
		
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
		if (this.tags != null) {
			for(ITemplateItem n:tags) {
				n.toCppDoubleEscaped(out,directTextOutputBuffer,cfg, mainParserResult);
			}
			CppOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		}
		
	}

	@Override
	public void directRender(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException {
		if (this.tags != null) {
			for(ITemplateItem n:tags) {
				n.directRender(out, cfg, mainParserResult, variables);
			}
		}
		
	}

	@Override
	public void directRenderDoubleEncoded(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException {
		if (this.tags != null) {
			for(ITemplateItem n:tags) {
				n.directRenderDoubleEncoded(out, cfg, mainParserResult, variables);
			}
		}
		
	}
}
