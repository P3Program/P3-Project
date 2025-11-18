package com.p3program.laugin_project_planner.repositories;

import com.p3program.laugin_project_planner.users.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * This repo allows spring data jpa to find the username
 * from the generated table. This query is done in the background
 * since we are using a specific naming convention/syntax
 * of method findByUsername.
 * It also extends existsByUsername, which queries the database for
 * duplicate usernames, used in UserController
 */

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByUsername(String username);

    Optional<AppUser> findByUsername(String username);

}