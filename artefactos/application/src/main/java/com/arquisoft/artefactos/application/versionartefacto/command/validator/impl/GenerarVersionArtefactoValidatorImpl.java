package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.validator.GenerarVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.model.ActividadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoExisteRule;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoNoOmitidoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.EstudiantePropietarioArtefactoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoNoOmitidoRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.EstudiantePropietarioArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorExisteRule;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorVigenteRule;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorVigenteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.ArtefactoNoCerradoRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionVersionAnteriorEnProgresoRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.VersionAnteriorRevisadaRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.ArtefactoNoCerradoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionVersionAnteriorEnProgresoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.VersionAnteriorRevisadaRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.CorrespondenciaCarga;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaCargaPendiente;
import com.arquisoft.artefactos.domain.versionartefacto.model.LimiteTamanoArchivo;
import com.arquisoft.artefactos.domain.versionartefacto.model.PrecondicionesGeneracionVersion;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CargaCorrespondeASolicitudRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CargaPendienteExistenteRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.TamanoArchivoPermitidoRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.CargaCorrespondeASolicitudRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.CargaPendienteExistenteRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.TamanoArchivoPermitidoRuleImpl;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionRepositorioArtefactoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class GenerarVersionArtefactoValidatorImpl implements GenerarVersionArtefactoValidator {

    private final ArtefactoExisteRule artefactoExisteRule;
    private final EstudiantePropietarioArtefactoRule estudiantePropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final AsesorExisteRule asesorExisteRule;
    private final AsesorVigenteRule asesorVigenteRule;
    private final VersionRepositorioArtefactoExisteRule versionRepositorioArtefactoExisteRule;
    private final VersionAnteriorRevisadaRule versionAnteriorRevisadaRule;
    private final ArtefactoNoCerradoRule artefactoNoCerradoRule;
    private final RevisionVersionAnteriorEnProgresoRule revisionVersionAnteriorEnProgresoRule;
    private final CargaPendienteExistenteRule cargaPendienteExistenteRule;
    private final CargaCorrespondeASolicitudRule cargaCorrespondeASolicitudRule;
    private final TamanoArchivoPermitidoRule tamanoArchivoPermitidoRule;

    public GenerarVersionArtefactoValidatorImpl() {
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.estudiantePropietarioArtefactoRule = new EstudiantePropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.asesorVigenteRule = new AsesorVigenteRuleImpl();
        this.versionRepositorioArtefactoExisteRule = new VersionRepositorioArtefactoExisteRuleImpl();
        this.versionAnteriorRevisadaRule = new VersionAnteriorRevisadaRuleImpl();
        this.artefactoNoCerradoRule = new ArtefactoNoCerradoRuleImpl();
        this.revisionVersionAnteriorEnProgresoRule = new RevisionVersionAnteriorEnProgresoRuleImpl();
        this.cargaPendienteExistenteRule = new CargaPendienteExistenteRuleImpl();
        this.cargaCorrespondeASolicitudRule = new CargaCorrespondeASolicitudRuleImpl();
        this.tamanoArchivoPermitidoRule = new TamanoArchivoPermitidoRuleImpl();
    }

    @Override
    public void validar(GeneracionVersionArtefactoDomain generacion, PrecondicionesGeneracionVersion previas) {
        var artefacto = previas.artefacto();
        var carga = previas.carga();

        artefactoExisteRule.validar(new ExistenciaArtefacto(generacion.getArtefacto(), artefacto));
        estudiantePropietarioArtefactoRule.validar(new PropiedadArtefacto(
                generacion.getSolicitante(), generacion.getArtefacto(), previas.estudianteVinculado()));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(artefacto));
        asesorExisteRule.validar(new ExistenciaAsesor(artefacto.getAsesor(), previas.asesor()));
        asesorVigenteRule.validar(new VigenciaAsesor(artefacto.getAsesor(), previas.asesor()));
        versionRepositorioArtefactoExisteRule.validar(new ExistenciaVersionRepositorioArtefacto(
                artefacto.getVersionRepositorioArtefacto(), previas.versionRepositorio()));
        cargaPendienteExistenteRule.validar(new ExistenciaCargaPendiente(generacion.getCarga(), carga));
        cargaCorrespondeASolicitudRule.validar(
                new CorrespondenciaCarga(carga, generacion.getSolicitante(), generacion.getArtefacto()));
        tamanoArchivoPermitidoRule.validar(new LimiteTamanoArchivo(
                carga.getNombreArchivo(), carga.getTamanoBytes(), previas.tamanoMaximoBytes()));
        versionAnteriorRevisadaRule.validar(previas.versionAnterior());
        artefactoNoCerradoRule.validar(previas.versionAnterior());
        revisionVersionAnteriorEnProgresoRule.validar(previas.versionAnterior());
    }
}
