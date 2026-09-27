package com.arquisoft.fichas.infrastructure.representantecomite.command.primaryadapter.amqp.usuarios.representantecomite;

import com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor.AgregarRepresentanteComiteInteractor;
import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.AgregarRepresentanteComiteCommand;
import com.arquisoft.fichas.application.representantecomite.command.result.AgregacionRepresentanteComiteResult;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
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
public class RepresentanteComiteAgregadoConsumer extends AbstractEventConsumer {

    private final AgregarRepresentanteComiteInteractor agregarRepresentanteComiteInteractor;
    private final AppLogger logger;

    public RepresentanteComiteAgregadoConsumer(
            AgregarRepresentanteComiteInteractor agregarRepresentanteComiteInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.agregarRepresentanteComiteInteractor = agregarRepresentanteComiteInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = FichasUsuariosQueueConfig.REPRESENTANTE_COMITE_AGREGADO_QUEUE)
    public void onRepresentanteComiteAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, RepresentanteComiteAgregadoPayload.class);

            logger.info(RepresentanteComiteKey.LOG_AGREGADO_RECIBIDO,
                    payload.idEvento(), payload.usuario(), UtilTexto.enmascararCorreo(payload.email()));

            var resultado = agregarRepresentanteComiteInteractor.ejecutar(AgregarRepresentanteComiteCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case AgregacionRepresentanteComiteResult.Agregada agregada ->
                        logger.info(RepresentanteComiteKey.LOG_AGREGADO, agregada.representanteComite());
                case AgregacionRepresentanteComiteResult.Reactivada reactivada ->
                        logger.info(RepresentanteComiteKey.LOG_REACTIVADO, reactivada.representanteComite());
                case AgregacionRepresentanteComiteResult.Duplicada duplicada ->
                        logger.info(RepresentanteComiteKey.LOG_DUPLICADO, duplicada.representanteComite());
                case AgregacionRepresentanteComiteResult.Descartada descartada ->
                        logger.debug(RepresentanteComiteKey.LOG_DESCARTADO,
                                descartada.representanteComite(), descartada.ocurridoEnVigente());
            }
        });
    }
}
