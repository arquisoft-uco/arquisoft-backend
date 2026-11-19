package com.arquisoft.proyectos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesoresAsignadosDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.EstadoActualProyectoGradoFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.EstadoProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.mapper.EstadoProyectoGradoMapper;
import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantesVigentesDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.result.ReanudacionProyectoGradoResult;
import com.arquisoft.proyectos.application.proyectogrado.command.result.mapper.ReanudacionProyectoGradoResultMapper;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.ReanudarProyectoGradoUseCase;
import com.arquisoft.proyectos.application.proyectogrado.command.validator.ReanudarProyectoGradoValidator;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.proyectos.domain.coordinador.model.ContactoCoordinador;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.proyectos.domain.proyectogrado.ReanudacionProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.event.ProyectoGradoEstadoCambiadoEvent;
import com.arquisoft.proyectos.domain.proyectogrado.model.DestinatariosEstadoProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReanudarProyectoGradoUseCaseImpl implements ReanudarProyectoGradoUseCase {

    private final EstadoProyectoGradoOutputPort estadoProyectoGradoOutputPort;
    private final ProyectoGradoBloqueadoPorIdFinder proyectoGradoBloqueadoPorIdFinder;
    private final EstadoActualProyectoGradoFinder estadoActualProyectoGradoFinder;
    private final CoordinadorPorIdFinder coordinadorPorIdFinder;
    private final EstudiantesVigentesDeProyectoGradoFinder estudiantesVigentesDeProyectoGradoFinder;
    private final AsesoresAsignadosDeProyectoGradoFinder asesoresAsignadosDeProyectoGradoFinder;
    private final ReanudarProyectoGradoValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public ReanudacionProyectoGradoResult ejecutar(ReanudacionProyectoGradoDomain reanudacion) {
        var proyecto = proyectoGradoBloqueadoPorIdFinder.obtener(reanudacion.getProyectoGrado());
        var estadoActual = estadoActualProyectoGradoFinder.obtener(reanudacion.getProyectoGrado());
        var coordinador = coordinadorPorIdFinder.obtener(proyecto.getCoordinador());
        var estudiantes = estudiantesVigentesDeProyectoGradoFinder.obtener(reanudacion.getProyectoGrado());
        var asesores = asesoresAsignadosDeProyectoGradoFinder.obtener(reanudacion.getProyectoGrado());
        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_REANUDACION,
                !proyecto.esVacio(), estadoActual.getEstadoProyecto().getId(), !coordinador.esVacio(),
                estudiantes.size(), asesores.size());

        validator.validar(reanudacion, proyecto, estadoActual, coordinador);

        if (!estadoActual.estaAtrasado() || !reanudacion.plazoVigente()) {
            return ReanudacionProyectoGradoResultMapper.toResultOmitido(reanudacion, estadoActual.getEstadoProyecto().getId());
        }

        var nuevoEstado = EstadoProyectoGradoDomain.crearConEstado(reanudacion.getProyectoGrado(), EstadoProyecto.EN_PROCESO);
        estadoProyectoGradoOutputPort.registrar(EstadoProyectoGradoMapper.toEntity(nuevoEstado));

        var contactoCoordinador = new ContactoCoordinador(
                coordinador.getId(), coordinador.getNombre(), coordinador.getEmail());
        var contactosEstudiantes = estudiantes.stream()
                .map(estudiante -> new ContactoEstudiante(
                        estudiante.getId(), estudiante.getNombre(), estudiante.getEmail()))
                .toList();
        var contactosAsesores = asesores.stream()
                .map(asesor -> new ContactoAsesor(asesor.getNombre(), asesor.getEmail()))
                .toList();
        eventPublisher.publish(new ProyectoGradoEstadoCambiadoEvent(
                proyecto.getId(), proyecto.getTituloProyecto(), estadoActual.getEstadoProyecto().getId(),
                EstadoProyecto.EN_PROCESO.getId(), UtilTexto.VACIO,
                new DestinatariosEstadoProyectoGrado(contactoCoordinador, contactosEstudiantes, contactosAsesores)));

        return ReanudacionProyectoGradoResultMapper.toResultReanudado(reanudacion);
    }
}
