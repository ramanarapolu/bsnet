package com.jda.bsnet.model

import com.fasterxml.jackson.annotation.JsonIgnore;

class Item {
	String _id
	String itemName
	String description
	Double price
	String imageUrl
	String category
	@JsonIgnore
	boolean success

	boolean isSuccess() {
		return success
	}
}
