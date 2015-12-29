package com.mtcent.funnymeet.model.area;

import java.util.ArrayList;
import java.util.List;

public final class City {
	/**
	 * 市名
	 */
	private String name;
	
	/**
	 * 市所在省的名字
	 */
	private String provinceName;
	
	/**
	 * 市包含的所有区的列表
	 */
	private List<District> districts;
	
	public void addDistrict(District district) {
		this.districts.add(district);
	}
	
	public City(String province, String name) {
		this.provinceName = province;
		this.name = name;
		this.districts = new ArrayList<District>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<District> getDistricts() {
		return districts;
	}

	public void setDistricts(List<District> districts) {
		this.districts = districts;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	
	public String toString() {
		return this.name;
	}
}
