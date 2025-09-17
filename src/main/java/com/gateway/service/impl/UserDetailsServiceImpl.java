package com.gateway.service.impl;

import com.gateway.entity.UserEntity;
import com.gateway.entity.permission.PermissionEntity;
import com.gateway.entity.permission.ServiceEntity;
import com.gateway.entity.permission.UserRoleEntity;
import com.gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findEnabledUserWithPermissionsByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<GrantedAuthority> authorities = user.getUserRoles().stream()
                .map(UserRoleEntity::getRole)
                .flatMap(role -> role.getRolePermissions().stream())
                .filter(rp -> rp.getPermission() != null && rp.getPermission().getServiceEntity().isActive())
                .map(rp -> {
                    PermissionEntity permission = rp.getPermission();
                    ServiceEntity service = permission.getServiceEntity();
                    return new SimpleGrantedAuthority(rp.getRole().getName() + ":" + service.getPathName() + "/" + permission.getPathPermission());
                })
                .collect(Collectors.toList());

        if (authorities.isEmpty()) {
            throw new UsernameNotFoundException("User has no permissions: " + username);
        }

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
