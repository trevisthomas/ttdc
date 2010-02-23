package org.ttdc.gwt.shared.commands;

/**
 * 
 * Different modes for the TopicCommand.  Topics are viewed in 3 different ways
 * but they share Command and Result objects
 *
 */
public enum TopicCommandType {
	FLAT,HIERARCHY,STARTERS,REPLIES,HIERARCHY_UNPAGED_SUMMARY,CONVERSATION, NESTED_THREAD_SUMMARY, NESTED_THREAD_SUMMARY_FETCH_MORE
}
