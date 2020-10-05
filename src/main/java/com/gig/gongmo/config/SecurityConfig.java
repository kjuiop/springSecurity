package com.gig.gongmo.config;

import com.gig.gongmo.account.AccountService;
import com.gig.gongmo.common.LoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

        http.addFilterBefore(new LoggingFilter(), WebAsyncManagerIntegrationFilter.class);


        http.authorizeRequests()
                .mvcMatchers("/", "/info", "/account/**", "/signup").permitAll()
                .mvcMatchers("/admin").hasRole("ADMIN")
                .mvcMatchers("/user").hasRole("USER")
                .anyRequest().authenticated()
                .expressionHandler(expressionHandler());
//                .accessDecisionManager(accessDecisionManager());


//        http.authorizeRequests()
//                .mvcMatchers("/user").hasAuthority("ROLE_USER")
//                .anyRequest().anonymous()
//                // 다시 로그인이 필요할 때
//                .anyRequest().fullyAuthenticated()
//                .anyRequest().denyAll()
//                .anyRequest().not().anonymous()


            http.formLogin()
                .loginPage("/login")
                .permitAll();

//              로그인 페이지 파라미터 커스텀
//                .usernameParameter("my-username")
//                .passwordParameter("my-password");

            http.httpBasic();

            // TODO ExceptionTranslatorFilter -> FilterSecurityInterceptor (AccessDecisionManager, AffirmativeBased)
            // TODO AuthenticationException -> AuthenticationEntryPoint
            // TODO AccessDeniedException -> AccessDeniedHandler


        // TODO 핸들러를 만들고 주입하는 방법으로 해보자.
            http.exceptionHandling()
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                        String username = principal.getUsername();
                        System.out.println(username + " is denied to access " + request.getRequestURI());
                        response.sendRedirect("/access-denied");
                    });
//                    .accessDeniedPage("/access-denied");


//         익명 사용자 관련 설정
//            http.anonymous()
//                .principal()
//                .authorities()
//                .key()



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

        // 시큐리티 세션 설정
//        http.sessionManagement()
//                .sessionFixation().changeSessionId();
//        http.sessionManagement()
//                .sessionFixation().migrateSession();
//        http.sessionManagement()
//                .sessionFixation()
//                .changeSessionId()
//                .invalidSessionUrl("/login");

//        http.sessionManagement()
//                .sessionFixation()
//                .changeSessionId()
//                .maximumSessions(1)
//                .maxSessionsPreventsLogin(false);

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS);


        http.rememberMe()
                // 폼에서 remember me 값을 안보내줘도 remember me 사용
                .alwaysRemember(false)
                // remember Me 파라미터 이름 설정
                .rememberMeParameter("remember")
                // 토큰 유효기간 설정, 기본값 2주
                .tokenValiditySeconds(1)
                // 쿠키 보안 설정
                .useSecureCookie(true)
                .userDetailsService(accountService)
                .key("remember-me-sample");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
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
