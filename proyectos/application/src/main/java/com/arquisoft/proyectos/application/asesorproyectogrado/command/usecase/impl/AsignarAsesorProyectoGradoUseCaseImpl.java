package com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesorYaAsignadoFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesoresAsignadosContadorFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.mapper.AsesorProyectoGradoMapper;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.AsignarAsesorProyectoGradoUseCase;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.AsignarAsesorProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsignacionAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.event.AsesorProyectoGradoAsignadoEvent;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarAsesorProyectoGradoUseCaseImpl implements AsignarAsesorProyectoGradoUseCase {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final AsesorYaAsignadoFinder asesorYaAsignadoFinder;
    private final AsesoresAsignadosContadorFinder asesoresAsignadosContadorFinder;
    private final AsignarAsesorProyectoGradoValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AsignacionAsesorProyectoGradoDomain entrada) {
        logger.info(AsesorProyectoGradoKey.LOG_ASIGNANDO, entrada.getProyectoGrado(), entrada.getAsesor());

        var proyecto = proyectoGradoPorIdFinder.obtener(entrada.getProyectoGrado());
        var asesor = asesorPorIdFinder.obtener(entrada.getAsesor());
        var yaAsignado = asesorYaAsignadoFinder.obtener(entrada);
        var asignadosActuales = asesoresAsignadosContadorFinder.obtener(entrada.getProyectoGrado());

        logger.debug(AsesorProyectoGradoKey.LOG_VERIFICACION_ASIGNAR,
                !proyecto.esVacio(), !asesor.esVacio() && !asesor.estaEliminado(), yaAsignado, asignadosActuales);

        validator.validar(entrada, proyecto, asesor, yaAsignado, asignadosActuales);

        var vinculo = entrada.getAsesorProyectoGrado();
        asesorProyectoGradoOutputPort.registrar(AsesorProyectoGradoMapper.toEntity(vinculo));

        eventPublisher.publish(new AsesorProyectoGradoAsignadoEvent(
                proyecto.getId(), proyecto.getTituloProyecto(),
                new ContactoAsesor(asesor.getNombre(), asesor.getEmail())));

        logger.info(AsesorProyectoGradoKey.LOG_ASIGNADO, entrada.getProyectoGrado(), entrada.getAsesor());

        return vinculo.getId();
    }
}
