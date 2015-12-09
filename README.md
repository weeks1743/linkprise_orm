# linkprise_orm
linkprise.com orm framework 暴力开发框架

## 简介

linkprise_orm 是一个适用、快速、简单、轻量级的 Java ORM 小型框架,无第三方包依赖，只有一个commons-logging.jar 。
整个jar包只有100KB，并封装了很多实用的操作函数，详细见api中的com.linkprise.dao.common

# 暴力开发
配合linkprise_angularJS、linkprise_ionic、linkprise_Resetful、linkprise_dx 完成移动APP前后台暴力开发（3天完成一个APP不是梦！）


## 简单的POJO生成工具

详见com.test.myql.Generator的使用方法

## 那么一点点代码事例

```java
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
dao.saveBusinessObjs(list1.get(0));

List<Book> newListBook = new ArrayList<Book>();

Book newBook1 = new Book(); 
newBook1.setDates(Timestamp.valueOf("2015-12-09 14:40:33"));

newListBook.add(newBook1);
newListBook.add(list1.get(0));		

dao.saveBusinessObjs(newListBook);


// 3.查询→List<Map<String, Object>>
List<Map<String, Object>> list2 =
		dao.queryForListMap("select * from book where id=?", new SqlParameter(Book.PROP_ID, 1));

for(Map<String, Object> map:list2){
	for(String str:map.keySet()){
		System.out.println(str+"--"+map.get(str));				
	}
}


// 4.批处理
String[] sqls = new String[3000];
for(int k=1;k<=3000;k++){
	sqls[k-1] = "delete from book where id ='tagID"+k+"'";
}



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


// 6.查询数量
Long count = dao.querySingleObject(Long.class, "select count(*) from book");
System.out.println("数量"+count);


// 7.Map查询
Map<String, Object> map1 =
		dao.queryForMap("select * from book where id=?", new SqlParameter(Book.PROP_ID, 1));

for(String str:map1.keySet()){
	System.out.println(str+"--"+map1.get(str));				
}


// 8.查询单个对象
Book b1 = dao.querySingleObject(Book.class, "select * from book where id=?", new SqlParameter(Book.PROP_ID, 1));		
System.out.println(b1.getId());
System.out.println(b1.getName());
System.out.println(b1.getDates());
System.out.println(b1.getPages());
System.out.println(b1.getAuthor());


// 9.更新
// 当第一个参数为true，则会忽略掉空值，则author字段不会进行更新
Book newBook = list1.get(0);
newBook.setDates(Timestamp.valueOf("2015-12-09 14:40:33"));
//newBook.setAuthor("weeks1743");
newBook.setAuthor(null);

int tag = dao.updateBusinessObjs(true, newBook);
System.out.println("修改标识："+tag);		

	
// 10.删除
// 找对象的PK进行删除
int tag2 = dao.deleteBusiness(newBook1);
System.out.println("删除标识："+tag2);
```

## 支持数据库类型：
DB2、Postgresql、Oracle、MS-SqlServer、MySql、H2Database、Derby、HSQL、Firebird、
Interbase、Informix、Ingres (9,10)、Unisys 2200 Relational Database (RDMS)、TimesTen 5.


## Dependency jar(依赖包)
只有一个依赖包 commons-logging.jar 