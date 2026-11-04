package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.SubirVersionRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.administrador.model.VigenciaAdministrador;
import com.arquisoft.repositorio_artefactos.domain.administrador.rules.AdministradorVigenteRule;
import com.arquisoft.repositorio_artefactos.domain.administrador.rules.impl.AdministradorVigenteRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.model.ExistenciaRepositorioArtefacto;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.RepositorioArtefactoExistenteRule;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.impl.RepositorioArtefactoExistenteRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.CorrespondenciaCarga;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaCargaPendiente;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaDocumento;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.LimiteTamanoArchivo;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.UltimaVersionDocumento;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.CargaCorrespondeASolicitudRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.CargaPendienteExistenteRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.DocumentoPreviamenteCreadoRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.NuevoDocumentoDisponibleRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.TamanoArchivoPermitidoRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.CargaCorrespondeASolicitudRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.CargaPendienteExistenteRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.DocumentoPreviamenteCreadoRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.NuevoDocumentoDisponibleRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.TamanoArchivoPermitidoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class SubirVersionRepositorioArtefactoValidatorImpl implements SubirVersionRepositorioArtefactoValidator {

    private final AdministradorVigenteRule administradorVigenteRule;
    private final RepositorioArtefactoExistenteRule repositorioArtefactoExistenteRule;
    private final CargaPendienteExistenteRule cargaPendienteExistenteRule;
    private final CargaCorrespondeASolicitudRule cargaCorrespondeASolicitudRule;
    private final TamanoArchivoPermitidoRule tamanoArchivoPermitidoRule;
    private final NuevoDocumentoDisponibleRule nuevoDocumentoDisponibleRule;
    private final DocumentoPreviamenteCreadoRule documentoPreviamenteCreadoRule;

    public SubirVersionRepositorioArtefactoValidatorImpl() {
        this.administradorVigenteRule = new AdministradorVigenteRuleImpl();
        this.repositorioArtefactoExistenteRule = new RepositorioArtefactoExistenteRuleImpl();
        this.cargaPendienteExistenteRule = new CargaPendienteExistenteRuleImpl();
        this.cargaCorrespondeASolicitudRule = new CargaCorrespondeASolicitudRuleImpl();
        this.tamanoArchivoPermitidoRule = new TamanoArchivoPermitidoRuleImpl();
        this.nuevoDocumentoDisponibleRule = new NuevoDocumentoDisponibleRuleImpl();
        this.documentoPreviamenteCreadoRule = new DocumentoPreviamenteCreadoRuleImpl();
    }

    @Override
    public void validar(SubidaVersionRepositorioArtefactoDomain subida, AdministradorDomain administrador,
                        RepositorioArtefactoDomain repositorio, CargaPendienteDomain carga,
                        long tamanoMaximoBytes, UltimaVersionDocumento ultimaVersion) {
        var existenciaDocumento = new ExistenciaDocumento(
                subida.getNombreDocumento(), subida.isNuevoDocumento(), ultimaVersion.numeroVersion());

        administradorVigenteRule.validar(new VigenciaAdministrador(subida.getSolicitante(), administrador));
        repositorioArtefactoExistenteRule.validar(
                new ExistenciaRepositorioArtefacto(subida.getRepositorio(), repositorio));
        cargaPendienteExistenteRule.validar(new ExistenciaCargaPendiente(subida.getCarga(), carga));
        cargaCorrespondeASolicitudRule.validar(
                new CorrespondenciaCarga(carga, subida.getSolicitante(), subida.getRepositorio()));
        tamanoArchivoPermitidoRule.validar(new LimiteTamanoArchivo(
                carga.getNombreArchivo(), carga.getTamanoBytes(), tamanoMaximoBytes));
        nuevoDocumentoDisponibleRule.validar(existenciaDocumento);
        documentoPreviamenteCreadoRule.validar(existenciaDocumento);
    }
}
