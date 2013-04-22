package com.jda.bsnet.uitransfer

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


public class JtableJson {

	 
	String Result	
	List<Object> Records

	JtableJson(String rslt , List<Object> rec){
		Result = rslt
		Records = rec
	}
	
	JtableJson(String rslt , Object rec){
		
		Records = new ArrayList<Object>()
		Result = rslt
		Records.add(rec)
		
	}
	
	
}

