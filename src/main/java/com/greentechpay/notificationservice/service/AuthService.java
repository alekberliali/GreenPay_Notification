package com.greentechpay.notificationservice.service;

import com.greentechpay.notificationservice.client.AuthClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthClient authClient;

    private List<String> getPermissions(String agentName, String agentPassword, String agentId, String accessToken,
                                        String authorization) {
        var result = authClient.getUserGrantedAuthority(agentName, agentPassword, agentId, accessToken, authorization);
        if (result.getData() != null) {
            return result.getData().getPermissions();
        } else {
            log.error("User is not granted");
            return new ArrayList<>();
        }
    }

    protected Boolean hasPermission(String agentName, String agentPassword, String agentId, String accessToken,
                                    String authorization) {
        var permissions = getPermissions(agentName, agentPassword, agentId, accessToken, authorization);
        return permissions.stream().anyMatch(permission -> permission.equals("Permission.Notification.SendAll"));
    }
}
