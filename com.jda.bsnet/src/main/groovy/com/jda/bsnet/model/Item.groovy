package com.jda.bsnet.model

import com.fasterxml.jackson.annotation.JsonIgnore;

class Item {
	String itemName
	String description
	Long price
	String imageUrl
	String category
	@JsonIgnore
	boolean success
}
