package com.hack.junction.sharecity.controller;

import com.hack.junction.sharecity.exception.ResourceNotFoundException;
import com.hack.junction.sharecity.model.User;
import com.hack.junction.sharecity.model.RoleName;
import com.hack.junction.sharecity.payload.*;
import com.hack.junction.sharecity.repository.PollRepository;
import com.hack.junction.sharecity.repository.UserRepository;
import com.hack.junction.sharecity.repository.VoteRepository;
import com.hack.junction.sharecity.security.CurrentUser;
import com.hack.junction.sharecity.security.UserPrincipal;
import com.hack.junction.sharecity.service.PollService;
import com.hack.junction.sharecity.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BusinessController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollService pollService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/business/me")
    @PreAuthorize("hasRole('BUSINESS')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName(), RoleName.ROLE_CORPORATE.name());
        return userSummary;
    }

    @GetMapping("/business/checkBusinessNameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "businessName") String businessName) {
        Boolean isAvailable = !userRepository.existsByUsername(businessName);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/business/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/businesses/{businessName}")
    public BusinessProfile getUserProfile(@PathVariable(value = "businessName") String businessName) {
        User user = userRepository.findByUsername(businessName)
                .orElseThrow(() -> new ResourceNotFoundException("Business", "businessName", businessName));

        long pollCount = pollRepository.countByCreatedBy(user.getId());

        BusinessProfile businessProfile = new BusinessProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt(), pollCount, RoleName.ROLE_CORPORATE.name());

        return businessProfile;
    }

    @GetMapping("/businesses/{businessName}/polls")
    public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable(value = "businessName") String businessName,
                                                         @CurrentUser UserPrincipal currentUser,
                                                         @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                         @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return pollService.getPollsCreatedBy(businessName, currentUser, page, size);
    }
}
