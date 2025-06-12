package com.expensemanagement.splitshare.controller;

import com.expensemanagement.splitshare.dao.TransactionsDao;
import com.expensemanagement.splitshare.dto.AddUserRequest;
import com.expensemanagement.splitshare.dto.AddUserResponse;
import com.expensemanagement.splitshare.dto.CreateGroupRequest;
import com.expensemanagement.splitshare.dto.CreateGroupResponse;
import com.expensemanagement.splitshare.service.GroupService;
import com.expensemanagement.splitshare.validate.Validator;
import jakarta.transaction.Transactional;
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
    private final Validator addUsersValidator;
    private final TransactionsDao transactionsDao;

    @Autowired
    public GroupController(GroupService groupService, @Qualifier("createGroupValidator") Validator createGroupValidator,
                           TransactionsDao transactionsDao, @Qualifier("addUsersValidator") Validator addUsersValidator) {
        this.createGroupValidator = createGroupValidator;
        this.addUsersValidator = addUsersValidator;
        this.groupService = groupService;
        this.transactionsDao = transactionsDao;
    }

    @PostMapping("/create-group")
    @Transactional
    public ResponseEntity<?> createNewGroup(@RequestBody CreateGroupRequest createGroupRequest, @RequestHeader Map<String, String> requestHeaders) {
        createGroupValidator.validate(createGroupRequest);
        CreateGroupResponse createGroupResponse = groupService.createGroup(createGroupRequest);
        transactionsDao.populateTransactionHistory(createGroupResponse);
        return new ResponseEntity<>(createGroupResponse, HttpStatus.OK);
    }

    @PostMapping("/add-users")
    @Transactional
    public ResponseEntity<?> addUsers(@RequestBody AddUserRequest addUserRequest) {
        addUsersValidator.validate(addUserRequest);
        AddUserResponse addUserResponse = groupService.addUsers(addUserRequest);
        transactionsDao.populateTransactionHistory(addUserResponse);
        return new ResponseEntity<>(addUserResponse, HttpStatus.OK);
    }
}
