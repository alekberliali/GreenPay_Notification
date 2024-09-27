package com.greentechpay.notificationservice.service;

import com.greentechpay.notificationservice.client.AuthClient;
import com.greentechpay.notificationservice.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthClient authClient;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private List<String> getPermissions(String agentName, String agentPassword, String agentId, String accessToken,
                                  String authorization) {
        var result = authClient.getUserGrantedAuthority(agentName, agentPassword, agentId, accessToken, authorization);
        if (result.getData() == null) {
            logger.error("User roles not found");
            throw new UserNotFoundException("User roles not found");
        }

        return result.getData().getPermissions();
    }

    protected Boolean hasPermission(String agentName, String agentPassword, String agentId, String accessToken,
                              String authorization) {
        var permissions = getPermissions(agentName, agentPassword, agentId, accessToken, authorization);
        return permissions.stream().anyMatch(permission -> permission.equals("Permission.Notification.SendAll"));
    }
}
