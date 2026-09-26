package com.arquisoft.proyectos.infrastructure.coordinador.command.primaryadapter.amqp.usuarios.coordinador;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.RemoverCoordinadorInteractor;
import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.RemoverCoordinadorCommand;
import com.arquisoft.proyectos.application.coordinador.command.result.RemocionCoordinadorResult;
import com.arquisoft.proyectos.infrastructure.config.ProyectosUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.CoordinadorKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class CoordinadorRemovidoConsumer extends AbstractEventConsumer {

    private final RemoverCoordinadorInteractor removerCoordinadorInteractor;
    private final AppLogger logger;

    public CoordinadorRemovidoConsumer(
            RemoverCoordinadorInteractor removerCoordinadorInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.removerCoordinadorInteractor = removerCoordinadorInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ProyectosUsuariosQueueConfig.COORDINADOR_REMOVIDO_QUEUE)
    public void onCoordinadorRemovido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, CoordinadorRemovidoPayload.class);

            logger.info(CoordinadorKey.LOG_REMOVIDO_RECIBIDO, payload.idEvento(), payload.usuario());

            var resultado = removerCoordinadorInteractor.ejecutar(RemoverCoordinadorCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case RemocionCoordinadorResult.Removida removida ->
                        logger.info(CoordinadorKey.LOG_REMOVIDO, removida.coordinador());
                case RemocionCoordinadorResult.Lapida lapida ->
                        logger.info(CoordinadorKey.LOG_LAPIDA, lapida.coordinador());
                case RemocionCoordinadorResult.Descartada descartada ->
                        logger.debug(CoordinadorKey.LOG_REMOCION_DESCARTADA,
                                descartada.coordinador(), descartada.ocurridoEnVigente());
            }
        });
    }
}
