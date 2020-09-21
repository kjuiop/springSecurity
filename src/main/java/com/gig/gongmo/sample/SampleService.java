package com.gig.gongmo.sample;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SampleService {

    public void dashboard() {

        // SecurityContextHolder 를 통해서 로그인 한 객체를 어디서든지 사용할 수 있음.
        // UsernamePasswordAuthenticationToken 타입
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // principal 은 로그인한 객체로 User 타입 ( UserDetails 타입 안의 User )
        Object principal = authentication.getPrincipal();

        // 로그인한 principal 의 권한 ( Roles )
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 인증을 한 후에는 가지고 있지 않음
        Object credentials = authentication.getCredentials();

        // 인증된 사용자인지 여부
        boolean authenticated = authentication.isAuthenticated();
    }

}
