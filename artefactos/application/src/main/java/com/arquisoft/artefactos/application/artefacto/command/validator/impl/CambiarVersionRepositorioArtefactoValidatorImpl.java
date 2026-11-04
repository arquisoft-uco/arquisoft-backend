package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.application.artefacto.command.validator.CambiarVersionRepositorioArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.CambioVersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.ActividadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.CambioVersionRepositorio;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.MomentoCambioVersionRepositorio;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoExisteRule;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoNoOmitidoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.AsesorPropietarioArtefactoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.CambioVersionRepositorioPermitidoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.VersionRepositorioMismoDocumentoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.VersionRepositorioDistintaRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoNoOmitidoRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.AsesorPropietarioArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.CambioVersionRepositorioPermitidoRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.VersionRepositorioMismoDocumentoRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.VersionRepositorioDistintaRuleImpl;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorExisteRule;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionRepositorioArtefactoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarVersionRepositorioArtefactoValidatorImpl implements CambiarVersionRepositorioArtefactoValidator {

    private final ArtefactoExisteRule artefactoExisteRule;
    private final AsesorExisteRule asesorExisteRule;
    private final VersionRepositorioArtefactoExisteRule versionRepositorioArtefactoExisteRule;
    private final AsesorPropietarioArtefactoRule asesorPropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final VersionRepositorioMismoDocumentoRule versionRepositorioMismoDocumentoRule;
    private final VersionRepositorioDistintaRule versionRepositorioDistintaRule;
    private final CambioVersionRepositorioPermitidoRule cambioVersionRepositorioPermitidoRule;

    public CambiarVersionRepositorioArtefactoValidatorImpl() {
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.versionRepositorioArtefactoExisteRule = new VersionRepositorioArtefactoExisteRuleImpl();
        this.asesorPropietarioArtefactoRule = new AsesorPropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.versionRepositorioMismoDocumentoRule = new VersionRepositorioMismoDocumentoRuleImpl();
        this.versionRepositorioDistintaRule = new VersionRepositorioDistintaRuleImpl();
        this.cambioVersionRepositorioPermitidoRule = new CambioVersionRepositorioPermitidoRuleImpl();
    }

    @Override
    public void validar(
            CambioVersionRepositorioArtefactoDomain cambio,
            ArtefactoDomain artefacto,
            AsesorDomain asesor,
            VersionRepositorioArtefactoDomain actual,
            VersionRepositorioArtefactoDomain elegida,
            RevisionVersionAnterior versionAnterior) {
        artefactoExisteRule.validar(new ExistenciaArtefacto(cambio.getArtefacto(), artefacto));
        asesorExisteRule.validar(new ExistenciaAsesor(cambio.getAsesor(), asesor));
        versionRepositorioArtefactoExisteRule.validar(new ExistenciaVersionRepositorioArtefacto(
                artefacto.getVersionRepositorioArtefacto(), actual));
        versionRepositorioArtefactoExisteRule.validar(new ExistenciaVersionRepositorioArtefacto(
                cambio.getVersionRepositorioArtefacto(), elegida));
        asesorPropietarioArtefactoRule.validar(new PropiedadArtefactoAsesor(cambio.getAsesor(), artefacto));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(artefacto));
        versionRepositorioMismoDocumentoRule.validar(new CambioVersionRepositorio(actual, elegida));
        versionRepositorioDistintaRule.validar(new CambioVersionRepositorio(actual, elegida));
        cambioVersionRepositorioPermitidoRule.validar(new MomentoCambioVersionRepositorio(
                cambio.getArtefacto(), actual.getNombreDocumento(), versionAnterior));
    }
}
