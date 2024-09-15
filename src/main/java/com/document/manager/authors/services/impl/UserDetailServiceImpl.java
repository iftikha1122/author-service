package com.document.manager.authors.services.impl;

import com.document.manager.authors.domain.UserDetailsImpl;
import com.document.manager.authors.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final AuthorRepository authorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userinfo = authorRepository.findActiveByUserName(username);
         if(userinfo.isEmpty()) throw new UsernameNotFoundException("User not found");
        return UserDetailsImpl.build(userinfo.get());
    }
}
