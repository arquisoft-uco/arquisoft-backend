package com.arquisoft.proyectos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.usecase.AsignarEstudiantesProyectoGradoUseCase;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoDeFichaExisteFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.result.RegistroProyectoGradoResult;
import com.arquisoft.proyectos.application.proyectogrado.command.result.mapper.RegistroProyectoGradoResultMapper;
import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.mapper.ProyectoGradoMapper;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.RegistrarProyectoGradoUseCase;
import com.arquisoft.proyectos.application.proyectogrado.command.validator.RegistrarProyectoGradoValidator;
import com.arquisoft.proyectos.domain.coordinador.model.ContactoCoordinador;
import com.arquisoft.proyectos.domain.proyectogrado.RegistroProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.event.ProyectoGradoRegistradoEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarProyectoGradoUseCaseImpl implements RegistrarProyectoGradoUseCase {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;
    private final ProyectoGradoDeFichaExisteFinder proyectoGradoDeFichaExisteFinder;
    private final CoordinadorPorIdFinder coordinadorPorIdFinder;
    private final RegistrarProyectoGradoValidator validator;
    private final AsignarEstudiantesProyectoGradoUseCase asignarEstudiantesProyectoGradoUseCase;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public RegistroProyectoGradoResult ejecutar(RegistroProyectoGradoDomain registro) {
        var proyecto = registro.getProyecto();

        logger.info(ProyectoGradoKey.LOG_REGISTRANDO, proyecto.getFichaPerfil(), proyecto.getCoordinador());

        var yaRegistrado = proyectoGradoDeFichaExisteFinder.obtener(proyecto.getFichaPerfil());
        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_PREVIA, proyecto.getFichaPerfil(), yaRegistrado);

        if (yaRegistrado) {
            return RegistroProyectoGradoResultMapper.toResultDuplicado(proyecto);
        }

        var coordinador = coordinadorPorIdFinder.obtener(proyecto.getCoordinador());

        validator.validar(proyecto.getCoordinador(), coordinador);

        proyectoGradoOutputPort.registrar(ProyectoGradoMapper.toEntity(proyecto));

        asignarEstudiantesProyectoGradoUseCase.ejecutar(registro.getEstudiantes());

        eventPublisher.publish(new ProyectoGradoRegistradoEvent(
                proyecto.getId(), proyecto.getFichaPerfil(), proyecto.getTituloProyecto(),
                new ContactoCoordinador(coordinador.getNombre(), coordinador.getEmail())));

        logger.info(ProyectoGradoKey.LOG_REGISTRADO_CIERRE, proyecto.getId());

        return RegistroProyectoGradoResultMapper.toResultRegistrado(proyecto);
    }
}
