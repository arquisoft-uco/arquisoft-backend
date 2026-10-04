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
public class FichaPerfilAprobadaConsumer extends AbstractNotificacionConsumer {

    private static final String ESTADO_APROBADA_CON_OBSERVACIONES = "APROBADA_CON_OBSERVACIONES";

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public FichaPerfilAprobadaConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesFichasQueueConfig.FICHA_PERFIL_APROBADA_QUEUE)
    public void onFichaPerfilAprobada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, FichaPerfilAprobadaPayload.class);
            var estudiantes = UtilColeccion.aplicarPorDefecto(payload.estudiantes());

            logger.info(ConsumidorKey.LOG_FICHA_APROBADA_RECIBIDO,
                    payload.fichaPerfilId(), payload.estadoFicha(), estudiantes.size());

            var estado = textoEstado(payload.estadoFicha());

            estudiantes.forEach(estudiante -> notificar(payload, estudiante.contacto(), estado,
                    TipoNotificacionEvento.FICHA_PERFIL_APROBADA_ESTUDIANTE,
                    PlantillaKey.CUERPO_FICHA_APROBADA_ESTUDIANTE));
            notificar(payload, payload.asesor(), estado,
                    TipoNotificacionEvento.FICHA_PERFIL_APROBADA_ASESOR,
                    PlantillaKey.CUERPO_FICHA_APROBADA_ASESOR);
        });
    }

    private String textoEstado(String estadoFicha) {
        return switch (estadoFicha) {
            case ESTADO_APROBADA_CON_OBSERVACIONES -> plantilla(PlantillaKey.TEXTO_ESTADO_APROBADA_CON_OBSERVACIONES);
            case null, default -> plantilla(PlantillaKey.TEXTO_ESTADO_APROBADA);
        };
    }

    private void notificar(FichaPerfilAprobadaPayload payload, FichaPerfilAprobadaPayload.ContactoPayload contacto,
                           String estado, TipoNotificacionEvento tipo, PlantillaKey cuerpo) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                tipo.getCodigo(),
                contacto.nombre(),
                contacto.email(),
                plantilla(PlantillaKey.ASUNTO_FICHA_APROBADA, payload.tituloProyecto()),
                plantilla(cuerpo, contacto.nombre(), payload.tituloProyecto(), estado),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }
}
