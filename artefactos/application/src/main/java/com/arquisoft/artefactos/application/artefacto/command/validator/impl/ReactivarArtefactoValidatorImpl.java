package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.application.artefacto.command.validator.ReactivarArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.ReactivacionArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorExisteRule;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.artefactos.domain.asesorproyectogrado.rules.AsesorAsignadoProyectoGradoRule;
import com.arquisoft.artefactos.domain.asesorproyectogrado.rules.impl.AsesorAsignadoProyectoGradoRuleImpl;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.CupoEstudiantesArtefacto;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.EstudiantesDentroDelProyectoRule;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl.EstudiantesDentroDelProyectoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReactivarArtefactoValidatorImpl implements ReactivarArtefactoValidator {

    private final AsesorExisteRule asesorExisteRule;
    private final AsesorAsignadoProyectoGradoRule asesorAsignadoProyectoGradoRule;
    private final EstudiantesDentroDelProyectoRule estudiantesDentroDelProyectoRule;

    public ReactivarArtefactoValidatorImpl() {
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.asesorAsignadoProyectoGradoRule = new AsesorAsignadoProyectoGradoRuleImpl();
        this.estudiantesDentroDelProyectoRule = new EstudiantesDentroDelProyectoRuleImpl();
    }

    @Override
    public void validar(
            ReactivacionArtefactoDomain reactivacion,
            AsesorDomain asesor,
            List<AsesorProyectoGradoDomain> vinculosMasRecientes,
            CupoEstudiantesArtefacto cupo) {
        asesorExisteRule.validar(new ExistenciaAsesor(reactivacion.getAsesor(), asesor));
        asesorAsignadoProyectoGradoRule.validar(new VigenciaAsesorProyectoGrado(
                reactivacion.getAsesor(), asesor.getNombre(), reactivacion.getProyectoGrado(), vinculosMasRecientes));
        estudiantesDentroDelProyectoRule.validar(cupo);
    }
}
