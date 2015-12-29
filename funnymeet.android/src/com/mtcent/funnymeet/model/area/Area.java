package com.mtcent.funnymeet.model.area;

//test
import java.util.List;

public interface Area {

	List<Province> getProvinces();
	
	List<City> getCities(String provineName);
	
	List<District> getDistricts(String provinceName, String cityName);
}
