package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.evaluaciones.evaluacion;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.RegistrarResultadoEvaluacionEntregableProyectoGradoInteractor;
import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.RegistrarResultadoEvaluacionEntregableProyectoGradoCommand;
import com.arquisoft.entregables.application.entregableproyectogrado.command.result.RegistroResultadoEvaluacionEntregableProyectoGradoResult;
import com.arquisoft.entregables.infrastructure.config.EntregablesEvaluacionesQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EvaluacionFinalizadaConsumer extends AbstractEventConsumer {

    private final RegistrarResultadoEvaluacionEntregableProyectoGradoInteractor
            registrarResultadoEvaluacionEntregableProyectoGradoInteractor;
    private final AppLogger logger;

    public EvaluacionFinalizadaConsumer(
            RegistrarResultadoEvaluacionEntregableProyectoGradoInteractor
                    registrarResultadoEvaluacionEntregableProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.registrarResultadoEvaluacionEntregableProyectoGradoInteractor =
                registrarResultadoEvaluacionEntregableProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EntregablesEvaluacionesQueueConfig.EVALUACION_FINALIZADA_QUEUE)
    public void onEvaluacionFinalizada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EvaluacionFinalizadaPayload.class);

            logger.info(EntregableProyectoGradoKey.LOG_RESULTADO_EVALUACION_RECIBIDO,
                    payload.idEvento(), payload.entregableId(), payload.resultado());

            var resultado = registrarResultadoEvaluacionEntregableProyectoGradoInteractor.ejecutar(
                    RegistrarResultadoEvaluacionEntregableProyectoGradoCommand.crear(
                            payload.entregableId(), ResultadoEvaluacionEvento.estadoEntregableDe(payload.resultado())));

            switch (resultado) {
                case RegistroResultadoEvaluacionEntregableProyectoGradoResult.Registrado registrado ->
                        logger.info(EntregableProyectoGradoKey.LOG_RESULTADO_EVALUACION_REGISTRADO,
                                registrado.entregableProyectoGrado(), registrado.estadoEntregable());
                case RegistroResultadoEvaluacionEntregableProyectoGradoResult.Descartado descartado ->
                        logger.info(EntregableProyectoGradoKey.LOG_RESULTADO_EVALUACION_DESCARTADO,
                                descartado.entregableProyectoGrado(), descartado.estadoActual(),
                                descartado.estadoProyecto());
            }
        });
    }
}
