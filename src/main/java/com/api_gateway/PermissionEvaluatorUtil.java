package com.api_gateway;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class PermissionEvaluatorUtil {


    public static boolean hasPermission(Collection<? extends GrantedAuthority> userPermission, String[] args) {
        if (args.length >= 3) {
            String path = args[1] + "/" + args[2];
            return userPermission.stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(auth -> auth.split(":")[1].trim())
                    .anyMatch(auth -> {
                        String[] parts = auth.split("/");
                        String[] pathParts = path.split("/");

                        if (auth.endsWith("/**")) {
                            return pathParts.length >= 1 &&
                                    parts[0].equalsIgnoreCase(pathParts[0]);
                        }

                        if (parts.length >= 2 && pathParts.length >= 2) {
                            String normalizedAuth = parts[0] + "/" + parts[1];
                            String normalizedPath = pathParts[0] + "/" + pathParts[1];
                            return normalizedAuth.equalsIgnoreCase(normalizedPath);
                        }
                        return false;
                    });
        }
        return false;
    }


}

