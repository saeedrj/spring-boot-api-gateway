package com.gateway.service.impl;

import com.gateway.dto.*;
import com.gateway.dto.enumDto.EventType;
import com.gateway.dto.eventDto.UserEventDto;
import com.gateway.dto.userRolleDto.LoginRequestDto;
import com.gateway.dto.userRolleDto.LoginResponseDto;
import com.gateway.dto.userDto.CreateAccountRequestDto;
import com.gateway.dto.userDto.CreateAccountResponseDto;
import com.gateway.dto.userDto.UserDto;
import com.gateway.entity.UserEntity;
import com.gateway.entity.UserSecurityEntity;
import com.gateway.entity.profile.ConfirmationEntity;
import com.gateway.entity.profile.UserProfileEntity;
import com.gateway.excepotion.RecordException;
import com.gateway.repository.ConfirmationRepository;
import com.gateway.repository.UserProfileRepository;
import com.gateway.repository.UserRepository;
import com.gateway.repository.UserSecurityRepository;
import com.gateway.service.UserService;
import com.gateway.utils.JwtUtil;
import com.gateway.utils.UserServiceUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.gateway.dto.enumDto.EnumResult.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSecurityRepository userSecurityRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;
    private final ApplicationEventPublisher publisher;
    private final ConfirmationRepository confirmationRepository;


    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) {
        String redisKey = "user:" + loginRequestDto.username();
        String ipRedisKey = "ip:" + request.getRemoteAddr();
        Integer failedAttempts;
        Integer ipFailedAttempts;
        try {
            failedAttempts = redisService.getFailedAttempts(redisKey);
            ipFailedAttempts = redisService.getFailedAttempts(ipRedisKey);
        } catch (Exception e) {
            log.error("exception Redis server error :", e);
            throw new RecordException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            if (failedAttempts >= 5 || ipFailedAttempts >= 5) {
                throw new RecordException(IP_LOCK, HttpStatus.LOCKED);
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password()));

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                Map<String, Object> claims = new HashMap<>();

                List<String> permissions = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
                UserEntity user = userRepository.findByUsername(loginRequestDto.username()).get();


                claims.put("permissions", permissions);
                String uuid = UUID.randomUUID().toString();

                claims.put("jti", uuid);
                redisService.setIdLogin(uuid, user.getId().toString());
                String token = "Bearer " + jwtUtil.generateToken(userDetails, claims);

                UserDto userDto = new UserDto(user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getLastLoginDate(), UserServiceUtil.createRolePermissionMap(permissions));
                updateLastLoginInfo(user.getId(), request.getRemoteAddr());
                redisService.resetFailedAttempts(redisKey);
                return new LoginResponseDto(token, userDto);
            } else {
                redisService.incrementFailedAttempts(redisKey);
                throw new RecordException(AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
            }
        } catch (BadCredentialsException e) {
            redisService.incrementFailedAttempts(redisKey);
            redisService.incrementFailedAttempts(ipRedisKey);
            throw new RecordException(BAD_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        } catch (DisabledException e) {
            redisService.incrementFailedAttempts(redisKey);
            throw new RecordException(ACCOUNT_DISABLED, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            if (e instanceof RecordException) {
                throw e;
            }
            redisService.incrementFailedAttempts(redisKey);
            throw new RecordException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CreateAccountResponseDto createAccount(CreateAccountRequestDto requestDto) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String createdByUsername = userDetails.getUsername();
            UserEntity createdByUser = userRepository.findByUsername(createdByUsername).orElse(null);

            if (createdByUser == null)
                throw new RecordException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);

            if (userRepository.existsByEmail(requestDto.email()))
                throw new RecordException(EMAIL_IS_USED, HttpStatus.CONFLICT);

            if (userRepository.existsByUsername(requestDto.username()))
                throw new RecordException(USER_IS_USED, HttpStatus.CONFLICT);

            if (userRepository.existsByPhoneNumber(requestDto.phoneNumber()))
                throw new RecordException(PHONE_IS_USED, HttpStatus.CONFLICT);

            UserEntity newUser = new UserEntity();
            newUser.setUsername(requestDto.username());
            newUser.setPassword(passwordEncoder.encode(requestDto.password()));
            newUser.setEmail(requestDto.email());
            newUser.setFirstName(requestDto.firstName());
            newUser.setLastName(requestDto.lastName());
            newUser.setManager(createdByUser);
            newUser.setEnabled(true);
            newUser.setCreatedDate(LocalDateTime.now());
            newUser.setLastPasswordResetDate(LocalDateTime.now());
            newUser.setLastLoginDate(LocalDateTime.now());

            newUser = userRepository.saveAndFlush(newUser);

            UserProfileEntity userProfileEntity = new UserProfileEntity();
            userProfileEntity.setUserId(newUser.getId());

            UserSecurityEntity userSecurity = new UserSecurityEntity();
            userSecurity.setUserId(newUser.getId());
            userSecurity.setSalt("salt");


            userProfileRepository.save(userProfileEntity);
            userSecurityRepository.save(userSecurity);

            return new CreateAccountResponseDto(new UserDto(newUser.getUsername(), newUser.getEmail(), newUser.getFirstName(), newUser.getLastName(), newUser.getLastLoginDate(), null));
        } catch (Exception e) {
            if (e instanceof RecordException) throw e;
            log.error("Error creating account: {}", e.getMessage());
            throw new RecordException(ERROR_CREATE_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateLastLoginInfo(UUID userId, String ipAddress) {
        userRepository.updateLastLoginInfo(userId, LocalDateTime.now(), ipAddress);
    }

    @Override
    public ResponseDto updatePassword(String newPassword, String currentPassword) {

        if (newPassword.equals(currentPassword))
            throw new RecordException(BAD_REQUEST, HttpStatus.BAD_REQUEST);

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String createdByUsername = userDetails.getUsername();
        UserEntity createdByUser = userRepository.findByUsername(createdByUsername).stream().findFirst().orElse(null);

        if (createdByUser == null)
            throw new RecordException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);

        log.info("Starting password update for userId: {}", createdByUser.getUsername());

        if (!passwordEncoder.matches(currentPassword, createdByUser.getPassword())) {
            log.warn("Password mismatch for userId: {}", createdByUser.getUsername());
            throw new RecordException(PASSWORD_IS_NOT_MACH, HttpStatus.BAD_REQUEST);
        }
        log.info("Password successfully updated for userId: {}", createdByUser.getUsername());
        try {
            userRepository.updatePassword(createdByUser.getId(), LocalDateTime.now(), passwordEncoder.encode(newPassword));
        } catch (Exception e) {
            if (e instanceof RecordException) throw e;
            log.error("Error updating password for userId: {}", createdByUser.getUsername());
            throw new RecordException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseDto(PASSWORD_CHANGED);
    }

    @Override
    public ResponseDto forgotPassword(String contact) {
        try {
            UserEntity user = userRepository.findByEmailOrPhoneNumber(contact, contact).stream().findFirst().orElse(null);
            if (user == null)
                throw new RecordException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);

            if (redisService.forgetPasswordAttempts(user.getId().toString()))
                throw new RecordException(LOCK_ACCOUNT, HttpStatus.LOCKED);

            ConfirmationEntity confirmationEntity = new ConfirmationEntity(user);
            confirmationRepository.save(confirmationEntity);
            publisher.publishEvent(new UserEventDto(user, EventType.REGISTRATION, Map.of("key", confirmationEntity.getKey())));

            log.warn(confirmationEntity.getKey()); //TODO delete this line in real APPs
            return new ResponseDto(OK);
        } catch (Exception e) {
            if (e instanceof RecordException) throw e;
            log.error("Error forgot password for userId: {}", contact);
            throw new RecordException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseDto resetPassword(String token, String newPassword) {
        try {
            ConfirmationEntity confirmationEntity = confirmationRepository.findByKey(token).stream().findFirst().orElse(null);
            if (confirmationEntity == null) throw new RecordException(TOKEN_NOT_FOUNT, HttpStatus.NOT_FOUND);
            confirmationEntity.getUserEntity().setPassword(passwordEncoder.encode(newPassword));
            confirmationEntity.getUserEntity().setLastPasswordResetDate(LocalDateTime.now());
            userRepository.save(confirmationEntity.getUserEntity());
            confirmationRepository.delete(confirmationEntity);
            return new ResponseDto(CHANGED_PASSWORD);
        } catch (Exception e) {
            if (e instanceof RecordException) throw e;
            log.error("Error resetPassword for userId: {}", token);
            throw new RecordException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseDto logout( HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");
            var claims = jwtUtil.extractAllClaims(header.substring(7));
            if (redisService.getIdLogin(claims.get("jti").toString()) != null) {
                redisService.deleteIdLogin(claims.get("jti").toString());
                return new ResponseDto(OK);
            } else throw new RecordException(NOT_VALID_TOKEN, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage(), e);
            throw new RecordException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



}
