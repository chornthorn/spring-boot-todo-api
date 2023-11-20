package com.khodedev.app.user;

import com.khodedev.app.user.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Long> {
}
