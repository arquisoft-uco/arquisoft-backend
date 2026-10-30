package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.application.artefacto.command.validator.RegistrarArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.DisponibilidadRepositorioProyecto;
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
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionRepositorioArtefactoExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RegistrarArtefactoValidatorImpl implements RegistrarArtefactoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final AsesorExisteRule asesorExisteRule;
    private final AsesorAsignadoProyectoGradoRule asesorAsignadoProyectoGradoRule;
    private final VersionRepositorioArtefactoExisteRule versionRepositorioArtefactoExisteRule;
    private final RepositorioArtefactoUnicoEnProyectoRule repositorioArtefactoUnicoEnProyectoRule;

    public RegistrarArtefactoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.asesorAsignadoProyectoGradoRule = new AsesorAsignadoProyectoGradoRuleImpl();
        this.versionRepositorioArtefactoExisteRule = new VersionRepositorioArtefactoExisteRuleImpl();
        this.repositorioArtefactoUnicoEnProyectoRule = new RepositorioArtefactoUnicoEnProyectoRuleImpl();
    }

    @Override
    public void validar(
            AsignacionArtefactoDomain registro,
            boolean proyectoExiste,
            AsesorDomain asesor,
            List<AsesorProyectoGradoDomain> vinculosMasRecientes,
            VersionRepositorioArtefactoDomain versionRepositorio,
            List<UUID> repositoriosAsignados) {
        var artefacto = registro.getArtefacto();
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(artefacto.getProyectoGrado(), proyectoExiste));
        asesorExisteRule.validar(new ExistenciaAsesor(artefacto.getAsesor(), asesor));
        asesorAsignadoProyectoGradoRule.validar(new VigenciaAsesorProyectoGrado(
                artefacto.getAsesor(), asesor.getNombre(), artefacto.getProyectoGrado(), vinculosMasRecientes));
        versionRepositorioArtefactoExisteRule.validar(new ExistenciaVersionRepositorioArtefacto(
                artefacto.getVersionRepositorioArtefacto(), versionRepositorio));
        repositorioArtefactoUnicoEnProyectoRule.validar(
                new DisponibilidadRepositorioProyecto(versionRepositorio, repositoriosAsignados));
    }
}
