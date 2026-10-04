package com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.AsignarEstudiantesProyectoGradoValidator;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.CupoEstudiantesProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.VigenciaEstudiantesProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudianteProyectoGradoCupoDisponibleRule;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudiantesSinDuplicadosRule;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudiantesVigentesProyectoGradoRule;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl.EstudianteProyectoGradoCupoDisponibleRuleImpl;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl.EstudiantesSinDuplicadosRuleImpl;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl.EstudiantesVigentesProyectoGradoRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AsignarEstudiantesProyectoGradoValidatorImpl implements AsignarEstudiantesProyectoGradoValidator {

    private final EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule;
    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final EstudiantesVigentesProyectoGradoRule estudiantesVigentesRule;
    private final EstudianteProyectoGradoCupoDisponibleRule cupoDisponibleRule;

    public AsignarEstudiantesProyectoGradoValidatorImpl() {
        this.estudiantesSinDuplicadosRule = new EstudiantesSinDuplicadosRuleImpl();
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.estudiantesVigentesRule = new EstudiantesVigentesProyectoGradoRuleImpl();
        this.cupoDisponibleRule = new EstudianteProyectoGradoCupoDisponibleRuleImpl();
    }

    @Override
    public void validar(AgregacionEstudiantesProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                        List<EstudianteDomain> estudiantesVigentes, long vinculadosActuales) {
        estudiantesSinDuplicadosRule.validar(entrada.getEstudiantes());

        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(entrada.getProyectoGrado(), !proyecto.esVacio()));
        estudiantesVigentesRule.validar(new VigenciaEstudiantesProyectoGrado(
                new HashSet<>(entrada.getEstudiantes()),
                estudiantesVigentes.stream().map(EstudianteDomain::getId).collect(Collectors.toSet())));
        // TODO: validar que los estudiantes no estén ya vinculados al proyecto de grado
        //  cuando exista el endpoint de asignar estudiantes a un proyecto existente.

        // TODO: validar que el estado del proyecto de grado no sea FINALIZADO
        //  cuando exista el endpoint de asignar estudiantes a un proyecto existente.
        cupoDisponibleRule.validar(new CupoEstudiantesProyectoGrado(vinculadosActuales, entrada.getCantidad()));
    }
}
