package org.ttdc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Median<T> {
	
	/**
	 * Create a custom implementation of this method to feed the median calculator.
	 * 
	 * @author Trevis
	 *
	 * @param <T>
	 */
	public interface SourceValueReader<T>{
		long readSourceValue(T target);
	}
	
	/**
	 * Takes a list and distrubutes it into a list of lists using the median value of T.
	 * The median is calulated using an int read from T using a custom implementation of 
	 * SourceValueReader<T>  
	 * 
	 * @param splits  How many times to split the list.  This value determins how many lists you will receive. 
	 * 				  A split of 3 will return 8 lists. 2 will return 4, 4 will return 16 (i think)
	 * @param sourceList
	 * @return
	 */
	public List<List<T>> medianDistribution(int splits,List<T> sourceList, SourceValueReader<T> reader){
		this.reader = reader; 
		List<List<T>> results = new ArrayList<List<T>>();
		doit(results,sourceList,splits-1);
		return results;
	}
	
	private void split(List<T> list, double median, List<T> low, List<T> high) {
		for (T t : list) {
			if (reader.readSourceValue(t) > median) {
				high.add(t);
			}
			else {
				low.add(t);
			}
		}
	}
	
	private SourceValueReader<T> reader;
	
	
	
	private void doit(List<List<T>> results, List<T> sourceList,int count){
		double median = findMedian(sourceList);
		List<T> low = new ArrayList<T>();
		List<T> high = new ArrayList<T>();
		split(sourceList,median,low,high);
		if(count > 0){
			count = count-1;
			doit(results,low,count);
			doit(results,high,count);
		}
		else{
			results.add(low);
			results.add(high);
		}
	}
	
	

	private long[] buildCountArray(List<T> list) {
		long[] array = new long[list.size()];
		for (T t : list) {
			array[list.indexOf(t)] = reader.readSourceValue(t);
		}
		return array;
	}
	public double findMedian(List<T> list) {
		return findMedian(buildCountArray(list));
	}
	
	public static double findMedian(long[] a) {
		Arrays.sort(a);
		if (a.length == 0)
			// no elements, just return 0
			return 0;
		else if (a.length % 2 == 1)
			// odd-length, return middle element
			return a[a.length / 2];
		else
			// even-length, return average of middle two elements
			// be sure to divide by 2.0 and not by 2!
			return (a[a.length / 2] + a[(a.length / 2) - 1]) / 2.0;
	}
}
