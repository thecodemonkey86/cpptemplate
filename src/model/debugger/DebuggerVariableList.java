package model.debugger;

import java.util.HashMap;

public class DebuggerVariableList {
	Variable[] vars;
	HashMap<String,String> uniqueExpressions;
	int index;
	
	public DebuggerVariableList(Variable[] vars) {
		index=0;
		this.vars = vars;
		HashMap<String, Integer> h=new HashMap<>();
		uniqueExpressions = new HashMap<>();
		for(Variable v:vars) {
			if(h.containsKey(v.expression)) {
				h.put(v.expression, h.get(v.expression)+1);
			} else {
				h.put(v.expression,1);
			}
		}
		h.forEach((String k,Integer v)-> {
			if(v==1) {
				for(Variable var:vars) {
					if(var.expression.equals(k)) {
						uniqueExpressions.put(k,var.value);
						break;
					}
				}
				
			}
		});
	}

	public String getString(String expression) {
		if(uniqueExpressions.containsKey(expression)) {
			return uniqueExpressions.get(expression);
		}
		
		if(index< vars.length && vars[index].expression.equals(expression)) {
			return vars[index].value;
		}
		
		return null;
	}
	
	public String getStringAndIncrement(String expression) {
		if(uniqueExpressions.containsKey(expression)) {
			index++;
			return uniqueExpressions.get(expression);			
		}
		
		if(index< vars.length && vars[index].expression.equals(expression)) {
			return vars[index++].value;
		}
		
		return null;
	}
	
	public Integer getIntAndIncrement(String expression) {
		String s=getStringAndIncrement(expression);
		if(s!=null) {
			return Integer.parseInt(s);
		}
		return null;
	}
	
	public int getIndex() {
		return index;
	}

	public void increment() {
		index++;		
	}

	public boolean getBoolAndIncrement(String stringValue) {
		return getStringAndIncrement(stringValue).equals("1");
	}
}
