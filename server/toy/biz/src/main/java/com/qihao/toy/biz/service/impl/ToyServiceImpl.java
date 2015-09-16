package com.qihao.toy.biz.service.impl;

import com.qihao.toy.biz.service.ToyService;
import com.qihao.toy.dal.domain.MyToyDO;
import com.qihao.toy.dal.domain.ToyDO;
import com.qihao.toy.dal.persistent.MyToyMapper;
import com.qihao.toy.dal.persistent.ToyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ToyServiceImpl implements ToyService {
	@Autowired
	private ToyMapper toyMapper;
	@Autowired
	private MyToyMapper myToyMapper;
	
	public Long insert(ToyDO toy) {
		toyMapper.insert(toy);
		return toy.getId();
	}

	public ToyDO getItemByToySN(String toySN) {
		return toyMapper.getItemByToySN(toySN);
	}

	public Boolean update(String toySN, ToyDO toy) {
		toy.setToySN(toySN);
		return toyMapper.update(toy);
	}

	public Boolean toAcitvateToy(long activatorId, String toySN, String toyMac) {		
		ToyDO toy =  toyMapper.getItemByToySN(toySN);
		if(null ==toy) {
			throw new RuntimeException("故事机不存在!");
		}
		if(toy.getStatus().equals(ToyDO.ToyStatus.Activated)){
			return true;
		}
		if(!toy.getStatus().equals(ToyDO.ToyStatus.Initial)) {
			log.error("故事机已被激活!");
			throw new RuntimeException("故事机已被激活!");
		}
		toy.setToySN(toySN);
		toy.setToyMac(toyMac);
		toy.setActivatorId(activatorId);
		toy.setStatus(ToyDO.ToyStatus.Activated);
		return toyMapper.update(toy);
	}
	public ToyDO toClaimToy(long ownerId, String toySN) {		
		ToyDO toy =  toyMapper.getItemByToySN(toySN);
		if(null ==toy) {
			throw new RuntimeException("故事机不存在!");
		}
		if(toy.getStatus().equals(ToyDO.ToyStatus.Claimed)) {
			if(toy.getOwnerId().equals(ownerId)) {
				return toy;
			}
		}
		if(!toy.getStatus().equals(ToyDO.ToyStatus.Activated)) {
			throw new RuntimeException("故事机已被认领!");
		}
		toy.setToySN(toySN);
		toy.setOwnerId(ownerId);
		toy.setStatus(ToyDO.ToyStatus.Claimed);
		toy.setGmtOwned(new Date());
		toyMapper.update(toy);
		//加入我管理的故事机队列
		MyToyDO myToyDO = new MyToyDO();
		myToyDO.setMyId(ownerId);
		myToyDO.setToySN(toySN);
		myToyDO.setToyUserId(toy.getActivatorId());
		myToyMapper.insert(myToyDO);
		return toy;
	}

	public List<ToyDO> getAll(ToyDO toy) {
		return toyMapper.getAll(toy);
	}
	public List<Long> getMyToyUserIds(long myId) {
		return myToyMapper.getMyToyUserIds(myId);
	}
	public List<ToyDO> getMyClaimToys(long ownerId) {
		ToyDO toyDO = new ToyDO();
		toyDO.setOwnerId(ownerId);
		
		return toyMapper.getAll(toyDO);
	}

	public List<ToyDO> getMyManageToys(long myId) {
		return toyMapper.getMyManageToys(myId);
	}
	public ToyDO getMyManageToy(long myId, long toyUserId) {
		return toyMapper.getMyManageToy(myId,toyUserId);
	}	
}
