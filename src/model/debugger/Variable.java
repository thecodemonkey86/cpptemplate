package model.debugger;

public class Variable  {

	public String expression, value;
	
	public Variable(String expression, String value) {
		 this.expression = expression;
		 this.value = value;
	}

	public static boolean eq(Object o1, Object o2) {
		return o1!=null && o2!=null && o1.equals(o2);
	}
	
	@Override
	public String toString() {
		return expression+"="+value ;
	}

}
