package com.qihao.toy.biz.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.lucene.document.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qihao.toy.biz.service.ToyService;
import com.qihao.toy.dal.domain.MyToyDO;
import com.qihao.toy.dal.domain.ToyDO;
import com.qihao.toy.dal.enums.ToyStatusEnum;
import com.qihao.toy.dal.persistent.MyToyMapper;
import com.qihao.toy.dal.persistent.ToyMapper;

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
		if(toy.getStatus().equals(ToyStatusEnum.Activated.numberValue())){
			return true;
		}
		if(!toy.getStatus().equals(ToyStatusEnum.Initial.numberValue())) {
			log.error("故事机已被激活!");
			throw new RuntimeException("故事机已被激活!");
		}
		toy.setToySN(toySN);
		toy.setToyMac(toyMac);
		toy.setActivatorId(activatorId);
		toy.setStatus(ToyStatusEnum.Activated.numberValue());
		return toyMapper.update(toy);
	}
	public ToyDO toClaimToy(long ownerId, String toySN) {		
		ToyDO toy =  toyMapper.getItemByToySN(toySN);
		if(null ==toy) {
			throw new RuntimeException("故事机不存在!");
		}
		if(toy.getStatus().equals(ToyStatusEnum.Claimed.numberValue())) {
			if(toy.getOwnerId().equals(ownerId)) {
				return toy;
			}
		}
		if(!toy.getStatus().equals(ToyStatusEnum.Activated.numberValue())) {
			throw new RuntimeException("故事机已被认领!");
		}
		toy.setToySN(toySN);
		toy.setOwnerId(ownerId);
		toy.setStatus(ToyStatusEnum.Claimed.numberValue());
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
	public ToyDO toNameToy(long ownerId, String toySN, String toyName, String kidName, Integer kidGender, Integer kidAge, Date kidBirth){
		ToyDO toy =  toyMapper.getItemByToySN(toySN);
		if(null ==toy) {
			throw new RuntimeException("故事机不存在!");
		}
		if(!toy.getStatus().equals(ToyStatusEnum.Claimed.numberValue())) {
			throw new RuntimeException("故事机已被认领!");
		}
		if(!toy.getOwnerId().equals(ownerId)){
			throw new RuntimeException("您不是故事机主人!");
		}
		toy.setToySN(toySN);
		toy.setToyName(toyName);
		toy.setKidName(kidName);
		toy.setKidGender(kidGender);
		toy.setKidAge(kidAge);
		toy.setKidBirth(kidBirth);

		return toyMapper.update(toy)? toy : null;	
	}
	public ToyDO toNameToy(long ownerId, String toySN, String toyName, Map<String, String> kidParams) {		
		ToyDO toy =  toyMapper.getItemByToySN(toySN);
		if(null ==toy) {
			throw new RuntimeException("故事机不存在!");
		}
		if(toy.getStatus().equals(ToyStatusEnum.Initial.numberValue())) {
			throw new RuntimeException("故事机已被认领!");
		}
		if(!toy.getOwnerId().equals(ownerId)){
			throw new RuntimeException("您不是故事机主人!");
		}
		toy.setToySN(toySN);
		toy.setToyName(toyName);
		toy.setKidName(kidParams.containsKey("kidName")? kidParams.get("kidName"):"");
		toy.setKidGender(kidParams.containsKey("kidGender")? Integer.valueOf(kidParams.get("kidGender")):-1);
		toy.setKidAge(kidParams.containsKey("kidAge")? Integer.valueOf(kidParams.get("kidAge")):-1);
		try {
			toy.setKidBirth(kidParams.containsKey("kidBirh")? DateTools.stringToDate(kidParams.get("kidBirth")):null);
		} catch (ParseException e) {
		}
		return toyMapper.update(toy)? toy : null;	
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
