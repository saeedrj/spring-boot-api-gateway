package com.api_gateway.service.impl;

import com.api_gateway.entity.UserEntity;
import com.api_gateway.entity.permission.PermissionEntity;
import com.api_gateway.entity.permission.ServiceEntity;
import com.api_gateway.entity.permission.UserRoleEntity;
import com.api_gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findEnabledUserWithPermissionsByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<GrantedAuthority> authorities = user.getUserRoles().stream()
                .map(UserRoleEntity::getRole)
                .flatMap(role -> role.getRolePermissions().stream())
                .filter(rp -> rp.getPermission() != null && rp.getPermission().getServiceEntity().isActive())
                .map(rp -> {
                    PermissionEntity permission = rp.getPermission();
                    ServiceEntity service = permission.getServiceEntity();
                    return new SimpleGrantedAuthority(rp.getRole().getName()+":"+service.getPathName() + "/" + permission.getPathPermission());
                })
                .collect(Collectors.toSet());

        if (authorities.isEmpty()) {
            throw new UsernameNotFoundException("User has no permissions: " + username);
        }

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
