package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesProyectosQueueConfig;
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
public class ProyectoGradoCanceladoConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public ProyectoGradoCanceladoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesProyectosQueueConfig.PROYECTO_GRADO_CANCELADO_QUEUE)
    public void onProyectoGradoCancelado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ProyectoGradoCanceladoPayload.class);
            var estudiantes = UtilColeccion.aplicarPorDefecto(payload.estudiantes());
            var asesores = UtilColeccion.aplicarPorDefecto(payload.asesores());

            logger.info(ConsumidorKey.LOG_PROYECTO_GRADO_CANCELADO_RECIBIDO,
                    payload.proyectoGradoId(), estudiantes.size(), asesores.size());

            estudiantes.forEach(estudiante -> notificarEstudiante(payload, estudiante));
            asesores.forEach(asesor -> notificarAsesor(payload, asesor));
        });
    }

    private void notificarEstudiante(ProyectoGradoCanceladoPayload payload,
                                     ProyectoGradoCanceladoPayload.ContactoPayload estudiante) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                TipoNotificacionEvento.PROYECTO_GRADO_CANCELADO_ESTUDIANTE.getCodigo(),
                estudiante.nombre(),
                estudiante.email(),
                plantilla(PlantillaKey.ASUNTO_PROYECTO_GRADO_CANCELADO_ESTUDIANTE, payload.tituloProyecto()),
                plantilla(PlantillaKey.CUERPO_PROYECTO_GRADO_CANCELADO_ESTUDIANTE,
                        estudiante.nombre(), payload.tituloProyecto(), payload.motivo()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }

    private void notificarAsesor(ProyectoGradoCanceladoPayload payload,
                                 ProyectoGradoCanceladoPayload.ContactoPayload asesor) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                TipoNotificacionEvento.PROYECTO_GRADO_CANCELADO_ASESOR.getCodigo(),
                asesor.nombre(),
                asesor.email(),
                plantilla(PlantillaKey.ASUNTO_PROYECTO_GRADO_CANCELADO_ASESOR, payload.tituloProyecto()),
                plantilla(PlantillaKey.CUERPO_PROYECTO_GRADO_CANCELADO_ASESOR,
                        asesor.nombre(), payload.tituloProyecto(), payload.motivo()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }
}
