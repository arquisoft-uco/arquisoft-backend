package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.interactor.ActualizarEstadoEntregableProyectoGradoInteractor;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.model.ActualizarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.result.ActualizacionEstadoEntregableProyectoGradoResult;
import com.arquisoft.proyectos.infrastructure.config.ProyectosEntregablesQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EntregableProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilNumero;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EntregableProyectoGradoGeneradoConsumer extends AbstractEventConsumer {

    private final ActualizarEstadoEntregableProyectoGradoInteractor actualizarInteractor;
    private final AppLogger logger;

    public EntregableProyectoGradoGeneradoConsumer(
            ActualizarEstadoEntregableProyectoGradoInteractor actualizarInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.actualizarInteractor = actualizarInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ProyectosEntregablesQueueConfig.ENTREGABLE_GENERADO_QUEUE)
    public void onEntregableProyectoGradoGenerado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EntregableProyectoGradoGeneradoPayload.class);
            var estado = EstadoEntregableEvento.NUEVO.getCodigo();

            logger.info(EntregableProyectoGradoKey.LOG_EVENTO_RECIBIDO,
                    payload.idEvento(), payload.entregableProyectoGrado(), estado);

            var resultado = actualizarInteractor.ejecutar(ActualizarEstadoEntregableProyectoGradoCommand.crear(
                    payload.entregableProyectoGrado(), payload.proyectoGrado(),
                    UtilNumero.obtenerPorDefecto(payload.version()), estado, payload.ocurridoEn()));

            switch (resultado) {
                case ActualizacionEstadoEntregableProyectoGradoResult.Actualizado actualizado ->
                        logger.info(EntregableProyectoGradoKey.LOG_ACTUALIZADO,
                                actualizado.entregableProyectoGrado(), actualizado.estadoEntregable());
                case ActualizacionEstadoEntregableProyectoGradoResult.Descartado descartado ->
                        logger.debug(EntregableProyectoGradoKey.LOG_DESCARTADO,
                                descartado.entregableProyectoGrado(), descartado.ocurridoEnVigente());
            }
        });
    }
}
