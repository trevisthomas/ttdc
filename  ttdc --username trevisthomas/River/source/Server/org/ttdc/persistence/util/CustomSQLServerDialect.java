package org.ttdc.persistence.util;

import org.hibernate.Hibernate;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;

/**
 * You should try to delete this some time.  I dont think that it is used.
 *
 */
@Deprecated
public class CustomSQLServerDialect extends SQLServerDialect{
   public CustomSQLServerDialect() {
       super();
       //post.metaMask & :filterMask <> :filterMask
       //registerFunction("bit_filter", new MsSQLBitwiseFilterMaskSQLFunction("bit_filter", Hibernate.LONG));
       registerFunction("trev", new SQLFunctionTemplate(Hibernate.LONG, "?1 & ?2 <> ?2)"));
   }
}
