package model;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;

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
		// TODO Auto-generated method stub
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
	
}
