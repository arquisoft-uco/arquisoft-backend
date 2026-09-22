package com.arquisoft.proyectos.infrastructure.usuario.command.primaryadapter.amqp.usuarios.usuario;

import com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.ActualizarAsesorInteractor;
import com.arquisoft.proyectos.application.asesor.command.primaryport.model.ActualizarAsesorCommand;
import com.arquisoft.proyectos.application.asesor.command.result.ActualizacionAsesorResult;
import com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.ActualizarCoordinadorInteractor;
import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.ActualizarCoordinadorCommand;
import com.arquisoft.proyectos.application.coordinador.command.result.ActualizacionCoordinadorResult;
import com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor.ActualizarEstudianteInteractor;
import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.ActualizarEstudianteCommand;
import com.arquisoft.proyectos.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.proyectos.infrastructure.config.ProyectosUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorKey;
import com.arquisoft.shared.message.key.proyectos.CoordinadorKey;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectosKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class UsuarioModificadoConsumer extends AbstractEventConsumer {

    private final ActualizarEstudianteInteractor actualizarEstudianteInteractor;
    private final ActualizarAsesorInteractor actualizarAsesorInteractor;
    private final ActualizarCoordinadorInteractor actualizarCoordinadorInteractor;
    private final AppLogger logger;

    public UsuarioModificadoConsumer(
            ActualizarEstudianteInteractor actualizarEstudianteInteractor,
            ActualizarAsesorInteractor actualizarAsesorInteractor,
            ActualizarCoordinadorInteractor actualizarCoordinadorInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.actualizarEstudianteInteractor = actualizarEstudianteInteractor;
        this.actualizarAsesorInteractor = actualizarAsesorInteractor;
        this.actualizarCoordinadorInteractor = actualizarCoordinadorInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ProyectosUsuariosQueueConfig.USUARIO_MODIFICADO_QUEUE)
    public void onUsuarioModificado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, UsuarioModificadoPayload.class);

            logger.info(EstudianteProyectosKey.LOG_USUARIO_MODIFICADO_RECIBIDO,
                    payload.idEvento(), payload.usuario());

            registrarEstudiante(payload);
            registrarAsesor(payload);
            registrarCoordinador(payload);
        });
    }

    private void registrarEstudiante(UsuarioModificadoPayload payload) {
        var resultado = actualizarEstudianteInteractor.ejecutar(ActualizarEstudianteCommand.crear(
                payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                payload.ocurridoEn()));

        switch (resultado) {
            case ActualizacionEstudianteResult.Actualizada actualizada ->
                    logger.info(EstudianteProyectosKey.LOG_ACTUALIZADO, actualizada.estudiante());
            case ActualizacionEstudianteResult.Descartada descartada ->
                    logger.info(EstudianteProyectosKey.LOG_ACTUALIZACION_DESCARTADA,
                            descartada.estudiante(), descartada.ocurridoEnVigente(),
                            payload.ocurridoEn());
            case ActualizacionEstudianteResult.NoReplicado noReplicado ->
                    logger.debug(EstudianteProyectosKey.LOG_ACTUALIZACION_NO_REPLICADO,
                            noReplicado.estudiante());
        }
    }

    private void registrarAsesor(UsuarioModificadoPayload payload) {
        var resultado = actualizarAsesorInteractor.ejecutar(ActualizarAsesorCommand.crear(
                payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                payload.ocurridoEn()));

        switch (resultado) {
            case ActualizacionAsesorResult.Actualizada actualizada ->
                    logger.info(AsesorKey.LOG_ACTUALIZADO, actualizada.asesor());
            case ActualizacionAsesorResult.Descartada descartada ->
                    logger.info(AsesorKey.LOG_ACTUALIZACION_DESCARTADA, descartada.asesor(),
                            descartada.ocurridoEnVigente(), payload.ocurridoEn());
            case ActualizacionAsesorResult.NoReplicado noReplicado ->
                    logger.debug(AsesorKey.LOG_ACTUALIZACION_NO_REPLICADO, noReplicado.asesor());
        }
    }

    private void registrarCoordinador(UsuarioModificadoPayload payload) {
        var resultado = actualizarCoordinadorInteractor.ejecutar(ActualizarCoordinadorCommand.crear(
                payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                payload.ocurridoEn()));

        switch (resultado) {
            case ActualizacionCoordinadorResult.Actualizada actualizada ->
                    logger.info(CoordinadorKey.LOG_ACTUALIZADO, actualizada.coordinador());
            case ActualizacionCoordinadorResult.Descartada descartada ->
                    logger.info(CoordinadorKey.LOG_ACTUALIZACION_DESCARTADA, descartada.coordinador(),
                            descartada.ocurridoEnVigente(), payload.ocurridoEn());
            case ActualizacionCoordinadorResult.NoReplicado noReplicado ->
                    logger.debug(CoordinadorKey.LOG_ACTUALIZACION_NO_REPLICADO,
                            noReplicado.coordinador());
        }
    }
}
