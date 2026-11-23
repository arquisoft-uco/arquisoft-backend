package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.evaluaciones.evaluacion;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesEvaluacionesQueueConfig;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.AbstractNotificacionConsumer;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.TipoNotificacionEvento;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.message.key.notificaciones.PlantillaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilColeccion;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.stream.Stream;

@Component
public class EvaluacionFinalizadaConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public EvaluacionFinalizadaConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesEvaluacionesQueueConfig.EVALUACION_FINALIZADA_QUEUE)
    public void onEvaluacionFinalizada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EvaluacionFinalizadaPayload.class);

            logger.info(ConsumidorKey.LOG_EVALUACION_FINALIZADA_RECIBIDO,
                    payload.idEvento(), payload.evaluacionId(), payload.resultado());

            Stream.concat(
                            Stream.of(payload.coordinador(), payload.asesor()),
                            UtilColeccion.aplicarPorDefecto(payload.estudiantes()).stream())
                    .forEach(destinatario -> notificar(payload, destinatario));
        });
    }

    private void notificar(
            EvaluacionFinalizadaPayload payload, EvaluacionFinalizadaPayload.ContactoPayload destinatario) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                TipoNotificacionEvento.EVALUACION_FINALIZADA.getCodigo(),
                destinatario.nombre(),
                destinatario.email(),
                plantilla(PlantillaKey.ASUNTO_EVALUACION_FINALIZADA, payload.nombreProyecto()),
                plantilla(PlantillaKey.CUERPO_EVALUACION_FINALIZADA,
                        destinatario.nombre(), payload.nombreProyecto(), payload.versionEntregable(),
                        payload.resultadoNombre()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }
}
