package com.qihao.toy.dal.persistent;

import org.apache.ibatis.annotations.Param;

import com.qihao.toy.dal.domain.MyFriendDO;


public interface MyFriendMapper extends CRUDMapper<MyFriendDO> {
	
	Integer modifyRelation(@Param("myId") Long myId, @Param("friendId")  Long friendId, @Param("relation") String relation);
}
