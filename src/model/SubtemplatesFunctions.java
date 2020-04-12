package model;

import java.util.ArrayList;
import java.util.List;

import util.Pair;

public class SubtemplatesFunctions {
	protected List<Pair<Subtemplate,Boolean>> subtemplatesAsFunctions; // if subtemplates have arguments their rendering must be extracted as function
	
	public void addSubtemplatesAsFunction(Subtemplate t,boolean doubleEncode) {
		if(this.subtemplatesAsFunctions==null) {
			this.subtemplatesAsFunctions = new ArrayList<>();
		}
		boolean found=false;
		
		for(Pair<Subtemplate, Boolean> p : subtemplatesAsFunctions) {
			if(p.getValue1().getSubtemplateIdentifier().equals(t.getSubtemplateIdentifier()) && p.getValue2().equals(doubleEncode)) {
				found = true;
				break;
			}
		}
		if(!found)
			this.subtemplatesAsFunctions.add(new Pair<Subtemplate, Boolean>(t, doubleEncode) );
	}
	
	public boolean hasSubtemplatesAsFunction() {
		return subtemplatesAsFunctions != null && !subtemplatesAsFunctions.isEmpty();
	}

	public List<Pair<Subtemplate, Boolean>> getSubtemplatesAsFunctions() {
		return subtemplatesAsFunctions;
	}
}
