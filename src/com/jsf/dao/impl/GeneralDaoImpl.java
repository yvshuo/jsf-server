package com.jsf.dao.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.jsf.constants.TableConstant;
import com.jsf.dao.GeneralDao;
import com.jsf.exception.DaoException;
import com.jsf.util.ObjectUtil;
import com.jsf.util.StringUtil;
import com.jsf.wxpay.entity.PageBean;


@Repository
public class GeneralDaoImpl implements GeneralDao {
	
	private static Logger logger = Logger.getLogger(GeneralDaoImpl.class);
	
	/**
	 * 在讲解依赖注入的3种实现方式之前，这里先澄清一下依赖注入的意义：
	 * 让组件依赖于抽象，当组件要与其他实际对象发生依赖关系时，通过抽象来注入依赖的实际对象。 
	 * 依赖注入的3种实现方式分别是：接口注入（interface injection）、Set注入（setter injection）和构造注入（constructor injection）。
	 */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public <T> List<T> getEntityList(Class<T> beanClass, String sql, Object[] values) throws Exception{
		List<T> beanList = new ArrayList<T>();
		try{
			beanList = jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(beanClass), values);
		}catch (DataAccessException e){
			logger.error("GeneralDao getEntityList has failed!" + e);
		}
		return beanList;
	}
	
	@Override
	public <T>PageBean<T> getPageBeans(Class<T> beanClass, String sql, int pageNo, int pageSize, Object...objs) throws Exception{
		PageBean<T> page = new PageBean<T>();
		if(pageNo<1){
			pageNo = 1;
		}
		if(pageSize<1){
			pageSize = 10;
		}
		try {
			sql = sql.toLowerCase();
			String countsql = " select count(1) " + sql.substring(sql.indexOf("from"));
			Integer count = jdbcTemplate.queryForObject(countsql, objs, Integer.class);
			String limitsql = sql+" limit "+(pageNo-1)*pageSize +","+pageSize;
			List<T> dataList = (List<T>) getEntityList(beanClass, limitsql, objs);
			page.setPageNo(pageNo+"");
			page.setPageSize(pageSize);
			page.setRowsCount(count != null ? count.intValue() : 0);
			page.setDatas(dataList);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return page;
	}

	@Override
	public <T> T getEntity(Class<T> beanClass, String sql, Object[] values)
			throws Exception {
		List<T> beanList = null;
		try{
//			bean = jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(beanClass), values);
			beanList = jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(beanClass), values);
		}catch(EmptyResultDataAccessException e){
			return null;
		}catch(DataAccessException e){
			logger.error("GeneralDao getEntity with sql has failed!"+ sql + e);
		}
		return beanList != null && beanList.size() > 0 ? beanList.get(0) : null;
	}
	
	
	/**
	 * 通过主键值查出对象
	 * @param beanClass 
	 * @param primaryKeyValue 主键的值，根据该值查出对象数据
	 */
	@Override
	public <T> T getEntity(Class<T> beanClass, Object primaryKeyValue) throws Exception{
		T bean = null;
		String tableName = TableConstant.TABLE_BEAN.get(beanClass.getName());
		String primaryKey = TableConstant.TABLE_PRIMARY_KEY.get(tableName);
		if(StringUtil.isEmpty(tableName)){
			logger.info("对象实体bean未与数据库表名称映射，请维护TableConstant.java");
			return null;
		}
		if(StringUtil.isEmpty(primaryKey)){
			logger.info("数据库表名称未与表主键字段映射，请维护TableConstant.java");
			return null;
		}
		StringBuilder sql = new StringBuilder(" select * from ");
		sql.append(tableName).append(" where ").append(primaryKey).append(" = ? ");
		try{
			bean = jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(beanClass), primaryKeyValue);
		}catch(EmptyResultDataAccessException e){
			logger.info("There is no result,Incorrect result size: expected 1, actual 0");
			return null;
		}catch (DataAccessException e){
			logger.error("GeneralDao getEntity by primaryKeyValue has failed!" + e);
		}
		return bean;
	}
	
	/**
	 * 获取Map集合值
	 * @param sql
	 * @param obj
	 * @return
	 */
	@Override
	public Map<String,?> getMap(String sql,Object[] obj) throws Exception{
		
		Map<String, ?> map = null;
		try {
			map = jdbcTemplate.queryForMap(sql, obj);
		} catch (Exception e) {
			logger.info("获取Map集合值失败！" + e);
			throw new DaoException("数据库操作失败！",e);
		}
		return map;
	}

	@Override
	public boolean delEntity(Object entity) throws Exception {
		String tableName = TableConstant.TABLE_BEAN.get(entity.getClass().getName());
		String primaryKey = TableConstant.TABLE_PRIMARY_KEY.get(tableName);
		if(StringUtil.isEmpty(tableName)){
			logger.info("对象实体bean未与数据库表名称映射，请维护TableConstant.java");
			return false;
		}
		if(StringUtil.isEmpty(primaryKey)){
			logger.info("数据库表名称未与表主键字段映射，请维护TableConstant.java");
			return false;
		}
		StringBuilder sql = new StringBuilder(" delete from ");
		sql.append(tableName).append(" where ");
		sql.append(primaryKey).append(" = ? ");
		List<Object> objList = new ArrayList<Object>();
		PropertyDescriptor pd = new PropertyDescriptor(primaryKey, entity.getClass());   
        Method getMethod = pd.getReadMethod();  //获得get方法   
        Object primaryKeyValue = getMethod.invoke(entity);
		objList.add(primaryKeyValue);
//		try{
//			int returnFlag = jdbcTemplate.update(sql.toString(), objList.toArray());
//			logger.info("returnFlag:" + returnFlag);
//		}catch(DataAccessException e){
//			logger.error("GeneralDao delEntity by has failed!" + e);
//			delFlag = false;
//		}
//		return delFlag;
		return delEntityBySql(sql.toString(), objList.toArray());
	}
	
	@Override
	public boolean delEntityBySql(String sql, final Object... objs) throws Exception{
		boolean delFlag = true;
		try{
			int returnFlag = jdbcTemplate.update(sql.toString(), objs);
			logger.info("returnFlag:" + returnFlag);
		}catch(DataAccessException e){
			logger.error("GeneralDao delEntity by has failed!" + e);
			delFlag = false;
		}
		return delFlag;
	}
//	
//	@Override
//	public boolean saveEntity(String sql, Object obj) throws Exception {
//		return addOrUpdate(sql, obj);
//	}
	
	@Override
	public int saveEntityReturnKey(String insertSql, final Object... params) throws Exception {
		final String sql = insertSql;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, 
						PreparedStatement.RETURN_GENERATED_KEYS);
				PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(params);
				try {
					if (pss != null) {
						pss.setValues(ps);
					}
				} finally {
					if (pss instanceof ParameterDisposer) {
						((ParameterDisposer) pss).cleanupParameters();
					}
				}
				return ps;
            }
            
        }, keyHolder);
        logger.info("GeneralDao saveEntityReturnKey the primaryKey is:" + keyHolder.getKey().intValue());
        return keyHolder.getKey().intValue();
	}
	
	@Override
	public int saveEntityReturnKeyHolder(String insertSql, final Object... params) throws Exception {
		final String sql = insertSql;
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, 
						new String[]{"id"});
				PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(params);
				try {
					if (pss != null) {
						pss.setValues(ps);
					}
				} finally {
					if (pss instanceof ParameterDisposer) {
						((ParameterDisposer) pss).cleanupParameters();
					}
				}
				return ps;
			}
			
		}, keyHolder);
		logger.info("GeneralDao saveEntityReturnKey the primaryKey is:" + keyHolder.getKey().intValue());
		return keyHolder.getKey().intValue();
	}
	
	@Override
	public int saveEntityReturnKey(Object obj) throws Exception{
		Field[] fields = obj.getClass().getDeclaredFields();
		List<Object> objectList = new ArrayList<Object>();
		for(Field field: fields){
			if(!field.getName().endsWith("Str") 
					&& !"serialVersionUID".equalsIgnoreCase(field.getName()) 
					&& !TableConstant.TABLE_PRIMARY_KEY.get(
							TableConstant.TABLE_BEAN.get(obj.getClass().getName())
							).equalsIgnoreCase(field.getName())){
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(), obj.getClass());
				Method getMethod = pd.getReadMethod();//获得get方法
				objectList.add(getMethod.invoke(obj));
			}
		}
		return saveEntityReturnKey(createInserSqlStr(obj), objectList.toArray());
	}
	
	@Override
	public boolean saveEntity(Object obj) throws Exception{
		return insert(createInserSqlStr(obj), obj);
	}
	
	private String createInserSqlStr(Object obj) throws Exception{
//		Class<? extends Object> c = obj.getClass(); 
		if(StringUtil.isEmpty(TableConstant.TABLE_BEAN.get(obj.getClass().getName()))
				|| StringUtil.isEmpty(TableConstant.TABLE_PRIMARY_KEY.get(TableConstant.TABLE_BEAN.get(obj.getClass().getName())))){
			throw new RuntimeException("请将要操作的数据库表维护到TableConstant类中！");
		}
		String sqlStr = "";
		String[] fieldArr = ObjectUtil.getEntityFiled(obj.getClass());
		StringBuilder sb = new StringBuilder(" insert into ");
		sb.append(TableConstant.TABLE_BEAN.get(obj.getClass().getName()));
		sb.append(" ( ");
		for(String field : fieldArr){
			sb.append(field).append(",");
		}
		sqlStr = sb.toString();
		sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
		sb = new StringBuilder(sqlStr);
		sb.append(" ) values ( ");
		for(int i=0;i<fieldArr.length;i++){
//		for(String field : fieldArr){
			sb.append(" ?,");
		}
		sqlStr = sb.toString();
		sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
		sqlStr += ")";
		logger.info("GeneralDao createInserSqlStr is:" + sqlStr);
		return sqlStr;
	}
	
	@Override
	public boolean updateEntity(String sql, Object[] values) throws Exception{
		boolean updateFlag = true;
		try{
			int returnFlag = jdbcTemplate.update(sql, values);
			logger.info("returnFlag打印输出：" + returnFlag);
		}catch(DataAccessException e){
			logger.error("GeneralDao updateEntity has failed!" + e);
			updateFlag = false;
		}
		return updateFlag;
	}
	
	@Override
	public boolean updateEntity(Object entity, String... updateFields) throws Exception {
		Class<?> clazz = entity.getClass();   
		String tableName = TableConstant.TABLE_BEAN.get(clazz.getName());
		String primaryKey = TableConstant.TABLE_PRIMARY_KEY.get(tableName);
		if(StringUtil.isEmpty(tableName)){
			logger.error("对象实体bean未与数据库表名称映射，请维护TableConstant.java");
			throw new Exception("对象实体bean未与数据库表名称映射，请维护TableConstant.java");
		}
		if(StringUtil.isEmpty(primaryKey)){
			logger.error("数据库表名称未与表主键字段映射，请维护TableConstant.java");
			throw new Exception("数据库表名称未与表主键字段映射，请维护TableConstant.java");
		}
		return updateEntityString(clazz, tableName, primaryKey, entity, updateFields);
    }
	
	private boolean updateEntityString (Class<?> clazz, String tableName, String primaryKey, Object entity, String ... updateFields) throws Exception{
		boolean updateFlag = true;
		StringBuilder sqlBuilder = new StringBuilder("update ").append(tableName);
		sqlBuilder.append(" set ");
		for(int i=0; i<updateFields.length; i++){
			sqlBuilder.append(updateFields[i] + " = ? ").append(i == updateFields.length - 1 ? "" : ", ");
		}
//		jdbcTemplate.update("update stu set s_name=?,s_sex=?,s_brith=? where s_id=?", "a", "b" ,"c", "d");
		List<Object> objList = new ArrayList<Object>();
		for(String field:updateFields){
			//需要利用反射机制，将对象的值取出放到objList中
		    PropertyDescriptor pd = new PropertyDescriptor(field, clazz);   
	        Method getMethod = pd.getReadMethod();  //获得get方法   
			objList.add(getMethod.invoke(entity));
		}
		sqlBuilder.append(" where ");
		sqlBuilder.append(primaryKey).append(" = ? ");   //获得表的主键字段名称
		PropertyDescriptor pd = new PropertyDescriptor(primaryKey, clazz);   
        Method getMethod = pd.getReadMethod();  //获得get方法   
		objList.add(getMethod.invoke(entity));
		try{
			int returnFlag = jdbcTemplate.update(sqlBuilder.toString(), objList.toArray());
			logger.info("returnFlag updateEntityString :" + returnFlag);
		}catch(DataAccessException e){
			logger.error("GeneralDao updateEntityString has failed!" + e);
			updateFlag = false;
		}
        return updateFlag;
	}
	
	private boolean insert(String sql,Object obj){
		boolean updateFlag = true;
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			List<Object> objectList = new ArrayList<Object>();
			for(Field field: fields){
				if(!field.getName().endsWith("Str") 
						&& !"serialVersionUID".equalsIgnoreCase(field.getName()) 
						&& !TableConstant.TABLE_PRIMARY_KEY.get(
								TableConstant.TABLE_BEAN.get(obj.getClass().getName())
								).equalsIgnoreCase(field.getName())){
					PropertyDescriptor pd = new PropertyDescriptor(field.getName(), obj.getClass());
					Method getMethod = pd.getReadMethod();//获得get方法
					objectList.add(getMethod.invoke(obj));
				}
			}
			PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(objectList.toArray());
			int returnFlag = jdbcTemplate.update(sql, pss);
			logger.info("returnFlag打印输出：" + returnFlag);
		} catch (Exception e) {
			logger.error("GeneralDao insert has failed!" + e);
			throw new DaoException("数据库操作失败！",e);
		}
		return updateFlag;
	}
	
	@Override
	public int[] batchUpdate(String sql, List<Object[]> batchArgs){
		return jdbcTemplate.batchUpdate(sql, batchArgs);
	}

	
//	非常重要
//	/**
//	 * 数据存取适配器  
//	 * @author David Day
//	 */
//	public class JdbcTemplateAdapter extends JdbcTemplate {
//		
//		public JdbcTemplateAdapter() {
//			super();
//		}
//		
//		public JdbcTemplateAdapter(DataSource ds) {
//			super(ds);
//		}
//		
//		/**
//		 * 增加并且获取主键
//		 * @param sql sql语句
//		 * @param params 参数列表
//		 * @return 主键
//		 */
//		public Object insertAndGetKey(final String sql, final Object... params) {
//			logger.debug("Executing SQL update and returning generated keys");
//			
//			final KeyHolder key = new GeneratedKeyHolder();
//
//			update(new PreparedStatementCreator() {
//
//				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//					PreparedStatement ps = con.prepareStatement(sql, 
//							PreparedStatement.RETURN_GENERATED_KEYS);
//					PreparedStatementSetter pss = newArgPreparedStatementSetter(params);
//					try {
//						if (pss != null) {
//							pss.setValues(ps);
//						}
//					} finally {
//						if (pss instanceof ParameterDisposer) {
//							((ParameterDisposer) pss).cleanupParameters();
//						}
//					}
//					return ps;
//				}
//				
//			}, key);
//			
//			return key.getKey();
//		}
//
//	}
	
	
	
	/**
	 * 测试
	 * jack
	 * 2013-9-5
	 */
//	public List<User> test() throws Exception{
//		List<User> userList = null;
//		userList = getEntityList(User.class, "select * from user where userName like ? and passWord = ? ", new Object[]{"%wang%", "admins"});
//		return userList;
//	}
	
	
	
//	public boolean testBatchUpdate(List<User> userList){
//		String sql = "insert into user (userName, passWord) VALUES (:userName, :passWord)";
//		SqlParameterSource[] sqlArray = new SqlParameterSource[userList.size()];
//		for(int i=0; i<userList.size(); i++){
//			SqlParameterSource param = new BeanPropertySqlParameterSource(userList.get(i));
//			sqlArray[i] = param;
//		}
//		simpleJdbcTemplate.batchUpdate(sql, sqlArray);
		
		
//		String sql = "insert into user (userName, passWord) VALUES (? ,?)";
//		List<Object[]> batchArgs = new ArrayList<Object[]>();
//		for(User user:userList){
//			batchArgs.add(new Object[]{user.getUserName(), user.getPassWord()});
//		}
//		simpleJdbcTemplate.batchUpdate(sql, batchArgs);
		
		
		//该方法比较老，不知道与新方法比较而言，哪个更好，先留下注释版本，待研究
//		final List<User> batchUser = userList;
//		simpleJdbcTemplate.getJdbcOperations().batchUpdate(sql, 
//				new BatchPreparedStatementSetter() {
//					
//					@Override
//					public void setValues(PreparedStatement ps, int index) throws SQLException {
//						User user = batchUser.get(index);
//						ps.setString(1, user.getUserName());
//						ps.setString(2, user.getPassWord());
//						//每2000条进行事物提交   
//	                    if (index%2000 == 0) {   
//	                        ps.executeBatch(); //执行prepareStatement对象中所有的sql语句   
//	                    }   
//					}
//					
//					@Override
//					public int getBatchSize() {
//						return batchUser.size();
//					}
//				}
//		);
//		return true;
//	}

}
