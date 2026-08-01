package com.arquisoft.fichas.infrastructure.config;

import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudianteFichaPerfilCupoDisponibleRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudianteFichaPerfilCupoDisponibleRuleImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FichasDomainRulesConfig {

    @Bean
    public EstudianteFichaPerfilCupoDisponibleRule estudianteFichaPerfilCupoDisponibleRule(
            EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        return new EstudianteFichaPerfilCupoDisponibleRuleImpl(estudianteFichaPerfilOutputPort);
    }
}
