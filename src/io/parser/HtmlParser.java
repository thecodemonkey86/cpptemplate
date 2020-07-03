package io.parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import config.TemplateConfig;
import util.Pair;
import util.ParseUtil;
import model.AbstractNode;
import model.AttrValue;
import model.CppButtonTag;
import model.CppCaseTag;
import model.CppCodeCheckedAttr;
import model.CppCodeDisabledAttr;
import model.CppCodeHiddenAttr;
import model.CppCodeSelectedAttr;
import model.CppCodeTag;
import model.CppCommentTag;
import model.CppElseIfTag;
import model.CppElseTag;
import model.CppForTag;
import model.CppFormSelect;
import model.CppFormSelectOption;
import model.CppIfTag;
import model.CppRenderSubtemplateTag;
import model.CppRenderSectionTag;
import model.CppSectionTag;
import model.CppSubtemplateTag;
import model.CppSwitchTag;
import model.CppThenTag;
import model.CppTranslate;
import model.DynamicHtmlAttr;
import model.EmptyHtmlAttr;
import model.HtmlAttr;
import model.HtmlBr;
import model.HtmlStyleTag;
import model.HtmlTag;
import model.IAttrValueElement;
import model.ParserResult;
import model.QStringHtmlEscapedOutputSection;
import model.RawOutputSection;
import model.RenderTagAsAttrValue;
import model.Subtemplate;
import model.SubtemplatesFunctions;
import model.Template;
import model.TextAttrValueElement;
import model.TextNode;
import model.CppIncludeTag;
import model.CppInputTag;

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
	protected Path filePath;
	protected TemplateConfig cfg;

	public ParserResult parse(TemplateConfig cfg, Path filePath, SubtemplatesFunctions subtemplatesFunctions)
			throws IOException {
		this.filePath = filePath;
		this.cfg = cfg;
		this.currentPos = 0;
		this.html = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
		this.result = new ParserResult(subtemplatesFunctions);
		parseRoot();

		return this.result;
	}

	protected boolean atEnd() {
		return currentPos >= html.length();
	}

	protected void next() {
		currentPos++;
	}

	protected void next(int offset) {
		currentPos += offset;
	}

	protected void setPos(int pos) {
		currentPos = pos;
	}

	protected char currChar() throws IOException {
		if (atEnd()) {
			throw new IOException(
					"syntax error in file " + filePath + ",  at position " + currentPos + " in html " + html);
		}
		return html.charAt(currentPos);
	}

	protected boolean currSubstrEquals(String substr) throws IOException {
		if (atEnd()) {
			throw new IOException(
					"syntax error in file " + filePath + ",  at position " + currentPos + " in html " + html);
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

	private void addTextNode(int startIndex, ParserResult result) throws IOException {
		String text = html.substring(startIndex, currentPos);

		if (!text.isEmpty())
			result.addNode(new TextNode(text));

	}

	private void addTextNodeToEnd(int startIndex, ParserResult result) throws IOException {
		String text = html.substring(startIndex);

		if (!text.isEmpty())
			result.addNode(new TextNode(text));

	}

	private void addTextNode(HtmlTag tag, int startIndex) throws IOException {
		String text = html.substring(startIndex, currentPos);
		if (!text.isEmpty())
			tag.addChildNode(new TextNode(html.substring(startIndex, currentPos)));

	}

	private void checkInitSimpleTemplate() throws IOException {
		if (result.getSimpleTemplate() == null && !result.hasLayoutTemplate()) {
			result.setSimpleTemplate(new Template());
		} else if (result.hasLayoutTemplate()) {
			throw new IOException("illegal state");
		}
	}

	protected void parseRoot() throws IOException {
		int startIndex = 0;
		while (!atEnd()) {
			if (currSubstrEquals(HtmlParser.CPP_CODE_TAG)) {

				// next();

				if (isCurrTagCppBeginPreprocessor()) {
					addTextNode(startIndex, result);
					result.addPreprocessorTag(parseCppPreprocessorCodeSection());
					startIndex = currentPos;
				} else {
					checkInitSimpleTemplate();
					addTextNode(startIndex, result);
					result.addNode(parseCodeTag());
					startIndex = currentPos + HtmlParser.CPP_CODE_END_TAG.length();
				}
			} else if (currSubstrEquals(CppIncludeTag.CPP_TPl_INCLUDE_START_TAG)) {
				result.addPreprocessorTag(parseCppIncludeTag());
				startIndex = currentPos;

			} else if (currSubstrEquals(HtmlParser.CPP_INLINE_RAW_START)) {
				checkInitSimpleTemplate();
				addTextNode(startIndex, result);
				result.addNode(parseRawOutputSection());

				startIndex = currentPos + CPP_INLINE_RAW_START.length();
			} else if (currChar() == HtmlParser.CPP_INLINE_START) {
				checkInitSimpleTemplate();
				addTextNode(startIndex, result);
				result.addNode(parseQStringHtmlEscapedOutputSection());

				startIndex = currentPos + 1;
//			} else if(currSubstrEquals(String.format("<%s:%s", HtmlParser.CPP_NS,CppSectionTag.TAG_NAME))) {
//				result.addSectionTag(parseSectionTag());
//				startIndex = currentPos+1;	
			} else if (currChar() == '<') {
				if (!currSubstrEquals(String.format("<%s:%s", HtmlParser.CPP_NS, CppSectionTag.TAG_NAME))) {
					checkInitSimpleTemplate();
					addTextNode(startIndex, result);
					if (!checkSkipHtmlComment()) {

						next();
						result.addNode(parseNode());
					}
				} else {
					String text = html.substring(startIndex, currentPos).trim();

					if (!text.isEmpty()) {
						throw new IOException("invalid characters");
					}

					next();
					AbstractNode node = parseNode();
					if (!(node instanceof CppSectionTag)) {
						throw new IOException(String.format("expected %s tag", CppSectionTag.TAG_NAME));
					}
					result.addTemplateTag((CppSectionTag) node);
				}

				startIndex = currentPos + 1;

			}
			next();
		}
		if (startIndex < html.length() && !html.substring(startIndex).trim().isEmpty()) {
			checkInitSimpleTemplate();
			addTextNodeToEnd(startIndex, result);
		}
	}

	/*
	 * private CppSectionTag parseSectionTag() throws IOException { String endTag =
	 * String.format("</%s:%s",CppSectionTag.TAG_NAME );
	 * next(CppSectionTag.TAG_NAME.length() + 1); int startIndex = currentPos;
	 * CppSectionTag tag = new CppSectionTag(); boolean quot = false; boolean escape
	 * = false; while(!atEnd()) { if (!quot && currSubstrEquals(endTag)) {
	 * tag.setHtml(html.substring(startIndex,currentPos)); return tag; } else
	 * if(!escape && currChar() == '\"') { quot = !quot; } else if (!escape &&
	 * currChar() == '\\') { escape = true; } else if (escape) { escape = false; }
	 * next(); } throw new IOException("missing end tag: "+endTag); }
	 */

	private boolean checkSkipHtmlComment() throws IOException {
		if (currSubstrEquals(HTML_COMMENT_START)) {
			currentPos = html.indexOf(HTML_COMMENT_END, currentPos + HTML_COMMENT_START.length())
					+ HTML_COMMENT_END.length() - 1;
			return true;
		} else if (currSubstrEquals(String.format("<%s:%s", HtmlParser.CPP_NS, CppCommentTag.TAG_NAME))) {
			String endOfComment = String.format("</%s:%s>", HtmlParser.CPP_NS, CppCommentTag.TAG_NAME);
			currentPos = html.indexOf(endOfComment, currentPos + endOfComment.length()) + endOfComment.length() - 1;
			return true;
		}
		return false;
	}

	private void addTextNode(AttrValue val, int startIndex) {
		String text = html.substring(startIndex, currentPos);
		if (!text.isEmpty())
			val.addElement(new TextAttrValueElement(text));
	}

	protected CppIncludeTag parseCppIncludeTag() throws IOException {
		next(CppIncludeTag.CPP_TPl_INCLUDE_END_TAG.length() + 1);
		int startIndex = currentPos;

		boolean quot = false;
		boolean escape = false;
		while (!atEnd()) {
			if (!quot && currSubstrEquals(CppIncludeTag.CPP_TPl_INCLUDE_END_TAG)) {
				CppIncludeTag tag = new CppIncludeTag(html.substring(startIndex, currentPos), false);
				next(CppIncludeTag.CPP_TPl_INCLUDE_END_TAG.length());
				while (!atEnd() && currChar() != '>') {
					next();
				}
				next();
				if (tag.getIncludeLayoutTemplatePath() != null) {
					result.setHasLayoutTemplate();
				}
				return tag;
			} else if (!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			}
			next();
		}
		CppIncludeTag tag = new CppIncludeTag(html.substring(startIndex), false);
		if (tag.getIncludeLayoutTemplatePath() != null) {
			result.setHasLayoutTemplate();
		}
		return tag;
	}

	protected HtmlAttr parseDynamicAttr() throws IOException {
		int bracesCount = 0;
		boolean quot = false;
		boolean escape = false;
		next();
		int start = currentPos;

		while (!atEnd() && currChar() != '}' && bracesCount == 0) {
			if (!quot) {
				if (currChar() == '{') {
					bracesCount++;
				} else if (currChar() == '}') {
					bracesCount--;
				}
			} else {
				if (!escape && currChar() == '\"') {
					quot = !quot;
				} else if (!escape && currChar() == '\\') {
					escape = true;
				} else if (escape) {
					escape = false;
				}
			}
			next();
		}
		if (quot) {
			throw new IOException("unterminated quote near position " + currentPos + " in html " + html);
		}
		return new DynamicHtmlAttr(html.substring(start, currentPos));
	}

	protected HtmlAttr parseAttr() throws IOException {
		Pair<Integer, Character> firstIndexOf = ParseUtil.firstIndexOf(html,
				new char[] { ' ', '=', '>', '\r', '\n', '\t' }, currentPos);

		String attrName = html.substring(currentPos, firstIndexOf.getValue1()).trim();

		if (!attrName.matches("[a-zA-Z-:]+")) {
			throw new IOException(String.format("syntax error in file " + filePath + ",  at position " + currentPos
					+ " in html " + html + ". Attr name [%s] must match [a-zA-Z-]+", attrName));
		}
		if (firstIndexOf.getValue2() != '=') {
			currentPos += attrName.length() - 1;
			return new EmptyHtmlAttr(attrName);
		}

		Pair<Integer, Character> pQuot = ParseUtil.firstIndexOf(html, '\"', '\'', firstIndexOf.getValue1());
		int indexQuot = pQuot.getValue1();
		if (indexQuot == -1) {
			throw new IOException("syntax error in file " + filePath + ",  at position " + currentPos + " in html "
					+ html + ". Expected single or double quote");
		}
		setPos(indexQuot + 1);
		AttrValue val = new AttrValue();
		int startIndex = currentPos;
		while (!atEnd()) {
			if (currSubstrEquals(HtmlParser.CPP_CODE_TAG)) {
				addTextNode(val, startIndex);

				val.addElement(parseCodeTag());

				startIndex = currentPos + HtmlParser.CPP_CODE_END_TAG.length();
			} else if (currSubstrEquals(String.format("<%s:%s", HtmlParser.CPP_NS, CppRenderSubtemplateTag.TAG_NAME))) {
				addTextNode(val, startIndex);

				val.addElement(parseRenderSubtemplateAttributeValue());
				startIndex = currentPos + 1;
			} else if (currSubstrEquals(String.format("<%s:%s", HtmlParser.CPP_NS, CppTranslate.TAG_NAME))) {
				addTextNode(val, startIndex);

				val.addElement(parseTranslateAttributeValue());
				startIndex = currentPos + 1;
			} else if (currSubstrEquals(HtmlParser.CPP_INLINE_RAW_START)) {
				addTextNode(val, startIndex);

				val.addElement(parseRawOutputSection());

				startIndex = currentPos + CPP_INLINE_RAW_START.length();
			} else if (currChar() == HtmlParser.CPP_INLINE_START) {
				addTextNode(val, startIndex);

				val.addElement(parseQStringHtmlEscapedOutputSection());

				startIndex = currentPos + 1;
			} else if (currChar() == pQuot.getValue2()) {
				addTextNode(val, startIndex);
				if (attrName.startsWith(CPP_NS + ":")) {
					HtmlAttr attr;
					String[] split = attrName.split(":");
					if (split.length != 2) {
						throw new IOException();
					}
					switch (split[1]) {
					case CppCodeDisabledAttr.NAME:
						attr = new CppCodeDisabledAttr(attrName, val);
						break;
					case CppCodeCheckedAttr.NAME:
						attr = new CppCodeCheckedAttr(attrName, val);
						break;
					case CppCodeHiddenAttr.NAME:
						attr = new CppCodeHiddenAttr(attrName, val);
						break;
					case CppCodeSelectedAttr.NAME:
						attr = new CppCodeSelectedAttr(attrName, val);
						break;
					default:
						throw new IOException();
					}
					setPos(currentPos);
					return attr;
				} else {
					HtmlAttr attr = new HtmlAttr(attrName, val);
					setPos(currentPos);
					return attr;
				}
			}
			next();
		}
		throw new IOException("syntax error in file " + filePath + ",  at position " + currentPos + " in html " + html
				+ ". missing closing quote");

	}

	private IAttrValueElement parseTranslateAttributeValue() throws IOException {
		int start = currentPos;
		CppTranslate t = new CppTranslate();
		RenderTagAsAttrValue val = new RenderTagAsAttrValue(t);

		boolean escape = false;

		while (!atEnd()) {
			if (!escape) {
				switch (currChar()) {
				case '\\':
					escape = true;
					break;
				case '=':
					Pair<Integer, Character> pQuot = ParseUtil.firstIndexOf(html, '\"', '\'', currentPos);
					int indexQuotEnd = html.indexOf(pQuot.getValue2(), pQuot.getValue1() + 1);
					AttrValue v = new AttrValue();
					v.addElement(new TextAttrValueElement(html.substring(pQuot.getValue1() + 1, indexQuotEnd)
							.replace("\\\\", "\\").replace("\\\"", "\"").replace("\\\'", "\'")));
					String attrName = html.substring(start, currentPos);
					if (!attrName.equals("key")) {
						throw new IOException("Expected \"key\" attribute");
					}
					t.addAttr(new HtmlAttr(attrName, v));
					currentPos = indexQuotEnd;
					start = indexQuotEnd;
					break;
				case '/':
					next();
					if (currChar() == '>') {
						return val;
					} else {
						throw new IOException("syntax error in file " + filePath + ",  at position " + currentPos
								+ " in html " + html);
					}
				case ' ':
				case '\n':
				case '\r':
				case '\t':
					start = currentPos + 1;
					break;
				default:
					break;
				}
			} else {
				escape = false;
			}
			next();
		}
		throw new IOException("syntax error in file " + filePath + ",  at position " + currentPos + " in html " + html);
	}

	private IAttrValueElement parseRenderSubtemplateAttributeValue() throws IOException {
		int start = currentPos;
		CppRenderSubtemplateTag t = new CppRenderSubtemplateTag();
		RenderTagAsAttrValue val = new RenderTagAsAttrValue(t);
		boolean escape = false;

		while (!atEnd()) {
			if (!escape) {
				switch (currChar()) {
				case '\\':
					escape = true;
					break;
				case '=':
					Pair<Integer, Character> pQuot = ParseUtil.firstIndexOf(html, '\"', '\'', currentPos);
					int indexQuotEnd = html.indexOf(pQuot.getValue2(), pQuot.getValue1() + 1);
					AttrValue v = new AttrValue();
					v.addElement(new TextAttrValueElement(html.substring(pQuot.getValue1() + 1, indexQuotEnd)
							.replace("\\\\", "\\").replace("\\\"", "\"").replace("\\\'", "\'")));
					String attrName = html.substring(start, currentPos);
					if (!attrName.equals("name") && !attrName.equals("args")) {
						throw new IOException("Expected \"name\" or \"args\" attribute");
					}
					t.addAttr(new HtmlAttr(attrName, v));
					currentPos = indexQuotEnd;
					start = indexQuotEnd;
					break;
				case '/':
					next();
					if (currChar() == '>') {
						return val;
					} else {
						throw new IOException("syntax error in file " + filePath + ",  at position " + currentPos
								+ " in html " + html);
					}
				case ' ':
				case '\n':
				case '\r':
				case '\t':
					start = currentPos + 1;
					break;
				default:
					break;
				}
			} else {
				escape = false;
			}
			next();
		}
		throw new IOException("syntax error in file " + filePath + ",  at position " + currentPos + " in html " + html);
		/*
		 * Pair<Integer, Character> nextWhitespace = ParseUtil.firstIndexOf(html, new
		 * char[]{' ','\t','\r','\n'}, currentPos); currentPos =
		 * nextWhitespace.getValue1(); Pair<String, Integer> pEq =
		 * ParseUtil.getIndexAndSubstrToNextChar(html, currentPos, '='); int indexEq =
		 * pEq.getValue2(); String attrName = pEq.getValue1().trim(); if
		 * (!attrName.equals("name")) { throw new IOException("Expected \"name\" tag");
		 * } Pair<Integer, Character> pQuot = ParseUtil.firstIndexOf(html, '\"', '\'',
		 * indexEq); int indexQuot = pQuot.getValue1(); if (indexQuot == -1) { throw new
		 * IOException("syntax error. Expected single or double quote"); } int end =
		 * html.indexOf(pQuot.getValue2(), indexQuot+1); CppRenderSubtemplateTag t = new
		 * CppRenderSubtemplateTag(); AttrValue v = new AttrValue(); v.addElement(new
		 * TextAttrValueElement(html.substring(indexQuot+1,end))); t.addAttr(new
		 * HtmlAttr(attrName, v, pQuot.getValue2())); RenderSubtemplateAttrValue val =
		 * new RenderSubtemplateAttrValue(t); currentPos = end; next(); while(!atEnd())
		 * { switch (currChar()) { case '\n': case '\r': case '\t': case ' ': next();
		 * continue; case '/': next(); if(currChar()=='>') { return val; } else { throw
		 * new IOException("syntax error in file " + filePath
		 * +",  at position "+currentPos+" in html "+html); } default: throw new
		 * IOException("syntax error in file " + filePath
		 * +",  at position "+currentPos+" in html "+html); } } return val;
		 */
	}

	protected HtmlTag parseNode() throws IOException {
		String namespaceTagName = ParseUtil.substrToNextChar(html, new char[] { ' ', '\r', '\t', '\n', '>' },
				currentPos);
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
				} else if (tagName.equals(CppSwitchTag.TAG_NAME)) {
					tag = new CppSwitchTag();
				} else if (tagName.equals(CppCaseTag.TAG_NAME)) {
					tag = new CppCaseTag();
				} else if (tagName.equals(CppFormSelect.TAG_NAME)) {
					tag = new CppFormSelect();
				} else if (tagName.equals(CppInputTag.TAG_NAME)) {
					tag = new CppInputTag();
				} else if (tagName.equals(CppButtonTag.TAG_NAME)) {
					tag = new CppButtonTag();
				} else if (tagName.equals(CppFormSelectOption.TAG_NAME)) {
					tag = new CppFormSelectOption();
				} else if (tagName.equals(CppTranslate.TAG_NAME)) {
					tag = new CppTranslate();
				} else if (tagName.equals(CppRenderSubtemplateTag.TAG_NAME)) {
					tag = new CppRenderSubtemplateTag();
				} else if (tagName.equals(CppSubtemplateTag.TAG_NAME)) {
					tag = new CppSubtemplateTag(TemplateConfig.getSrcPath().resolve(TemplateConfig.DIR_SUBTEMPLATES)
							.relativize(filePath).toString());
					Subtemplate.addSubtemplatesFunctionHeader((CppSubtemplateTag) tag);
				}
			}
		} else {
			tagName = namespaceTagName;
			if (tagName.equals(HtmlBr.TAG_NAME)) {
				tag = new HtmlBr();
				next(HtmlBr.TAG_NAME.length());
				while (currChar() != '>') {
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
			} else if (tagName.equals(HtmlStyleTag.TAG_NAME)) {
				HtmlStyleTag styleTag = new HtmlStyleTag();
				next(HtmlStyleTag.TAG_NAME.length());
				while (!atEnd() && currChar() != '>') {
					if (Character.isAlphabetic(currChar()) || currChar() == '-') {
						styleTag.addAttr(parseAttr());
					}
					next();
				}

				next();
				String endTag = String.format("</%s>", HtmlStyleTag.TAG_NAME);
				Pair<String, Integer> pair = ParseUtil.getIndexAndSubstrToNextString(html, currentPos, endTag);
				styleTag.setCss(pair.getValue1());
				currentPos = pair.getValue2() + endTag.length() - 1;
				return styleTag;
			} else {
				tag = new HtmlTag(tagName);
			}
		}

		if (tag == null) {
			throw new IOException("tag " + namespaceTagName + " not supported");
		}

		next(namespaceTagName.length());
		while (!atEnd() && currChar() != '>') {
			if (currChar() == '{') {
				tag.addAttr(parseDynamicAttr());
			} else if (Character.isAlphabetic(currChar()) || currChar() == '-') {
				tag.addAttr(parseAttr());
			} else if (currChar() == '/') {
				tag.setSelfClosing();
				while (!atEnd() && currChar() != '>') {
					next();
				}
				return tag;
			}
			next();
		}

		if (HtmlTag.isVoidTag(namespaceTagName)) {
			return tag;
		}
		next();
		parseTagContent(tag, currentPos);
		return tag;
	}

	protected CppCodeTag parseCodeTag() throws IOException {
		next(HtmlParser.CPP_CODE_TAG.length() + 1);
		int startIndex = currentPos;

		boolean quot = false;
		boolean escape = false;
		while (!atEnd()) {
			if (!quot && currSubstrEquals(HtmlParser.CPP_CODE_END_TAG)) {
				CppCodeTag tag = new CppCodeTag(html.substring(startIndex, currentPos));
				return tag;
			} else if (!escape && currChar() == '\"') {
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
		while (!atEnd()) {
			if (!quot && leftBraceCount == 0 && currChar() == HtmlParser.CPP_INLINE_END) {
				QStringHtmlEscapedOutputSection section = new QStringHtmlEscapedOutputSection(
						html.substring(startIndex, currentPos));
				return section;
			} else if (!escape && currChar() == '\"') {
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
		while (!atEnd()) {
			if (!quot && currSubstrEquals(HtmlParser.CPP_INLINE_RAW_END)) {
				RawOutputSection section = new RawOutputSection(html.substring(startIndex, currentPos));
				return section;
			} else if (!escape && currChar() == '\"') {
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
		while (!atEnd()) {
			if (currSubstrEquals(HtmlParser.HTML_END_TAG)) {
				addTextNode(tag, startIndex);
				startIndex = currentPos;
				next(HtmlParser.HTML_END_TAG.length());
				if (!currSubstrEquals(tag.getNamespaceAndTagName())) {
					throw new IOException(filePath + ": end tag does not match: " + tag.getNamespaceAndTagName()
							+ ", pos " + currentPos);
				}
				next(tag.getNamespaceAndTagName().length());
				while (currChar() != '>') {
					next();
				}
				return;
			} else if (currSubstrEquals(HtmlParser.CPP_CODE_TAG)) {
				addTextNode(tag, startIndex);
				startIndex = currentPos;
				// next();
				if (isCurrTagCppBeginPreprocessor()) {
					result.addPreprocessorTag(parseCppPreprocessorCodeSection());
				} else {
					tag.addChildNode(parseCodeTag());
				}
				startIndex = currentPos + HtmlParser.CPP_CODE_END_TAG.length();
			} else if (currSubstrEquals(HtmlParser.CPP_INLINE_RAW_START)) {
				addTextNode(tag, startIndex);
				startIndex = currentPos;
				tag.addChildNode(parseRawOutputSection());

				startIndex = currentPos + CPP_INLINE_RAW_START.length();
			} else if (currChar() == HtmlParser.CPP_INLINE_START) {
				addTextNode(tag, startIndex);
				startIndex = currentPos;
				tag.addChildNode(parseQStringHtmlEscapedOutputSection());

				startIndex = currentPos + 1;
			} else if (currChar() == '<') {
				if (!checkSkipHtmlComment()) {
					addTextNode(tag, startIndex);
					startIndex = currentPos;
					next();

					tag.addChildNode(parseNode());
				}
				startIndex = currentPos + 1;

			}
			next();
		}
	}

	protected CppIncludeTag parseCppPreprocessorCodeSection() throws IOException {
		next(HtmlParser.CPP_CODE_TAG.length() + 1);
		int startIndex = currentPos;

		boolean quot = false;
		boolean escape = false;
		while (!atEnd()) {
			if (!quot && currSubstrEquals(HtmlParser.CPP_CODE_END_TAG)) {
				CppIncludeTag tag = new CppIncludeTag(html.substring(startIndex, currentPos), true);
				if (tag.getIncludeLayoutTemplatePath() != null) {
					result.setHasLayoutTemplate();
				}
				next(HtmlParser.CPP_CODE_END_TAG.length());
				return tag;
			} else if (!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			}
			next();
		}
		CppIncludeTag tag = new CppIncludeTag(html.substring(startIndex), true);
		if (tag.getIncludeLayoutTemplatePath() != null) {
			result.setHasLayoutTemplate();
		}
		return tag;
	}

}
