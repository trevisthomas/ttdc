package org.ttdc.gwt.client.messaging;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class Event<T,S> implements IsSerializable{
	abstract public T getType();
	abstract public S getSource();
	public boolean is(T type){
		return getType().equals(type);
	}
	@Override
	public boolean equals(Object obj) {
		Event<T,S> e = (Event<T,S>)obj;
		return getSource().equals(e.getSource()) && getType().equals(e.getType());
	}
}
