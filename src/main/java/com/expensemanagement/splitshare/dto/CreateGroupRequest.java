package com.expensemanagement.splitshare.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateGroupRequest {
    private String groupName;
    private Long userId;
    private MultipartFile groupImage;
}
