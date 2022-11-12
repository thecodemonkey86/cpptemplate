package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import config.TemplateConfig;
import model.debugger.DebuggerVariableList;


public class AttrValue {

	private List<IAttrValueElement> elements;
	
	public AttrValue() {
		elements = new ArrayList<>();
	}
	
	public AttrValue(IAttrValueElement e) {
		this();
		elements.add(e);
	}
	
	public AttrValue(String s) {
		this();
		elements.add(new TextAttrValueElement( s));
	}
	
	public void addElement(IAttrValueElement element) {
		this.elements.add(element);
	}
	
	public List<IAttrValueElement> getElements() {
		return elements;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		for(IAttrValueElement e:elements) {
			sb.append(e.toString());
		}
		return sb.toString();
	}

	public void directRender(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException {
		for(IAttrValueElement e:elements) {
			e.directRender(out, cfg, mainParserResult, variables);
		}
		
	}

}
