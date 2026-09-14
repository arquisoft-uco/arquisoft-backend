package com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.interactor.SincronizarEntregableProyectoAccesoInteractor;
import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.model.SincronizarEntregableProyectoAccesoCommand;
import com.arquisoft.evaluaciones.infrastructure.config.EvaluacionesEntregablesQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ProyeccionAccesoEvaluacionKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EntregableGeneradoConsumer extends AbstractEventConsumer {

    private final SincronizarEntregableProyectoAccesoInteractor sincronizarEntregableProyectoAccesoInteractor;
    private final AppLogger logger;

    public EntregableGeneradoConsumer(
            SincronizarEntregableProyectoAccesoInteractor sincronizarEntregableProyectoAccesoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.sincronizarEntregableProyectoAccesoInteractor = sincronizarEntregableProyectoAccesoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EvaluacionesEntregablesQueueConfig.ENTREGABLE_GENERADO_QUEUE)
    public void onEntregableGenerado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            EntregableGeneradoPayload payload = deserialize(message, EntregableGeneradoPayload.class);

            logger.info(ProyeccionAccesoEvaluacionKey.LOG_EVENTO_RECIBIDO,
                    payload.idEvento(), payload.entregableId());

            sincronizarEntregableProyectoAccesoInteractor.ejecutar(SincronizarEntregableProyectoAccesoCommand.crear(
                    payload.entregableId(),
                    payload.proyectoId(),
                    payload.versionEntregable(),
                    payload.ocurridoEn()));
        });
    }
}
