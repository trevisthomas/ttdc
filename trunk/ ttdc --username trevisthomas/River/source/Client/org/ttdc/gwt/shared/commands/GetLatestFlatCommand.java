package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.PostListCommandResult;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GetLatestFlatCommand extends Command<PostListCommandResult> implements IsSerializable{
	public GetLatestFlatCommand(){}
}
