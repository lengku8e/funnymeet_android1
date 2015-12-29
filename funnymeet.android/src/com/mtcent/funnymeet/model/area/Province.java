package com.mtcent.funnymeet.model.area;

import java.util.ArrayList;
import java.util.List;

public final class Province {
	/**
	 * 省名称
	 */
	private String name;
	
	/**
	 * 省包含的市列表
	 */
	private List<City> cities;
	
	public List<City> getCities() {
		return cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}

	public Province(String name) {
		this.setName(name);
		this.cities = new ArrayList<City>();
	}
	
	public void addCity(City city) {
		this.cities.add(city);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}
}
