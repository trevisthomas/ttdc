package org.ttdc.gwt.server.command;

import org.ttdc.gwt.server.command.CommandExecutorFactory;
import org.ttdc.gwt.server.command.executors.AssociationPostTagCommandExecutor;
import org.ttdc.gwt.server.command.executors.CreatePostCommandExecutor;
import org.ttdc.gwt.server.command.executors.LatestFlatCommandExecutor;
import org.ttdc.gwt.server.command.executors.LatestHierarchyCommandExecutor;
import org.ttdc.gwt.server.command.executors.PersonDetailsCommandExecutor;
import org.ttdc.gwt.server.command.executors.SearchPostsCommandExecutor;
import org.ttdc.gwt.server.command.executors.SearchTagsCommandExecutor;
import org.ttdc.gwt.server.command.executors.ServerEventListCommandExecutor;
import org.ttdc.gwt.server.command.executors.ServerEventOpenConnectionCommandExecutor;
import org.ttdc.gwt.server.command.executors.TagSuggestionCommandExecutor;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.CreatePostCommand;
import org.ttdc.gwt.shared.commands.GetLatestFlatCommand;
import org.ttdc.gwt.shared.commands.GetLatestHierarchyCommand;
import org.ttdc.gwt.shared.commands.GetPersonDetailsCommand;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.commands.ServerEventListCommand;
import org.ttdc.gwt.shared.commands.ServerEventOpenConnectionCommand;
import org.ttdc.gwt.shared.commands.TagSuggestionCommand;

/*
 * You can probably delete this. I just made the factory a real factory.
 */

@Deprecated
public class CommandExecuteTestBase {
//	static{
//		CommandExecutorFactory.registerExecutorForCommand(GetLatestHierarchyCommand.class, LatestHierarchyCommandExecutor.class);
//		CommandExecutorFactory.registerExecutorForCommand(GetLatestFlatCommand.class, LatestFlatCommandExecutor.class);
//		CommandExecutorFactory.registerExecutorForCommand(GetPersonDetailsCommand.class, PersonDetailsCommandExecutor.class);
//		CommandExecutorFactory.registerExecutorForCommand(ServerEventOpenConnectionCommand.class, ServerEventOpenConnectionCommandExecutor.class);
//		CommandExecutorFactory.registerExecutorForCommand(ServerEventListCommand.class, ServerEventListCommandExecutor.class);
//		CommandExecutorFactory.registerExecutorForCommand(CreatePostCommand.class, CreatePostCommandExecutor.class);
//		CommandExecutorFactory.registerExecutorForCommand(SearchPostsCommand.class, SearchPostsCommandExecutor.class);
//		CommandExecutorFactory.registerExecutorForCommand(TagSuggestionCommand.class, TagSuggestionCommandExecutor.class);
//		CommandExecutorFactory.registerExecutorForCommand(AssociationPostTagCommand.class, AssociationPostTagCommandExecutor.class);
//		CommandExecutorFactory.registerExecutorForCommand(SearchTagsCommand.class, SearchTagsCommandExecutor.class);
//		
//	}
}
