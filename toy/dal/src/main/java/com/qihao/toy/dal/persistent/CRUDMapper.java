package com.qihao.toy.dal.persistent;

import java.io.Serializable;
import java.util.List;

/**
 * 通用的
 */
public interface CRUDMapper<T extends Serializable> {

	/**
	 * getById 正常long主键的
	 * 
	 * @param id
	 * @return
	 */
	T getById(Long id);

	/**
	 * getAll
	 * 
	 * @param id
	 * @return
	 */
	List<T> getAll(T t);

	/**
	 * insert，返回的是影响的行数
	 * 
	 * @param bean
	 * @return
	 */
	Long insert(T bean);

	/**
	 * update
	 * 
	 * @param bean
	 * @return
	 */
	boolean update(T bean);

	/**
	 * deletebyId
	 */
	boolean deleteById(Long id);
}
