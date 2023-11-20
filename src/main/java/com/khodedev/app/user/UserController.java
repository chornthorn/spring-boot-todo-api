package com.khodedev.app.user;

import com.khodedev.app.common.annotations.KeycloakAuthorz;
import com.khodedev.app.common.types.Scope;
import com.khodedev.app.user.dto.CreateUserDto;
import com.khodedev.app.user.dto.UpdateUserDto;
import com.khodedev.app.user.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @KeycloakAuthorz(resource = "users",scope = Scope.READ)
    @GetMapping
    public Iterable<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findOne(@PathVariable Long id) {
        return userService.findOne(id);
    }

    @PostMapping
    public void createUser(@RequestBody CreateUserDto createUserDto) {
        userService.createUser(createUserDto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody UpdateUserDto updateUserDto) {
        userService.update(id, updateUserDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
