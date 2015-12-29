package com.mtcent.funnymeet.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WeekCalendar {
	public static final String[] monthday_fields = new String[] { "",
			"sunday_monthday", "monday_monthday", "tuesday_monthday",
			"wendesday_monthday", "thursday_monthday", "friday_monthday",
			"saturday_monthday", };

	// private Date today;

	public Map<String, String> getMonthDays(Date today) {
		Map<String, String> result = new HashMap<String, String>();
		// this.today = today;
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd",
				Locale.getDefault());
		int weekday = c.get(Calendar.DAY_OF_WEEK);
//		System.out.println("weekday:::::::::" + weekday);
		result.put(monthday_fields[weekday], sdf.format(today));
		// SimpleDateFormatter

		//sunday
		Date date = today;
		c.setTime(today);
		c.add(Calendar.DAY_OF_MONTH, 7 - weekday + 1);
		date = c.getTime();
		result.put(monthday_fields[1], sdf.format(date));
		//end of sunday handling
		for (int i = 2; i <= 7; i++) {
			c.setTime(today);
			c.add(Calendar.DAY_OF_MONTH, i - weekday);
			date = c.getTime();
			result.put(monthday_fields[i], sdf.format(date));
		}
		return result;
	}
}
