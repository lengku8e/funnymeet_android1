package com.mtcent.funnymeet.model.area;

public final class District {
	/**
	 * 地区名称
	 */
	private String name;
	
	/**
	 * 所属市名称
	 */
	private String cityName;
	
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	/**
	 * 所属省名称
	 */
	private String provinceName;

	public District(String province, String city, String district) {
		this.provinceName = province;
		this.cityName = city;
		this.name = district;
	}

	public District(String name) {
		this.name = name;
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
