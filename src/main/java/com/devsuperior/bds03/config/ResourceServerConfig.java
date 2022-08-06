package com.devsuperior.bds03.config;


import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer //Habilita as funcionalidades do server do OAuth2
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	//Enpoints públicos
	private static final String[] PUBLICA = {"/oauth/token", "/h2-console/**"};
	
	private static final String[] OPERATOR_OR_ADMIN = {"/products/**","/categories/**"};
	
	private static final String[] ADMIN = {"/users/**"};
	
	/*Faz com que o resource Server seja capaz de decodificar o Token 
	 * e ver se eles está válido ou não */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}
	
	/* Configurando as Rotas */

	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		//Liberando h2
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}
		
		http.authorizeRequests()
		.antMatchers(PUBLICA).permitAll()//Permite para todo mundo
		.antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()//Permitido para as Rotas GET 
		.antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR","ADMIN")//Permitido apenas para esses papeis
		.antMatchers(ADMIN).hasRole("ADMIN")
		.anyRequest().authenticated();
	}
	
}
