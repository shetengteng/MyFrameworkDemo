package com.stt.pojo;

import java.util.List;

public class Result<T> {

	private String info;
	private List<T> items;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "Result [info=" + info + ", items=" + items + "]";
	}

}
