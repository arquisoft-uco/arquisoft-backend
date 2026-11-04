package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.interactor.DestituirEstudianteProyectoGradoInteractor;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model.DestituirEstudianteProyectoGradoCommand;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.DestitucionEstudianteProyectoGradoResult;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosProyectosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EstudianteProyectoGradoDestituidoConsumer extends AbstractEventConsumer {

    private final DestituirEstudianteProyectoGradoInteractor destituirEstudianteProyectoGradoInteractor;
    private final AppLogger logger;

    public EstudianteProyectoGradoDestituidoConsumer(
            DestituirEstudianteProyectoGradoInteractor destituirEstudianteProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.destituirEstudianteProyectoGradoInteractor = destituirEstudianteProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosProyectosQueueConfig.ESTUDIANTE_PROYECTO_GRADO_DESTITUIDO_QUEUE)
    public void onEstudianteProyectoGradoDestituido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EstudianteProyectoGradoDestituidoPayload.class);
            logger.info(EstudianteProyectoGradoKey.LOG_DESTITUIDO_RECIBIDO,
                    payload.idEvento(), payload.proyectoGradoId(), payload.estudianteId(), payload.ocurridoEn());

            var resultado = destituirEstudianteProyectoGradoInteractor.ejecutar(
                    DestituirEstudianteProyectoGradoCommand.crear(
                            payload.estudianteId(), payload.proyectoGradoId(), payload.ocurridoEn()));

            switch (resultado) {
                case DestitucionEstudianteProyectoGradoResult.Destituida destituida ->
                        logger.info(EstudianteProyectoGradoKey.LOG_DESTITUIDO,
                                destituida.estudiante(), destituida.proyectoGrado());
                case DestitucionEstudianteProyectoGradoResult.Lapida lapida ->
                        logger.info(EstudianteProyectoGradoKey.LOG_LAPIDA,
                                lapida.estudiante(), lapida.proyectoGrado());
                case DestitucionEstudianteProyectoGradoResult.Descartada descartada ->
                        logger.debug(EstudianteProyectoGradoKey.LOG_DESTITUCION_DESCARTADA,
                                descartada.estudiante(), descartada.proyectoGrado(), descartada.ocurridoEnVigente());
            }
        });
    }
}
