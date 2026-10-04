package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.estadofichaperfil;

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
public class EstadoFichaPerfilAgregadoConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public EstadoFichaPerfilAgregadoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesFichasQueueConfig.ESTADO_FICHA_PERFIL_AGREGADO_QUEUE)
    public void onEstadoFichaPerfilAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EstadoFichaPerfilAgregadoPayload.class);
            var estudiantes = UtilColeccion.aplicarPorDefecto(payload.estudiantes());

            logger.info(ConsumidorKey.LOG_ESTADO_FICHA_PERFIL_AGREGADO_RECIBIDO,
                    payload.fichaPerfilId(), payload.estadoFicha(), estudiantes.size());

            estudiantes.forEach(estudiante -> notificar(payload, estudiante));
        });
    }

    private void notificar(
            EstadoFichaPerfilAgregadoPayload payload,
            EstadoFichaPerfilAgregadoPayload.ContactoPayload estudiante) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                TipoNotificacionEvento.ESTADO_FICHA_PERFIL_AGREGADO.getCodigo(),
                estudiante.nombre(),
                estudiante.email(),
                plantilla(PlantillaKey.ASUNTO_ESTADO_FICHA_PERFIL_AGREGADO,
                        payload.tituloProyecto(), payload.estadoFichaNombre()),
                plantilla(PlantillaKey.CUERPO_ESTADO_FICHA_PERFIL_AGREGADO,
                        estudiante.nombre(), payload.tituloProyecto(), payload.estadoFichaNombre()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }
}
