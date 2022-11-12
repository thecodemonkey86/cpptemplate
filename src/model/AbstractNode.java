package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import config.TemplateConfig;
import model.debugger.DebuggerVariableList;


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
	
	
	public void walkTree(TemplateConfig tplCfg,WalkTreeAction action,ParserResult parserResult) throws IOException {
		action.currentNode(this, parserResult);
		if (this.childNodes != null) {
			for(AbstractNode n:childNodes) {
				n.walkTree(tplCfg,action, parserResult);
			}
		}
	}
	
	@Override
	public void directRender(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException {
		if (this.childNodes != null) {
			for(AbstractNode n:childNodes) {
				n.directRender(out, cfg, mainParserResult, variables);
			}
		}
	}
	@Override
	public void directRenderDoubleEncoded(StringBuilder out,TemplateConfig cfg, ParserResult mainParserResult, DebuggerVariableList variables) throws IOException {
		if (this.childNodes != null) {
			for(AbstractNode n:childNodes) {
				n.directRenderDoubleEncoded(out, cfg, mainParserResult, variables);
			}
		}
	}
}
