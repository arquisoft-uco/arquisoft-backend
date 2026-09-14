package com.arquisoft.proyectos.infrastructure.coordinador.command.primaryadapter.amqp.usuarios.coordinador;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.AgregarCoordinadorProyectosInteractor;
import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.AgregarCoordinadorCommand;
import com.arquisoft.proyectos.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.proyectos.infrastructure.config.ProyectosUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.CoordinadorKey;
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
public class CoordinadorAgregadoProyectosConsumer extends AbstractEventConsumer {

    private final AgregarCoordinadorProyectosInteractor agregarCoordinadorProyectosInteractor;
    private final AppLogger logger;

    public CoordinadorAgregadoProyectosConsumer(
            AgregarCoordinadorProyectosInteractor agregarCoordinadorProyectosInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.agregarCoordinadorProyectosInteractor = agregarCoordinadorProyectosInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ProyectosUsuariosQueueConfig.COORDINADOR_AGREGADO_QUEUE)
    public void onCoordinadorAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, CoordinadorAgregadoPayload.class);

            logger.info(CoordinadorKey.LOG_AGREGADO_RECIBIDO,
                    payload.idEvento(), payload.usuario(), UtilTexto.enmascararCorreo(payload.email()));

            var resultado = agregarCoordinadorProyectosInteractor.ejecutar(AgregarCoordinadorCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case AgregacionCoordinadorResult.Agregada agregada ->
                        logger.info(CoordinadorKey.LOG_AGREGADO, agregada.coordinador());
                case AgregacionCoordinadorResult.Duplicada duplicada ->
                        logger.info(CoordinadorKey.LOG_DUPLICADO, duplicada.coordinador());
                case AgregacionCoordinadorResult.Descartada descartada ->
                        logger.debug(CoordinadorKey.LOG_DESCARTADO,
                                descartada.coordinador(), descartada.ocurridoEnVigente());
            }
        });
    }
}
