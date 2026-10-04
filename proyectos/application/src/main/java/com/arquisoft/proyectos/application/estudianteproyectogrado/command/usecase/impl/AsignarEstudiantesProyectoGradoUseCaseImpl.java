package com.arquisoft.proyectos.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantesVigentesPorIdsFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.EstudiantesVinculadosContadorFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.usecase.AsignarEstudiantesProyectoGradoUseCase;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.AsignarEstudiantesProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.event.EstudiantesProyectoGradoAsignadosEvent;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AsignarEstudiantesProyectoGradoUseCaseImpl implements AsignarEstudiantesProyectoGradoUseCase {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final EstudiantesVigentesPorIdsFinder estudiantesVigentesPorIdsFinder;
    private final EstudiantesVinculadosContadorFinder estudiantesVinculadosContadorFinder;
    private final AsignarEstudiantesProyectoGradoValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(AgregacionEstudiantesProyectoGradoDomain entrada) {
        logger.info(EstudianteProyectoGradoKey.LOG_ASIGNANDO, entrada.getProyectoGrado(), entrada.getCantidad());

        var proyecto = proyectoGradoPorIdFinder.obtener(entrada.getProyectoGrado());
        var vigentes = estudiantesVigentesPorIdsFinder.obtener(entrada.getEstudiantes());
        var vinculadosActuales = estudiantesVinculadosContadorFinder.obtener(entrada.getProyectoGrado());

        logger.debug(EstudianteProyectoGradoKey.LOG_VERIFICACION_ASIGNAR,
                !proyecto.esVacio(), vigentes.size(), vinculadosActuales);

        validator.validar(entrada, proyecto, vigentes, vinculadosActuales);

        estudianteProyectoGradoOutputPort.vincular(
                entrada.getRelaciones().stream().map(EstudianteProyectoGradoMapper::toEntity).toList());

        eventPublisher.publish(new EstudiantesProyectoGradoAsignadosEvent(
                proyecto.getId(), proyecto.getTituloProyecto(), contactos(vigentes)));

        logger.info(EstudianteProyectoGradoKey.LOG_ASIGNADOS, entrada.getProyectoGrado(), entrada.getCantidad());
    }

    private static List<ContactoEstudiante> contactos(List<EstudianteDomain> estudiantes) {
        return estudiantes.stream()
                .map(estudiante -> new ContactoEstudiante(estudiante.getNombre(), estudiante.getEmail()))
                .toList();
    }
}
