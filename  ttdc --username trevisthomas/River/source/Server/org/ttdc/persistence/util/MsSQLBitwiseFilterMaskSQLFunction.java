package org.ttdc.persistence.util;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.Type;

/**
 * You should try to delete this some time.  I dont think that it is used.
 *
 */
@Deprecated  
public class MsSQLBitwiseFilterMaskSQLFunction extends StandardSQLFunction implements SQLFunction {

	   public MsSQLBitwiseFilterMaskSQLFunction(String name) {
	       super(name);
	   }

	   public MsSQLBitwiseFilterMaskSQLFunction(String name, Type typeValue) {
	       super(name, typeValue);
	   }

	   //post.metaMask & :filterMask <> :filterMask
	   
	   @Override
	   public String render(List args, SessionFactoryImplementor factory) throws QueryException {
	       if (args.size() != 2){
	           throw new IllegalArgumentException("the function must be passed 2 arguments");
	       }
	       StringBuffer buffer = new StringBuffer(args.get(0).toString());
	       buffer.append(" & ").append(args.get(1))
	       .append(" <> ").append(args.get(1));
	       return buffer.toString();
	   }
	   
	
}
