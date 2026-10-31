package com.arquisoft.artefactos.application.estudianteartefacto.command.validator.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.validator.RemoverEstudianteArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.ActividadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoExisteRule;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoNoOmitidoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.AsesorPropietarioArtefactoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoNoOmitidoRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.AsesorPropietarioArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorExisteRule;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudiante.model.ExistenciaEstudiante;
import com.arquisoft.artefactos.domain.estudiante.rules.EstudianteExisteRule;
import com.arquisoft.artefactos.domain.estudiante.rules.impl.EstudianteExisteRuleImpl;
import com.arquisoft.artefactos.domain.estudianteartefacto.RemocionEstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.VinculoEstudianteArtefacto;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.EstudianteVinculadoArtefactoRule;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl.EstudianteVinculadoArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionRepositorioArtefactoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RemoverEstudianteArtefactoValidatorImpl implements RemoverEstudianteArtefactoValidator {

    private final ArtefactoExisteRule artefactoExisteRule;
    private final AsesorExisteRule asesorExisteRule;
    private final VersionRepositorioArtefactoExisteRule versionRepositorioArtefactoExisteRule;
    private final EstudianteExisteRule estudianteExisteRule;
    private final AsesorPropietarioArtefactoRule asesorPropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final EstudianteVinculadoArtefactoRule estudianteVinculadoArtefactoRule;

    public RemoverEstudianteArtefactoValidatorImpl() {
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.versionRepositorioArtefactoExisteRule = new VersionRepositorioArtefactoExisteRuleImpl();
        this.estudianteExisteRule = new EstudianteExisteRuleImpl();
        this.asesorPropietarioArtefactoRule = new AsesorPropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.estudianteVinculadoArtefactoRule = new EstudianteVinculadoArtefactoRuleImpl();
    }

    @Override
    public void validar(
            RemocionEstudianteArtefactoDomain entrada,
            ArtefactoDomain artefacto,
            AsesorDomain asesor,
            VersionRepositorioArtefactoDomain versionRepositorio,
            EstudianteDomain estudiante,
            boolean vinculado) {
        artefactoExisteRule.validar(new ExistenciaArtefacto(entrada.getArtefacto(), artefacto));
        asesorExisteRule.validar(new ExistenciaAsesor(entrada.getAsesor(), asesor));
        versionRepositorioArtefactoExisteRule.validar(new ExistenciaVersionRepositorioArtefacto(
                artefacto.getVersionRepositorioArtefacto(), versionRepositorio));
        estudianteExisteRule.validar(new ExistenciaEstudiante(entrada.getEstudiante(), !estudiante.esVacio()));
        asesorPropietarioArtefactoRule.validar(new PropiedadArtefactoAsesor(entrada.getAsesor(), artefacto));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(artefacto));
        estudianteVinculadoArtefactoRule.validar(new VinculoEstudianteArtefacto(
                entrada.getEstudiante(), entrada.getArtefacto(), estudiante.getNombre(), vinculado));
    }
}
