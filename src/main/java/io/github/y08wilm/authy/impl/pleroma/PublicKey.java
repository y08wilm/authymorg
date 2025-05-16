package io.github.y08wilm.authy.impl.pleroma;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PublicKey {
	
	public volatile String id;
	public volatile String owner;
	public volatile String publicKeyPem;
	
	public PublicKey() {

	}
	
	public PublicKey(String json) {
		readFromJson(json);
	}

	private void readFromJson(String json) {
		Gson gson = new GsonBuilder().create();
		PublicKey clazz = gson.fromJson(json, PublicKey.class);
		this.id = clazz.id;
		this.owner = clazz.owner;
		this.publicKeyPem = clazz.publicKeyPem;
	}
	
	private String exportAsJson() {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(this);
	}
	
	@Override
	public String toString() {
		return exportAsJson();
	}

}
