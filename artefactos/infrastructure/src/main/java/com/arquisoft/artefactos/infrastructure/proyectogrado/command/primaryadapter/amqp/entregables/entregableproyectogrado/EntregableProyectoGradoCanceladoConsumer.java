package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.DescongelarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.DescongelarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.DescongelacionProyectoGradoResult;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosEntregablesQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EntregableProyectoGradoCanceladoConsumer extends AbstractEventConsumer {

    private static final String UN_SOLO_CONSUMIDOR = "1";

    private final DescongelarProyectoGradoInteractor descongelarProyectoGradoInteractor;
    private final AppLogger logger;

    public EntregableProyectoGradoCanceladoConsumer(
            DescongelarProyectoGradoInteractor descongelarProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.descongelarProyectoGradoInteractor = descongelarProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosEntregablesQueueConfig.ENTREGABLE_PROYECTO_GRADO_CANCELADO_QUEUE,
            concurrency = UN_SOLO_CONSUMIDOR)
    public void onEntregableProyectoGradoCancelado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EntregableProyectoGradoCanceladoPayload.class);
            logger.info(ProyectoGradoKey.LOG_CANCELACION_RECIBIDA,
                    payload.idEvento(), payload.entregableProyectoGrado(), payload.proyectoGrado(),
                    payload.version());

            var descongelacion = descongelarProyectoGradoInteractor.ejecutar(
                    DescongelarProyectoGradoCommand.crear(payload.proyectoGrado(), payload.ocurridoEn()));

            switch (descongelacion) {
                case DescongelacionProyectoGradoResult.Descongelado descongelado ->
                        logger.info(ProyectoGradoKey.LOG_DESCONGELADO, descongelado.proyectoGrado());
                case DescongelacionProyectoGradoResult.Omitido omitido ->
                        logger.info(ProyectoGradoKey.LOG_OMITIDO_DESCONGELAR, omitido.proyectoGrado());
            }
        });
    }
}
