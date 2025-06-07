package com.expensemanagement.splitshare.model;

import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class JwtBody {
    public String userId;
    public String phoneNumber;
    public Date expiresAt;
}
