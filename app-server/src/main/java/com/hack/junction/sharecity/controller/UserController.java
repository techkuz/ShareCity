package com.hack.junction.sharecity.controller;

import com.hack.junction.sharecity.exception.ResourceNotFoundException;
import com.hack.junction.sharecity.model.AppUser;
import com.hack.junction.sharecity.model.User;
import com.hack.junction.sharecity.model.RoleName;
import com.hack.junction.sharecity.payload.*;
import com.hack.junction.sharecity.payload.test.AppUserProfile;
import com.hack.junction.sharecity.repository.AppUserRepository;
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
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PollService pollService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('ROLE_CORPORATE') or hasRole('ROLE_STARTUP')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName(),
                currentUser.getRole().getName().name());
        return userSummary;
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !appUserRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !appUserRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/users/{username}")
    public AppUserProfile getUserProfile(@PathVariable(value = "username") String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        long voteCount = voteRepository.countByUserId(user.getId());

        AppUserProfile userProfile = new AppUserProfile(user.getId(), user.getBytomId(), user.getUsername(), user.getName(),
                user.getBalance(), user.getShortDescription(), user.getDescription(), user.getFounded(),
                user.getWebsite(), user.getCity(), user.getCountry(), user.getRoles().iterator().next().getName().name());

        return userProfile;
    }

    @GetMapping("/users/{username}/votes")
    public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable(value = "username") String username,
                                                       @CurrentUser UserPrincipal currentUser,
                                                       @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                       @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return pollService.getPollsVotedBy(username, currentUser, page, size);
    }
}
