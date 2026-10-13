package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.fichaperfil;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesFichasQueueConfig;
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

@Component
public class FichaPerfilNoAprobadaConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public FichaPerfilNoAprobadaConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesFichasQueueConfig.FICHA_PERFIL_NO_APROBADA_QUEUE)
    public void onFichaPerfilNoAprobada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, FichaPerfilNoAprobadaPayload.class);
            var estudiantes = UtilColeccion.aplicarPorDefecto(payload.estudiantes());

            logger.info(ConsumidorKey.LOG_FICHA_NO_APROBADA_RECIBIDO, payload.fichaPerfilId(), estudiantes.size());

            estudiantes.forEach(estudiante -> notificar(payload, estudiante,
                    TipoNotificacionEvento.FICHA_PERFIL_NO_APROBADA_ESTUDIANTE,
                    PlantillaKey.CUERPO_FICHA_NO_APROBADA_ESTUDIANTE));
            notificar(payload, payload.asesor(),
                    TipoNotificacionEvento.FICHA_PERFIL_NO_APROBADA_ASESOR,
                    PlantillaKey.CUERPO_FICHA_NO_APROBADA_ASESOR);
        });
    }

    private void notificar(FichaPerfilNoAprobadaPayload payload, FichaPerfilNoAprobadaPayload.ContactoPayload contacto,
                           TipoNotificacionEvento tipo, PlantillaKey cuerpo) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                tipo.getCodigo(),
                contacto.nombre(),
                contacto.email(),
                plantilla(PlantillaKey.ASUNTO_FICHA_NO_APROBADA, payload.tituloProyecto()),
                plantilla(cuerpo, contacto.nombre(), payload.tituloProyecto()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }
}
