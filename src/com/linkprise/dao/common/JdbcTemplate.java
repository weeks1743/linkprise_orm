/*     */ package com.linkprise.dao.common;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;

/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;

import com.linkprise.DataBaseType;
import com.linkprise.dao.common.nativejdbc.C3P0NativeJdbcExtractor;
import com.linkprise.dao.common.nativejdbc.INativeJdbcExtractor;
import com.linkprise.dao.dialect.DB2Dialect;
import com.linkprise.dao.dialect.DerbyDialect;
import com.linkprise.dao.dialect.Dialect;
import com.linkprise.dao.dialect.FirebirdDialect;
import com.linkprise.dao.dialect.H2Dialect;
import com.linkprise.dao.dialect.HSQLDialect;
import com.linkprise.dao.dialect.InformixDialect;
import com.linkprise.dao.dialect.Ingres10Dialect;
import com.linkprise.dao.dialect.Ingres9Dialect;
import com.linkprise.dao.dialect.IngresDialect;
import com.linkprise.dao.dialect.InterbaseDialect;
import com.linkprise.dao.dialect.MySQLDialect;
import com.linkprise.dao.dialect.Oracle10gDialect;
import com.linkprise.dao.dialect.PostgreSQLDialect;
import com.linkprise.dao.dialect.RDMSOS2200Dialect;
import com.linkprise.dao.dialect.SQLServerDialect;
import com.linkprise.dao.dialect.TimesTenDialect;
import com.linkprise.dao.id.DefaultIdentifierGeneratorFactory;
import com.linkprise.dao.id.IdentifierGeneratorFactory;
import com.linkprise.dao.transation.TransactionManager;
import com.linkprise.orm.annotation.ClassMapping;
import com.linkprise.orm.annotation.FieldMapping;
import com.linkprise.orm.convert.GenericConverterFactory;
import com.linkprise.orm.convert.ITypeConverter;
import com.linkprise.orm.mapping.IObjectReader;
import com.linkprise.orm.mapping.ObjectReader;
import com.linkprise.utils.Assert;
/*     */ 
/*     */ public class JdbcTemplate
/*     */ {
/*  72 */   private static final Log logger = LogFactory.getLog(JdbcTemplate.class);
/*     */   private ConnectionFactory connectionFactory;
/*     */   private IObjectReader objectReader;
/*  76 */   private Dialect dialect = null;
/*  77 */   private IdentifierGeneratorFactory identifierGeneratorFactory = null;
/*     */ 
/*  79 */   protected boolean autoManagerTransaction = true;
/*     */ 
/*  81 */   private INativeJdbcExtractor nativeJdbcExtractor = null;
/*  82 */   private boolean inited = false;
/*     */ 
/*     */   public JdbcTemplate()
/*     */   {
/*  88 */     this.objectReader = new ObjectReader();
/*  89 */     this.identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
/*     */   }
/*     */ 
/*     */   public JdbcTemplate(ConnectionFactory connectionFactory)
/*     */   {
/*  97 */     this.connectionFactory = connectionFactory;
/*  98 */     this.objectReader = new ObjectReader();
/*  99 */     this.identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
/*     */   }
/*     */ 
/*     */   public JdbcTemplate(ConnectionFactory connectionFactory, IObjectReader objectReader)
/*     */   {
/* 109 */     this.connectionFactory = connectionFactory;
/* 110 */     this.objectReader = objectReader;
/* 111 */     this.identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
/*     */   }
/*     */ 
/*     */   public <T> T doExecute(ConnectionCallback<T> action) throws SQLException {
/* 115 */     Assert.notNull(action, "Callback object must not be null");
/* 116 */     Connection connection = TransactionManager.getConnection(this.connectionFactory);
/*     */     try {
/* 118 */       Object result = action.doInConnection(connection);
/* 119 */       Object localObject2 = result;
/*     */       return (T) localObject2;
/*     */     } catch (SQLException ex) {
/* 121 */       throw ex;
/*     */     } catch (RuntimeException ex) {
/* 123 */       throw ex;
/*     */     } finally {
/* 125 */       TransactionManager.closeConnection(connection);
/* 126 */     }
/*     */   }
/*     */ 
/*     */   protected <T> T doExecuteInTransation(ConnectionCallback<T> action) throws SQLException {
/* 130 */     Assert.notNull(action, "Callback object must not be null");
/* 131 */     TransactionManager.startManagedConnection(this.connectionFactory);
/* 132 */     Connection connection = TransactionManager.getConnection(this.connectionFactory);
/*     */     try {
/* 134 */       Object result = action.doInConnection(connection);
/* 135 */       TransactionManager.commit();
/* 136 */       Object localObject2 = result;
/*     */       return (T) localObject2;
/*     */     }
/*     */     catch (SQLException ex)
/*     */     {
/*     */       throw ex;
/*     */     }
/*     */     catch (RuntimeException ex)
/*     */     {
/*     */       throw ex;
/*     */     } finally {
/* 144 */       TransactionManager.closeManagedConnection();
/* 145 */     }
/*     */   }
/*     */ 
/*     */   public <T> T doExecute(StatementCallback<T> action) throws SQLException {
/* 149 */     Assert.notNull(action, "Callback object must not be null");
/* 150 */     Connection connection = TransactionManager.getConnection(this.connectionFactory);
/* 151 */     Statement stmt = null;
/*     */     try {
/* 153 */       stmt = connection.createStatement();
/* 154 */       Object result = action.doInStatement(stmt);
/* 155 */       Object localObject2 = result;
/*     */       return (T) localObject2;
/*     */     } catch (SQLException ex) {
/* 157 */       throw ex;
/*     */     } catch (RuntimeException ex) {
/* 159 */       throw ex;
/*     */     } finally {
/* 161 */       JdbcUtils.closeStatement(stmt);
/* 162 */       stmt = null;
/* 163 */       TransactionManager.closeConnection(connection);
/* 164 */     }
/*     */   }
/*     */ 
/*     */   protected <T> T doExecute(StatementCallback<T> action, String sql, Class<?> paramClass, SqlParameter[] params)
/*     */     throws SQLException
/*     */   {
/* 177 */     Assert.notNull(action, "Callback object must not be null");
/* 178 */     Assert.notNull(sql, "sql must not be null");
/* 179 */     Connection connection = TransactionManager.getConnection(this.connectionFactory);
/* 180 */     PreparedStatement stmt = null;
/*     */     try {
/* 182 */       if (logger.isDebugEnabled()) {
/* 183 */         if ((params != null) && (params.length > 0))
/* 184 */           logger.debug("Executing SQL statement [" + sql + "] values: " + Arrays.asList(params));
/*     */         else {
/* 186 */           logger.debug("Executing SQL statement [" + sql + "]");
/*     */         }
/*     */       }
/* 189 */       stmt = connection.prepareStatement(sql);
/* 190 */       int idx = 1;
/* 191 */       for (SqlParameter param : params) {
/* 192 */         int sqltype = 12;
/* 193 */          Map<String, FieldMapping>  fieldMappings = null;
/* 194 */         if (param.getOrmClass() != null)
/* 195 */           fieldMappings = this.objectReader.getObjectFieldMap(param.getOrmClass());
/* 196 */         else if (paramClass != null) {
/* 197 */           fieldMappings = this.objectReader.getObjectFieldMap(paramClass);
/*     */         }
/* 199 */         if (fieldMappings != null) {
/* 200 */           FieldMapping field = (FieldMapping)fieldMappings.get(param.getName());
/* 201 */           if (field == null)
/*     */           {
/* 203 */             for (FieldMapping fieldl : fieldMappings.values())
/* 204 */               if (fieldl.columnName().equalsIgnoreCase(param.getName())) {
/* 205 */                 sqltype = fieldl.columnType();
/* 206 */                 break;
/*     */               }
/*     */           }
/*     */           else {
/* 210 */             sqltype = field.columnType();
/*     */           }
/*     */ 
/* 213 */           Object val = param.getValue();
/* 214 */           if (val != null) {
/* 215 */             Class targetsqlcls = getObjectReader().getTargetSqlClass(sqltype);
/* 216 */             if (targetsqlcls != null) {
/* 217 */               Class srcCls = val.getClass();
/* 218 */               if (GenericConverterFactory.getInstance().needConvert(srcCls, targetsqlcls)) {
/* 219 */                 ITypeConverter converter = GenericConverterFactory.getInstance().getSqlConverter();
/* 220 */                 if (converter != null) {
/* 221 */                   val = converter.convert(val, targetsqlcls);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/* 226 */           stmt.setObject(idx, val, sqltype);
/*     */         } else {
/* 228 */           stmt.setObject(idx, param.getValue());
/*     */         }
/* 230 */         idx++;
/*     */       }
/* 232 */       Object result = action.doInStatement(stmt);
/* 233 */       Object localObject2 = result;
/*     */       return (T) localObject2;
/*     */     } catch (SQLException ex) {
/* 235 */       throw ex;
/*     */     } catch (Exception e) {
/* 237 */       throw new SQLException(e);
/*     */     } finally {
/* 239 */       JdbcUtils.closeStatement(stmt);
/* 240 */       stmt = null;
/* 241 */       TransactionManager.closeConnection(connection);
/* 242 */     }
/*     */   }
/*     */ 
/*     */   protected <T> T doExecuteInTransation(StatementCallback<T> action) throws SQLException {
/* 246 */     Assert.notNull(action, "Callback object must not be null");
/* 247 */     TransactionManager.startManagedConnection(this.connectionFactory);
/* 248 */     Connection connection = TransactionManager.getConnection(this.connectionFactory);
/* 249 */     Statement stmt = null;
/*     */     try {
/* 251 */       stmt = connection.createStatement();
/* 252 */       Object result = action.doInStatement(stmt);
/* 253 */       TransactionManager.commit();
/* 254 */       Object localObject2 = result;
/*     */       return (T) localObject2;
/*     */     } catch (SQLException ex) {
/* 256 */       TransactionManager.rollback();
/* 257 */       throw ex;
/*     */     } catch (RuntimeException ex) {
/* 259 */       TransactionManager.rollback();
/* 260 */       throw ex;
/*     */     } finally {
/* 262 */       JdbcUtils.closeStatement(stmt);
/* 263 */       stmt = null;
/* 264 */       TransactionManager.closeManagedConnection();
/* 265 */     }
/*     */   }
/*     */ 
/*     */   protected <T> T doExecuteInTransation(StatementCallback<T> action, String sql, Class<T> paramClass, SqlParameter[] params)
/*     */     throws SQLException
/*     */   {
/* 279 */     Assert.notNull(action, "Callback object must not be null");
/* 280 */     Assert.notNull(sql, "sql must not be null");
/* 281 */     TransactionManager.startManagedConnection(this.connectionFactory);
/* 282 */     Connection connection = TransactionManager.getConnection(this.connectionFactory);
/* 283 */     PreparedStatement stmt = null;
/*     */     try {
/* 285 */       if (logger.isDebugEnabled()) {
/* 286 */         if ((params != null) && (params.length > 0))
/* 287 */           logger.debug("Executing SQL statement [" + sql + "] values: " + Arrays.asList(params));
/*     */         else {
/* 289 */           logger.debug("Executing SQL statement [" + sql + "]");
/*     */         }
/*     */       }
/* 292 */       stmt = connection.prepareStatement(sql);
/* 293 */       int idx = 1;
/* 294 */       for (SqlParameter param : params) {
/* 295 */         int sqltype = 12;
/* 296 */          Map<String, FieldMapping>  fieldMappings = null;
/* 297 */         if (param.getOrmClass() != null)
/* 298 */           fieldMappings = this.objectReader.getObjectFieldMap(param.getOrmClass());
/* 299 */         else if (paramClass != null) {
/* 300 */           fieldMappings = this.objectReader.getObjectFieldMap(paramClass);
/*     */         }
/* 302 */         if (fieldMappings != null) {
/* 303 */           FieldMapping field = (FieldMapping)fieldMappings.get(param.getName());
/* 304 */           if (field == null)
/*     */           {
/* 306 */             for (FieldMapping fieldl : fieldMappings.values())
/* 307 */               if (fieldl.columnName().equalsIgnoreCase(param.getName())) {
/* 308 */                 sqltype = fieldl.columnType();
/* 309 */                 break;
/*     */               }
/*     */           }
/*     */           else {
/* 313 */             sqltype = field.columnType();
/*     */           }
/*     */ 
/* 316 */           Object val = param.getValue();
/* 317 */           if (val != null) {
/* 318 */             Class targetsqlcls = getObjectReader().getTargetSqlClass(sqltype);
/* 319 */             if (targetsqlcls != null) {
/* 320 */               Class srcCls = val.getClass();
/* 321 */               if (GenericConverterFactory.getInstance().needConvert(srcCls, targetsqlcls)) {
/* 322 */                 ITypeConverter converter = GenericConverterFactory.getInstance().getSqlConverter();
/* 323 */                 if (converter != null) {
/* 324 */                   val = converter.convert(val, targetsqlcls);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/* 329 */           stmt.setObject(idx, val, sqltype);
/*     */         } else {
/* 331 */           stmt.setObject(idx, param.getValue());
/*     */         }
/* 333 */         idx++;
/*     */       }
/* 335 */       Object result = action.doInStatement(stmt);
/* 336 */       TransactionManager.commit();
/* 337 */       Object localObject2 = result;
/*     */       return (T) localObject2;
/*     */     } catch (SQLException ex) {
/* 339 */       TransactionManager.rollback();
/* 340 */       throw ex;
/*     */     } catch (Exception ex) {
/* 342 */       TransactionManager.rollback();
/* 343 */       throw new SQLException(ex);
/*     */     } finally {
/* 345 */       JdbcUtils.closeStatement(stmt);
/* 346 */       stmt = null;
/* 347 */       TransactionManager.closeManagedConnection();
/* 348 */     }
/*     */   }
/*     */ 
/*     */   public boolean execute(final String sql, final SqlParameter... params) throws SQLException {
	class ExecuteStatementCallback implements StatementCallback<Boolean> {
		/* (non-Javadoc)
		 * @see org.uorm.dao.common.StatementCallback#doInStatement(java.sql.Statement)
		 */
		@Override
		public Boolean doInStatement(Statement stmt) throws SQLException {
			if (logger.isDebugEnabled()) {
				if(params != null && params.length >0){
					logger.debug("Executing SQL statement [" + sql + "] values: " + Arrays.asList(params));
				}else{
					logger.debug("Executing SQL statement [" + sql + "]");
				}
			}
			if(stmt instanceof PreparedStatement){
				return ((PreparedStatement)stmt).execute();
			}else{
				return stmt.execute(sql);
			}
		}
	}
	if(params == null || params.length == 0){
		if ( autoManagerTransaction ) {
			return doExecuteInTransation(new ExecuteStatementCallback());
		} else {
			return doExecute(new ExecuteStatementCallback());
		}
	}else{
		if ( autoManagerTransaction ) {
			return doExecuteInTransation(new ExecuteStatementCallback(), sql, null, params);
		} else {
			return doExecute(new ExecuteStatementCallback(), sql, null, params);
		}
	}
}
/*     */ 
/*     */   public <T> T execute(ConnectionCallback<T> action)
/*     */     throws SQLException
/*     */   {
/* 388 */     if (this.autoManagerTransaction) {
/* 389 */       return doExecuteInTransation(action);
/*     */     }
/* 391 */     return doExecute(action);
/*     */   }
/*     */ 
/*     */   public <T> T execute(StatementCallback<T> action) throws SQLException
/*     */   {
/* 396 */     if (this.autoManagerTransaction) {
/* 397 */       return doExecuteInTransation(action);
/*     */     }
/* 399 */     return doExecute(action);
/*     */   }
/*     */ 
/**
 * query
 * @param <T>
 * @param sql
 * @param rse
 * @param params
 * @return
 * @throws SQLException
 */
public <T> T query(final String sql, final ResultSetExtractor<T> rse, final SqlParameter... params) throws SQLException {
	Assert.notNull(sql, "SQL must not be null");
	Assert.notNull(rse, "ResultSetExtractor must not be null");
	class QueryStatementCallback implements StatementCallback<T> {

		/* (non-Javadoc)
		 * @see org.uorm.dao.common.StatementCallback#doInStatement(java.sql.Statement)
		 */
		@Override
		public T doInStatement(Statement stmt) throws SQLException {
			ResultSet rs = null;
			try {
				if (logger.isDebugEnabled()) {
					if(params != null && params.length >0){
						logger.debug("Executing SQL query [" + sql + "] values: " + Arrays.asList(params));
					}else{
						logger.debug("Executing SQL query [" + sql + "]");
					}
				}
				if(stmt instanceof PreparedStatement){
					rs = ((PreparedStatement)stmt).executeQuery();
				}else{
					rs = stmt.executeQuery(sql);
				}
				return rse.extractData(rs);
			} finally {
				JdbcUtils.closeResultSet(rs);
			}
		}
	}
	if(params == null || params.length == 0){
		return doExecute(new QueryStatementCallback());
	}else{
		return doExecute(new QueryStatementCallback(), sql, null, params);
	}
}
/*     */ 
/**
 * query
 * @param <T>
 * @param sql
 * @param rse
 * @param paramClass: 参数@param params参考的ORM class，参数@param params中定义的优先
 * @param params
 * @return
 * @throws SQLException
 */
public <T> T query(final String sql, final ResultSetExtractor<T> rse, Class<?> paramClass, final SqlParameter... params) throws SQLException {
	if(params == null || params.length == 0){
		return query(sql, rse);
	}else{
		Assert.notNull(sql, "SQL must not be null");
		Assert.notNull(rse, "ResultSetExtractor must not be null");
		class QueryStatementCallback implements StatementCallback<T> {

			/* (non-Javadoc)
			 * @see org.uorm.dao.common.StatementCallback#doInStatement(java.sql.Statement)
			 */
			@Override
			public T doInStatement(Statement stmt) throws SQLException {
				ResultSet rs = null;
				try {
//					if (logger.isDebugEnabled()) {
//						if(params != null && params.length >0){
//							logger.debug("Executing SQL query [" + sql + "] values: " + Arrays.asList(params));
//						}else{
//							logger.debug("Executing SQL query [" + sql + "]");
//						}
					//}
					if(stmt instanceof PreparedStatement){
						rs = ((PreparedStatement)stmt).executeQuery();
					}else{
						rs = stmt.executeQuery(sql);
					}
					return rse.extractData(rs);
				} finally {
					JdbcUtils.closeResultSet(rs);
				}
			}
		}
		return doExecute(new QueryStatementCallback(), sql, paramClass, params);
	}
}
/*     */ 
/*     */   public Map<String, Object> queryForMap(String sql, SqlParameter[] params)
/*     */     throws SQLException
/*     */   {
/* 504 */     List results = null;
/* 505 */     if ((params == null) || (params.length == 0))
/* 506 */       results = (List)query(sql, new RowMapResultSetExtractor(this.objectReader, 1), new SqlParameter[0]);
/*     */     else {
/* 508 */       results = (List)query(sql, new RowMapResultSetExtractor(this.objectReader, 1), null, params);
/*     */     }
/* 510 */     if ((results != null) && (results.size() > 0)) {
/* 511 */       return (Map)results.get(0);
/*     */     }
/* 513 */     return null;
/*     */   }
/*     */ 
/*     */   public <T> T queryForObject(Class<T> cls, String sql, SqlParameter[] params)
/*     */     throws SQLException
/*     */   {
/* 526 */     RowsResultSetExtractor extractor = new RowsResultSetExtractor(this.objectReader, cls);
/* 527 */     extractor.setMax(1);
/* 528 */     List results = null;
/* 529 */     if ((params == null) || (params.length == 0))
/* 530 */       results = (List)query(sql, extractor, new SqlParameter[0]);
/*     */     else {
/* 532 */       results = (List)query(sql, extractor, cls, params);
/*     */     }
/* 534 */     if ((results != null) && (results.size() > 0)) {
/* 535 */       return (T) results.get(0);
/*     */     }
/* 537 */     return null;
/*     */   }
/*     */ 
/*     */   public List<Map<String, Object>> queryForList(String sql, SqlParameter[] params)
/*     */     throws SQLException
/*     */   {
/* 548 */     if ((params == null) || (params.length == 0)) {
/* 549 */       return (List)query(sql, new RowMapResultSetExtractor(this.objectReader), new SqlParameter[0]);
/*     */     }
/* 551 */     return (List)query(sql, new RowMapResultSetExtractor(this.objectReader), null, params);
/*     */   }
/*     */ 
/*     */   public <T> List<T> queryForList(Class<T> cls, String sql, SqlParameter[] params)
/*     */     throws SQLException
/*     */   {
/* 565 */     if ((params == null) || (params.length == 0)) {
/* 566 */       return (List)query(sql, new RowsResultSetExtractor(this.objectReader, cls), new SqlParameter[0]);
/*     */     }
/* 568 */     return (List)query(sql, new RowsResultSetExtractor(this.objectReader, cls), cls, params);
/*     */   }
/*     */ 
/**
 * 更新
 * @param sql
 * @return
 * @throws SQLException
 */
public int update(final String sql, final SqlParameter... params) throws SQLException {
	Assert.notNull(sql, "SQL must not be null");
	class UpdateStatementCallback implements StatementCallback<Integer> {

		/* (non-Javadoc)
		 * @see org.uorm.dao.common.StatementCallback#doInStatement(java.sql.Statement)
		 */
		@Override
		public Integer doInStatement(Statement stmt) throws SQLException {
			if (logger.isDebugEnabled()) {
				if(params != null && params.length >0){
					logger.debug("Executing SQL update [" + sql + "] values: " + Arrays.asList(params));
				}else{
					logger.debug("Executing SQL update [" + sql + "]");
				}
			}
			if(stmt instanceof PreparedStatement){
				return ((PreparedStatement)stmt).executeUpdate();
			}else{
				return stmt.executeUpdate(sql);
			}
		}
		
	}
	if(params == null || params.length == 0){
		if ( autoManagerTransaction ) {
			return doExecuteInTransation(new UpdateStatementCallback());
		} else {
			return doExecute(new UpdateStatementCallback());
		}
	}else{
		if ( autoManagerTransaction ) {
			return doExecuteInTransation(new UpdateStatementCallback(), sql, null, params);
		} else {
			return doExecute(new UpdateStatementCallback(), sql, null, params);
		}
	}
} 
/**
 * 批量更新
 * @param sql
 * @return
 * @throws SQLException
 */
public int[] batchUpdate(final String[] sql) throws SQLException {
	Assert.notEmpty(sql, "SQL array must not be empty");
	class BatchUpdateStatementCallback implements StatementCallback<int[]> {

		/* (non-Javadoc)
		 * @see org.uorm.dao.common.StatementCallback#doInStatement(java.sql.Statement)
		 */
		@Override
		public int[] doInStatement(Statement stmt) throws SQLException {
			int[] rowsAffected = new int[sql.length];
			for (String sqlStmt : sql) {
				stmt.addBatch(sqlStmt);
			}
			rowsAffected = stmt.executeBatch();
			return rowsAffected;
		}
		
	}
	if ( autoManagerTransaction ) {
		return doExecuteInTransation(new BatchUpdateStatementCallback());
	} else {
		return doExecute(new BatchUpdateStatementCallback());
	}
}
/*     */ 
/*     */   public ConnectionFactory getConnectionFactory()
/*     */   {
/* 652 */     return this.connectionFactory;
/*     */   }
/*     */ 
/*     */   public void setConnectionFactory(ConnectionFactory connectionFactory)
/*     */   {
/* 659 */     this.connectionFactory = connectionFactory;
/*     */   }
/*     */ 
/*     */   public IObjectReader getObjectReader()
/*     */   {
/* 666 */     return this.objectReader;
/*     */   }
/*     */ 
/*     */   public void setObjectReader(IObjectReader objectReader)
/*     */   {
/* 673 */     this.objectReader = objectReader;
/*     */   }
/*     */ 
/*     */   public IdentifierGeneratorFactory getIdentifierGeneratorFactory()
/*     */   {
/* 680 */     return this.identifierGeneratorFactory;
/*     */   }
/*     */ 
/*     */   public void setIdentifierGeneratorFactory(IdentifierGeneratorFactory identifierGeneratorFactory)
/*     */   {
/* 688 */     this.identifierGeneratorFactory = identifierGeneratorFactory;
/*     */   }
/*     */ 
/*     */   public Dialect getDialect()
/*     */   {
/* 695 */     if (this.dialect == null) {
/* 696 */       this.dialect = genDialect(this.connectionFactory.getConfiguration().getDatabasetype(), this.connectionFactory.getConfiguration().getDialectClass());
/*     */     }
/* 698 */     return this.dialect;
/*     */   }
/*     */ 
/*     */   public boolean isOracle()
/*     */   {
/* 706 */     return DataBaseType.ORACLE == this.connectionFactory.getConfiguration().getDatabasetype();
/*     */   }
/*     */ 
/*     */   public INativeJdbcExtractor getNativeJdbcExtractor()
/*     */   {
/* 714 */     if (!this.inited) {
/* 715 */       this.inited = true;
/* 716 */       initNativeJdbcExtractor();
/*     */     }
/* 718 */     return this.nativeJdbcExtractor;
/*     */   }
/*     */ 
/*     */   protected void initNativeJdbcExtractor()
/*     */   {
/* 726 */     String pooltype = this.connectionFactory.getConfiguration().getPoolType();
/* 727 */     if ((pooltype != null) && 
/* 728 */       ("c3p0".equals(pooltype)))
/* 729 */       this.nativeJdbcExtractor = new C3P0NativeJdbcExtractor();
/*     */   }
/*     */ 
/*     */   protected Connection getOracleNatveJdbcConnection(Connection con)
/*     */     throws SQLException
/*     */   {
/* 735 */     if ((isOracle()) && 
/* 736 */       (getNativeJdbcExtractor() != null)) {
/* 737 */       return getNativeJdbcExtractor().doGetNativeConnection(con);
/*     */     }
/*     */ 
/* 740 */     return con;
/*     */   }
/*     */ 
/*     */   private Dialect genDialect(DataBaseType databasetype, String dialectClass)
/*     */   {
/* 750 */     Dialect dialect = null;
/* 751 */     if ((dialectClass == null) || (dialectClass.length() == 0));
/* 752 */     switch (databasetype) {
/*     */     case DB2:
/* 754 */       dialect = new DB2Dialect();
/* 755 */       break;
/*     */     case INFORMIX:
/* 757 */       dialect = new InformixDialect();
/* 758 */       break;
/*     */     case INGRES10:
/* 760 */       dialect = new Ingres10Dialect();
/* 761 */       break;
/*     */     case HSQL:
/* 763 */       dialect = new HSQLDialect();
/* 764 */       break;
/*     */     case FIREBIRD:
/* 766 */       dialect = new FirebirdDialect();
/* 767 */       break;
/*     */     case DERBY:
/* 769 */       dialect = new DerbyDialect();
/* 770 */       break;
/*     */     case INGRES:
/* 772 */       dialect = new IngresDialect();
/* 773 */       break;
/*     */     case H2:
/* 775 */       dialect = new H2Dialect();
/* 776 */       break;
/*     */     case INGRES9:
/* 778 */       dialect = new Ingres9Dialect();
/* 779 */       break;
/*     */     case INTERBASE:
/* 781 */       dialect = new InterbaseDialect();
/* 782 */       break;
/*     */     case MYSQL:
/* 784 */       dialect = new MySQLDialect();
/* 785 */       break;
/*     */     case ORACLE:
/* 787 */       dialect = new Oracle10gDialect();
/* 788 */       break;
/*     */     case OTHER:
/* 790 */       dialect = new MySQLDialect();
/* 791 */       break;
/*     */     case PSQL:
/* 793 */       dialect = new PostgreSQLDialect();
/* 794 */       break;
/*     */     case RDMS2200:
/* 796 */       dialect = new RDMSOS2200Dialect();
/* 797 */       break;
/*     */     case SQLSERVER:
/* 799 */       dialect = new SQLServerDialect();
/* 800 */       break;
/*     */     default:
	/* 805 */       dialect = constructDialect(dialectClass);
/* 804 */       break;
/*     */     }
/* 807 */     return dialect;
/*     */   }
/*     */ 
/*     */   private Dialect constructDialect(String typeClass)
/*     */   {
/*     */     try
/*     */     {
/* 818 */       Class<?> _class = Class.forName(typeClass);
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/*     */       Class _class;
/* 820 */       logger.error(e);
/* 821 */       return null;
/*     */     }
/*     */     try
/*     */     {
/*     */       Class _class = null;
/* 824 */       Object insObject = _class.newInstance();
/* 825 */       if ((insObject instanceof Dialect))
/* 826 */         return (Dialect)insObject;
/*     */     }
/*     */     catch (InstantiationException e) {
/* 829 */       logger.error(e);
/*     */     } catch (IllegalAccessException e) {
/* 831 */       logger.error(e);
/*     */     }
/* 833 */     return null;
/*     */   }

			// 判断是否为的关键字
			private String isKeyWord4GenerateInertSql(String column){
				List<String> mysqlKeyWord = new ArrayList<String>();
				mysqlKeyWord.add("READ");
				mysqlKeyWord.add("COMMENT");
				
				if(DataBaseType.MYSQL == this.connectionFactory.getConfiguration().getDatabasetype()){
					if(mysqlKeyWord.contains(column.toUpperCase())){
						return "`"+column+"`";
					}else{
						return column;
					}
				}else{
					return column;
				}
				
			}

/*     */   protected String generateInsertSql(Class<?> cls)
/*     */   {
/* 842 */     StringBuffer sql = new StringBuffer("INSERT INTO ");
/* 843 */     ClassMapping clsmapping = getObjectReader().getClassMapping(cls);
/* 844 */     String tableName = null;
/* 845 */     if (clsmapping != null) {
/* 846 */       tableName = clsmapping.tableName();
/*     */     }
/* 848 */     if (tableName == null) {
/* 849 */       tableName = cls.getSimpleName().toUpperCase();
/*     */     }
/* 851 */     Map<String, FieldMapping>  fieldMappings = getObjectReader().getObjectFieldMap(cls);
/* 852 */     sql.append(tableName).append('(');
/* 853 */     int count = 0;
/* 854 */     for (FieldMapping field : fieldMappings.values()) {
/* 855 */       if ((!field.includeInWrites()) || (
/* 856 */         (field.primary()) && 
/* 857 */         (clsmapping != null) && 
/* 858 */         ("native".equals(clsmapping.keyGenerator()))))
/*     */       {
/*     */         continue;
/*     */       }
/* 862 */       if (count == 0)
/* 863 */         sql.append(isKeyWord4GenerateInertSql(field.columnName()));
/*     */       else {
				  sql.append(',').append(isKeyWord4GenerateInertSql(field.columnName()));
/*     */       }
/* 867 */       count++;
/*     */     }
/*     */ 
/* 870 */     sql.append(") VALUES(");
/* 871 */     for (int i = 0; i < count; i++) {
/* 872 */       if (i == 0)
/* 873 */         sql.append('?');
/*     */       else {
/* 875 */         sql.append(", ?");
/*     */       }
/*     */     }
/* 878 */     sql.append(')');
/* 879 */     return sql.toString();
/*     */   }
/*     */ 
/*     */   protected UpdateSqlInfo generateUpdateSql(Class<?> cls, Object pojo) throws SQLException {
/* 883 */     UpdateSqlInfo updateSqlInfo = new UpdateSqlInfo();
/* 884 */     StringBuffer sql = new StringBuffer("UPDATE ");
/* 885 */     ClassMapping clsmapping = getObjectReader().getClassMapping(cls);
/* 886 */     String tableName = null;
/* 887 */     String keyOrder = null;
/* 888 */     if (clsmapping != null) {
/* 889 */       tableName = clsmapping.tableName();
/* 890 */       keyOrder = clsmapping.keyOrder();
/*     */     }
/* 892 */     if (tableName == null) {
/* 893 */       tableName = cls.getSimpleName().toUpperCase();
/*     */     }
/* 895 */     sql.append(tableName);
/* 896 */      Map<String, FieldMapping>  fieldMappings = getObjectReader().getObjectFieldMap(cls);
/* 897 */     Map datas = null;
/* 898 */     if (pojo != null) {
/*     */       try
/*     */       {
/* 901 */         datas = this.objectReader.readValue2Map(pojo);
/*     */       } catch (Exception e) {
/* 903 */         throw new SQLException(e);
/*     */       }
/*     */     }
/* 906 */     int count = 0;
/* 907 */     for (FieldMapping field : fieldMappings.values()) {
/* 908 */       if ((field.includeInWrites()) && (!field.primary())) {
/* 909 */         if (datas != null)
/*     */         {
/* 911 */           if (datas.get(field.columnName()) != null) {
/* 912 */             if (count == 0)
/* 913 */               sql.append(" SET ").append(field.columnName()).append(" = ?");
/*     */             else {
/* 915 */               sql.append(", ").append(field.columnName()).append(" = ?");
/*     */             }
/* 917 */             updateSqlInfo.addParameter(field);
/* 918 */             count++;
/*     */           }
/*     */         } else {
/* 921 */           if (count == 0)
/* 922 */             sql.append(" SET ").append(field.columnName()).append(" = ?");
/*     */           else {
/* 924 */             sql.append(", ").append(field.columnName()).append(" = ?");
/*     */           }
/* 926 */           updateSqlInfo.addParameter(field);
/* 927 */           count++;
/*     */         }
/*     */       }
/*     */     }
/* 931 */     FieldMapping[] pkfields = this.objectReader.getClassPrimaryKeys(cls, keyOrder);
/* 932 */     if ((pkfields != null) && (pkfields.length > 0)) {
/* 933 */       int idx = 0;
/* 934 */       for (FieldMapping pkfield : pkfields) {
/* 935 */         if (idx == 0)
/* 936 */           sql.append(" WHERE ").append(pkfield.columnName()).append(" = ?");
/*     */         else {
/* 938 */           sql.append(" AND ").append(pkfield.columnName()).append(" = ?");
/*     */         }
/* 940 */         updateSqlInfo.addParameter(pkfield);
/* 941 */         idx++;
/*     */       }
/*     */     }
/* 944 */     updateSqlInfo.setSql(sql.toString());
/* 945 */     return updateSqlInfo;
/*     */   }
/*     */ 
/*     */   protected UpdateSqlInfo generateDeleteSql(Class<?> cls) throws SQLException {
/* 949 */     UpdateSqlInfo updateSqlInfo = new UpdateSqlInfo();
/* 950 */     ClassMapping clsmapping = getObjectReader().getClassMapping(cls);
/* 951 */     StringBuffer sql = new StringBuffer("DELETE FROM ");
/* 952 */     String tableName = null;
/* 953 */     if (clsmapping != null) {
/* 954 */       tableName = clsmapping.tableName();
/*     */     }
/* 956 */     if (tableName == null) {
/* 957 */       tableName = cls.getSimpleName().toUpperCase();
/*     */     }
/* 959 */     sql.append(tableName).append(" WHERE ");
/* 960 */     FieldMapping[] pkfields = getObjectReader().getClassPrimaryKeys(cls, clsmapping.keyOrder());
/* 961 */     if (pkfields == null) {
/* 962 */       throw new IllegalArgumentException("can not find the primary key infomation.");
/*     */     }
/* 964 */     int idx = 0;
/* 965 */     for (FieldMapping field : pkfields) {
/* 966 */       updateSqlInfo.addParameter(field);
/* 967 */       if (idx == 0)
/* 968 */         sql.append(field.columnName()).append(" = ?");
/*     */       else {
/* 970 */         sql.append(" and ").append(field.columnName()).append(" = ?");
/*     */       }
/* 972 */       idx++;
/*     */     }
/* 974 */     updateSqlInfo.setSql(sql.toString());
/* 975 */     return updateSqlInfo;
/*     */   }
/*     */ 
/*     */   public boolean isAutoManagerTransaction() {
/* 979 */     return this.autoManagerTransaction;
/*     */   }
/*     */ 
/*     */   public void setAutoManagerTransaction(boolean autoManagerTransaction) {
/* 983 */     this.autoManagerTransaction = autoManagerTransaction;
/*     */   }
/*     */ }

/* Location:           C:\Users\ADMINI~1\AppData\Local\Temp\Rar$DIa0.241\
 * Qualified Name:     org.uorm.dao.common.JdbcTemplate
 * JD-Core Version:    0.6.0
 */