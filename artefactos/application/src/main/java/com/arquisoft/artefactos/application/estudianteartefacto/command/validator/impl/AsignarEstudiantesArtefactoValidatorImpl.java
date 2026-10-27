package com.arquisoft.artefactos.application.estudianteartefacto.command.validator.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.validator.AsignarEstudiantesArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefactos;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefactosAsesor;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactosExistenRule;
import com.arquisoft.artefactos.domain.artefacto.rules.AsesorPropietarioArtefactosRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactosExistenRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.AsesorPropietarioArtefactosRuleImpl;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorExisteRule;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorVigenteRule;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorVigenteRuleImpl;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudiante.model.ExistenciaEstudiantes;
import com.arquisoft.artefactos.domain.estudiante.model.VigenciaEstudiantes;
import com.arquisoft.artefactos.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.artefactos.domain.estudiante.rules.EstudiantesVigentesRule;
import com.arquisoft.artefactos.domain.estudiante.rules.impl.EstudiantesExistenRuleImpl;
import com.arquisoft.artefactos.domain.estudiante.rules.impl.EstudiantesVigentesRuleImpl;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ParEstudianteArtefacto;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.PertenenciaProyectoGradoEstudiantes;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.VinculosEstudiantesArtefacto;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.EstudiantesNoVinculadosArtefactoRule;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.EstudiantesVinculadosProyectoGradoRule;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl.EstudiantesNoVinculadosArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl.EstudiantesVinculadosProyectoGradoRuleImpl;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionesRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionesRepositorioArtefactoExistenRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionesRepositorioArtefactoExistenRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AsignarEstudiantesArtefactoValidatorImpl implements AsignarEstudiantesArtefactoValidator {

    private final ArtefactosExistenRule artefactosExistenRule;
    private final AsesorExisteRule asesorExisteRule;
    private final AsesorVigenteRule asesorVigenteRule;
    private final VersionesRepositorioArtefactoExistenRule versionesRepositorioArtefactoExistenRule;
    private final EstudiantesExistenRule estudiantesExistenRule;
    private final AsesorPropietarioArtefactosRule asesorPropietarioArtefactosRule;
    private final EstudiantesVigentesRule estudiantesVigentesRule;
    private final EstudiantesVinculadosProyectoGradoRule estudiantesVinculadosProyectoGradoRule;
    private final EstudiantesNoVinculadosArtefactoRule estudiantesNoVinculadosArtefactoRule;

    public AsignarEstudiantesArtefactoValidatorImpl() {
        this.artefactosExistenRule = new ArtefactosExistenRuleImpl();
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.asesorVigenteRule = new AsesorVigenteRuleImpl();
        this.versionesRepositorioArtefactoExistenRule = new VersionesRepositorioArtefactoExistenRuleImpl();
        this.estudiantesExistenRule = new EstudiantesExistenRuleImpl();
        this.asesorPropietarioArtefactosRule = new AsesorPropietarioArtefactosRuleImpl();
        this.estudiantesVigentesRule = new EstudiantesVigentesRuleImpl();
        this.estudiantesVinculadosProyectoGradoRule = new EstudiantesVinculadosProyectoGradoRuleImpl();
        this.estudiantesNoVinculadosArtefactoRule = new EstudiantesNoVinculadosArtefactoRuleImpl();
    }

    @Override
    public void validar(
            AgregacionEstudiantesArtefactoDomain entrada,
            List<ArtefactoDomain> artefactos,
            AsesorDomain asesor,
            List<VersionRepositorioArtefactoDomain> versiones,
            List<EstudianteDomain> estudiantes,
            List<ParEstudianteArtefacto> pertenencias,
            List<ParEstudianteArtefacto> yaVinculados) {
        artefactosExistenRule.validar(new ExistenciaArtefactos(entrada.getArtefactos(), artefactos));
        asesorExisteRule.validar(new ExistenciaAsesor(entrada.getAsesor(), asesor));
        asesorVigenteRule.validar(new VigenciaAsesor(entrada.getAsesor(), asesor));
        versionesRepositorioArtefactoExistenRule.validar(new ExistenciaVersionesRepositorioArtefacto(
                artefactos.stream().map(ArtefactoDomain::getVersionRepositorioArtefacto).toList(), versiones));
        estudiantesExistenRule.validar(new ExistenciaEstudiantes(entrada.getEstudiantes(), estudiantes));
        asesorPropietarioArtefactosRule.validar(new PropiedadArtefactosAsesor(entrada.getAsesor(), artefactos));
        estudiantesVigentesRule.validar(new VigenciaEstudiantes(estudiantes));
        estudiantesVinculadosProyectoGradoRule.validar(
                new PertenenciaProyectoGradoEstudiantes(estudiantes, entrada.getPares(), pertenencias));
        estudiantesNoVinculadosArtefactoRule.validar(new VinculosEstudiantesArtefacto(estudiantes, yaVinculados));
    }
}
