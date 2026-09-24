package com.arquisoft.fichas.infrastructure.usuario.command.primaryadapter.amqp.usuarios.usuario;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.ActualizarAsesorFichaInteractor;
import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.ActualizarAsesorFichaCommand;
import com.arquisoft.fichas.application.asesorficha.command.result.ActualizacionAsesorFichaResult;
import com.arquisoft.fichas.application.estudiante.command.primaryport.interactor.ActualizarEstudianteInteractor;
import com.arquisoft.fichas.application.estudiante.command.primaryport.model.ActualizarEstudianteCommand;
import com.arquisoft.fichas.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.AsesorFichaKey;
import com.arquisoft.shared.message.key.fichas.EstudianteKey;
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
    private final ActualizarAsesorFichaInteractor actualizarAsesorFichaInteractor;
    private final AppLogger logger;

    public UsuarioModificadoConsumer(
            ActualizarEstudianteInteractor actualizarEstudianteInteractor,
            ActualizarAsesorFichaInteractor actualizarAsesorFichaInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.actualizarEstudianteInteractor = actualizarEstudianteInteractor;
        this.actualizarAsesorFichaInteractor = actualizarAsesorFichaInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = FichasUsuariosQueueConfig.USUARIO_MODIFICADO_QUEUE)
    public void onUsuarioModificado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, UsuarioModificadoPayload.class);

            logger.info(EstudianteKey.LOG_USUARIO_MODIFICADO_RECIBIDO,
                    payload.idEvento(), payload.usuario());

            registrarEstudiante(payload);
            registrarAsesorFicha(payload);
        });
    }

    private void registrarEstudiante(UsuarioModificadoPayload payload) {
        var resultado = actualizarEstudianteInteractor.ejecutar(ActualizarEstudianteCommand.crear(
                payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                payload.ocurridoEn()));

        switch (resultado) {
            case ActualizacionEstudianteResult.Actualizada actualizada ->
                    logger.info(EstudianteKey.LOG_ACTUALIZADO, actualizada.estudiante());
            case ActualizacionEstudianteResult.Descartada descartada ->
                    logger.info(EstudianteKey.LOG_ACTUALIZACION_DESCARTADA, descartada.estudiante(),
                            descartada.ocurridoEnVigente(), payload.ocurridoEn());
            case ActualizacionEstudianteResult.NoReplicado noReplicado ->
                    logger.debug(EstudianteKey.LOG_ACTUALIZACION_NO_REPLICADO, noReplicado.estudiante());
        }
    }

    private void registrarAsesorFicha(UsuarioModificadoPayload payload) {
        var resultado = actualizarAsesorFichaInteractor.ejecutar(ActualizarAsesorFichaCommand.crear(
                payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                payload.ocurridoEn()));

        switch (resultado) {
            case ActualizacionAsesorFichaResult.Actualizada actualizada ->
                    logger.info(AsesorFichaKey.LOG_ACTUALIZADO, actualizada.asesorFicha());
            case ActualizacionAsesorFichaResult.Descartada descartada ->
                    logger.info(AsesorFichaKey.LOG_ACTUALIZACION_DESCARTADA, descartada.asesorFicha(),
                            descartada.ocurridoEnVigente(), payload.ocurridoEn());
            case ActualizacionAsesorFichaResult.NoReplicado noReplicado ->
                    logger.debug(AsesorFichaKey.LOG_ACTUALIZACION_NO_REPLICADO,
                            noReplicado.asesorFicha());
        }
    }
}
