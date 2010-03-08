package org.ttdc.persistence.util;

import org.hibernate.Hibernate;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;

public class CustomSQLServerDialect extends SQLServerDialect{
   public CustomSQLServerDialect() {
       super();
       //post.metaMask & :filterMask <> :filterMask
       //registerFunction("bit_filter", new MsSQLBitwiseFilterMaskSQLFunction("bit_filter", Hibernate.LONG));
       registerFunction("trev", new SQLFunctionTemplate(Hibernate.LONG, "?1 & ?2 <> ?2)"));
   }
}
