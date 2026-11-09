package com.arquisoft.entregables.infrastructure.paqueteentregable.command.primaryadapter.amqp.artefactos.paqueteentregable;

import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.interactor.AgregarPaqueteEntregableInteractor;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.AgregarPaqueteEntregableCommand;
import com.arquisoft.entregables.application.paqueteentregable.command.result.AgregacionPaqueteEntregableResult;
import com.arquisoft.entregables.infrastructure.config.EntregablesArtefactosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class PaqueteEntregableGeneradoConsumer extends AbstractEventConsumer {

    private final AgregarPaqueteEntregableInteractor agregarPaqueteEntregableInteractor;
    private final AppLogger logger;

    public PaqueteEntregableGeneradoConsumer(
            AgregarPaqueteEntregableInteractor agregarPaqueteEntregableInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.agregarPaqueteEntregableInteractor = agregarPaqueteEntregableInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EntregablesArtefactosQueueConfig.PAQUETE_ENTREGABLE_GENERADO_QUEUE)
    public void onPaqueteEntregableGenerado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, PaqueteEntregableGeneradoPayload.class);
            logger.info(PaqueteEntregableKey.LOG_PAQUETE_RECIBIDO,
                    payload.idEvento(), payload.entregableProyectoGrado(), payload.tamanoBytes());

            var resultado = agregarPaqueteEntregableInteractor.ejecutar(AgregarPaqueteEntregableCommand.crear(
                    payload.paqueteEntregable(), payload.entregableProyectoGrado(), payload.bucket(),
                    payload.claveObjeto(), payload.nombreArchivo(), payload.tamanoBytes(), payload.generadoEn()));

            switch (resultado) {
                case AgregacionPaqueteEntregableResult.Agregado agregado ->
                        logger.info(PaqueteEntregableKey.LOG_AGREGADO, agregado.entregableProyectoGrado());
                case AgregacionPaqueteEntregableResult.Duplicado duplicado ->
                        logger.info(PaqueteEntregableKey.LOG_DUPLICADO, duplicado.entregableProyectoGrado());
            }
        });
    }
}
