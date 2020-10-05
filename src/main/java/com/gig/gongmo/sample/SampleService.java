package com.gig.gongmo.sample;

import com.gig.gongmo.account.Account;
import com.gig.gongmo.account.AccountContext;
import com.gig.gongmo.common.SecurityLogger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

@Service
public class SampleService {


    @Secured("ROLE_USER")
//    @RolesAllowed("ROLE_USER")
//    @PreAuthorize("hasRole('USER')")
//    @PostAuthorize("hasRole('USER')")
    public void dashboard() {

        // SecurityContextHolder 를 통해서 로그인 한 객체를 어디서든지 사용할 수 있음.
        // UsernamePasswordAuthenticationToken 타입
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // principal 은 로그인한 객체로 User 타입 ( UserDetails 타입 안의 User )
        Object principal = authentication.getPrincipal();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        System.out.println("===============================");
        System.out.println(authentication);
        System.out.println(userDetails.getUsername());

        // 로그인한 principal 의 권한 ( Roles )
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 인증을 한 후에는 가지고 있지 않음
        Object credentials = authentication.getCredentials();

        // 인증된 사용자인지 여부
        boolean authenticated = authentication.isAuthenticated();
    }


    public void threadLocal() {
        Account account = AccountContext.getAccount();
        System.out.println("============");
        System.out.println(account.getUsername());
    }

    @Async
    public void asyncService() {
        SecurityLogger.log("Async Service");
        System.out.println("Async service is called");
    }

    @PreAuthorize("#username == authentication.principal.username")
    public void securedTest(String username) {
        System.out.println("============");
    }
}
