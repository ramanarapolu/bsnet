package com.jda.bsnet.uitransfer

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


public class JtableJson implements Serializable{


	String Result
	List<Object> Records
	int TotalRecordCount

	JtableJson(String rslt , List<Object> rec){
		Result = rslt
		Records = rec
	}

	JtableJson(String rslt , List<Object> rec, int Trc){
		Result = rslt
		Records = rec
		TotalRecordCount=Trc
	}


}

