package com.arquisoft.evaluaciones.infrastructure.coordinadorproyectogrado.command.primaryadapter.amqp.proyectos.coordinadorproyectogrado;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.interactor.AgregarCoordinadorProyectoGradoInteractor;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.model.AgregarCoordinadorProyectoGradoCommand;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.result.AgregacionCoordinadorProyectoGradoResult;
import com.arquisoft.evaluaciones.infrastructure.config.EvaluacionesProyectosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.CoordinadorProyectoGradoKey;
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

    private final AgregarCoordinadorProyectoGradoInteractor agregarCoordinadorProyectoGradoInteractor;
    private final AppLogger logger;

    public ProyectoGradoRegistradoConsumer(
            AgregarCoordinadorProyectoGradoInteractor agregarCoordinadorProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.agregarCoordinadorProyectoGradoInteractor = agregarCoordinadorProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EvaluacionesProyectosQueueConfig.PROYECTO_GRADO_REGISTRADO_QUEUE)
    public void onProyectoGradoRegistrado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ProyectoGradoRegistradoPayload.class);
            var coordinador = payload.coordinador();

            logger.info(CoordinadorProyectoGradoKey.LOG_RECIBIDO,
                    payload.idEvento(), payload.proyectoGradoId(), coordinador.id());

            var resultado = agregarCoordinadorProyectoGradoInteractor.ejecutar(
                    AgregarCoordinadorProyectoGradoCommand.crear(
                            payload.proyectoGradoId(), coordinador.id(), coordinador.nombre(),
                            coordinador.email(), payload.ocurridoEn()));

            switch (resultado) {
                case AgregacionCoordinadorProyectoGradoResult.Agregada agregada ->
                        logger.info(CoordinadorProyectoGradoKey.LOG_AGREGADO, agregada.proyectoGrado());
                case AgregacionCoordinadorProyectoGradoResult.Duplicada duplicada ->
                        logger.info(CoordinadorProyectoGradoKey.LOG_DUPLICADO, duplicada.proyectoGrado());
            }
        });
    }
}
