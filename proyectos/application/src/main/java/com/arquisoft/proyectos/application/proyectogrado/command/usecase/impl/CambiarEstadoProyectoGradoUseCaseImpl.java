package com.arquisoft.proyectos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesoresAsignadosDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesorAsignadoProyectoGradoFinder;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.finder.UltimoEntregableProyectoGradoFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.EstadoActualProyectoGradoFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.EstadoProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.mapper.EstadoProyectoGradoMapper;
import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantesVigentesDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.CambiarEstadoProyectoGradoUseCase;
import com.arquisoft.proyectos.application.proyectogrado.command.validator.CambiarEstadoProyectoGradoValidator;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.proyectos.domain.proyectogrado.CambioEstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.event.ProyectoGradoEstadoCambiadoEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarEstadoProyectoGradoUseCaseImpl implements CambiarEstadoProyectoGradoUseCase {

    private final EstadoProyectoGradoOutputPort estadoProyectoGradoOutputPort;
    private final ProyectoGradoBloqueadoPorIdFinder proyectoGradoBloqueadoPorIdFinder;
    private final EstadoActualProyectoGradoFinder estadoActualProyectoGradoFinder;
    private final AsesorAsignadoProyectoGradoFinder asesorAsignadoProyectoGradoFinder;
    private final UltimoEntregableProyectoGradoFinder ultimoEntregableProyectoGradoFinder;
    private final EstudiantesVigentesDeProyectoGradoFinder estudiantesVigentesDeProyectoGradoFinder;
    private final AsesoresAsignadosDeProyectoGradoFinder asesoresAsignadosDeProyectoGradoFinder;
    private final CambiarEstadoProyectoGradoValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoProyectoGradoDomain entrada) {
        logger.info(ProyectoGradoKey.LOG_CAMBIANDO_ESTADO,
                entrada.getProyectoGrado(), entrada.getAsesor(), entrada.getEstadoNuevo().getId());

        var proyecto = proyectoGradoBloqueadoPorIdFinder.obtener(entrada.getProyectoGrado());
        var estadoActual = estadoActualProyectoGradoFinder.obtener(entrada.getProyectoGrado());
        var asesorAsignado = asesorAsignadoProyectoGradoFinder.obtener(entrada);
        var ultimoEntregable = ultimoEntregableProyectoGradoFinder.obtener(entrada.getProyectoGrado());
        var estudiantes = estudiantesVigentesDeProyectoGradoFinder.obtener(entrada.getProyectoGrado());
        var asesores = asesoresAsignadosDeProyectoGradoFinder.obtener(entrada.getProyectoGrado());

        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_CAMBIO_ESTADO,
                !proyecto.esVacio(), !estadoActual.esVacio(), asesorAsignado,
                !ultimoEntregable.esVacio(), estudiantes.size(), asesores.size());

        validator.validar(entrada, proyecto, estadoActual, asesorAsignado, ultimoEntregable);

        var nuevoEstado = EstadoProyectoGradoDomain.crearConEstado(
                entrada.getProyectoGrado(), entrada.getEstadoNuevo());
        estadoProyectoGradoOutputPort.registrar(EstadoProyectoGradoMapper.toEntity(nuevoEstado));

        var contactos = estudiantes.stream()
                .map(estudiante -> new ContactoEstudiante(
                        estudiante.getId(), estudiante.getNombre(), estudiante.getEmail()))
                .toList();
        var contactosAsesores = asesores.stream()
                .map(asesor -> new ContactoAsesor(asesor.getNombre(), asesor.getEmail()))
                .toList();
        eventPublisher.publish(new ProyectoGradoEstadoCambiadoEvent(
                proyecto.getId(), proyecto.getTituloProyecto(), estadoActual.getEstadoProyecto().getId(),
                entrada.getEstadoNuevo().getId(), UtilTexto.VACIO, contactos, contactosAsesores));

        logger.info(ProyectoGradoKey.LOG_ESTADO_CAMBIADO,
                entrada.getProyectoGrado(), entrada.getAsesor(), entrada.getEstadoNuevo().getId());
    }
}
