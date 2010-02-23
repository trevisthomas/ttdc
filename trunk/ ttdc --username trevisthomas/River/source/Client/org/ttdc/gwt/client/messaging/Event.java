package org.ttdc.gwt.client.messaging;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class Event<T,S> implements IsSerializable{
	abstract public T getType();
	abstract public S getSource();
	public boolean is(T type){
		return getType().equals(type);
	}
}
