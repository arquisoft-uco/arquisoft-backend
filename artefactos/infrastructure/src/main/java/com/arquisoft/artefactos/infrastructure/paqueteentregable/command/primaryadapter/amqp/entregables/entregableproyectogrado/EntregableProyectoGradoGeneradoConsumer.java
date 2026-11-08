package com.arquisoft.artefactos.infrastructure.paqueteentregable.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.GenerarPaqueteEntregableInteractor;
import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.GenerarPaqueteEntregableCommand;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.GeneracionPaqueteEntregableResult;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosEntregablesQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.PaqueteEntregableKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EntregableProyectoGradoGeneradoConsumer extends AbstractEventConsumer {

    private static final String UN_SOLO_CONSUMIDOR = "1";

    private final GenerarPaqueteEntregableInteractor generarPaqueteEntregableInteractor;
    private final AppLogger logger;

    public EntregableProyectoGradoGeneradoConsumer(
            GenerarPaqueteEntregableInteractor generarPaqueteEntregableInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.generarPaqueteEntregableInteractor = generarPaqueteEntregableInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosEntregablesQueueConfig.ENTREGABLE_PROYECTO_GRADO_GENERADO_QUEUE,
            concurrency = UN_SOLO_CONSUMIDOR)
    public void onEntregableProyectoGradoGenerado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EntregableProyectoGradoGeneradoPayload.class);
            logger.info(PaqueteEntregableKey.LOG_ENTREGABLE_RECIBIDO,
                    payload.idEvento(), payload.entregableProyectoGrado(), payload.proyectoGrado(),
                    payload.version());

            var resultado = generarPaqueteEntregableInteractor.ejecutar(GenerarPaqueteEntregableCommand.crear(
                    payload.entregableProyectoGrado(), payload.proyectoGrado(), payload.version()));

            switch (resultado) {
                case GeneracionPaqueteEntregableResult.Generado generado ->
                        logger.info(PaqueteEntregableKey.LOG_GENERADO,
                                generado.paquete(), generado.entregableProyectoGrado(), generado.tamanoBytes());
                case GeneracionPaqueteEntregableResult.Duplicado duplicado ->
                        logger.info(PaqueteEntregableKey.LOG_DUPLICADO, duplicado.entregableProyectoGrado());
            }
        });
    }
}
