package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.interactor.ActualizarEstadoProyectoGradoInteractor;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.model.ActualizarEstadoProyectoGradoCommand;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.mapas_ruta.infrastructure.config.MapasRutaProyectosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.ProyectoGradoMapasRutaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class ProyectoGradoEstadoCambiadoConsumer extends AbstractEventConsumer {

    private final ActualizarEstadoProyectoGradoInteractor actualizarEstadoProyectoGradoInteractor;
    private final AppLogger logger;

    public ProyectoGradoEstadoCambiadoConsumer(
            ActualizarEstadoProyectoGradoInteractor actualizarEstadoProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.actualizarEstadoProyectoGradoInteractor = actualizarEstadoProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = MapasRutaProyectosQueueConfig.PROYECTO_GRADO_ESTADO_CAMBIADO_QUEUE)
    public void onProyectoGradoEstadoCambiado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ProyectoGradoEstadoCambiadoPayload.class);
            logger.info(ProyectoGradoMapasRutaKey.LOG_ESTADO_RECIBIDO,
                    payload.idEvento(), payload.proyectoGradoId(), payload.estadoNuevo(), payload.ocurridoEn());

            var actualizacion = actualizarEstadoProyectoGradoInteractor.ejecutar(
                    ActualizarEstadoProyectoGradoCommand.crear(
                            payload.proyectoGradoId(), payload.estadoNuevo(), payload.ocurridoEn()));

            switch (actualizacion) {
                case ActualizacionEstadoProyectoGradoResult.Actualizado actualizado ->
                        logger.info(ProyectoGradoMapasRutaKey.LOG_ESTADO_APLICADO,
                                actualizado.proyectoGrado(), actualizado.estadoNuevo());
                case ActualizacionEstadoProyectoGradoResult.Descartado descartado ->
                        logger.info(ProyectoGradoMapasRutaKey.LOG_ESTADO_DESCARTADO,
                                descartado.proyectoGrado(), descartado.ocurridoEnGuardado());
            }
        });
    }
}
