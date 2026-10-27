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
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.CoincidenciaTipoArchivo;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ConsistenciaCarga;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.CorrespondenciaCarga;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaCargaPendiente;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaDocumento;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.LimiteTamanoArchivo;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.CargaCorrespondeASolicitudRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.CargaPendienteExistenteRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.CargaSinModificarRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.DocumentoPreviamenteCreadoRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.NuevoDocumentoDisponibleRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.TamanoArchivoPermitidoRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.TipoArchivoCoincideRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.CargaCorrespondeASolicitudRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.CargaPendienteExistenteRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.CargaSinModificarRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.DocumentoPreviamenteCreadoRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.NuevoDocumentoDisponibleRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.TamanoArchivoPermitidoRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.TipoArchivoCoincideRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class SubirVersionRepositorioArtefactoValidatorImpl implements SubirVersionRepositorioArtefactoValidator {

    private final AdministradorVigenteRule administradorVigenteRule;
    private final RepositorioArtefactoExistenteRule repositorioArtefactoExistenteRule;
    private final CargaPendienteExistenteRule cargaPendienteExistenteRule;
    private final CargaCorrespondeASolicitudRule cargaCorrespondeASolicitudRule;
    private final CargaSinModificarRule cargaSinModificarRule;
    private final TamanoArchivoPermitidoRule tamanoArchivoPermitidoRule;
    private final TipoArchivoCoincideRule tipoArchivoCoincideRule;
    private final NuevoDocumentoDisponibleRule nuevoDocumentoDisponibleRule;
    private final DocumentoPreviamenteCreadoRule documentoPreviamenteCreadoRule;

    public SubirVersionRepositorioArtefactoValidatorImpl() {
        this.administradorVigenteRule = new AdministradorVigenteRuleImpl();
        this.repositorioArtefactoExistenteRule = new RepositorioArtefactoExistenteRuleImpl();
        this.cargaPendienteExistenteRule = new CargaPendienteExistenteRuleImpl();
        this.cargaCorrespondeASolicitudRule = new CargaCorrespondeASolicitudRuleImpl();
        this.cargaSinModificarRule = new CargaSinModificarRuleImpl();
        this.tamanoArchivoPermitidoRule = new TamanoArchivoPermitidoRuleImpl();
        this.tipoArchivoCoincideRule = new TipoArchivoCoincideRuleImpl();
        this.nuevoDocumentoDisponibleRule = new NuevoDocumentoDisponibleRuleImpl();
        this.documentoPreviamenteCreadoRule = new DocumentoPreviamenteCreadoRuleImpl();
    }

    @Override
    public void validarCarga(SubidaVersionRepositorioArtefactoDomain subida, AdministradorDomain administrador,
                             RepositorioArtefactoDomain repositorio, CargaPendienteDomain carga,
                             long tamanoMaximoBytes, int ultimoNumeroVersion) {
        var existenciaDocumento = new ExistenciaDocumento(
                subida.getNombreDocumento(), subida.isNuevoDocumento(), ultimoNumeroVersion);

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

    @Override
    public void validarContenido(SubidaVersionRepositorioArtefactoDomain subida, CargaPendienteDomain carga,
                                 AnalisisArchivoDomain analisis) {
        cargaSinModificarRule.validar(new ConsistenciaCarga(
                subida.getCarga(), carga.getNombreArchivo(), carga.getEtag(), analisis.getEtag()));
        tipoArchivoCoincideRule.validar(
                new CoincidenciaTipoArchivo(carga.getNombreArchivo(), carga.getTipoContenido(), analisis));
    }
}
