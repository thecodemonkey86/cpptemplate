package io.parser;

import java.io.IOException;

import util.Pair;
import util.ParseUtil;
import model.AbstractNode;
import model.AttrValue;
import model.CppCodeTag;
import model.CppCommentTag;
import model.CppElseIfTag;
import model.CppElseTag;
import model.CppForTag;
import model.CppIfTag;
import model.CppRenderSubtemplateTag;
import model.CppRenderSectionTag;
import model.CppSectionTag;
import model.CppThenTag;
import model.EmptyHtmlAttr;
import model.HtmlAttr;
import model.HtmlBr;
import model.HtmlTag;
import model.ParserResult;
import model.QStringHtmlEscapedOutputSection;
import model.RawOutputSection;
import model.Template;
import model.TextAttrValueElement;
import model.TextNode;
import model.TplPreprocessorTag;

public class HtmlParser {

	public final static String CPP_CODE_TAG = "<?cpp";
	public final static String CPP_CODE_END_TAG = "?>";
	public final static String CPP_TAG = "<cpp:";
	public final static String HTML_END_TAG = "</";
	public final static char CPP_INLINE_END = '}';
	public final static char CPP_INLINE_START = '{';
	public final static String CPP_INLINE_RAW_END = "}}";
	public final static String CPP_INLINE_RAW_START = "{{";
	public final static String CPP_NS = "cpp";
	public final static String HTML_COMMENT_START = "<!--";
	public final static String HTML_COMMENT_END = "-->";
	protected static int lineWidth;

	public static void setLineWidth(int lineWidth) {
		HtmlParser.lineWidth = lineWidth;
	}
	
	public static int getLineWidth() {
		return lineWidth;
	}
	
	protected String html;
	protected int currentPos;
	protected ParserResult result;
	
	
	
	public ParserResult parse(String html) throws IOException {
		this.currentPos = 0;
		this.html = html;
		this.result=new ParserResult();
		parseRoot();
		
		return this.result;
	}
	
	protected boolean atEnd() {
		return currentPos >= html.length();
	}
	
	protected void next()  {
		currentPos++;
	}
	protected void next(int offset)  {
		currentPos+=offset;
	}
	protected void setPos(int pos) {
		currentPos = pos;
	}
	protected char currChar() throws IOException {
		if(atEnd()) {
			throw new IOException("syntax error");
		}
		return html.charAt(currentPos);
	}
	protected boolean currSubstrEquals(String substr) throws IOException {
		if(atEnd()) {
			throw new IOException("syntax error");
		}
		return html.regionMatches(currentPos, substr, 0, substr.length());
	}
	
	protected boolean isCurrTagCppBeginPreprocessor() {
		int startIndexCodeSection = currentPos + HtmlParser.CPP_CODE_TAG.length();
		for (int k = startIndexCodeSection; k < html.length(); k++) {
			switch (html.charAt(k)) {
			case ' ':
			case '\t':
			case '\n':
			case '\r':
			case '/':
				continue;
			case '#':
				return true;
			default:
				return false;
			}
		}
		return false;
	}
	
	private void addTextNode(int startIndex,ParserResult result) throws IOException {
		String text = html.substring(startIndex,currentPos);
		
		if (!text.isEmpty())
			result.addNode(new TextNode(text));
		
	}
	
	private void addTextNodeToEnd(int startIndex,ParserResult result) throws IOException {
		String text = html.substring(startIndex);
		
		if (!text.isEmpty())
			result.addNode(new TextNode(text));
		
	}
	
	private void addTextNode(HtmlTag tag, int startIndex) throws IOException {
		String text = html.substring(startIndex,currentPos);
		if (!text.isEmpty())
			tag.addChildNode(new TextNode(html.substring(startIndex,currentPos)));
		
	}
	
	private void checkInitSimpleTemplate() throws IOException {
		if(result.getSimpleTemplate() == null && !result.hasLayoutTemplate()) {
			result.setSimpleTemplate(new Template());
		} else if(result.hasLayoutTemplate()) {
			throw new IOException("illegal state");
		}
	}
	
	protected void parseRoot() throws IOException {
		
		int startIndex = 0;
		while(!atEnd()) {
			if (currSubstrEquals(HtmlParser.CPP_CODE_TAG)) {
				
				//next();
				
				if(isCurrTagCppBeginPreprocessor()) {
					addTextNode(startIndex, result);
					result.addPreprocessorTag(parseCppPreprocessorCodeSection());
					startIndex = currentPos; 
				} else {
					checkInitSimpleTemplate();
					addTextNode(startIndex, result);
					result.addNode(parseCodeTag());
					startIndex = currentPos+HtmlParser.CPP_CODE_END_TAG.length(); 
				}
			} else if(currSubstrEquals(TplPreprocessorTag.CPP_TPl_INCLUDE_START_TAG)) {
				result.addPreprocessorTag(parseCppIncludeTag());
				startIndex = currentPos; 
				
			} else if(currSubstrEquals( HtmlParser.CPP_INLINE_RAW_START )) {
				checkInitSimpleTemplate();
				addTextNode( startIndex, result);
				result.addNode(parseRawOutputSection());
				
				startIndex = currentPos+CPP_INLINE_RAW_START.length();			
			} else if(currChar() == HtmlParser.CPP_INLINE_START ) {
				checkInitSimpleTemplate();
				addTextNode( startIndex, result);
				result.addNode(parseQStringHtmlEscapedOutputSection());
				
				startIndex = currentPos+1;	
//			} else if(currSubstrEquals(String.format("<%s:%s", HtmlParser.CPP_NS,CppSectionTag.TAG_NAME))) {
//				result.addSectionTag(parseSectionTag());
//				startIndex = currentPos+1;	
			} else if(currChar() == '<') {
				if (!currSubstrEquals(String.format("<%s:%s", HtmlParser.CPP_NS, CppSectionTag.TAG_NAME))) {
					if (!checkSkipHtmlComment()) {
					
						checkInitSimpleTemplate();
						addTextNode(startIndex, result);
						next();
						result.addNode(parseNode());
					}
				} else {
					String text = html.substring(startIndex,currentPos).trim();
					
					if (!text.isEmpty()) {
						throw new IOException("invalid characters");
					}
					
					next();
					AbstractNode node =  parseNode();
					if (!(node instanceof CppSectionTag)) {
						throw new IOException(String.format("expected %s tag",CppSectionTag.TAG_NAME));
					}
					result.addTemplateTag((CppSectionTag)node);
				}
				
				
				startIndex = currentPos+1; 
				
			}
			next();
		}
		if(startIndex < html.length() && !html.substring(startIndex).trim().isEmpty()) {
			checkInitSimpleTemplate();
			addTextNodeToEnd(startIndex, result);
			
		}
	}

	/*private CppSectionTag parseSectionTag() throws IOException {
		String endTag = String.format("</%s:%s",CppSectionTag.TAG_NAME );
		next(CppSectionTag.TAG_NAME.length() + 1);
		int startIndex = currentPos;
		CppSectionTag tag = new CppSectionTag();
		boolean quot = false;
		boolean escape = false;
		while(!atEnd()) {
			if (!quot && currSubstrEquals(endTag)) {
				tag.setHtml(html.substring(startIndex,currentPos));
				return tag;
			} else if(!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			}
			next();
		}
		throw new IOException("missing end tag: "+endTag);
	}*/

	private boolean checkSkipHtmlComment() throws IOException {
		if (currSubstrEquals(HTML_COMMENT_START)) {
			currentPos = html.indexOf(HTML_COMMENT_END, currentPos + HTML_COMMENT_START.length()) + HTML_COMMENT_END.length() - 1;
			return true;
		}
		return false;
	}

	private void addTextNode(AttrValue val,int startIndex) {
		String text = html.substring(startIndex,currentPos);
		if (!text.isEmpty())
			val.addElement(new TextAttrValueElement(text));
	}
	
	protected TplPreprocessorTag parseCppIncludeTag() throws IOException {
		next(TplPreprocessorTag.CPP_TPl_INCLUDE_END_TAG.length() + 1);
		int startIndex = currentPos;
		
		boolean quot = false;
		boolean escape = false;
		while(!atEnd()) {
			if (!quot && currSubstrEquals(TplPreprocessorTag.CPP_TPl_INCLUDE_END_TAG)) {
				TplPreprocessorTag tag = new TplPreprocessorTag(html.substring(startIndex,currentPos),false);
				next(TplPreprocessorTag.CPP_TPl_INCLUDE_END_TAG.length());
				while(!atEnd()&&currChar()!='>') {
					next();
				}
				next();
				if(tag.getIncludeLayoutTemplatePath()!=null) {
					result.setHasLayoutTemplate();
				}
				return tag;
			} else if(!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			}
			next();
		}
		TplPreprocessorTag tag = new TplPreprocessorTag(html.substring(startIndex),false);
		if(tag.getIncludeLayoutTemplatePath()!=null) {
			result.setHasLayoutTemplate();
		}
		return tag;
	}
	
	protected HtmlAttr parseAttr() throws IOException {
		Pair<String, Integer> pEq = ParseUtil.getIndexAndSubstrToNextChar(html, currentPos, '=');
		int indexEq = pEq.getValue2();
		String attrName = pEq.getValue1().trim();
		if (!attrName.matches("[a-zA-Z-]+")) {
			String[] arr = attrName.split("\\s");
			
			if(arr[0].matches("[a-zA-Z-]+")) {
				currentPos += arr[0].length();
				return new EmptyHtmlAttr(arr[0]);
			}
			
			throw new IOException(String.format("syntax error. Attr name [%s] must match [a-zA-Z-]+", attrName));
		}
		Pair<Integer, Character> pQuot = ParseUtil.firstIndexOf(html, '\"', '\'', indexEq);
		int indexQuot = pQuot.getValue1();
		if (indexQuot == -1) {
			throw new IOException("syntax error. Expected single or double quote");
		}
		setPos(indexQuot + 1);
		AttrValue val = new AttrValue();
		int startIndex = currentPos;
		while(!atEnd()) {
			if(currSubstrEquals(HtmlParser.CPP_CODE_TAG)) {
				addTextNode(val, startIndex);
				
				val.addElement(parseCodeTag());
				
				startIndex = currentPos + HtmlParser.CPP_CODE_END_TAG.length();
			} else if(currSubstrEquals( HtmlParser.CPP_INLINE_RAW_START )) {
				addTextNode(val, startIndex);
				
				val.addElement(parseRawOutputSection());
				
				startIndex = currentPos+CPP_INLINE_RAW_START.length();
			} else if(currChar() == HtmlParser.CPP_INLINE_START ) {
				addTextNode(val, startIndex);
				
				val.addElement(parseQStringHtmlEscapedOutputSection());
				
				startIndex = currentPos+1;
			} else if(currChar() == pQuot.getValue2()) {
				addTextNode(val, startIndex);
				
				HtmlAttr attr = new HtmlAttr(attrName, val, pQuot.getValue2());
				setPos(currentPos);
				return attr ;
			}
			next();
		}
		throw new IOException("syntax error. missing closing quote");
		
	}

	protected HtmlTag parseNode() throws IOException {
		String namespaceTagName = ParseUtil.substrToNextChar(html, new char[] {' ', '\r', '\t', '\n', '>'}, currentPos);
		String ns = null;
		String tagName = null;
		HtmlTag tag = null;
		
		if (namespaceTagName.contains(":")) {
			String[] parts = namespaceTagName.split(":");
			ns = parts[0];
			tagName = parts[1];
			if (ns.equals(HtmlParser.CPP_NS)) {
				if (tagName.equals(CppSectionTag.TAG_NAME)) {
					tag = new CppSectionTag();
				} else if (tagName.equals(CppRenderSectionTag.TAG_NAME)) {
					tag = new CppRenderSectionTag();
				} else if (tagName.equals(CppForTag.TAG_NAME)) {
					tag = new CppForTag();
				} else if (tagName.equals(CppIfTag.TAG_NAME)) {
					tag = new CppIfTag();
				} else if (tagName.equals(CppElseIfTag.TAG_NAME)) {
					tag = new CppElseIfTag();
				} else if (tagName.equals(CppThenTag.TAG_NAME)) {
					tag = new CppThenTag();
				} else if (tagName.equals(CppElseTag.TAG_NAME)) {
					tag = new CppElseTag();
				} else if (tagName.equals(CppCommentTag.TAG_NAME)) {
					tag = new CppCommentTag();
				} else if (tagName.equals(CppRenderSubtemplateTag.TAG_NAME)) {
					tag = new CppRenderSubtemplateTag();
				} 
			}
		} else {
			tagName = namespaceTagName;
			if (tagName.equals(HtmlBr.TAG_NAME)) {
				tag = new HtmlBr();
				next(HtmlBr.TAG_NAME.length());
				while(currChar() != '>') {
					switch (currChar()) {
					case '/':
					case '\n':
					case '\r':
					case ' ':
					case '\t':
						break;
					default:
						throw new IOException(String.format("illegal character %s", currChar()));
					}
					next();
				}
				return tag;
			} else {
				tag=new HtmlTag(tagName);
			}
		}
		
		if (tag == null ) {
			throw new IOException("tag " +namespaceTagName + " not supported");
		}
		
		next(namespaceTagName.length());
		while(!atEnd() && currChar() != '>') {
			if(Character.isAlphabetic(currChar()) || currChar() == '-') {
				tag.addAttr(parseAttr());
			} else if (currChar() == '/') {
				tag.setSelfClosing();
				while(!atEnd() && currChar() != '>') {
					next();
				}
				return tag;
			}
			next();
		}
		
		if (HtmlTag.isVoidTag( namespaceTagName)) {
			return tag;
		}
		next();
		parseTagContent(tag,currentPos);
		return tag;
	}
	
	protected CppCodeTag parseCodeTag() throws IOException {
		next(HtmlParser.CPP_CODE_TAG.length() + 1);
		int startIndex = currentPos;
		
		boolean quot = false;
		boolean escape = false;
		while(!atEnd()) {
			if (!quot && currSubstrEquals(HtmlParser.CPP_CODE_END_TAG)) {
				CppCodeTag tag = new CppCodeTag(html.substring(startIndex,currentPos));
				return tag;
			} else if(!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			}
			next();
		}
		CppCodeTag tag = new CppCodeTag(html.substring(startIndex));
		return tag;
	}
	
	protected QStringHtmlEscapedOutputSection parseQStringHtmlEscapedOutputSection() throws IOException {
		next();
		int startIndex = currentPos;
		
		boolean quot = false;
		boolean escape = false;
		int leftBraceCount = 0;
		while(!atEnd()) {
			if (!quot && leftBraceCount == 0 &&  currChar() == HtmlParser.CPP_INLINE_END) {
				QStringHtmlEscapedOutputSection section = new QStringHtmlEscapedOutputSection(html.substring(startIndex,currentPos));
				return section;
			} else if(!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			} else if (!quot && currChar() == HtmlParser.CPP_INLINE_START) {
				leftBraceCount++;
			} else if (!quot && currChar() == HtmlParser.CPP_INLINE_END) {
				leftBraceCount--;
			}
			next();
		}
		QStringHtmlEscapedOutputSection section = new QStringHtmlEscapedOutputSection(html.substring(startIndex));
		return section;
	}
	
	protected RawOutputSection parseRawOutputSection() throws IOException {
		next(HtmlParser.CPP_INLINE_RAW_START.length());
		int startIndex = currentPos;
		
		boolean quot = false;
		boolean escape = false;
		while(!atEnd()) {
			if (!quot &&  currSubstrEquals(HtmlParser.CPP_INLINE_RAW_END)) {
				RawOutputSection section = new RawOutputSection(html.substring(startIndex,currentPos));
				return section;
			} else if(!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			}
			next();
		}
		RawOutputSection section = new RawOutputSection(html.substring(startIndex));
		return section;
	}
	
	protected void parseTagContent(HtmlTag tag, int startIndex) throws IOException {
		while(!atEnd()) {
			if (currSubstrEquals(HtmlParser.HTML_END_TAG)) {
				addTextNode(tag,startIndex);
				startIndex = currentPos; 
				next(HtmlParser.HTML_END_TAG.length());
				if(!currSubstrEquals(tag.getNamespaceAndTagName())) {
					//System.out.println(html.substring(currentPos,currentPos+10));
					throw new IOException("end tag does not match: " + tag.getNamespaceAndTagName()+", pos "+currentPos);
				}
				next(tag.getNamespaceAndTagName().length());
				while(currChar() != '>') {
					next();
				}
				return;
			} else if(currSubstrEquals(HtmlParser.CPP_CODE_TAG)) {
				addTextNode(tag,startIndex);
				startIndex = currentPos; 
				//next();
				if(isCurrTagCppBeginPreprocessor()) {
					result.addPreprocessorTag(parseCppPreprocessorCodeSection());
				} else {
					tag.addChildNode(parseCodeTag());
				}
				startIndex = currentPos+HtmlParser.CPP_CODE_END_TAG.length(); 
			} else if(currSubstrEquals( HtmlParser.CPP_INLINE_RAW_START )) {
				addTextNode( tag,startIndex);
				startIndex = currentPos; 
				tag.addChildNode(parseRawOutputSection());
				
				startIndex = currentPos+CPP_INLINE_RAW_START.length();	
			} else if(currChar() == HtmlParser.CPP_INLINE_START ) {
				addTextNode( tag,startIndex);
				startIndex = currentPos; 
				tag.addChildNode(parseQStringHtmlEscapedOutputSection());
				
				startIndex = currentPos+1;
			} else if(currChar() == '<') {
				if (!checkSkipHtmlComment()) {
					addTextNode(tag,startIndex);
					startIndex = currentPos; 
					next();
					
					tag.addChildNode(parseNode());
				}
				startIndex = currentPos+1; 
				
			} 
			next();
		} 
	}
	
	protected TplPreprocessorTag parseCppPreprocessorCodeSection() throws IOException {
		next(HtmlParser.CPP_CODE_TAG.length() + 1);
		int startIndex = currentPos;
		
		boolean quot = false;
		boolean escape = false;
		while(!atEnd()) {
			if (!quot && currSubstrEquals(HtmlParser.CPP_CODE_END_TAG)) {
				TplPreprocessorTag tag = new TplPreprocessorTag(html.substring(startIndex,currentPos),true);
				if(tag.getIncludeLayoutTemplatePath()!=null) {
					result.setHasLayoutTemplate();
				}
				next(HtmlParser.CPP_CODE_END_TAG.length());
				return tag;
			} else if(!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			}
			next();
		}
		TplPreprocessorTag tag = new TplPreprocessorTag(html.substring(startIndex),true);
		if(tag.getIncludeLayoutTemplatePath()!=null) {
			result.setHasLayoutTemplate();
		}
		return tag;
	}
		
}
