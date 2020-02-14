package model;

import java.io.IOException;

public class CppButtonTag extends CppHtmlTagWithCodeConditionalAttributes {
	public static final String TAG_NAME = "button" ;
	public CppButtonTag() throws IOException {
		super(TAG_NAME);
	}
 
}
