package com.jda.server.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jda.model.Address;
import com.jda.model.User;
import com.jda.util.JsonUtils;

public class Test {

	public static void main(String[] args) {
		
		Address addr =  new Address();
		addr.setCity("wgl");
		addr.setHouseNo(1234);
		addr.setStreet("tong");
		int[] pinArr = {1234,145,56787};
		addr.setPincode(pinArr);
			
		User user = new User();
		user.setName("ramana");
		user.setMailId("ramana.rapolu@jda.com");
		List<Address> addrs = new ArrayList<>();
		addrs.add(addr);
		user.setAddrs(addrs);
		
		try {
			System.out.println(JsonUtils.convertObjToJsonString(user));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//JsonU
		
		
	}


}
