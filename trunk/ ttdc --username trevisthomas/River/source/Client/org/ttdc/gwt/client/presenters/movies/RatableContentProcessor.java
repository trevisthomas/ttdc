package org.ttdc.gwt.client.presenters.movies;

/**
 * 
 * An implementation of this interface is required by the movie rating presenter when it is 
 * in interactive mode.  When the user clicks on a rating this method is called with the 
 * value that they selected.  It is then up to the implementer to make the rating real.
 *
 */
public interface RatableContentProcessor {
	void processRatingRequest(float rating);
}
