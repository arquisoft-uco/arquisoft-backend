package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.application.artefacto.command.validator.CambiarAsesorArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.CambioAsesorArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoExisteRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorVigenteRule;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorVigenteRuleImpl;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.artefactos.domain.asesorproyectogrado.rules.AsesorAsignadoProyectoGradoRule;
import com.arquisoft.artefactos.domain.asesorproyectogrado.rules.impl.AsesorAsignadoProyectoGradoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CambiarAsesorArtefactoValidatorImpl implements CambiarAsesorArtefactoValidator {

    private final ArtefactoExisteRule artefactoExisteRule;
    private final AsesorVigenteRule asesorVigenteRule;
    private final AsesorAsignadoProyectoGradoRule asesorAsignadoProyectoGradoRule;

    public CambiarAsesorArtefactoValidatorImpl() {
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.asesorVigenteRule = new AsesorVigenteRuleImpl();
        this.asesorAsignadoProyectoGradoRule = new AsesorAsignadoProyectoGradoRuleImpl();
    }

    @Override
    public void validar(
            CambioAsesorArtefactoDomain cambio,
            ArtefactoDomain artefacto,
            AsesorDomain asesor,
            List<AsesorProyectoGradoDomain> vinculosMasRecientes) {
        artefactoExisteRule.validar(new ExistenciaArtefacto(cambio.getArtefacto(), artefacto));
        asesorVigenteRule.validar(new VigenciaAsesor(cambio.getAsesor(), asesor));
        asesorAsignadoProyectoGradoRule.validar(new VigenciaAsesorProyectoGrado(
                cambio.getAsesor(), artefacto.getProyectoGrado(), vinculosMasRecientes));
    }
}
