package org.ttdc.util;

import java.util.List;

import org.ttdc.persistence.objects.Tag;

public class CalculateAverageRating {
	
	/**
	 * Rips through a list of tags and calculates the average rating
	 * @param ratings
	 * @return
	 * @throws ServiceException
	 */
	public static String determineAverageRating(List<Tag> ratings) throws ServiceException{
		float sum = 0;
		float average = 0;
		int count = 0;
		for(Tag t : ratings){
			if(Tag.TYPE_RATING.equals(t.getType())){
				sum += Float.parseFloat(t.getValue());	
				count++;
			}
		}
		average = sum / count;
		return determineRating(average);
	}

	/**
	 * Calculates the median rating. Dont give me tags that arent ratings or i'll be very cross.
	 * 
	 * @param ratings
	 * @return
	 * @throws ServiceException
	 */
	public static String determineMedianRating(List<Tag> ratings) throws ServiceException{
		long[] values = new long[ratings.size()];
		int count = 0;
		double median;
		for(Tag t : ratings){
			values[count++] = (long)(10 * Float.parseFloat(t.getValue()));	 
		}
		median = Median.findMedian(values);
		
		return determineRating((float)median/10);
	}
	
	
	
	
	public static String determineRating(String rating) throws ServiceException{
		try{
			float value = Float.parseFloat(rating);
			return determineRating(value);
		}
		catch (NumberFormatException e) {
			throw new ServiceException(e);
		}
	}
	private static String determineRating(float value){
		if(value > 4.5){
			return Tag.VALUE_RATING_5;
		}
		else if(value > 4.25){
			return Tag.VALUE_RATING_4_5;
		}
		else if(value > 3.75){
			return Tag.VALUE_RATING_4;
		}
		else if(value >= 3.25){
			return Tag.VALUE_RATING_3_5;
		}
		else if(value >= 2.75){
			return Tag.VALUE_RATING_3;				
		}
		else if(value >= 2.25){
			return Tag.VALUE_RATING_2_5;	
		}
		else if(value >= 1.75){
			return Tag.VALUE_RATING_2;				
		}
		else if(value >= 1.25){
			return Tag.VALUE_RATING_1_5;
		}
		else if(value >= 0.75){
			return Tag.VALUE_RATING_1;				
		}
		else if(value > 0){
			return Tag.VALUE_RATING_0_5;
		}
		else{
			return Tag.VALUE_RATING_0;
		}
		
	}
}
