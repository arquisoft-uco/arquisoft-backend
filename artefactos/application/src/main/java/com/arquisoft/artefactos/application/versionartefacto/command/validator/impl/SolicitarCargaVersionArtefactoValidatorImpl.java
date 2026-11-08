package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.validator.SolicitarCargaVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.ActividadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoExisteRule;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoNoOmitidoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.EstudiantePropietarioArtefactoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoNoOmitidoRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.EstudiantePropietarioArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.artefactos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.revisionasesor.rules.ArtefactoNoCerradoRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionVersionAnteriorEnProgresoRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.VersionAnteriorRevisadaRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.ArtefactoNoCerradoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionVersionAnteriorEnProgresoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.VersionAnteriorRevisadaRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.SolicitudCargaVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.AlmacenamientoProyectoGrado;
import com.arquisoft.artefactos.domain.versionartefacto.model.CuotaProyectoGrado;
import com.arquisoft.artefactos.domain.versionartefacto.model.LimiteTamanoArchivo;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CuotaProyectoGradoNoExcedidaRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.TamanoArchivoPermitidoRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.CuotaProyectoGradoNoExcedidaRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.TamanoArchivoPermitidoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class SolicitarCargaVersionArtefactoValidatorImpl implements SolicitarCargaVersionArtefactoValidator {

    private final ArtefactoExisteRule artefactoExisteRule;
    private final EstudiantePropietarioArtefactoRule estudiantePropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final VersionAnteriorRevisadaRule versionAnteriorRevisadaRule;
    private final ArtefactoNoCerradoRule artefactoNoCerradoRule;
    private final RevisionVersionAnteriorEnProgresoRule revisionVersionAnteriorEnProgresoRule;
    private final TamanoArchivoPermitidoRule tamanoArchivoPermitidoRule;
    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final CuotaProyectoGradoNoExcedidaRule cuotaProyectoGradoNoExcedidaRule;

    public SolicitarCargaVersionArtefactoValidatorImpl() {
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.estudiantePropietarioArtefactoRule = new EstudiantePropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.versionAnteriorRevisadaRule = new VersionAnteriorRevisadaRuleImpl();
        this.artefactoNoCerradoRule = new ArtefactoNoCerradoRuleImpl();
        this.revisionVersionAnteriorEnProgresoRule = new RevisionVersionAnteriorEnProgresoRuleImpl();
        this.tamanoArchivoPermitidoRule = new TamanoArchivoPermitidoRuleImpl();
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.cuotaProyectoGradoNoExcedidaRule = new CuotaProyectoGradoNoExcedidaRuleImpl();
    }

    @Override
    public void validar(SolicitudCargaVersionArtefactoDomain solicitud, ArtefactoDomain artefacto,
                        boolean estudianteVinculado, long tamanoMaximoBytes, int ultimoNumeroVersion,
                        RevisionAsesorDomain revisionVigente, AlmacenamientoProyectoGrado almacenamiento) {
        artefactoExisteRule.validar(new ExistenciaArtefacto(solicitud.getArtefacto(), artefacto));
        estudiantePropietarioArtefactoRule.validar(
                new PropiedadArtefacto(solicitud.getSolicitante(), solicitud.getArtefacto(), estudianteVinculado));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(artefacto));
        var versionAnterior = new RevisionVersionAnterior(ultimoNumeroVersion, revisionVigente);
        versionAnteriorRevisadaRule.validar(versionAnterior);
        artefactoNoCerradoRule.validar(versionAnterior);
        revisionVersionAnteriorEnProgresoRule.validar(versionAnterior);
        tamanoArchivoPermitidoRule.validar(new LimiteTamanoArchivo(
                solicitud.getNombreArchivo(), solicitud.getTamanoBytes(), tamanoMaximoBytes));
        var proyecto = almacenamiento.proyectoGrado();
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(artefacto.getProyectoGrado(), !proyecto.esVacio()));
        cuotaProyectoGradoNoExcedidaRule.validar(new CuotaProyectoGrado(
                artefacto.getProyectoGrado(), proyecto.getNombre(), solicitud.getNombreArchivo(),
                almacenamiento.cuotaBytes(), almacenamiento.usoBytes(), solicitud.getTamanoBytes()));
    }
}
