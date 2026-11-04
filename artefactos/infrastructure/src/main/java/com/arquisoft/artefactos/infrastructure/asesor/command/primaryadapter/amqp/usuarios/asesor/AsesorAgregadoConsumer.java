package com.arquisoft.artefactos.infrastructure.asesor.command.primaryadapter.amqp.usuarios.asesor;

import com.arquisoft.artefactos.application.asesor.command.primaryport.interactor.AgregarAsesorInteractor;
import com.arquisoft.artefactos.application.asesor.command.primaryport.model.AgregarAsesorCommand;
import com.arquisoft.artefactos.application.asesor.command.result.AgregacionAsesorResult;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AsesorKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilTexto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AsesorAgregadoConsumer extends AbstractEventConsumer {

    private final AgregarAsesorInteractor agregarAsesorInteractor;
    private final AppLogger logger;

    public AsesorAgregadoConsumer(
            AgregarAsesorInteractor agregarAsesorInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.agregarAsesorInteractor = agregarAsesorInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosUsuariosQueueConfig.ASESOR_AGREGADO_QUEUE)
    public void onAsesorAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AsesorAgregadoPayload.class);

            logger.info(AsesorKey.LOG_AGREGADO_RECIBIDO,
                    payload.idEvento(), payload.usuario(), UtilTexto.enmascararCorreo(payload.email()));

            var resultado = agregarAsesorInteractor.ejecutar(AgregarAsesorCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case AgregacionAsesorResult.Agregada agregada ->
                        logger.info(AsesorKey.LOG_AGREGADO, agregada.asesor());
                case AgregacionAsesorResult.Reactivada reactivada ->
                        logger.info(AsesorKey.LOG_REACTIVADO, reactivada.asesor());
                case AgregacionAsesorResult.Duplicada duplicada ->
                        logger.info(AsesorKey.LOG_DUPLICADO, duplicada.asesor());
                case AgregacionAsesorResult.Descartada descartada ->
                        logger.debug(AsesorKey.LOG_DESCARTADO,
                                descartada.asesor(), descartada.ocurridoEnVigente());
            }
        });
    }
}
