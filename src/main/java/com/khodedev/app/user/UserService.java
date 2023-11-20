package com.khodedev.app.user;

import com.khodedev.app.common.exceptions.BadRequestException;
import com.khodedev.app.common.exceptions.NotFoundException;
import com.khodedev.app.user.dto.CreateUserDto;
import com.khodedev.app.user.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUser(CreateUserDto createUserDto) {
        try {
            var user = User.builder()
                    .firstName(createUserDto.getFirstName())
                    .lastName(createUserDto.getLastName())
                    .build();

            userRepository.save(user);

        } catch (Exception e) {
            throw new BadRequestException("Could not create user");
        }
    }

    public User findOne(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public void update(Long id, CreateUserDto createUserDto) {
        try {
            var user = findOne(id);
            user.setFirstName(createUserDto.getFirstName());
            user.setLastName(createUserDto.getLastName());

            userRepository.save(user);
        } catch (Exception e) {
            throw new BadRequestException("Could not update user");
        }
    }

    public void delete(Long id) {
        try {
            var user = findOne(id);
            userRepository.delete(user);

        } catch (Exception e) {
            throw new BadRequestException("Could not delete user");
        }
    }
}
