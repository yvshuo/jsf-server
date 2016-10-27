package com.jsf.util;

import java.lang.reflect.Field;

import com.jsf.wxpay.constants.TableConstant;


/**
 * 对象工具类
 */
public class ObjectUtil {
	
	private static <T> String getClassFieldStr(Class<T> beanClass){
		Field[] fields = beanClass.getDeclaredFields();
		StringBuilder str = new StringBuilder();
		for(Field field: fields){
			if(!field.getName().endsWith("Str") 
					&& !"serialVersionUID".equalsIgnoreCase(field.getName()) 
					&& !TableConstant.TABLE_PRIMARY_KEY.get(
							TableConstant.TABLE_BEAN.get(beanClass.getName())
							).equalsIgnoreCase(field.getName())){
				str.append(field.getName() + ",");
			}
		}
		return str.substring(0, str.length()-1);
	}
	
	/**
	 * 为框架的GeneralDao提供工具方法
	 * 拿到实体类后，返回需要insert到数据库中的字段数组，该数组不包括（serialVersionUID，主键）两个字段
	 */
	public static <T> String[] getEntityFiled(Class<T> beanClass){
		return getClassFieldStr(beanClass).split(",");
	}


}
