package model;

import java.io.IOException;

public class CppInputTag extends CppHtmlTagWithDisabledAttrAsCode {
	public static final String TAG_NAME = "input" ;
	public CppInputTag() throws IOException {
		super(TAG_NAME);
	}
 
}
