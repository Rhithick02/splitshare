package com.expensemanagement.splitshare.controller;

import com.expensemanagement.splitshare.dto.CreateGroupRequest;
import com.expensemanagement.splitshare.dto.CreateGroupResponse;
import com.expensemanagement.splitshare.service.GroupService;
import com.expensemanagement.splitshare.validate.Validator;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/groups")
public class GroupController {

    private final GroupService groupService;

    private final Validator createGroupValidator;

    @Autowired
    public GroupController(GroupService groupService, @Qualifier("createGroupValidator") Validator createGroupValidator) {
        this.createGroupValidator = createGroupValidator;
        this.groupService = groupService;
    }

    @PostMapping("/create-group")
    public ResponseEntity<?> createNewGroup(@RequestBody CreateGroupRequest createGroupRequest, @RequestHeader Map<String, String> requestHeaders) {
        createGroupValidator.validate(createGroupRequest);
        CreateGroupResponse createGroupResponse = groupService.createGroup(createGroupRequest);
        return new ResponseEntity<>(createGroupResponse, HttpStatus.OK);
    }
}
