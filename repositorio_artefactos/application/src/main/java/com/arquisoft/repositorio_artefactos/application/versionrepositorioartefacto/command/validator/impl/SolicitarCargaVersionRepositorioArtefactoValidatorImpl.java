package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.SolicitarCargaVersionRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.administrador.model.VigenciaAdministrador;
import com.arquisoft.repositorio_artefactos.domain.administrador.rules.AdministradorVigenteRule;
import com.arquisoft.repositorio_artefactos.domain.administrador.rules.impl.AdministradorVigenteRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.model.ExistenciaRepositorioArtefacto;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.RepositorioArtefactoExistenteRule;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.impl.RepositorioArtefactoExistenteRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SolicitudCargaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.LimiteTamanoArchivo;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.TamanoArchivoPermitidoRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.TamanoArchivoPermitidoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class SolicitarCargaVersionRepositorioArtefactoValidatorImpl
        implements SolicitarCargaVersionRepositorioArtefactoValidator {

    private final AdministradorVigenteRule administradorVigenteRule;
    private final RepositorioArtefactoExistenteRule repositorioArtefactoExistenteRule;
    private final TamanoArchivoPermitidoRule tamanoArchivoPermitidoRule;

    public SolicitarCargaVersionRepositorioArtefactoValidatorImpl() {
        this.administradorVigenteRule = new AdministradorVigenteRuleImpl();
        this.repositorioArtefactoExistenteRule = new RepositorioArtefactoExistenteRuleImpl();
        this.tamanoArchivoPermitidoRule = new TamanoArchivoPermitidoRuleImpl();
    }

    @Override
    public void validar(SolicitudCargaVersionRepositorioArtefactoDomain solicitud, AdministradorDomain administrador,
                        RepositorioArtefactoDomain repositorio, long tamanoMaximoBytes) {
        administradorVigenteRule.validar(new VigenciaAdministrador(solicitud.getSolicitante(), administrador));
        repositorioArtefactoExistenteRule.validar(
                new ExistenciaRepositorioArtefacto(solicitud.getRepositorio(), repositorio));
        tamanoArchivoPermitidoRule.validar(new LimiteTamanoArchivo(
                solicitud.getNombreArchivo(), solicitud.getTamanoBytes(), tamanoMaximoBytes));
    }
}
