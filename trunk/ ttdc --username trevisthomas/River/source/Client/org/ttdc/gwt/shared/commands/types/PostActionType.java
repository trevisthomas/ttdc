package org.ttdc.gwt.shared.commands.types;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum PostActionType implements IsSerializable{
	CREATE,READ,UPDATE,DELETE,REPARENT,UPDATE_META
}
