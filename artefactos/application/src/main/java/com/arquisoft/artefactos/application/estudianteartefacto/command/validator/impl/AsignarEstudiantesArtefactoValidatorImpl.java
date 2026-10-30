package com.arquisoft.artefactos.application.estudianteartefacto.command.validator.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.validator.AsignarEstudiantesArtefactoValidator;
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
import com.arquisoft.artefactos.domain.estudiante.model.ExistenciaEstudiantes;
import com.arquisoft.artefactos.domain.estudiante.model.VigenciaEstudiantes;
import com.arquisoft.artefactos.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.artefactos.domain.estudiante.rules.EstudiantesVigentesRule;
import com.arquisoft.artefactos.domain.estudiante.rules.impl.EstudiantesExistenRuleImpl;
import com.arquisoft.artefactos.domain.estudiante.rules.impl.EstudiantesVigentesRuleImpl;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.PertenenciaProyectoGradoEstudiantes;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.VinculosEstudiantesArtefacto;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.EstudiantesNoVinculadosArtefactoRule;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.EstudiantesVinculadosProyectoGradoRule;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl.EstudiantesNoVinculadosArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl.EstudiantesVinculadosProyectoGradoRuleImpl;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionRepositorioArtefactoExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AsignarEstudiantesArtefactoValidatorImpl implements AsignarEstudiantesArtefactoValidator {

    private final ArtefactoExisteRule artefactoExisteRule;
    private final AsesorExisteRule asesorExisteRule;
    private final VersionRepositorioArtefactoExisteRule versionRepositorioArtefactoExisteRule;
    private final EstudiantesExistenRule estudiantesExistenRule;
    private final AsesorPropietarioArtefactoRule asesorPropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final EstudiantesVigentesRule estudiantesVigentesRule;
    private final EstudiantesVinculadosProyectoGradoRule estudiantesVinculadosProyectoGradoRule;
    private final EstudiantesNoVinculadosArtefactoRule estudiantesNoVinculadosArtefactoRule;

    public AsignarEstudiantesArtefactoValidatorImpl() {
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.versionRepositorioArtefactoExisteRule = new VersionRepositorioArtefactoExisteRuleImpl();
        this.estudiantesExistenRule = new EstudiantesExistenRuleImpl();
        this.asesorPropietarioArtefactoRule = new AsesorPropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.estudiantesVigentesRule = new EstudiantesVigentesRuleImpl();
        this.estudiantesVinculadosProyectoGradoRule = new EstudiantesVinculadosProyectoGradoRuleImpl();
        this.estudiantesNoVinculadosArtefactoRule = new EstudiantesNoVinculadosArtefactoRuleImpl();
    }

    @Override
    public void validar(
            AgregacionEstudiantesArtefactoDomain entrada,
            ArtefactoDomain artefacto,
            AsesorDomain asesor,
            VersionRepositorioArtefactoDomain versionRepositorio,
            List<EstudianteDomain> estudiantes,
            List<UUID> pertenencias,
            List<UUID> yaVinculados) {
        artefactoExisteRule.validar(new ExistenciaArtefacto(entrada.getArtefacto(), artefacto));
        asesorExisteRule.validar(new ExistenciaAsesor(entrada.getAsesor(), asesor));
        versionRepositorioArtefactoExisteRule.validar(new ExistenciaVersionRepositorioArtefacto(
                artefacto.getVersionRepositorioArtefacto(), versionRepositorio));
        estudiantesExistenRule.validar(new ExistenciaEstudiantes(entrada.getEstudiantes(), estudiantes));
        asesorPropietarioArtefactoRule.validar(new PropiedadArtefactoAsesor(entrada.getAsesor(), artefacto));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(artefacto));
        estudiantesVigentesRule.validar(new VigenciaEstudiantes(estudiantes));
        estudiantesVinculadosProyectoGradoRule.validar(
                new PertenenciaProyectoGradoEstudiantes(estudiantes, pertenencias));
        estudiantesNoVinculadosArtefactoRule.validar(new VinculosEstudiantesArtefacto(estudiantes, yaVinculados));
    }
}
