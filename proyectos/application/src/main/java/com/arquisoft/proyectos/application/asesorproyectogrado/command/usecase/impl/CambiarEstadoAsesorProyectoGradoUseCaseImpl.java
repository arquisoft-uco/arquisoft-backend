package com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesorProyectoGradoVigenteFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.mapper.AsesorProyectoGradoMapper;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.CambiarEstadoAsesorProyectoGradoUseCase;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.CambiarEstadoAsesorProyectoGradoValidator;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.EstadoActualProyectoGradoFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.CambioEstadoAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.event.AsesorProyectoGradoFinalizadoEvent;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarEstadoAsesorProyectoGradoUseCaseImpl implements CambiarEstadoAsesorProyectoGradoUseCase {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final EstadoActualProyectoGradoFinder estadoActualProyectoGradoFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final AsesorProyectoGradoVigenteFinder asesorProyectoGradoVigenteFinder;
    private final CambiarEstadoAsesorProyectoGradoValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoAsesorProyectoGradoDomain entrada) {
        logger.info(AsesorProyectoGradoKey.LOG_CAMBIANDO_ESTADO,
                entrada.getProyectoGrado(), entrada.getAsesor(), entrada.getEstadoNuevo().getId());

        var proyecto = proyectoGradoPorIdFinder.obtener(entrada.getProyectoGrado());
        var estadoActual = estadoActualProyectoGradoFinder.obtener(entrada.getProyectoGrado());
        var asesor = asesorPorIdFinder.obtener(entrada.getAsesor());
        var vigente = asesorProyectoGradoVigenteFinder.obtener(entrada);

        logger.debug(AsesorProyectoGradoKey.LOG_VERIFICACION_CAMBIO_ESTADO,
                !proyecto.esVacio(), !estadoActual.esVacio(), !asesor.esVacio(), !vigente.esVacio());

        validator.validar(entrada, proyecto, estadoActual, asesor, vigente);

        var nuevoEstado = AsesorProyectoGradoDomain.crearConEstado(
                entrada.getAsesor(), entrada.getProyectoGrado(), entrada.getEstadoNuevo());
        asesorProyectoGradoOutputPort.registrar(AsesorProyectoGradoMapper.toEntity(nuevoEstado));

        eventPublisher.publish(new AsesorProyectoGradoFinalizadoEvent(
                proyecto.getId(), proyecto.getTituloProyecto(), asesor.getId(),
                new ContactoAsesor(asesor.getNombre(), asesor.getEmail())));

        logger.info(AsesorProyectoGradoKey.LOG_ESTADO_CAMBIADO,
                entrada.getProyectoGrado(), entrada.getAsesor(), entrada.getEstadoNuevo().getId());
    }
}
