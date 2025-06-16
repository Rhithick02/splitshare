package com.expensemanagement.splitshare.service;

import com.expensemanagement.splitshare.dao.GroupsDao;
import com.expensemanagement.splitshare.dao.UsersDao;
import com.expensemanagement.splitshare.dto.AddUserRequest;
import com.expensemanagement.splitshare.dto.AddUserResponse;
import com.expensemanagement.splitshare.dto.CreateGroupRequest;
import com.expensemanagement.splitshare.dto.CreateGroupResponse;
import com.expensemanagement.splitshare.entity.GroupsEntity;
import com.expensemanagement.splitshare.entity.UsersEntity;
import com.expensemanagement.splitshare.exception.InternalServerException;
import com.expensemanagement.splitshare.integration.ImageKitIntegration;
import io.imagekit.sdk.models.results.Result;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GroupService {

    private final GroupsDao groupsDao;
    private final UsersDao usersDao;
    private final ImageKitIntegration imageKitIntegration;

    private final MessageDigest digest;

    @Autowired
    public GroupService(GroupsDao groupsDao, UsersDao usersDao, ImageKitIntegration imageKitIntegration) throws NoSuchAlgorithmException {
        this.groupsDao = groupsDao;
        this.usersDao = usersDao;
        this.imageKitIntegration = imageKitIntegration;
        this.digest = MessageDigest.getInstance("SHA-256");
    }

    public CreateGroupResponse createGroup(CreateGroupRequest createGroupRequest) {
        try {
            // Get the userEntity object
            UsersEntity user = usersDao.getUserByUserId(createGroupRequest.getUserId());

            // Create a new group and to user
            GroupsEntity groupsEntity = new GroupsEntity();
            groupsEntity.setGroupName(createGroupRequest.getGroupName());
            groupsEntity.setGroupLink(generateToken(createGroupRequest.getGroupName()));
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
            createGroupResponse.setGroupLink(savedResponse.getGroupLink());
            return createGroupResponse;
        } catch (SQLException ex) {
            throw new InternalServerException(ex.getMessage());
        }
    }

    public AddUserResponse addUsers(AddUserRequest addUserRequest) {
        try {
            AddUserResponse addUserResponse = new AddUserResponse();
            GroupsEntity group;
            if (Objects.nonNull(addUserRequest.getGroupId())) {
                Long groupId = addUserRequest.getGroupId();
                group = groupsDao.getGroupByGroupId(groupId);
            } else {
                group = groupsDao.getGroupByGroupLink(addUserRequest.getGroupLink());
            }

            for (String phoneNumber : addUserRequest.getPhoneNumbers()) {
                UsersEntity user = usersDao.getUserByPhoneNumber(phoneNumber);
                group.getUsers().add(user);
                user.getGroups().add(group);
                addUserResponse.getUserIds().add(user.getUserId());
            }
            groupsDao.upsertGroupData(group);
            addUserResponse.setGroupId(group.getGroupId());

            return addUserResponse;
        } catch (SQLException ex) {
            throw new InternalServerException(ex.getMessage());
        }
    }

    private String generateToken(String groupName) {
        String uuid = UUID.randomUUID().toString();
        String randomString = uuid + groupName + System.currentTimeMillis();
        byte[] hash = digest.digest(randomString.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash);
        return encoded.substring(0, 12);
    }
}
