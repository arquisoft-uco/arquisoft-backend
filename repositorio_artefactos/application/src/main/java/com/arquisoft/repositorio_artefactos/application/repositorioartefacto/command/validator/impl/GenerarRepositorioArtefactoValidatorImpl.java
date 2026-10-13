package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.validator.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.validator.GenerarRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.administrador.model.VigenciaAdministrador;
import com.arquisoft.repositorio_artefactos.domain.administrador.rules.AdministradorVigenteRule;
import com.arquisoft.repositorio_artefactos.domain.administrador.rules.impl.AdministradorVigenteRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.model.DisponibilidadRepositorioArtefacto;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.RepositorioArtefactoUnicoRule;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.impl.RepositorioArtefactoUnicoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class GenerarRepositorioArtefactoValidatorImpl implements GenerarRepositorioArtefactoValidator {

    private final AdministradorVigenteRule administradorVigenteRule;
    private final RepositorioArtefactoUnicoRule repositorioArtefactoUnicoRule;

    public GenerarRepositorioArtefactoValidatorImpl() {
        this.administradorVigenteRule = new AdministradorVigenteRuleImpl();
        this.repositorioArtefactoUnicoRule = new RepositorioArtefactoUnicoRuleImpl();
    }

    @Override
    public void validar(RepositorioArtefactoDomain repositorio, AdministradorDomain administrador, boolean yaExiste) {
        administradorVigenteRule.validar(new VigenciaAdministrador(repositorio.getAdministrador(), administrador));
        repositorioArtefactoUnicoRule.validar(new DisponibilidadRepositorioArtefacto(
                repositorio.getNombreRepositorio(),
                repositorio.getDescripcion(),
                repositorio.getAdministrador(),
                yaExiste));
    }
}
