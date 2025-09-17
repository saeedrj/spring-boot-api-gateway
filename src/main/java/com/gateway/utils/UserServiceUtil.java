package com.gateway.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceUtil {

    public static Map<String, List<String>> createRolePermissionMap(List<String> flatPermissions) {
        Map<String, List<String>> rolePermissionsMap = new HashMap<>();

        for (String permissionString : flatPermissions) {
            String[] parts = permissionString.split(":");
            if (parts.length == 2) {
                String role = parts[0].trim();
                String permission = parts[1].trim();
                rolePermissionsMap.putIfAbsent(role, new ArrayList<>());
                rolePermissionsMap.get(role).add(permission);
            }
        }
        return rolePermissionsMap;
    }


}
