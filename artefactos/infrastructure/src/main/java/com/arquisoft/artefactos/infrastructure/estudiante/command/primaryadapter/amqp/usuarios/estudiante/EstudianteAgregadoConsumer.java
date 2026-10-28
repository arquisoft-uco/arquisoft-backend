package com.arquisoft.artefactos.infrastructure.estudiante.command.primaryadapter.amqp.usuarios.estudiante;

import com.arquisoft.artefactos.application.estudiante.command.primaryport.interactor.AgregarEstudianteInteractor;
import com.arquisoft.artefactos.application.estudiante.command.primaryport.model.AgregarEstudianteCommand;
import com.arquisoft.artefactos.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteKey;
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
public class EstudianteAgregadoConsumer extends AbstractEventConsumer {

    private final AgregarEstudianteInteractor agregarEstudianteInteractor;
    private final AppLogger logger;

    public EstudianteAgregadoConsumer(
            AgregarEstudianteInteractor agregarEstudianteInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.agregarEstudianteInteractor = agregarEstudianteInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosUsuariosQueueConfig.ESTUDIANTE_AGREGADO_QUEUE)
    public void onEstudianteAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EstudianteAgregadoPayload.class);

            logger.info(EstudianteKey.LOG_AGREGADO_RECIBIDO,
                    payload.idEvento(), payload.usuario(), UtilTexto.enmascararCorreo(payload.email()));

            var resultado = agregarEstudianteInteractor.ejecutar(AgregarEstudianteCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case AgregacionEstudianteResult.Agregada agregada ->
                        logger.info(EstudianteKey.LOG_AGREGADO, agregada.estudiante());
                case AgregacionEstudianteResult.Reactivada reactivada ->
                        logger.info(EstudianteKey.LOG_REACTIVADO, reactivada.estudiante());
                case AgregacionEstudianteResult.Duplicada duplicada ->
                        logger.info(EstudianteKey.LOG_DUPLICADO, duplicada.estudiante());
                case AgregacionEstudianteResult.Descartada descartada ->
                        logger.debug(EstudianteKey.LOG_DESCARTADO,
                                descartada.estudiante(), descartada.ocurridoEnVigente());
            }
        });
    }
}
