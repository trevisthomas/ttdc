package org.ttdc.gwt.client.beans;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This base class is essentially a marker to get help GWT use these effeciently 
 * as Generic types.  I went ahead and made this guy to implement IsSerializable
 * since the sub classes all need it anyway.
 * 
 * @author Trevis
 *
 */
public class GBase implements IsSerializable{

}
