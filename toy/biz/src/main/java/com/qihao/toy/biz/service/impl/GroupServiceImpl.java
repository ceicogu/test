package com.qihao.toy.biz.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qihao.toy.biz.service.GroupService;
import com.qihao.toy.dal.domain.MyGroupDO;
import com.qihao.toy.dal.domain.MyGroupMemberDO;
import com.qihao.toy.dal.enums.GroupTypeEnum;
import com.qihao.toy.dal.persistent.MyGroupMapper;
import com.qihao.toy.dal.persistent.MyGroupMemberMapper;

/**
 * 群管理
 * @author luqiao
 *
 */
@Service
public class GroupServiceImpl implements GroupService {
	@Autowired
	private MyGroupMapper myGroupMapper;
	@Autowired
	private MyGroupMemberMapper myGroupMemberMapper;
	
	public Long createGroup(long userId,String groupName, GroupTypeEnum groupType) {
		Preconditions.checkArgument(StringUtils.isNotBlank(groupName),"群名称不能为空");
		MyGroupDO group = new MyGroupDO();
		group.setMyId(userId);
		group.setGroupName(groupName);
		group.setGroupType(groupType.numberValue());
		return myGroupMapper.insert(group) > 0? group.getId() : null;
	}

	public boolean update(MyGroupDO group) {
		Preconditions.checkArgument(null !=group.getId(),"请指定群ID");		
		return myGroupMapper.update(group);
	}

	public boolean delete(long groupId) {
		return myGroupMapper.deleteById(groupId);
	}

	public MyGroupDO getItemById(long groupId) {
		return myGroupMapper.getById(groupId);
	}

	public boolean isGroupMember(long groupId, long userId) {
		MyGroupMemberDO member = new MyGroupMemberDO();
		member.setGroupId(groupId);
		member.setMemberId(userId);
		List<MyGroupMemberDO> resp = myGroupMemberMapper.getAll(member);
		return !CollectionUtils.isEmpty(resp); 
	}
	public Long insertGroupMember(long groupId, long memberId, String memberName) {
		boolean bExist = this.isGroupMember(groupId, memberId);
		Preconditions.checkArgument(false ==bExist,"该用户已是群成员");
		
		MyGroupMemberDO myGroupMember = new MyGroupMemberDO();
		myGroupMember.setGroupId(groupId);
		myGroupMember.setMemberId(memberId);
		myGroupMember.setMemberName(memberName);
		return myGroupMemberMapper.insert(myGroupMember)>0? myGroupMember.getId() : null;
	}
	public List<MyGroupDO> getMyCreatedGroups(long myId, GroupTypeEnum groupType) {
		MyGroupDO myGroup = new MyGroupDO();
		myGroup.setMyId(myId);
		if(null != groupType) {
			myGroup.setGroupType(groupType.numberValue());
		}
		return myGroupMapper.getAll(myGroup);
	}

	public List<Long> getMyJoinedGroups(long myId) {
		return this.getMyJoinedGroups(myId,null);
	}
	public List<Long> getMyJoinedGroups(long myId,Integer groupType) {
		MyGroupMemberDO myGroupMember = new MyGroupMemberDO();
		myGroupMember.setMemberId(myId);
		myGroupMember.setGroupType(groupType);
		List<MyGroupMemberDO> resp = myGroupMemberMapper.getAll(myGroupMember);
		if(CollectionUtils.isEmpty(resp)){
			return null;
		}
		List<Long> groupIds = Lists.newArrayList();
		for(MyGroupMemberDO member : resp) {
			if(null == groupType) {
				groupIds.add(member.getGroupId());
			}
			else{
				if(groupType.equals(member.getGroupType())){
					groupIds.add(member.getGroupId());
				}
			}
			
		}
		return groupIds;
	}

	public List<MyGroupMemberDO> getGroupMembersByGroupId(long groupId) {
		MyGroupMemberDO myGroupMember = new MyGroupMemberDO();
		myGroupMember.setGroupId(groupId);
		return myGroupMemberMapper.getAll(myGroupMember);
	}

	public List<Long> getUserIdsByGroupId(long groupId) {
		MyGroupMemberDO myGroupMember = new MyGroupMemberDO();
		myGroupMember.setGroupId(groupId);
		List<MyGroupMemberDO> resp = myGroupMemberMapper.getAll(myGroupMember);
		List<Long> userIds = Lists.newArrayList();
		for(MyGroupMemberDO member : resp) {
			userIds.add(member.getMemberId());
		}
		return userIds;
	}

	public List<MyGroupDO> getItemByIds(List<Long> groupIds) {
		MyGroupDO myGroup = new MyGroupDO();
		myGroup.setGroupIds(groupIds);
		return myGroupMapper.getAll(myGroup);	
	}
}
