package com.arquisoft.evaluaciones.infrastructure.jurado.command.primaryadapter.amqp.usuarios.jurado;

import com.arquisoft.evaluaciones.application.jurado.command.primaryport.interactor.RemoverJuradoInteractor;
import com.arquisoft.evaluaciones.application.jurado.command.primaryport.model.RemoverJuradoCommand;
import com.arquisoft.evaluaciones.application.jurado.command.result.RemocionJuradoResult;
import com.arquisoft.evaluaciones.infrastructure.config.EvaluacionesUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.JuradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class JuradoRemovidoConsumer extends AbstractEventConsumer {

    private final RemoverJuradoInteractor removerJuradoInteractor;
    private final AppLogger logger;

    public JuradoRemovidoConsumer(
            RemoverJuradoInteractor removerJuradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.removerJuradoInteractor = removerJuradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EvaluacionesUsuariosQueueConfig.JURADO_REMOVIDO_QUEUE)
    public void onJuradoRemovido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, JuradoRemovidoPayload.class);

            logger.info(JuradoKey.LOG_REMOVIDO_RECIBIDO, payload.idEvento(), payload.usuario());

            var resultado = removerJuradoInteractor.ejecutar(RemoverJuradoCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case RemocionJuradoResult.Removida removida ->
                        logger.info(JuradoKey.LOG_REMOVIDO, removida.jurado());
                case RemocionJuradoResult.Lapida lapida ->
                        logger.info(JuradoKey.LOG_LAPIDA, lapida.jurado());
                case RemocionJuradoResult.Descartada descartada ->
                        logger.debug(JuradoKey.LOG_REMOCION_DESCARTADA,
                                descartada.jurado(), descartada.ocurridoEnVigente());
            }
        });
    }
}
