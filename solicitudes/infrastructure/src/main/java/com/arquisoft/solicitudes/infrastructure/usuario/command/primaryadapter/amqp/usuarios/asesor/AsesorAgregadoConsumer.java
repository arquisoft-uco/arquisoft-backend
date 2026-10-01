package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.asesor;

import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.UsuarioReplicaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.RegistrarUsuarioInteractor;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.solicitudes.application.usuario.command.result.AgregacionUsuarioResult;
import com.arquisoft.solicitudes.infrastructure.config.SolicitudesUsuariosQueueConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AsesorAgregadoConsumer extends AbstractEventConsumer {

    private final RegistrarUsuarioInteractor registrarUsuarioInteractor;
    private final AppLogger logger;

    public AsesorAgregadoConsumer(
            RegistrarUsuarioInteractor registrarUsuarioInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.registrarUsuarioInteractor = registrarUsuarioInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = SolicitudesUsuariosQueueConfig.ASESOR_AGREGADO_QUEUE)
    public void onAsesorAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AsesorAgregadoPayload.class);

            logger.info(UsuarioReplicaKey.LOG_USUARIO_AGREGADO_RECIBIDO,
                    payload.usuario(), payload.identificador(), payload.nombre(),
                    UtilTexto.enmascararCorreo(payload.email()));

            var comando = RegistrarUsuarioCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn());
            var resultado = registrarUsuarioInteractor.ejecutar(comando);

            switch (resultado) {
                case AgregacionUsuarioResult.Agregada agregada ->
                        logger.info(UsuarioReplicaKey.LOG_AGREGADO, agregada.usuario());
                case AgregacionUsuarioResult.Duplicada duplicada ->
                        logger.info(UsuarioReplicaKey.LOG_DUPLICADO, duplicada.usuario());
                case AgregacionUsuarioResult.Descartada descartada ->
                        logger.debug(UsuarioReplicaKey.LOG_DESCARTADO,
                                descartada.usuario(), descartada.ocurridoEnVigente());
            }
        });
    }
}
