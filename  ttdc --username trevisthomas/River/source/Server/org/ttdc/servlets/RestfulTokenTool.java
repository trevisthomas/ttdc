package org.ttdc.servlets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.Ostermiller.util.Base64;

public class RestfulTokenTool {
	
	public static String toTokenString(RestfulToken token) throws IOException {
		return toEncodedString(token);
	}
	
	public static RestfulToken fromTokenString(String serializedToken) throws ClassNotFoundException, IOException {
		return (RestfulToken) fromEncodedString(serializedToken);
	}
	
	 /** Read the object from Base64 string. */
   private static Object fromEncodedString( String s ) throws IOException ,
                                                       ClassNotFoundException {
        byte [] data = Base64.decodeToBytes( s );
        ObjectInputStream ois = new ObjectInputStream( 
                                        new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
   }

    /** Write the object to a Base64 string. */
    private static String toEncodedString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.encodeToString(baos.toByteArray()); 
    }
}
