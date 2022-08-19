package com.example.securingweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import lombok.extern.slf4j.Slf4j;
/**
 * The class is annotated with @EnableWebSecurity to enable Spring Security’s
 * web security support and provide the Spring MVC integration. It also extends
 * WebSecurityConfigurerAdapter and overrides a couple of its methods to set
 * some specifics of the web security configuration.
 * 
 * 添加@EnableWebSecurity注释，以启用Spring Security的Web安全支持并提供Spring
 * MVC集成。它还扩展了WebSecurityConfigurerAdapter并覆盖了其一些方法来设置Web安全配置的某些细节。
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	/**Slf4j
	 * The configure(HttpSecurity) method defines which URL paths should be secured
	 * and which should not. Specifically, the / and /home paths are configured to
	 * not require any authentication. All other paths must be authenticated.
	 * 
	 * configure（HttpSecurity）方法定义应保护哪些URL路径，不应该保护哪些URL路径。
	 * 配置为不受保护的路径则不需要任何身份验证，其他路径必须经过验证。
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/", "/home").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.permitAll();
	}

	/**
	 * The userDetailsService() method sets up an in-memory user store with a single
	 * user. That user is given a user name of user, a password of password, and a
	 * role of USER.
	 */
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		// remember the password that is printed out and use in the next step
		log.trace(encoder.encode("password"));
		UserDetails user = User.withUsername("user")
				.password("{bcrypt}$2a$10$CWdgM73OCr/UFbw0LHucnOjeqTjlRurSlVUnjw/uE9mKOR8hhFxIu").roles("USER").build();
		return new InMemoryUserDetailsManager(user);
	}
}
