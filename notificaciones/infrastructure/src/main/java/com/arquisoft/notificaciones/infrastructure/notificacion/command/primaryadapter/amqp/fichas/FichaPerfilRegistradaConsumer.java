package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesFichasQueueConfig;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.AbstractNotificacionConsumer;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.TipoNotificacionEvento;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
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
import java.util.List;

@Component
public class FichaPerfilRegistradaConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public FichaPerfilRegistradaConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesFichasQueueConfig.FICHA_REGISTRADA_QUEUE)
    public void onFichaPerfilRegistrada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            FichaPerfilRegistradaPayload payload =
                    deserialize(message, FichaPerfilRegistradaPayload.class);

            List<FichaPerfilRegistradaPayload.DestinatarioPayload> estudiantes =
                    UtilColeccion.aplicarPorDefecto(payload.estudiantes());

            logger.info(
                    Mensajes.obtener(ConsumidorKey.LOG_FICHA_REGISTRADA_RECIBIDO),
                    payload.fichaPerfilId(),
                    estudiantes.size());

            notificarAsesor(payload);
            estudiantes.forEach(estudiante -> notificarEstudiante(payload, estudiante));
        });
    }

    private void notificarAsesor(FichaPerfilRegistradaPayload payload) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                TipoNotificacionEvento.FICHA_PERFIL_REGISTRADA_ASESOR.getCodigo(),
                payload.asesorNombre(),
                payload.asesorEmail(),
                plantilla(PlantillaKey.ASUNTO_FICHA_REGISTRADA_ASESOR, payload.tituloProyecto()),
                plantilla(PlantillaKey.CUERPO_FICHA_REGISTRADA_ASESOR,
                        payload.asesorNombre(), payload.tituloProyecto()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }

    private void notificarEstudiante(
            FichaPerfilRegistradaPayload payload,
            FichaPerfilRegistradaPayload.DestinatarioPayload estudiante) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                TipoNotificacionEvento.FICHA_PERFIL_REGISTRADA_ESTUDIANTE.getCodigo(),
                estudiante.nombre(),
                estudiante.email(),
                plantilla(PlantillaKey.ASUNTO_FICHA_REGISTRADA_ESTUDIANTE, payload.tituloProyecto()),
                plantilla(PlantillaKey.CUERPO_FICHA_REGISTRADA_ESTUDIANTE,
                        estudiante.nombre(), payload.tituloProyecto()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }
}
