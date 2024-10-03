package com.greentechpay.notificationservice.client;

import com.greentechpay.notificationservice.client.response.BaseResponse;
import com.greentechpay.notificationservice.client.response.UserGrantedAuthority;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.greentechpay.notificationservice.utils.HeaderKey.*;

//TODO move url to app secrets , auth ms
@FeignClient(value = "auth", url = "${app.feign.auth.url}")
public interface AuthClient {

    @GetMapping("/BoardOfDirectorAuth/GetRolesAndPermissionsById")
    BaseResponse<UserGrantedAuthority>
    getUserGrantedAuthority(@RequestHeader(value = AGENT_NAME) String agentName,
                            @RequestHeader(value = AGENT_PASSWORD) String agentPassword,
                            @RequestHeader(value = AGENT_ID) String agentId,
                            @RequestHeader(value = ACCESS_TOKEN) String accessToken,
                            @RequestHeader(value = AUTHORIZATION) String authorization);
}
