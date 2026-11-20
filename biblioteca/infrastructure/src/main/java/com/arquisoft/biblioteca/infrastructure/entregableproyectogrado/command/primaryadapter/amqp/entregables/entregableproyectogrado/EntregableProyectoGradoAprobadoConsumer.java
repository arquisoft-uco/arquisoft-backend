package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.interactor.AgregarEntregableProyectoGradoInteractor;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.model.AgregarEntregableProyectoGradoCommand;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.result.AgregacionEntregableProyectoGradoResult;
import com.arquisoft.biblioteca.infrastructure.config.BibliotecaEntregablesQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EntregableProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EntregableProyectoGradoAprobadoConsumer extends AbstractEventConsumer {

    private final AgregarEntregableProyectoGradoInteractor agregarEntregableProyectoGradoInteractor;
    private final AppLogger logger;

    public EntregableProyectoGradoAprobadoConsumer(
            AgregarEntregableProyectoGradoInteractor agregarEntregableProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.agregarEntregableProyectoGradoInteractor = agregarEntregableProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = BibliotecaEntregablesQueueConfig.ENTREGABLE_APROBADO_QUEUE)
    public void onEntregableAprobado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EntregableProyectoGradoAprobadoPayload.class);

            logger.info(EntregableProyectoGradoKey.LOG_APROBADO_RECIBIDO,
                    payload.idEvento(), payload.entregableProyectoGrado(), payload.proyectoGrado());

            var resultado = agregarEntregableProyectoGradoInteractor.ejecutar(
                    AgregarEntregableProyectoGradoCommand.crear(
                            payload.entregableProyectoGrado(),
                            payload.proyectoGrado(),
                            payload.version(),
                            payload.estadoEntregableNombre(),
                            payload.bucket(),
                            payload.claveObjeto(),
                            payload.nombreArchivo()));

            switch (resultado) {
                case AgregacionEntregableProyectoGradoResult.Agregado agregado ->
                        logger.info(EntregableProyectoGradoKey.LOG_AGREGADO,
                                agregado.entregableProyectoGrado(), agregado.proyectoGrado());
                case AgregacionEntregableProyectoGradoResult.Descartado descartado ->
                        logger.info(EntregableProyectoGradoKey.LOG_DESCARTADO,
                                descartado.entregableProyectoGrado());
            }
        });
    }
}
