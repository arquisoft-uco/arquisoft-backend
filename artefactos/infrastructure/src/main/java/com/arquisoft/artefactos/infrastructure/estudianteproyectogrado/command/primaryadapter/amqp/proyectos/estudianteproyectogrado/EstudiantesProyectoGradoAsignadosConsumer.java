package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.interactor.AgregarEstudiantesProyectoGradoInteractor;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosProyectosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilColeccion;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EstudiantesProyectoGradoAsignadosConsumer extends AbstractEventConsumer {

    private final AgregarEstudiantesProyectoGradoInteractor agregarEstudiantesProyectoGradoInteractor;
    private final AppLogger logger;

    public EstudiantesProyectoGradoAsignadosConsumer(
            AgregarEstudiantesProyectoGradoInteractor agregarEstudiantesProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.agregarEstudiantesProyectoGradoInteractor = agregarEstudiantesProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosProyectosQueueConfig.ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS_QUEUE)
    public void onEstudiantesProyectoGradoAsignados(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EstudiantesProyectoGradoAsignadosPayload.class);
            var estudiantes = UtilColeccion.aplicarPorDefecto(payload.estudiantes()).stream()
                    .map(EstudiantesProyectoGradoAsignadosPayload.EstudianteAsignado::id)
                    .toList();
            logger.info(EstudianteProyectoGradoKey.LOG_ASIGNADOS_RECIBIDO,
                    payload.idEvento(), payload.proyectoGradoId(), estudiantes.size(), payload.ocurridoEn());

            var resultado = agregarEstudiantesProyectoGradoInteractor.ejecutar(
                    AgregarEstudiantesProyectoGradoCommand.crear(
                            payload.proyectoGradoId(), estudiantes, payload.ocurridoEn()));

            logger.info(EstudianteProyectoGradoKey.LOG_AGREGADOS, resultado.proyectoGrado(),
                    resultado.agregados(), resultado.reactivados(), resultado.duplicados(),
                    resultado.descartados());
        });
    }
}
