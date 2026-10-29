package com.arquisoft.artefactos.infrastructure.usuario.command.primaryadapter.amqp.usuarios.usuario;

import com.arquisoft.artefactos.application.asesor.command.primaryport.interactor.ActualizarAsesorInteractor;
import com.arquisoft.artefactos.application.asesor.command.primaryport.model.ActualizarAsesorCommand;
import com.arquisoft.artefactos.application.asesor.command.result.ActualizacionAsesorResult;
import com.arquisoft.artefactos.application.estudiante.command.primaryport.interactor.ActualizarEstudianteInteractor;
import com.arquisoft.artefactos.application.estudiante.command.primaryport.model.ActualizarEstudianteCommand;
import com.arquisoft.artefactos.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AsesorKey;
import com.arquisoft.shared.message.key.artefactos.EstudianteKey;
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

    private final ActualizarAsesorInteractor actualizarAsesorInteractor;
    private final ActualizarEstudianteInteractor actualizarEstudianteInteractor;
    private final AppLogger logger;

    public UsuarioModificadoConsumer(
            ActualizarAsesorInteractor actualizarAsesorInteractor,
            ActualizarEstudianteInteractor actualizarEstudianteInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.actualizarAsesorInteractor = actualizarAsesorInteractor;
        this.actualizarEstudianteInteractor = actualizarEstudianteInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosUsuariosQueueConfig.USUARIO_MODIFICADO_QUEUE)
    public void onUsuarioModificado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, UsuarioModificadoPayload.class);

            logger.info(AsesorKey.LOG_USUARIO_MODIFICADO_RECIBIDO,
                    payload.idEvento(), payload.usuario());

            var resultadoAsesor = actualizarAsesorInteractor.ejecutar(ActualizarAsesorCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultadoAsesor) {
                case ActualizacionAsesorResult.Actualizada actualizada ->
                        logger.info(AsesorKey.LOG_ACTUALIZADO, actualizada.asesor());
                case ActualizacionAsesorResult.Descartada descartada ->
                        logger.info(AsesorKey.LOG_ACTUALIZACION_DESCARTADA, descartada.asesor(),
                                descartada.ocurridoEnVigente(), payload.ocurridoEn());
                case ActualizacionAsesorResult.NoReplicado noReplicado ->
                        logger.debug(AsesorKey.LOG_ACTUALIZACION_NO_REPLICADO,
                                noReplicado.asesor());
            }

            var resultadoEstudiante = actualizarEstudianteInteractor.ejecutar(ActualizarEstudianteCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultadoEstudiante) {
                case ActualizacionEstudianteResult.Actualizada actualizada ->
                        logger.info(EstudianteKey.LOG_ACTUALIZADO, actualizada.estudiante());
                case ActualizacionEstudianteResult.Descartada descartada ->
                        logger.info(EstudianteKey.LOG_ACTUALIZACION_DESCARTADA, descartada.estudiante(),
                                descartada.ocurridoEnVigente(), payload.ocurridoEn());
                case ActualizacionEstudianteResult.NoReplicado noReplicado ->
                        logger.debug(EstudianteKey.LOG_ACTUALIZACION_NO_REPLICADO,
                                noReplicado.estudiante());
            }
        });
    }
}
