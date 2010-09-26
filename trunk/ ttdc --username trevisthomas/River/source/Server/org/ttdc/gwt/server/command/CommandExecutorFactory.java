package org.ttdc.gwt.server.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.executors.AccountCommandExecutor;
import org.ttdc.gwt.server.command.executors.AssociationPostTagCommandExecutor;
import org.ttdc.gwt.server.command.executors.BrowsePopularTagsCommandExecutor;
import org.ttdc.gwt.server.command.executors.CalendarCommandExecutor;
import org.ttdc.gwt.server.command.executors.ForumCommandExecutor;
import org.ttdc.gwt.server.command.executors.ForumTopicListCommandExecutor;
import org.ttdc.gwt.server.command.executors.ImageCrudCommandExecutor;
import org.ttdc.gwt.server.command.executors.ImageListCommandExecutor;
import org.ttdc.gwt.server.command.executors.LatestPostCommandExecutor;
import org.ttdc.gwt.server.command.executors.MovieListCommandExecutor;
import org.ttdc.gwt.server.command.executors.PersonCommandExecutor;
import org.ttdc.gwt.server.command.executors.PersonListCommandExecutor;
import org.ttdc.gwt.server.command.executors.PostCrudCommandExecutor;
import org.ttdc.gwt.server.command.executors.PrivilegeCrudCommandExecutor;
import org.ttdc.gwt.server.command.executors.SearchPostsCommandExecutor;
import org.ttdc.gwt.server.command.executors.SearchTagsCommandExecutor;
import org.ttdc.gwt.server.command.executors.ServerEventListCommandExecutor;
import org.ttdc.gwt.server.command.executors.ServerEventOpenConnectionCommandExecutor;
import org.ttdc.gwt.server.command.executors.StyleCommandExecutor;
import org.ttdc.gwt.server.command.executors.StyleListCommandExecutor;
import org.ttdc.gwt.server.command.executors.TagCommandExecutor;
import org.ttdc.gwt.server.command.executors.TagSuggestionCommandExecutor;
import org.ttdc.gwt.server.command.executors.TopicCommandExecutor;
import org.ttdc.gwt.server.command.executors.UserObjectCrudCommandExecutor;
import org.ttdc.gwt.server.command.executors.UserObjectTemplateCommandExecutor;
import org.ttdc.gwt.server.command.executors.UserObjectTemplateListCommandExecutor;
import org.ttdc.gwt.shared.commands.AccountCommand;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.BrowsePopularTagsCommand;
import org.ttdc.gwt.shared.commands.CalendarCommand;
import org.ttdc.gwt.shared.commands.ForumCommand;
import org.ttdc.gwt.shared.commands.ForumTopicListCommand;
import org.ttdc.gwt.shared.commands.ImageCrudCommand;
import org.ttdc.gwt.shared.commands.ImageListCommand;
import org.ttdc.gwt.shared.commands.LatestPostsCommand;
import org.ttdc.gwt.shared.commands.MovieListCommand;
import org.ttdc.gwt.shared.commands.PersonCommand;
import org.ttdc.gwt.shared.commands.PersonListCommand;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.PrivilegeCrudCommand;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.commands.ServerEventListCommand;
import org.ttdc.gwt.shared.commands.ServerEventOpenConnectionCommand;
import org.ttdc.gwt.shared.commands.StyleCommand;
import org.ttdc.gwt.shared.commands.StyleListCommand;
import org.ttdc.gwt.shared.commands.TagCommand;
import org.ttdc.gwt.shared.commands.TagSuggestionCommand;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.UserObjectCrudCommand;
import org.ttdc.gwt.shared.commands.UserObjectTemplateCommand;
import org.ttdc.gwt.shared.commands.UserObjectTemplateListCommand;

/**
 * This factory allows you to register one Class to be the key for getting the instance of another
 * one.  Internally it's a singleton but the external interface is static.   
 * 
 * Implementation is thread safe. 
 * 
 * @author Trevis
 *
 */


public class CommandExecutorFactory {
	static{
		CommandExecutorFactory.registerExecutorForCommand(ServerEventOpenConnectionCommand.class, ServerEventOpenConnectionCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(ServerEventListCommand.class, ServerEventListCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(SearchPostsCommand.class, SearchPostsCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(TagSuggestionCommand.class, TagSuggestionCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(AssociationPostTagCommand.class, AssociationPostTagCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(SearchTagsCommand.class, SearchTagsCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(BrowsePopularTagsCommand.class, BrowsePopularTagsCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(CalendarCommand.class, CalendarCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(TopicCommand.class, TopicCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(PostCrudCommand.class, PostCrudCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(PersonListCommand.class, PersonListCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(MovieListCommand.class, MovieListCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(ImageListCommand.class, ImageListCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(ImageCrudCommand.class, ImageCrudCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(PrivilegeCrudCommand.class, PrivilegeCrudCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(PersonCommand.class, PersonCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(UserObjectTemplateListCommand.class, UserObjectTemplateListCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(UserObjectTemplateCommand.class, UserObjectTemplateCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(StyleCommand.class, StyleCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(StyleListCommand.class, StyleListCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(AccountCommand.class, AccountCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(UserObjectCrudCommand.class, UserObjectCrudCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(LatestPostsCommand.class, LatestPostCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(TagCommand.class, TagCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(ForumCommand.class, ForumCommandExecutor.class);
		CommandExecutorFactory.registerExecutorForCommand(ForumTopicListCommand.class, ForumTopicListCommandExecutor.class);
		
	}
	
	private final static Logger log = Logger.getLogger(CommandExecutorFactory.class);
	@SuppressWarnings("unchecked") private final Map<Class,Class> map = new ConcurrentHashMap<Class,Class>();
	
	public static final CommandExecutorFactory getInstance(){
		return SingletonHolder.INSTANCE;
	}
	private static class SingletonHolder {
		private final static CommandExecutorFactory INSTANCE = new CommandExecutorFactory();
	}
	
	@SuppressWarnings("unchecked")
	public static void registerExecutorForCommand(Class commandClass, Class executorClass){
		CommandExecutorFactory.getInstance().map.put(commandClass,executorClass);
	}
	
	public static <T extends CommandResult> CommandExecutor<T> createExecutor(String personId, Command<T> command){
		@SuppressWarnings("unchecked") Class clazz = CommandExecutorFactory.getInstance().map.get(command.getClass());
		if(clazz == null){
			throw new RuntimeException("Could not locate CommandExecutor implementation class instance for command: " + command);
		}
		try {
			@SuppressWarnings("unchecked") CommandExecutor<T> executor = (CommandExecutor<T>)clazz.newInstance();
			executor.initialize(personId, command);
			return executor;
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException("Could not create CommandExecutor instance for command: " + command);
		} 
	}
	
	
}
