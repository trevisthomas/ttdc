package org.ttdc.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtils {
	public static class GPoint implements GlobePoint{
		private double longitude;
		private double latitude;
		
		public GPoint(double latitude, double longitude){
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public double getLatitude() {
			return latitude;
		}

		public double getLongitude() {
			return longitude;
		}
	}
	public static double RADIUS_MILES = 3963.0;
	public static double RADIUS_NAUTICAL_MILES = 3437.74677;
	public static double KILOMETERS = 6378.7;
	public static double PI = 3.14159265358979323846;
	private static double RADIAN_DIVISIOR = 180/PI; 
	
	/**
	 *  r=3437.74677 (nautical miles)
	 *  r=6378.7 (kilometers)
	 *  r=3963.0 (statute miles)
	 *   
	 *   Adapted from these functions :
	 *   http://www.meridianworlddata.com/Distance-Calculation.asp
	 *   
	 * @param a
	 * @param b
	 * @return
	 */
	public static double distance(GlobePoint a, GlobePoint b){
		double distance = RADIUS_MILES
				* Math.acos(Math.sin(a.getLatitude()/RADIAN_DIVISIOR)
						  * Math.sin(b.getLatitude()/RADIAN_DIVISIOR) + Math.cos(a.getLatitude()/RADIAN_DIVISIOR)
						  * Math.cos(b.getLatitude()/RADIAN_DIVISIOR)
						  * Math.cos(b.getLongitude()/RADIAN_DIVISIOR - a.getLongitude()/RADIAN_DIVISIOR));
		return distance;
	}
	
	public interface GlobePoint{
		public double getLongitude();
		public double getLatitude();
	}
	
	/**
	 * This method is intended to generate a map of GlobePoint objects key'd by their distance from
	 * the point arguement. If the keys of this map are sorted you can then have a list of 
	 * the source GlobePoints sorted by distance from the point arguement.
	 * 
	 * @param point
	 * @param source
	 * @return
	 */
	public static Map<Double,GlobePoint> generateDistanceMap(GlobePoint point, List<GlobePoint> source){
		Map<Double,GlobePoint> map = new HashMap<Double,GlobePoint>();
		for(GlobePoint current : source){
			double d = distance(point,current);
			map.put(d, current);
		}
		return map;
	}
	
}
