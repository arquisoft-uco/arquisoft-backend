package com.arquisoft.proyectos.infrastructure.asesor.command.primaryadapter.amqp.usuarios.asesor;

import com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.RemoverAsesorInteractor;
import com.arquisoft.proyectos.application.asesor.command.primaryport.model.RemoverAsesorCommand;
import com.arquisoft.proyectos.application.asesor.command.result.RemocionAsesorResult;
import com.arquisoft.proyectos.infrastructure.config.ProyectosUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AsesorRemovidoConsumer extends AbstractEventConsumer {

    private final RemoverAsesorInteractor removerAsesorInteractor;
    private final AppLogger logger;

    public AsesorRemovidoConsumer(
            RemoverAsesorInteractor removerAsesorInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.removerAsesorInteractor = removerAsesorInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ProyectosUsuariosQueueConfig.ASESOR_REMOVIDO_QUEUE)
    public void onAsesorRemovido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AsesorRemovidoPayload.class);

            logger.info(AsesorKey.LOG_REMOVIDO_RECIBIDO, payload.idEvento(), payload.usuario());

            var resultado = removerAsesorInteractor.ejecutar(RemoverAsesorCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case RemocionAsesorResult.Removida removida ->
                        logger.info(AsesorKey.LOG_REMOVIDO, removida.asesor());
                case RemocionAsesorResult.Lapida lapida ->
                        logger.info(AsesorKey.LOG_LAPIDA, lapida.asesor());
                case RemocionAsesorResult.Descartada descartada ->
                        logger.debug(AsesorKey.LOG_REMOCION_DESCARTADA,
                                descartada.asesor(), descartada.ocurridoEnVigente());
            }
        });
    }
}
