package com.gateway.service;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    List<String> getUserPermissions(UUID userId, String requestPath);

}
