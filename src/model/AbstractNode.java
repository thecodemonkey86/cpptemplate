package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import config.TemplateConfig;

public abstract class AbstractNode implements ITemplateItem{

	protected List<AbstractNode> childNodes;
	
	public AbstractNode() {
		this.childNodes = null;
	}
	

	public void addChildNode(AbstractNode node) {
		if (this.childNodes == null) {
			this.childNodes = new ArrayList<>();
		}
		if (!(node instanceof CppCommentTag)) {
			this.childNodes.add(node);
		}
	}

	public List<AbstractNode> getChildNodes() {
		return childNodes;
	}
	
	/**
	 * @param out
	 * @throws IOException 
	 */
	public abstract void toCpp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg, ParserResult mainParserResult) ;
	
	public void walkTree(TemplateConfig tplCfg,WalkTreeAction action,ParserResult parserResult) throws IOException {
		action.currentNode(this, parserResult);
		if (this.childNodes != null) {
			for(AbstractNode n:childNodes) {
				n.walkTree(tplCfg,action, parserResult);
			}
		}
	}
}
