package com.arquisoft.fichas.infrastructure.estudiante.command.primaryadapter.amqp.usuarios.estudiante;

import com.arquisoft.fichas.application.estudiante.command.primaryport.interactor.RemoverEstudianteInteractor;
import com.arquisoft.fichas.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.fichas.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstudianteKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EstudianteRemovidoConsumer extends AbstractEventConsumer {

    private final RemoverEstudianteInteractor removerEstudianteInteractor;
    private final AppLogger logger;

    public EstudianteRemovidoConsumer(
            RemoverEstudianteInteractor removerEstudianteInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.removerEstudianteInteractor = removerEstudianteInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = FichasUsuariosQueueConfig.ESTUDIANTE_REMOVIDO_QUEUE)
    public void onEstudianteRemovido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EstudianteRemovidoPayload.class);

            logger.info(EstudianteKey.LOG_REMOVIDO_RECIBIDO, payload.idEvento(), payload.usuario());

            var resultado = removerEstudianteInteractor.ejecutar(RemoverEstudianteCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case RemocionEstudianteResult.Removida removida ->
                        logger.info(EstudianteKey.LOG_REMOVIDO, removida.estudiante());
                case RemocionEstudianteResult.Lapida lapida ->
                        logger.info(EstudianteKey.LOG_LAPIDA, lapida.estudiante());
                case RemocionEstudianteResult.Descartada descartada ->
                        logger.debug(EstudianteKey.LOG_REMOCION_DESCARTADA,
                                descartada.estudiante(), descartada.ocurridoEnVigente());
            }
        });
    }
}
