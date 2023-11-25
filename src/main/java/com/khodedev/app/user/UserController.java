package com.khodedev.app.user;

import com.khodedev.app.common.annotations.PreAuthz;
import com.khodedev.app.common.types.Scope;
import com.khodedev.app.user.dto.CreateUserDto;
import com.khodedev.app.user.dto.UpdateUserDto;
import com.khodedev.app.user.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/users")
@AllArgsConstructor
@PreAuthz(resource = "users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthz(scope = Scope.READ)
    public Iterable<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthz(scope = Scope.READBY)
    public User findOne(@PathVariable Long id) {
        return userService.findOne(id);
    }

    @PostMapping
    @PreAuthz(scope = Scope.CREATE)
    public void createUser(@RequestBody CreateUserDto createUserDto) {
        userService.createUser(createUserDto);
    }

    @PutMapping("/{id}")
    @PreAuthz(scope = Scope.UPDATE)
    public void update(@PathVariable Long id, @RequestBody UpdateUserDto updateUserDto) {
        userService.update(id, updateUserDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthz(scope = Scope.DELETE)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
