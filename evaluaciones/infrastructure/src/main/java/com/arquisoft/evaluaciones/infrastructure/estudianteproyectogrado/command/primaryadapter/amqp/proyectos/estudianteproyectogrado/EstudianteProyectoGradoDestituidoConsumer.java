package com.arquisoft.evaluaciones.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.interactor.RemoverEstudianteProyectoGradoInteractor;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.model.RemoverEstudianteProyectoGradoCommand;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.evaluaciones.infrastructure.config.EvaluacionesProyectosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EstudianteProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EstudianteProyectoGradoDestituidoConsumer extends AbstractEventConsumer {

    private final RemoverEstudianteProyectoGradoInteractor removerEstudianteProyectoGradoInteractor;
    private final AppLogger logger;

    public EstudianteProyectoGradoDestituidoConsumer(
            RemoverEstudianteProyectoGradoInteractor removerEstudianteProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.removerEstudianteProyectoGradoInteractor = removerEstudianteProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EvaluacionesProyectosQueueConfig.ESTUDIANTE_DESTITUIDO_QUEUE)
    public void onEstudianteDestituido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EstudianteProyectoGradoDestituidoPayload.class);

            logger.info(EstudianteProyectoGradoKey.LOG_DESTITUIDO_RECIBIDO,
                    payload.idEvento(), payload.proyectoGradoId(), payload.estudianteId());

            var resultado = removerEstudianteProyectoGradoInteractor.ejecutar(
                    RemoverEstudianteProyectoGradoCommand.crear(
                            payload.proyectoGradoId(), payload.estudianteId(), payload.ocurridoEn()));

            switch (resultado) {
                case RemocionEstudianteProyectoGradoResult.Removida removida ->
                        logger.info(EstudianteProyectoGradoKey.LOG_REMOVIDO,
                                removida.proyectoGrado(), removida.estudiante());
                case RemocionEstudianteProyectoGradoResult.Lapida lapida ->
                        logger.info(EstudianteProyectoGradoKey.LOG_LAPIDA,
                                lapida.proyectoGrado(), lapida.estudiante());
                case RemocionEstudianteProyectoGradoResult.Descartada descartada ->
                        logger.debug(EstudianteProyectoGradoKey.LOG_REMOCION_DESCARTADA,
                                descartada.proyectoGrado(), descartada.estudiante(), descartada.ocurridoEnVigente());
            }
        });
    }
}
