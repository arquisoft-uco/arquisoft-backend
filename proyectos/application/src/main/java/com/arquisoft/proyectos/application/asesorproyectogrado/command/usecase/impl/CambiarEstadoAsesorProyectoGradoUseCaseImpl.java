package com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesorProyectoGradoPorIdYProyectoFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.CambiarEstadoAsesorProyectoGradoUseCase;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.CambiarEstadoAsesorProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
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
    private final AsesorProyectoGradoPorIdYProyectoFinder asesorProyectoGradoPorIdYProyectoFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final CambiarEstadoAsesorProyectoGradoValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoAsesorProyectoGradoDomain entrada) {
        logger.info(AsesorProyectoGradoKey.LOG_CAMBIANDO_ESTADO,
                entrada.getProyectoGrado(), entrada.getAsesorProyectoGrado(), entrada.getEstadoNuevo().getId());

        var proyecto = proyectoGradoPorIdFinder.obtener(entrada.getProyectoGrado());
        var vinculo = asesorProyectoGradoPorIdYProyectoFinder.obtener(entrada);

        var asesor = asesorPorIdFinder.obtener(vinculo.getAsesor());

        logger.debug(AsesorProyectoGradoKey.LOG_VERIFICACION_CAMBIO_ESTADO,
                !proyecto.esVacio(), !vinculo.esVacio(), !asesor.esVacio());

        validator.validar(entrada, proyecto, vinculo, asesor);

        asesorProyectoGradoOutputPort.actualizarEstado(vinculo.getId(), entrada.getEstadoNuevo().getId());

        eventPublisher.publish(new AsesorProyectoGradoFinalizadoEvent(
                proyecto.getId(), proyecto.getTituloProyecto(),
                new ContactoAsesor(asesor.getNombre(), asesor.getEmail())));

        logger.info(AsesorProyectoGradoKey.LOG_ESTADO_CAMBIADO,
                entrada.getProyectoGrado(), entrada.getAsesorProyectoGrado(), entrada.getEstadoNuevo().getId());
    }
}
