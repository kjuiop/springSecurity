package com.gig.gongmo;

import com.gig.gongmo.account.Account;
import com.gig.gongmo.account.AccountService;
import com.gig.gongmo.sample.SampleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleServiceTest {

    @Autowired
    SampleService sampleService;

    @Autowired
    AccountService accountService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Test
//    @WithMockUser
    public void dashboard() {
        // 계정을 생성한다.
        Account account = new Account();
        account.setRole("USER");
        account.setUsername("jake");
        account.setPassword("123");
        accountService.createNew(account);

        // 생성한 계정을 UserDetails 타입으로 불러온다.
        UserDetails userDetails = accountService.loadUserByUsername("jake");

        // 인증할 수 있는 UserDetails 의 토큰을 발행한다.
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, "123");

        // 발행된 토큰을 통해 인증한다.
        Authentication authentication = authenticationManager.authenticate(token);

        // 다른 필터에서 사용할 수 있도록 SecurityContextHolder 에 등록한다.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        sampleService.dashboard();
    }
}
