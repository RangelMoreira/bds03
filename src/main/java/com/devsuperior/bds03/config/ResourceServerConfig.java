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
	private static final String[] PUBLIC = {"/oauth/token", "/h2-console/**"};
	
	private static final String[] OPERATOR_GET= {"/departments/**","/employees/**"};
	
	
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
		.antMatchers(PUBLIC).permitAll()//Permite para todo mundo
		.antMatchers(HttpMethod.GET, OPERATOR_GET).hasAnyRole("OPERATOR")//Rotas Get permitas para o operador
		.anyRequest().hasAnyRole("ADMIN");
	}
	
}
