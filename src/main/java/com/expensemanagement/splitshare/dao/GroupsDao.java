package com.expensemanagement.splitshare.dao;

import com.expensemanagement.splitshare.entity.GroupsEntity;
import com.expensemanagement.splitshare.entity.UsersEntity;
import com.expensemanagement.splitshare.exception.NotFoundException;
import com.expensemanagement.splitshare.repository.GroupsRepository;
import com.expensemanagement.splitshare.repository.UsersRepository;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GroupsDao {

    private final GroupsRepository groupsRepository;

    @Autowired
    public GroupsDao(GroupsRepository groupsRepository) {
        this.groupsRepository = groupsRepository;
    }

    public GroupsEntity getGroupByGroupId(Long groupId) {
        GroupsEntity group = groupsRepository.findByGroupId(groupId);
        if (Objects.isNull(group)) {
            log.error("No valid group is found for the groupId - {}", groupId);
            throw new NotFoundException("groupId", groupId.toString());
        }
        log.info("Found groupId = {} with the groupName = {}", group.getGroupId(), group.getGroupName());
        return group;
    }

    public boolean doesGroupIdExist(Long groupId) {
        GroupsEntity group = groupsRepository.findByGroupId(groupId);
        if (Objects.isNull(group)) {
            return false;
        }
        return true;
    }

    public GroupsEntity upsertGroupData(GroupsEntity group) {
        GroupsEntity groupDataFromDb = null;
        if (Objects.nonNull(group.getGroupId()) && doesGroupIdExist(group.getGroupId())) {
            groupDataFromDb = getGroupByGroupId(group.getGroupId());
        }
        if (Objects.nonNull(groupDataFromDb)) {
            if (Objects.isNull(group.getGroupName())) {
                group.setGroupName(groupDataFromDb.getGroupName());
            }
        }
        GroupsEntity savedResponse = groupsRepository.save(group);
        return savedResponse;
    }

    public GroupsEntity getGroupByGroupLink(String groupLink) {
        GroupsEntity group = groupsRepository.findByGroupLink(groupLink);
        if (Objects.isNull(group)) {
            log.error("No valid group is found for the groupLink - {}", groupLink);
            throw new NotFoundException("groupId", groupLink);
        }
        log.info("Found groupId = {} with the groupName = {}", group.getGroupId(), group.getGroupName());
        return group;
    }
}
