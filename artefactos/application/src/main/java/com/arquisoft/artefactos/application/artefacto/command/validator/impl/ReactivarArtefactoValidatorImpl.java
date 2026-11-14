package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.application.artefacto.command.validator.ReactivarArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.ReactivacionArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.PesoArtefactoReactivable;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoExisteRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoExisteRuleImpl;
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
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoNoCongeladoRule;
import com.arquisoft.artefactos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.artefactos.domain.proyectogrado.rules.impl.ProyectoGradoNoCongeladoRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.model.AlmacenamientoProyectoGrado;
import com.arquisoft.artefactos.domain.versionartefacto.model.CuotaProyectoGrado;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CuotaProyectoGradoNoExcedidaRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.CuotaProyectoGradoNoExcedidaRuleImpl;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionRepositorioArtefactoExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReactivarArtefactoValidatorImpl implements ReactivarArtefactoValidator {

    private final AsesorExisteRule asesorExisteRule;
    private final AsesorAsignadoProyectoGradoRule asesorAsignadoProyectoGradoRule;
    private final EstudiantesDentroDelProyectoRule estudiantesDentroDelProyectoRule;
    private final ArtefactoExisteRule artefactoExisteRule;
    private final VersionRepositorioArtefactoExisteRule versionRepositorioArtefactoExisteRule;
    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final ProyectoGradoNoCongeladoRule proyectoGradoNoCongeladoRule;
    private final CuotaProyectoGradoNoExcedidaRule cuotaProyectoGradoNoExcedidaRule;

    public ReactivarArtefactoValidatorImpl() {
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.asesorAsignadoProyectoGradoRule = new AsesorAsignadoProyectoGradoRuleImpl();
        this.estudiantesDentroDelProyectoRule = new EstudiantesDentroDelProyectoRuleImpl();
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.versionRepositorioArtefactoExisteRule = new VersionRepositorioArtefactoExisteRuleImpl();
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.proyectoGradoNoCongeladoRule = new ProyectoGradoNoCongeladoRuleImpl();
        this.cuotaProyectoGradoNoExcedidaRule = new CuotaProyectoGradoNoExcedidaRuleImpl();
    }

    @Override
    public void validar(
            ReactivacionArtefactoDomain reactivacion,
            AsesorDomain asesor,
            List<AsesorProyectoGradoDomain> vinculosMasRecientes,
            CupoEstudiantesArtefacto cupo,
            PesoArtefactoReactivable peso,
            AlmacenamientoProyectoGrado almacenamiento) {
        asesorExisteRule.validar(new ExistenciaAsesor(reactivacion.getAsesor(), asesor));
        asesorAsignadoProyectoGradoRule.validar(new VigenciaAsesorProyectoGrado(
                reactivacion.getAsesor(), asesor.getNombre(), reactivacion.getProyectoGrado(), vinculosMasRecientes));
        estudiantesDentroDelProyectoRule.validar(cupo);
        artefactoExisteRule.validar(new ExistenciaArtefacto(reactivacion.getArtefacto(), peso.artefacto()));
        versionRepositorioArtefactoExisteRule.validar(new ExistenciaVersionRepositorioArtefacto(
                peso.artefacto().getVersionRepositorioArtefacto(), peso.versionRepositorio()));
        var proyecto = almacenamiento.proyectoGrado();
        proyectoGradoExisteRule.validar(
                new ExistenciaProyectoGrado(reactivacion.getProyectoGrado(), !proyecto.esVacio()));
        proyectoGradoNoCongeladoRule.validar(new EstadoProyectoGrado(
                reactivacion.getProyectoGrado(), proyecto.getNombre(), proyecto.estaCongelado(), proyecto.estaConcluido()));
        cuotaProyectoGradoNoExcedidaRule.validar(new CuotaProyectoGrado(
                reactivacion.getProyectoGrado(), proyecto.getNombre(), peso.versionRepositorio().getNombreDocumento(),
                almacenamiento.cuotaBytes(), almacenamiento.usoBytes(), peso.pesoVigenteBytes()));
    }
}
