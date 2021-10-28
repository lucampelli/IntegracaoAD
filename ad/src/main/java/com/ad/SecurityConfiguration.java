package com.ad;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http.csrf().disable().authorizeRequests().anyRequest().fullyAuthenticated().and().httpBasic().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.ldapAuthentication()
			.userDnPatterns("CN={0},OU=TestFocal")
			.groupSearchBase("OU=TestFocal")
			.contextSource()
			.url("ldap://localhost:8389/DC=GAFISACT,DC=COM,DC=BR")
			.and()
			.passwordCompare()
			.passwordEncoder(new BCryptPasswordEncoder())
			.passwordAttribute("userPassword");
	}

}
