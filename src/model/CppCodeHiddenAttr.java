package model;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;
import model.debugger.DebuggerVariableList;

public class CppCodeHiddenAttr extends HtmlAttr {

	public static final String NAME ="hidden";
	
	public CppCodeHiddenAttr(String name, AttrValue value) {
		super(name, value);
	}

	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) {
	}
	
	@Override
	public void toCppDoubleEscaped(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg,
			ParserResult mainParserResult) {
	}
	
	@Override
	public void preProcessAttr(HtmlTag tag) {
		super.preProcessAttr(tag);
		HtmlAttr attrClass;
		if(tag.hasAttr("class")) {
			attrClass = tag.getAttrByName("class");
		} else {
			attrClass=new HtmlAttr("class",new AttrValue());
			tag.addAttr(attrClass);
		}
		try {
			attrClass.getValue().addElement(new RawOutputSection(getStringValue()+" ? "+CodeUtil.quote(" d-none")) );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void directRender(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		if(variables.getStringAndIncrement(getStringValue()).equals("1")) {
			out.append(" ").append(NAME);
		}
	}
	
	@Override
	public void directRenderDoubleEncoded(StringBuilder out, TemplateConfig cfg, ParserResult mainParserResult,
			DebuggerVariableList variables) throws IOException {
		directRender(out, cfg, mainParserResult, variables);
	}
}
