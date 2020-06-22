package com.zygateley.resume;

public interface TopLevel {
	/**
	 * QUERY_FINAL
	 * 
	 * Second-level table left join First-level table
	 * i.e. Expected results:
	 * 		TOP_LEVEL_ID	...		SECOND_LEVEL_ID 	...
	 * 			1							1
	 * 			1							2
	 * 			1							3 
	 */
	static String QUERY_FINAL = "";
	
	/** 
	 * Section extends SQLite.Section
	 * 
	 * Mirrors a single record in Experience table.
	 * See SQLite.Section for more details.
	 */
	public static class Section extends SQLite.Section {
		/**
		 * Detail extends SQLite.Section.Detail
		 * 
		 * Mirrors a single record in ExperienceDetail table.
		 * See SQLite.Section.Detail for more information.
		 */
		public static class Detail extends SQLite.Section.Detail {}
	}
	
	/**
	 * writeFormOptions
	 * 
	 * Called from Form Servlet.
	 * Writes checkboxes for available output.
	 */
	public static void writeFormOptions() {}
}