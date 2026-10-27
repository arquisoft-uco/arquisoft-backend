package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.application.artefacto.command.validator.AsignarArtefactosValidator;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactosDomain;
import com.arquisoft.artefactos.domain.artefacto.model.DisponibilidadRepositoriosProyecto;
import com.arquisoft.artefactos.domain.artefacto.rules.RepositorioArtefactoUnicoEnProyectoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.RepositorioArtefactoUnicoEnProyectoRuleImpl;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorExisteRule;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.artefactos.domain.asesorproyectogrado.rules.AsesorAsignadoProyectoGradoRule;
import com.arquisoft.artefactos.domain.asesorproyectogrado.rules.impl.AsesorAsignadoProyectoGradoRuleImpl;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.artefactos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionesRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionesRepositorioArtefactoExistenRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionesRepositorioArtefactoExistenRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AsignarArtefactosValidatorImpl implements AsignarArtefactosValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final AsesorExisteRule asesorExisteRule;
    private final AsesorAsignadoProyectoGradoRule asesorAsignadoProyectoGradoRule;
    private final VersionesRepositorioArtefactoExistenRule versionesRepositorioArtefactoExistenRule;
    private final RepositorioArtefactoUnicoEnProyectoRule repositorioArtefactoUnicoEnProyectoRule;

    public AsignarArtefactosValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.asesorAsignadoProyectoGradoRule = new AsesorAsignadoProyectoGradoRuleImpl();
        this.versionesRepositorioArtefactoExistenRule = new VersionesRepositorioArtefactoExistenRuleImpl();
        this.repositorioArtefactoUnicoEnProyectoRule = new RepositorioArtefactoUnicoEnProyectoRuleImpl();
    }

    @Override
    public void validar(
            AsignacionArtefactosDomain asignacion,
            boolean proyectoExiste,
            AsesorDomain asesor,
            List<AsesorProyectoGradoDomain> vinculosMasRecientes,
            List<VersionRepositorioArtefactoDomain> versiones,
            List<UUID> repositoriosAsignados) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(asignacion.getProyectoGrado(), proyectoExiste));
        asesorExisteRule.validar(new ExistenciaAsesor(asignacion.getAsesor(), asesor));
        asesorAsignadoProyectoGradoRule.validar(new VigenciaAsesorProyectoGrado(
                asignacion.getAsesor(), asesor.getNombre(), asignacion.getProyectoGrado(), vinculosMasRecientes));
        versionesRepositorioArtefactoExistenRule.validar(new ExistenciaVersionesRepositorioArtefacto(
                asignacion.getVersionesRepositorioArtefacto(), versiones));
        repositorioArtefactoUnicoEnProyectoRule.validar(
                new DisponibilidadRepositoriosProyecto(versiones, repositoriosAsignados));
    }
}
