package model;

import config.TemplateConfig;
import io.parser.HtmlParser;

public class CppSectionTag extends HtmlTag {

	public static final String TAG_NAME = "section" ;
	protected String[] passedArgs;
	
	public CppSectionTag() {
		super(TAG_NAME);
		setNs(HtmlParser.CPP_NS);
	}
	
	@Override
	public void toCpp(StringBuilder out,StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		if (childNodes != null) { 
			boolean hasAttr = hasAttr("args");
			if(hasAttr) {
				HtmlAttr args = getAttrByName("args");
				String[] arguments = args.getStringValue().split(",");
				StringBuilder sb = new StringBuilder();
				if(this.passedArgs == null || arguments.length != passedArgs.length) {
					throw new RuntimeException("arguments not matching");
				}
				for(int i = 0; i < arguments.length; i++) {
					sb.append(String.format("auto %s = %s;\n", arguments[i], passedArgs[i]));
				}
				new CppCodeTag("{").toCpp(out, directTextOutputBuffer, cfg);
				CppCodeTag code = new CppCodeTag(sb.toString());
				code.toCpp(out, directTextOutputBuffer, cfg);
			}
			for(AbstractNode n:childNodes) {
				n.toCpp(out,directTextOutputBuffer,cfg);
			}
			if(hasAttr) {
				new CppCodeTag("}").toCpp(out, directTextOutputBuffer, cfg);
			}
		}
	}
	
	public void setPassedArgs(String[] passedArgs) {
		this.passedArgs = passedArgs;
	}

}
