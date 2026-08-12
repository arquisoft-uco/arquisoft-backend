package com.arquisoft.usuarios.infrastructure.config;

import com.arquisoft.usuarios.domain.usuario.rules.UsuarioEmailUnicoRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioEmailUnicoRuleImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsuariosDomainRulesConfig {

    @Bean
    public UsuarioEmailUnicoRule usuarioEmailUnicoRule() {
        return new UsuarioEmailUnicoRuleImpl();
    }
}
