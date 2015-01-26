package com.qihao.toy.dal.persistent;

import com.qihao.toy.dal.domain.MyToyDO;


public interface MyToyMapper extends CRUDMapper<MyToyDO> {
	MyToyDO getItemByToySN(String toySN);
}
