package com.arquisoft.proyectos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesoresAsignadosDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.EstadoActualProyectoGradoFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.EstadoProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.mapper.EstadoProyectoGradoMapper;
import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantesVigentesDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.CancelarProyectoGradoUseCase;
import com.arquisoft.proyectos.application.proyectogrado.command.validator.CancelarProyectoGradoValidator;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.proyectos.domain.proyectogrado.CancelacionProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.event.ProyectoGradoCanceladoEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelarProyectoGradoUseCaseImpl implements CancelarProyectoGradoUseCase {

    private final EstadoProyectoGradoOutputPort estadoProyectoGradoOutputPort;
    private final ProyectoGradoBloqueadoPorIdFinder proyectoGradoBloqueadoPorIdFinder;
    private final EstadoActualProyectoGradoFinder estadoActualProyectoGradoFinder;
    private final EstudiantesVigentesDeProyectoGradoFinder estudiantesVigentesDeProyectoGradoFinder;
    private final AsesoresAsignadosDeProyectoGradoFinder asesoresAsignadosDeProyectoGradoFinder;
    private final CancelarProyectoGradoValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CancelacionProyectoGradoDomain entrada) {
        logger.info(ProyectoGradoKey.LOG_CANCELANDO, entrada.getProyectoGrado(), entrada.getCoordinador());

        var proyecto = proyectoGradoBloqueadoPorIdFinder.obtener(entrada.getProyectoGrado());
        var estadoActual = estadoActualProyectoGradoFinder.obtener(entrada.getProyectoGrado());
        var estudiantes = estudiantesVigentesDeProyectoGradoFinder.obtener(entrada.getProyectoGrado());
        var asesores = asesoresAsignadosDeProyectoGradoFinder.obtener(entrada.getProyectoGrado());

        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_CANCELACION,
                !proyecto.esVacio(), !estadoActual.esVacio(), estudiantes.size(), asesores.size());

        validator.validar(entrada, proyecto, estadoActual);

        var estadoCancelado = EstadoProyectoGradoDomain.crearCancelado(
                entrada.getProyectoGrado(), entrada.getMotivo());
        estadoProyectoGradoOutputPort.registrar(EstadoProyectoGradoMapper.toEntity(estadoCancelado));

        var contactosEstudiantes = estudiantes.stream()
                .map(estudiante -> new ContactoEstudiante(
                        estudiante.getId(), estudiante.getNombre(), estudiante.getEmail()))
                .toList();
        var contactosAsesores = asesores.stream()
                .map(asesor -> new ContactoAsesor(asesor.getNombre(), asesor.getEmail()))
                .toList();
        eventPublisher.publish(new ProyectoGradoCanceladoEvent(
                proyecto.getId(), proyecto.getTituloProyecto(), entrada.getMotivo(),
                contactosEstudiantes, contactosAsesores));

        logger.info(ProyectoGradoKey.LOG_CANCELADO, entrada.getProyectoGrado(), entrada.getCoordinador());
    }
}
