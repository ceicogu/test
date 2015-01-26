package com.qihao.toy.dal.persistent;

import org.apache.ibatis.annotations.Param;

import com.qihao.toy.dal.domain.UserDO;

/**
 * 用户登录信息表DAO
 */
public interface UserMapper extends CRUDMapper<UserDO> {
	/**
	 * 登录
	 */
	UserDO login(@Param("loginName") String loginName, @Param("password") String password);

	/**
	 * 修改密码（知道老密码）
	 * @param uid　　　　		帐号ID
	 * @param oldMd5Pwd	原密码
	 * @param md5Pwd			新密码
	 * @return
	 */
	Integer modifyPassword(@Param("id") Long id, @Param("oldPassword")  String oldPassword, @Param("password") String password);
}
