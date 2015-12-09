package com.test.sqlserver;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.linkprise.dao.common.CommonDaoImpl;
import com.linkprise.dao.common.ConnectionFactory;
import com.linkprise.dao.common.DatasourceConfig;
import com.linkprise.dao.common.DefaultConnectionFactory;
import com.linkprise.dao.common.ICommonDao;
import com.linkprise.dao.common.PaginationSupport;
import com.linkprise.dao.common.SqlParameter;

public class TestDao {

	public static void main(String[] args) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String url = "jdbc:sqlserver://127.0.0.1;DatabaseName=my_orm_db";
		String username = "sa";
		String password = "Tubashu666";
				
		long start = System.currentTimeMillis();
		DatasourceConfig config = new DatasourceConfig();
//		config.setDatabasetype(DataBaseType.MYSQL);
		config.setDriverClass(driver);
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);		
		
		Map<String, String> poolPerperties = new HashMap<String, String>();
		poolPerperties.put("___POOL_TYPE_", "c3p0");
		poolPerperties.put("initialPoolSize", "2");
		poolPerperties.put("maxIdleTime", "600");
		poolPerperties.put("idleConnectionTestPeriod", "600");
		config.setPoolPerperties(poolPerperties);
		
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(config);
		ICommonDao dao = new CommonDaoImpl(connectionFactory );
//		dao.beginTransation();
		//User user = dao.queryBusinessObjByPk(User.class, 1);		
		
		// 先在数据库手动插入一条数据，方便测试
		
		// 1.查询泛型集合
		List<Book> list1 = dao.queryBusinessObjs(Book.class,
				"select * from book where id=?", new SqlParameter(Book.PROP_ID, 1));
		
		for(Book book:list1){
			System.out.println(book.getId());
			System.out.println(book.getName());
			System.out.println(book.getDates());
			System.out.println(book.getPages());
			System.out.println(book.getAuthor());
		}
		
		// 2.保存（也可保存集合）
		//dao.saveBusinessObjs(list1.get(0));
		
		List<Book> newListBook = new ArrayList<Book>();
		
		Book newBook1 = new Book(); 
		newBook1.setDates(Timestamp.valueOf("2015-12-09 14:40:33"));
		newBook1.setId(1);
		newBook1.setName("weeks book");
		
		//newListBook.add(newBook1);
		newListBook.add(list1.get(0));
		
		dao.saveBusinessObjs(newListBook);
		
		System.out.println("==============");
		
		// 3.查询→List<Map<String, Object>>
		List<Map<String, Object>> list2 =
				dao.queryForListMap("select * from book where id=?", new SqlParameter(Book.PROP_ID, 1));
		
		for(Map<String, Object> map:list2){
			for(String str:map.keySet()){
				System.out.println(str+"--"+map.get(str));				
			}
		}
		
		System.out.println("==============");
		
		// 4.批处理
		String[] sqls = new String[30000];
		for(int k=1;k<=30000;k++){
			sqls[k-1] = "delete from book where name ='tagID"+k+"'";
		}
		
		System.out.println(df.format(new Date()));
		
		dao.batchUpdate(sqls);
		
		System.out.println(df.format(new Date()));
		
		System.out.println("==============");
		
		// 5.分页（PaginationSupport为分页对象）
		PaginationSupport<Book> upage = dao.queryByPagedQuery(Book.class, "select * from book", 0, 3);
		
		System.out.println("页数量："+upage.getPageCount());
		System.out.println("总记录数："+upage.getTotalCount());
		
		for(Book book:upage.getItems()){
			System.out.println(book.getId());
			System.out.println(book.getName());
			System.out.println(book.getDates());
			System.out.println(book.getPages());
			System.out.println(book.getAuthor());
		}
		
		System.out.println("==============");
		
		// 6.查询数量
		Long count = dao.querySingleObject(Long.class, "select count(*) from book");
		System.out.println("数量"+count);
		
		// 7.Map查询
		Map<String, Object> map1 =
				dao.queryForMap("select * from book where id=?", new SqlParameter(Book.PROP_ID, 1));
		
		for(String str:map1.keySet()){
			System.out.println(str+"--"+map1.get(str));				
		}
		
		System.out.println("==============");
		
		// 8.查询单个对象
		Book b1 = dao.querySingleObject(Book.class, "select * from book where id=?", new SqlParameter(Book.PROP_ID, 1));		
		System.out.println(b1.getId());
		System.out.println(b1.getName());
		System.out.println(b1.getDates());
		System.out.println(b1.getPages());
		System.out.println(b1.getAuthor());
		
		System.out.println("==============");
		
		Book newBook = list1.get(0);
		newBook.setDates(Timestamp.valueOf("2015-12-09 14:40:33"));
		//newBook.setAuthor("weeks1743");
		newBook.setAuthor(null);
		
		// 9.更新
		// 当第一个参数为true，则会忽略掉空值，则author字段不会进行更新
		int tag = dao.updateBusinessObjs(true, newBook);
		System.out.println("修改标识："+tag);
		
		// 10.删除
		// 找对象的PK进行删除
		//int tag2 = dao.deleteBusiness(newBook1);
		//System.out.println("删除标识："+tag2);

		
	}

}
