package com.jda.bsnet.model

import org.codehaus.jackson.annotate.JsonIgnore


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
