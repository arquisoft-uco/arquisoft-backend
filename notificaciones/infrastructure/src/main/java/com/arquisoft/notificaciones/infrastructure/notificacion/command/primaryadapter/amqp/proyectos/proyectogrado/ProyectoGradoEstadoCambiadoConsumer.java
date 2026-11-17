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
public class ProyectoGradoEstadoCambiadoConsumer extends AbstractNotificacionConsumer {

    private static final String ESTADO_FINALIZADO = "FINALIZADO";
    private static final String ESTADO_CANCELADO = "CANCELADO";

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public ProyectoGradoEstadoCambiadoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesProyectosQueueConfig.PROYECTO_GRADO_ESTADO_CAMBIADO_QUEUE)
    public void onProyectoGradoEstadoCambiado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ProyectoGradoEstadoCambiadoPayload.class);
            var estudiantes = UtilColeccion.aplicarPorDefecto(payload.estudiantes());
            var asesores = UtilColeccion.aplicarPorDefecto(payload.asesores());

            logger.info(ConsumidorKey.LOG_PROYECTO_GRADO_ESTADO_CAMBIADO_RECIBIDO,
                    payload.proyectoGradoId(), payload.estadoAnterior(), payload.estadoNuevo(),
                    estudiantes.size(), asesores.size());

            if (ESTADO_FINALIZADO.equals(payload.estadoNuevo())) {
                estudiantes.forEach(estudiante -> notificarFinalizadoEstudiante(payload, estudiante));
                return;
            }

            if (ESTADO_CANCELADO.equals(payload.estadoNuevo())) {
                estudiantes.forEach(estudiante -> notificarCanceladoEstudiante(payload, estudiante));
                asesores.forEach(asesor -> notificarCanceladoAsesor(payload, asesor));
                return;
            }

            logger.info(ConsumidorKey.LOG_PROYECTO_GRADO_ESTADO_CAMBIADO_IGNORADO,
                    payload.proyectoGradoId(), payload.estadoNuevo());
        });
    }

    private void notificarFinalizadoEstudiante(ProyectoGradoEstadoCambiadoPayload payload,
                                               ProyectoGradoEstadoCambiadoPayload.ContactoPayload estudiante) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                TipoNotificacionEvento.PROYECTO_GRADO_FINALIZADO_ESTUDIANTE.getCodigo(),
                estudiante.nombre(),
                estudiante.email(),
                plantilla(PlantillaKey.ASUNTO_PROYECTO_GRADO_FINALIZADO_ESTUDIANTE, payload.tituloProyecto()),
                plantilla(PlantillaKey.CUERPO_PROYECTO_GRADO_FINALIZADO_ESTUDIANTE,
                        estudiante.nombre(), payload.tituloProyecto()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }

    private void notificarCanceladoEstudiante(ProyectoGradoEstadoCambiadoPayload payload,
                                              ProyectoGradoEstadoCambiadoPayload.ContactoPayload estudiante) {
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

    private void notificarCanceladoAsesor(ProyectoGradoEstadoCambiadoPayload payload,
                                          ProyectoGradoEstadoCambiadoPayload.ContactoPayload asesor) {
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
