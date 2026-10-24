package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.validator.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.validator.ModificarRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.administrador.model.VigenciaAdministrador;
import com.arquisoft.repositorio_artefactos.domain.administrador.rules.AdministradorVigenteRule;
import com.arquisoft.repositorio_artefactos.domain.administrador.rules.impl.AdministradorVigenteRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.ModificacionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.model.DisponibilidadRepositorioArtefacto;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.model.ExistenciaRepositorioArtefacto;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.RepositorioArtefactoExistenteRule;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.RepositorioArtefactoUnicoRule;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.impl.RepositorioArtefactoExistenteRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.impl.RepositorioArtefactoUnicoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ModificarRepositorioArtefactoValidatorImpl implements ModificarRepositorioArtefactoValidator {

    private final AdministradorVigenteRule administradorVigenteRule;
    private final RepositorioArtefactoExistenteRule repositorioArtefactoExistenteRule;
    private final RepositorioArtefactoUnicoRule repositorioArtefactoUnicoRule;

    public ModificarRepositorioArtefactoValidatorImpl() {
        this.administradorVigenteRule = new AdministradorVigenteRuleImpl();
        this.repositorioArtefactoExistenteRule = new RepositorioArtefactoExistenteRuleImpl();
        this.repositorioArtefactoUnicoRule = new RepositorioArtefactoUnicoRuleImpl();
    }

    @Override
    public void validar(
            ModificacionRepositorioArtefactoDomain modificacion,
            AdministradorDomain administrador,
            RepositorioArtefactoDomain repositorio,
            boolean nombreEnOtroRepositorio) {
        administradorVigenteRule.validar(new VigenciaAdministrador(modificacion.getAdministrador(), administrador));
        repositorioArtefactoExistenteRule.validar(
                new ExistenciaRepositorioArtefacto(modificacion.getRepositorio(), repositorio));
        repositorioArtefactoUnicoRule.validar(new DisponibilidadRepositorioArtefacto(
                modificacion.getNombreRepositorio(), nombreEnOtroRepositorio));
    }
}
