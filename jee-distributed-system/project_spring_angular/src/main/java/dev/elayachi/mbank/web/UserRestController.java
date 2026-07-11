package dev.elayachi.mbank.web;

import dev.elayachi.mbank.dtos.Requests.ChangePasswordRequest;
import dev.elayachi.mbank.exceptions.InvalidPasswordException;
import dev.elayachi.mbank.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserRestController {

    private final UserService userService;

    @PostMapping("/changePassword")
    public void changePassword(@RequestBody ChangePasswordRequest request, Principal principal)
            throws InvalidPasswordException {
        userService.changePassword(principal.getName(), request.oldPassword(), request.newPassword());
    }
}
