package com.expensemanagement.splitshare.service;

import com.expensemanagement.splitshare.dao.GroupsDao;
import com.expensemanagement.splitshare.dao.UsersDao;
import com.expensemanagement.splitshare.dto.CreateGroupRequest;
import com.expensemanagement.splitshare.dto.CreateGroupResponse;
import com.expensemanagement.splitshare.entity.GroupsEntity;
import com.expensemanagement.splitshare.entity.UsersEntity;
import com.expensemanagement.splitshare.integration.ImageKitIntegration;
import io.imagekit.sdk.models.results.Result;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GroupService {

    private final GroupsDao groupsDao;
    private final UsersDao usersDao;
    private final ImageKitIntegration imageKitIntegration;

    @Autowired
    public GroupService(GroupsDao groupsDao, UsersDao usersDao, ImageKitIntegration imageKitIntegration) {
        this.groupsDao = groupsDao;
        this.usersDao = usersDao;
        this.imageKitIntegration = imageKitIntegration;
    }

    public CreateGroupResponse createGroup(CreateGroupRequest createGroupRequest) {
        // Get the userEntity object
        UsersEntity user = usersDao.getUserByUserId(createGroupRequest.getUserId());

        // Create a new group and to user
        GroupsEntity groupsEntity = new GroupsEntity();
        groupsEntity.setGroupName(createGroupRequest.getGroupName());
        groupsEntity.getUsers().add(user);
        user.getGroups().add(groupsEntity);
        GroupsEntity savedResponse = groupsDao.upsertGroupData(groupsEntity);

        // Upload image to imageKit if present
        if (Objects.nonNull(createGroupRequest.getGroupImage())) {
            Result result = imageKitIntegration.uploadImage(createGroupRequest.getGroupImage(), savedResponse.getGroupId());
        }

        // Return response
        CreateGroupResponse createGroupResponse = new CreateGroupResponse();
        createGroupResponse.setGroupId(savedResponse.getGroupId());
        createGroupResponse.setGroupName(savedResponse.getGroupName());
        createGroupResponse.setUserId(user.getUserId());
        return createGroupResponse;
    }
}
