package com.arquisoft.fichas.infrastructure.config;

import com.arquisoft.fichas.domain.estudiante.port.out.EstudianteOutputPort;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiante.rules.impl.EstudiantesExistenRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudianteFichaPerfilCupoDisponibleRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesNoVinculadosRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesSinDuplicadosRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudianteFichaPerfilCupoDisponibleRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantesNoVinculadosRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantesSinDuplicadosRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FichasDomainRulesConfig {

    @Bean
    public EstudianteFichaPerfilCupoDisponibleRule estudianteFichaPerfilCupoDisponibleRule(
            EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        return new EstudianteFichaPerfilCupoDisponibleRuleImpl(estudianteFichaPerfilOutputPort);
    }

    @Bean
    public EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule() {
        return new EstudiantesSinDuplicadosRuleImpl();
    }

    @Bean
    public EstudiantesNoVinculadosRule estudiantesNoVinculadosRule(
            EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        return new EstudiantesNoVinculadosRuleImpl(estudianteFichaPerfilOutputPort);
    }

    @Bean
    public EstudiantesExistenRule estudiantesExistenRule(EstudianteOutputPort estudianteOutputPort) {
        return new EstudiantesExistenRuleImpl(estudianteOutputPort);
    }

    @Bean
    public FichaPerfilExisteRule fichaPerfilExisteRule(FichaPerfilOutputPort fichaPerfilOutputPort) {
        return new FichaPerfilExisteRuleImpl(fichaPerfilOutputPort);
    }
}
