package com.keeponme.web;

import com.keeponme.user.User;
import com.keeponme.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser != null) {
            model.addAttribute("username", oidcUser.getPreferredUsername());
            model.addAttribute("authenticated", true);
        } else {
            model.addAttribute("authenticated", false);
        }
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser != null) {
            // Sync user with database
            User user = userService.syncUserFromOidc(oidcUser);
            
            model.addAttribute("user", user);
            model.addAttribute("username", oidcUser.getPreferredUsername());
            model.addAttribute("email", oidcUser.getEmail());
            model.addAttribute("firstName", oidcUser.getGivenName());
            model.addAttribute("lastName", oidcUser.getFamilyName());
        }
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser != null) {
            User user = userService.syncUserFromOidc(oidcUser);
            model.addAttribute("user", user);
        }
        return "profile";
    }
}
