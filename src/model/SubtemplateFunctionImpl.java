package model;

import java.io.IOException;
import java.util.Objects;

import config.TemplateConfig;

public class SubtemplateFunctionImpl {

	Subtemplate subtemplate;
	boolean doubleEncode;
	TemplateConfig cfg;
	ParserResult mainParserResult;
	
	public SubtemplateFunctionImpl(TemplateConfig cfg,ParserResult mainParserResult,Subtemplate subtemplate,boolean doubleEncode) {
		this.subtemplate = subtemplate;
		this.doubleEncode = doubleEncode;
		this.cfg = cfg;
		this.mainParserResult = mainParserResult;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return Objects.hash(subtemplate.getSubtemplateIdentifier(),doubleEncode,cfg.isRenderToString());
	}
	
	@Override
	public boolean equals(Object obj) {
		SubtemplateFunctionImpl s = (SubtemplateFunctionImpl) obj;
		return subtemplate.getSubtemplateIdentifier().equals(s.subtemplate.getSubtemplateIdentifier()) 
				&& doubleEncode == s.doubleEncode
				&& cfg.isRenderToString() == s.cfg.isRenderToString();
	}

	public String toCpp( ) throws IOException {
		StringBuilder out= new StringBuilder();
		if(doubleEncode) {
			subtemplate.toCppDoubleEscaped(out, new StringBuilder(), cfg, mainParserResult);
		}else { 
			subtemplate.toCpp(out, new StringBuilder(), cfg, mainParserResult);
		
		}
		return out.toString();
	}
	
	public String toCppHeader( ) throws IOException {
		StringBuilder out= new StringBuilder();
		subtemplate.toCppHeader(out,cfg,doubleEncode);
		return out.toString();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return  subtemplate.getSubtemplateIdentifier()+(doubleEncode?"Double":"");
	}

}
