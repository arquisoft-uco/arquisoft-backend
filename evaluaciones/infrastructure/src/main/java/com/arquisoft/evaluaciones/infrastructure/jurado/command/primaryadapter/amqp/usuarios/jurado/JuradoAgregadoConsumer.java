package com.arquisoft.evaluaciones.infrastructure.jurado.command.primaryadapter.amqp.usuarios.jurado;

import com.arquisoft.evaluaciones.application.jurado.command.primaryport.interactor.AgregarJuradoInteractor;
import com.arquisoft.evaluaciones.application.jurado.command.primaryport.model.AgregarJuradoCommand;
import com.arquisoft.evaluaciones.application.jurado.command.result.AgregacionJuradoResult;
import com.arquisoft.evaluaciones.infrastructure.config.EvaluacionesUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.JuradoKey;
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
public class JuradoAgregadoConsumer extends AbstractEventConsumer {

    private final AgregarJuradoInteractor agregarJuradoInteractor;
    private final AppLogger logger;

    public JuradoAgregadoConsumer(
            AgregarJuradoInteractor agregarJuradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.agregarJuradoInteractor = agregarJuradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EvaluacionesUsuariosQueueConfig.JURADO_AGREGADO_QUEUE)
    public void onJuradoAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, JuradoAgregadoPayload.class);

            logger.info(JuradoKey.LOG_AGREGADO_RECIBIDO,
                    payload.idEvento(), payload.usuario(), UtilTexto.enmascararCorreo(payload.email()));

            var resultado = agregarJuradoInteractor.ejecutar(AgregarJuradoCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case AgregacionJuradoResult.Agregada agregada ->
                        logger.info(JuradoKey.LOG_AGREGADO, agregada.jurado());
                case AgregacionJuradoResult.Reactivada reactivada ->
                        logger.info(JuradoKey.LOG_REACTIVADO, reactivada.jurado());
                case AgregacionJuradoResult.Duplicada duplicada ->
                        logger.info(JuradoKey.LOG_DUPLICADO, duplicada.jurado());
                case AgregacionJuradoResult.Descartada descartada ->
                        logger.debug(JuradoKey.LOG_DESCARTADO,
                                descartada.jurado(), descartada.ocurridoEnVigente());
            }
        });
    }
}
