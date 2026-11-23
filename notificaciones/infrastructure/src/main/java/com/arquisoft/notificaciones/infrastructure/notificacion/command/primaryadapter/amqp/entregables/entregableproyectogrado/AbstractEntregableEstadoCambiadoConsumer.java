package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.AbstractNotificacionConsumer;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.TipoNotificacionEvento;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.notificaciones.PlantillaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilColeccion;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

public abstract class AbstractEntregableEstadoCambiadoConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    protected AbstractEntregableEstadoCambiadoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    protected void notificar(String idEvento, String nombreProyecto, Integer version, String estadoNombre,
                             List<ContactoEstudiantePayload> estudiantes) {
        UtilColeccion.aplicarPorDefecto(estudiantes).forEach(estudiante ->
                registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                        idEvento,
                        TipoNotificacionEvento.ENTREGABLE_ESTADO_CAMBIADO.getCodigo(),
                        estudiante.nombre(),
                        estudiante.email(),
                        plantilla(PlantillaKey.ASUNTO_ENTREGABLE_ESTADO_CAMBIADO, nombreProyecto, estadoNombre),
                        plantilla(PlantillaKey.CUERPO_ENTREGABLE_ESTADO_CAMBIADO,
                                estudiante.nombre(), version, nombreProyecto, estadoNombre),
                        plantilla(PlantillaKey.PIE_GENERICO)))));
    }
}
