package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.validator.GenerarVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoExisteRule;
import com.arquisoft.artefactos.domain.artefacto.rules.EstudiantePropietarioArtefactoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.EstudiantePropietarioArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorExisteRule;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorVigenteRule;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorVigenteRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.CargaAnalizada;
import com.arquisoft.artefactos.domain.versionartefacto.model.CoincidenciaTipoArchivo;
import com.arquisoft.artefactos.domain.versionartefacto.model.ConsistenciaCarga;
import com.arquisoft.artefactos.domain.versionartefacto.model.CorrespondenciaCarga;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaCargaPendiente;
import com.arquisoft.artefactos.domain.versionartefacto.model.LimiteTamanoArchivo;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CargaCorrespondeASolicitudRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CargaPendienteExistenteRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CargaSinModificarRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.TamanoArchivoPermitidoRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.TipoArchivoCoincideRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.CargaCorrespondeASolicitudRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.CargaPendienteExistenteRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.CargaSinModificarRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.TamanoArchivoPermitidoRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.TipoArchivoCoincideRuleImpl;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionesRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionesRepositorioArtefactoExistenRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionesRepositorioArtefactoExistenRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenerarVersionArtefactoValidatorImpl implements GenerarVersionArtefactoValidator {

    private final ArtefactoExisteRule artefactoExisteRule;
    private final EstudiantePropietarioArtefactoRule estudiantePropietarioArtefactoRule;
    private final AsesorExisteRule asesorExisteRule;
    private final AsesorVigenteRule asesorVigenteRule;
    private final VersionesRepositorioArtefactoExistenRule versionesRepositorioArtefactoExistenRule;
    private final CargaPendienteExistenteRule cargaPendienteExistenteRule;
    private final CargaCorrespondeASolicitudRule cargaCorrespondeASolicitudRule;
    private final CargaSinModificarRule cargaSinModificarRule;
    private final TamanoArchivoPermitidoRule tamanoArchivoPermitidoRule;
    private final TipoArchivoCoincideRule tipoArchivoCoincideRule;

    public GenerarVersionArtefactoValidatorImpl() {
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.estudiantePropietarioArtefactoRule = new EstudiantePropietarioArtefactoRuleImpl();
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.asesorVigenteRule = new AsesorVigenteRuleImpl();
        this.versionesRepositorioArtefactoExistenRule = new VersionesRepositorioArtefactoExistenRuleImpl();
        this.cargaPendienteExistenteRule = new CargaPendienteExistenteRuleImpl();
        this.cargaCorrespondeASolicitudRule = new CargaCorrespondeASolicitudRuleImpl();
        this.cargaSinModificarRule = new CargaSinModificarRuleImpl();
        this.tamanoArchivoPermitidoRule = new TamanoArchivoPermitidoRuleImpl();
        this.tipoArchivoCoincideRule = new TipoArchivoCoincideRuleImpl();
    }

    @Override
    public void validarCarga(GeneracionVersionArtefactoDomain generacion, ArtefactoDomain artefacto,
                             boolean estudianteVinculado, AsesorDomain asesor,
                             List<VersionRepositorioArtefactoDomain> versionesRepositorio, CargaPendienteDomain carga,
                             long tamanoMaximoBytes) {
        artefactoExisteRule.validar(new ExistenciaArtefacto(generacion.getArtefacto(), artefacto));
        estudiantePropietarioArtefactoRule.validar(
                new PropiedadArtefacto(generacion.getSolicitante(), generacion.getArtefacto(), estudianteVinculado));
        asesorExisteRule.validar(new ExistenciaAsesor(artefacto.getAsesor(), asesor));
        asesorVigenteRule.validar(new VigenciaAsesor(artefacto.getAsesor(), asesor));
        versionesRepositorioArtefactoExistenRule.validar(new ExistenciaVersionesRepositorioArtefacto(
                List.of(artefacto.getVersionRepositorioArtefacto()), versionesRepositorio));
        cargaPendienteExistenteRule.validar(new ExistenciaCargaPendiente(generacion.getCarga(), carga));
        cargaCorrespondeASolicitudRule.validar(
                new CorrespondenciaCarga(carga, generacion.getSolicitante(), generacion.getArtefacto()));
        tamanoArchivoPermitidoRule.validar(new LimiteTamanoArchivo(
                carga.getNombreArchivo(), carga.getTamanoBytes(), tamanoMaximoBytes));
    }

    @Override
    public void validarContenido(GeneracionVersionArtefactoDomain generacion, CargaAnalizada cargaAnalizada) {
        var carga = cargaAnalizada.carga();
        var analisis = cargaAnalizada.analisis();
        cargaSinModificarRule.validar(
                new ConsistenciaCarga(
                        generacion.getCarga(), carga.getNombreArchivo(), carga.getEtag(), analisis.getEtag()));
        tipoArchivoCoincideRule.validar(
                new CoincidenciaTipoArchivo(carga.getNombreArchivo(), carga.getTipoContenido(), analisis));
    }
}
