package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.validator.SolicitarCargaVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoExisteRule;
import com.arquisoft.artefactos.domain.artefacto.rules.EstudiantePropietarioArtefactoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.EstudiantePropietarioArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.SolicitudCargaVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.LimiteTamanoArchivo;
import com.arquisoft.artefactos.domain.versionartefacto.rules.TamanoArchivoPermitidoRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.TamanoArchivoPermitidoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class SolicitarCargaVersionArtefactoValidatorImpl implements SolicitarCargaVersionArtefactoValidator {

    private final ArtefactoExisteRule artefactoExisteRule;
    private final EstudiantePropietarioArtefactoRule estudiantePropietarioArtefactoRule;
    private final TamanoArchivoPermitidoRule tamanoArchivoPermitidoRule;

    public SolicitarCargaVersionArtefactoValidatorImpl() {
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.estudiantePropietarioArtefactoRule = new EstudiantePropietarioArtefactoRuleImpl();
        this.tamanoArchivoPermitidoRule = new TamanoArchivoPermitidoRuleImpl();
    }

    @Override
    public void validar(SolicitudCargaVersionArtefactoDomain solicitud, ArtefactoDomain artefacto,
                        boolean estudianteVinculado, long tamanoMaximoBytes) {
        artefactoExisteRule.validar(new ExistenciaArtefacto(solicitud.getArtefacto(), artefacto));
        estudiantePropietarioArtefactoRule.validar(
                new PropiedadArtefacto(solicitud.getSolicitante(), solicitud.getArtefacto(), estudianteVinculado));
        tamanoArchivoPermitidoRule.validar(new LimiteTamanoArchivo(solicitud.getTamanoBytes(), tamanoMaximoBytes));
    }
}
