package com.arquisoft.entregables.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.AgregarProyectoGradoInteractor;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.AgregarProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.entregables.infrastructure.config.EntregablesProyectosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class ProyectoGradoRegistradoConsumer extends AbstractEventConsumer {

    private final AgregarProyectoGradoInteractor agregarProyectoGradoInteractor;
    private final AppLogger logger;

    public ProyectoGradoRegistradoConsumer(
            AgregarProyectoGradoInteractor agregarProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.agregarProyectoGradoInteractor = agregarProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EntregablesProyectosQueueConfig.PROYECTO_GRADO_REGISTRADO_QUEUE)
    public void onProyectoGradoRegistrado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ProyectoGradoRegistradoPayload.class);
            logger.info(ProyectoGradoKey.LOG_PROYECTO_RECIBIDO,
                    payload.idEvento(), payload.proyectoGradoId(), payload.tituloProyecto());

            var resultado = agregarProyectoGradoInteractor.ejecutar(
                    AgregarProyectoGradoCommand.crear(payload.proyectoGradoId(), payload.tituloProyecto()));

            switch (resultado) {
                case AgregacionProyectoGradoResult.Agregado agregado ->
                        logger.info(ProyectoGradoKey.LOG_AGREGADO, agregado.proyectoGrado());
                case AgregacionProyectoGradoResult.Duplicado duplicado ->
                        logger.info(ProyectoGradoKey.LOG_DUPLICADO, duplicado.proyectoGrado());
            }
        });
    }
}
