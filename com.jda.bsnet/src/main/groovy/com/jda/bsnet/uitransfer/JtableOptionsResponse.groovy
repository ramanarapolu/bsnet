package com.jda.bsnet.uitransfer

import java.util.List;

class JtableOptionsResponse {

	String Result
	List<Object> Options
	

	JtableOptionsResponse(String rslt , List<Object> rec){
		Result = rslt
		Options = rec
	}
	
	/*JtableOptionsResponse(String rslt , List<Object> rec, int Trc){
		Result = rslt
		Records = rec
		TotalRecordCount=Trc
	}*/
	
	
}
