package com.jsf.wxpay.dao;

import java.util.List;
import java.util.Map;

import com.jsf.wxpay.entity.PageBean;


public interface GeneralDao {
	
	/**
	 * 通过sql查询对象list
	 * @param sql 数据库select语句格式：select * from user where userName like ? and passWord = ? 
	 * @param values 对象数组，sql语句有几个？，则数组有几个对象，一一对应: new Object[]{"%wang%", "admins"}
	 */
	public <T> List<T> getEntityList(Class<T> beanClass, String sql, Object[] values) throws Exception;
	/**
	 * 通过sql以及分页页数查询分页数据list
	 */
	public <T>PageBean<T> getPageBeans(Class<T> beanClass, String sql, int pageNo, int pageSize, Object...objs) throws Exception;
	
	/**
	 * 通过sql查询对象
	 * @param sql 数据库select语句格式：select * from user where userName = ? 
	 * @param values 对象数组，sql语句有几个？，则数组有几个对象，一一对应: new Object[]{"wang"}
	 */
	public <T> T getEntity(Class<T> beanClass, String sql, Object[] values) throws Exception;
	
	/**
	 * 通过主键值查出对象
	 * @param beanClass 
	 * @param primaryKeyValue 主键的值，根据该值查出对象数据
	 */
	public <T> T getEntity(Class<T> beanClass, Object primaryKeyValue) throws Exception;
	
	/**
	 * 获取Map集合值
	 * @param sql
	 * @param obj
	 * @return
	 */
	public Map<String,?> getMap(String sql,Object[] obj) throws Exception;
	
	/**
	 * 删除对象
	 * @param entity 对象中主键必须有值
	 * jack
	 * 2013-9-5
	 */
	public boolean delEntity(Object entity) throws Exception;
	
	/**
	 * 执行删除的sql语句
	 */
	public boolean delEntityBySql(String sql, final Object... objs) throws Exception;
	
	/**
	 * 新增数据返回主键
	 * 相比saveEntityReturnKey(Object obj)效率更高一些
	 * 1.sql语句格式：insert into user(username,password,first_name,last_name,birthday,age) 
	 * 					values(?,?,?,?,?,?)
	 * @param sql 
	 * @param obj 实体对象
	 * @return
	 */
	public int saveEntityReturnKey(String insertSql, final Object... params) throws Exception;
	
	/**
	 * 新增数据返回主键
	 * oracle专用
	 * 相比saveEntityReturnKey(Object obj)效率更高一些
	 * 1.sql语句格式：insert into user(username,password,first_name,last_name,birthday,age) 
	 * 					values(?,?,?,?,?,?)
	 * @param sql 
	 * @param obj 实体对象
	 * @return
	 */
	public int saveEntityReturnKeyHolder(String insertSql, final Object... params) throws Exception;
	/**
	 * 新增数据返回主键
	 * 相比saveEntityReturnKey(String insertSql, final Object... params)效率低一些
	 */
	public int saveEntityReturnKey(Object obj) throws Exception;
	
	/**
	 * 新增数据库数据，只需要传入对象即可
	 */
	public boolean saveEntity(Object obj) throws Exception;
	
	/**
	 * 通过sql更新数据库
	 * @param sql 数据库update语句格式：update stu set s_name=?, s_sex=?, s_brith=? where id=?
	 * @param values 对象数组，update的sql语句有几个？，则数组有几个对象，一一对应
	 */
	public boolean updateEntity(String sql, Object[] values) throws Exception;
	
	/**
	 * 更新一个对象的指定字段
	 * @param entity 要更新的对象，主键必须有值
	 * @param updateFields String的数组，更新对象的哪些字段
	 */
	public boolean updateEntity(Object entity, String... updateFields) throws Exception;
	
	public int[] batchUpdate(String sql, List<Object[]> batchArgs);

}
