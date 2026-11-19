package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.amqp.mapasruta.maparuta;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.AtrasarProyectoGradoInteractor;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.AtrasarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.result.AtrasoProyectoGradoResult;
import com.arquisoft.proyectos.infrastructure.config.ProyectosMapasRutaQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class MapaRutaPlazoVencidoConsumer extends AbstractEventConsumer {

    private final AtrasarProyectoGradoInteractor atrasarProyectoGradoInteractor;
    private final AppLogger logger;

    public MapaRutaPlazoVencidoConsumer(
            AtrasarProyectoGradoInteractor atrasarProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.atrasarProyectoGradoInteractor = atrasarProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ProyectosMapasRutaQueueConfig.MAPA_RUTA_PLAZO_VENCIDO_QUEUE)
    public void onMapaRutaPlazoVencido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, MapaRutaPlazoVencidoPayload.class);
            logger.info(ProyectoGradoKey.LOG_PLAZO_VENCIDO_RECIBIDO,
                    payload.idEvento(), payload.mapaRuta(), payload.proyectoGrado(), payload.fechaFin());

            var resultado = atrasarProyectoGradoInteractor.ejecutar(
                    AtrasarProyectoGradoCommand.crear(payload.proyectoGrado()));

            switch (resultado) {
                case AtrasoProyectoGradoResult.Atrasado atrasado ->
                        logger.info(ProyectoGradoKey.LOG_ATRASADO, atrasado.proyectoGrado());
                case AtrasoProyectoGradoResult.Omitido omitido ->
                        logger.info(ProyectoGradoKey.LOG_ATRASO_OMITIDO,
                                omitido.proyectoGrado(), omitido.estadoActual());
            }
        });
    }
}
