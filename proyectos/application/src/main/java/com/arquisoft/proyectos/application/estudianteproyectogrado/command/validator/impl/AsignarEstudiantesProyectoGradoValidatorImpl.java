package com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.AsignarEstudiantesProyectoGradoValidator;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.CupoEstudiantesProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.VigenciaEstudiantesProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.VinculosEstudiantesProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudianteProyectoGradoCupoDisponibleRule;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudiantesSinDuplicadosRule;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudiantesNoVinculadosRule;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudiantesVigentesProyectoGradoRule;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl.EstudianteProyectoGradoCupoDisponibleRuleImpl;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl.EstudiantesSinDuplicadosRuleImpl;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl.EstudiantesNoVinculadosRuleImpl;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl.EstudiantesVigentesProyectoGradoRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.model.PropiedadCoordinadorProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.CoordinadorProyectoGradoPropietarioRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoNoFinalizadoRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.CoordinadorProyectoGradoPropietarioRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoNoFinalizadoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AsignarEstudiantesProyectoGradoValidatorImpl implements AsignarEstudiantesProyectoGradoValidator {

    private final EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule;
    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final CoordinadorProyectoGradoPropietarioRule coordinadorPropietarioRule;
    private final EstudiantesVigentesProyectoGradoRule estudiantesVigentesRule;
    private final EstudiantesNoVinculadosRule estudiantesNoVinculadosRule;
    private final ProyectoGradoNoFinalizadoRule proyectoGradoNoFinalizadoRule;
    private final EstudianteProyectoGradoCupoDisponibleRule cupoDisponibleRule;

    public AsignarEstudiantesProyectoGradoValidatorImpl() {
        this.estudiantesSinDuplicadosRule = new EstudiantesSinDuplicadosRuleImpl();
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.coordinadorPropietarioRule = new CoordinadorProyectoGradoPropietarioRuleImpl();
        this.estudiantesVigentesRule = new EstudiantesVigentesProyectoGradoRuleImpl();
        this.estudiantesNoVinculadosRule = new EstudiantesNoVinculadosRuleImpl();
        this.proyectoGradoNoFinalizadoRule = new ProyectoGradoNoFinalizadoRuleImpl();
        this.cupoDisponibleRule = new EstudianteProyectoGradoCupoDisponibleRuleImpl();
    }

    @Override
    public void validar(AgregacionEstudiantesProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                        List<EstudianteDomain> estudiantesVigentes, List<UUID> yaVinculados, long vinculadosActuales) {
        estudiantesSinDuplicadosRule.validar(entrada.getEstudiantes());

        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(entrada.getProyectoGrado(), !proyecto.esVacio()));
        coordinadorPropietarioRule.validar(new PropiedadCoordinadorProyectoGrado(
                entrada.getProyectoGrado(), proyecto.getCoordinador(), entrada.getCoordinador()));
        estudiantesVigentesRule.validar(new VigenciaEstudiantesProyectoGrado(
                new HashSet<>(entrada.getEstudiantes()),
                estudiantesVigentes.stream().map(EstudianteDomain::getId).collect(Collectors.toSet())));

        estudiantesNoVinculadosRule.validar(new VinculosEstudiantesProyectoGrado(yaVinculados));
        proyectoGradoNoFinalizadoRule.validar(
                new EstadoActualProyectoGrado(entrada.getProyectoGrado(), proyecto.getEstadoProyectoGrado()));
        cupoDisponibleRule.validar(new CupoEstudiantesProyectoGrado(vinculadosActuales, entrada.getCantidad()));
    }
}
