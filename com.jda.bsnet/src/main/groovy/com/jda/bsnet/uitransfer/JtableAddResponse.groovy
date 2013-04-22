package com.jda.bsnet.uitransfer

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


public class JtableAddResponse {


	String Result
	Object Record

	JtableAddResponse(String rslt , Object rec){


		Result = rslt
		Record=rec
	}
}

