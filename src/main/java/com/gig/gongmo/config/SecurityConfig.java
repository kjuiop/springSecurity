package com.gig.gongmo.config;

import com.gig.gongmo.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;

    public SecurityExpressionHandler expressionHandler() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        // handler 도 같은 걸 쓰고 있는데, roleHierarchy 설정을 추가해준 것 뿐임
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    /**
     * AccessDecisionManager 자체를 재정의한 후 리턴한 방식
     *
     */
//    public AccessDecisionManager accessDecisionManager() {
//        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
//
//        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
//        // handler 도 같은 걸 쓰고 있는데, roleHierarchy 설정을 추가해준 것 뿐임
//        handler.setRoleHierarchy(roleHierarchy);
//
//        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
//        webExpressionVoter.setExpressionHandler(handler);
//
//        List<AccessDecisionVoter<? extends Object>> voters = Arrays.asList(webExpressionVoter);
//        return new AffirmativeBased(voters);
//    }

    @Override
    public void configure(WebSecurity web) throws Exception {
//      web.ignoring().mvcMatchers("/favicon.ico");
//      스프링 시큐리티 적용에 아예 제외해서 서블릿 resource 를 아낌
//      정적인 resource 에 관련한 부분은 여기서 처리하는 것이 효율적임
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//        web.ignoring().requestMatchers(PathRequest.toH2Console());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/info", "/account/**", "/signup").permitAll()
                .mvcMatchers("/admin").hasRole("ADMIN")
                .mvcMatchers("/user").hasRole("USER")
                .anyRequest().authenticated()
                .expressionHandler(expressionHandler());
//                .accessDecisionManager(accessDecisionManager());

            http.formLogin()
                .loginPage("/login")
                .permitAll();
//                .usernameParameter("my-username")
//                .passwordParameter("my-password");

            http.httpBasic();



            // csrf 토큰을 사용하지 않겠다는 소스
            // http.csrf().disable();

        // 다른 Thread 아래의 것들도 공유해서 사용할 수 있음.
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);

        http.logout()
//                .logoutUrl("/logout")
                .logoutSuccessUrl("/");
//                .logoutRequestMatcher()
//                .invalidateHttpSession(true)
//                .deleteCookies()
//                .addLogoutHandler()
//                .logoutSuccessHandler();

    }



    /**
     userDetailsService 주입 방법

     @Override
     protected void configure(AuthenticationManagerBuilder auth) throws Exception {
     auth.userDetailsService(accountService);
     }
     */



    /**
    Password Encode 를 활용하지 않은 인메모리 유저 로그인 방식
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("jake").password("{noop}123").roles("USER")
                .and()
                .withUser("admin").password("{noop}!@#").roles("ADMIN");
    }
    */
}
