package org.tny.test;

public class TestBean {

	String myname;
	public void setmyname(String myname){
		this.myname = myname;
	}
	
	
	@Override
	public String toString(){
		return this.myname;
		
	}
}
