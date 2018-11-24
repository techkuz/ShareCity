package com.hack.junction.sharecity.controller;

import com.hack.junction.sharecity.exception.AppException;
import com.hack.junction.sharecity.model.AppUser;
import com.hack.junction.sharecity.model.Role;
import com.hack.junction.sharecity.model.RoleName;
import com.hack.junction.sharecity.model.User;
import com.hack.junction.sharecity.payload.ApiResponse;
import com.hack.junction.sharecity.payload.JwtAuthenticationResponse;
import com.hack.junction.sharecity.payload.LoginRequest;
import com.hack.junction.sharecity.payload.SignUpRequest;
import com.hack.junction.sharecity.repository.AppUserRepository;
import com.hack.junction.sharecity.repository.RoleRepository;
import com.hack.junction.sharecity.repository.UserRepository;
import com.hack.junction.sharecity.security.JwtTokenProvider;
import com.hack.junction.sharecity.util.BytomUtil;
import io.bytom.exception.BytomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(appUserRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(appUserRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        Map<String, String> bytomInfo = new HashMap<>();
        try {
            bytomInfo = BytomUtil.createNewKeyAndUser(signUpRequest.getName(), signUpRequest.getPassword());
        } catch (BytomException ex) {
            ex.printStackTrace();
        }

        AppUser user = new AppUser(bytomInfo.get("id"), signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword(), 0.0);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(signUpRequest.isBusiness() ? RoleName.ROLE_CORPORATE : RoleName.ROLE_STARTUP)
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));

        AppUser result = appUserRepository.save(user);

        String apiRoleRoute = signUpRequest.isBusiness() ? "corporate" : "startup";

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/" + apiRoleRoute + "/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

}
