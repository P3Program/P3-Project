package com.p3program.laugin_project_planner.services;

import com.p3program.laugin_project_planner.dto.SecurityUser;
import com.p3program.laugin_project_planner.repositories.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository repository;

    public AppUserDetailsService(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}


/*
   This is some debugging code, which might be relevant when more users are implemented. Keep for now
   -Peter
 */


/* @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return repository.findByUsername(username)
               .map(user -> {
                   System.out.println("DEBUG: Successfully loaded user from DB -> " + user.getUsername());
                   return new SecurityUser(user);
               })
               .orElseThrow(() -> {
                   System.out.println("DEBUG: User not found -> " + username);
                   return new UsernameNotFoundException("User not found");
               });
   }
} */