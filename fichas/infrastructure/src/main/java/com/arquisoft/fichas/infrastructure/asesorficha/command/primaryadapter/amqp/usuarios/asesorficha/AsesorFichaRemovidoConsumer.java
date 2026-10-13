package com.arquisoft.fichas.infrastructure.asesorficha.command.primaryadapter.amqp.usuarios.asesorficha;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.RemoverAsesorFichaInteractor;
import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.RemoverAsesorFichaCommand;
import com.arquisoft.fichas.application.asesorficha.command.result.RemocionAsesorFichaResult;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.AsesorFichaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AsesorFichaRemovidoConsumer extends AbstractEventConsumer {

    private final RemoverAsesorFichaInteractor removerAsesorFichaInteractor;
    private final AppLogger logger;

    public AsesorFichaRemovidoConsumer(
            RemoverAsesorFichaInteractor removerAsesorFichaInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.removerAsesorFichaInteractor = removerAsesorFichaInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = FichasUsuariosQueueConfig.ASESOR_FICHA_REMOVIDO_QUEUE)
    public void onAsesorFichaRemovido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AsesorFichaRemovidoPayload.class);

            logger.info(AsesorFichaKey.LOG_REMOVIDO_RECIBIDO, payload.idEvento(), payload.usuario());

            var resultado = removerAsesorFichaInteractor.ejecutar(RemoverAsesorFichaCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case RemocionAsesorFichaResult.Removida removida ->
                        logger.info(AsesorFichaKey.LOG_REMOVIDO, removida.asesorFicha());
                case RemocionAsesorFichaResult.Lapida lapida ->
                        logger.info(AsesorFichaKey.LOG_LAPIDA, lapida.asesorFicha());
                case RemocionAsesorFichaResult.Descartada descartada ->
                        logger.debug(AsesorFichaKey.LOG_REMOCION_DESCARTADA,
                                descartada.asesorFicha(), descartada.ocurridoEnVigente());
            }
        });
    }
}
