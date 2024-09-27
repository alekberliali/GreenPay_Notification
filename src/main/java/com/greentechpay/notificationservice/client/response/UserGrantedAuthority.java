package com.greentechpay.notificationservice.client.response;

import lombok.Data;

import java.util.List;

@Data
public class UserGrantedAuthority {
    private String userName;
    private List<String> roles;
    private List<String> permissions;
}
