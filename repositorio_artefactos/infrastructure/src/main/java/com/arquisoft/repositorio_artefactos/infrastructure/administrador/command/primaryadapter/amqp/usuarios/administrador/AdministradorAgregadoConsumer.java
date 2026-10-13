package com.arquisoft.repositorio_artefactos.infrastructure.administrador.command.primaryadapter.amqp.usuarios.administrador;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor.AgregarAdministradorInteractor;
import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.AgregarAdministradorCommand;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.AgregacionAdministradorResult;
import com.arquisoft.repositorio_artefactos.infrastructure.config.RepositorioArtefactosUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.AdministradorKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilTexto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AdministradorAgregadoConsumer extends AbstractEventConsumer {

    private final AgregarAdministradorInteractor agregarAdministradorInteractor;
    private final AppLogger logger;

    public AdministradorAgregadoConsumer(
            AgregarAdministradorInteractor agregarAdministradorInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.agregarAdministradorInteractor = agregarAdministradorInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = RepositorioArtefactosUsuariosQueueConfig.ADMINISTRADOR_AGREGADO_QUEUE)
    public void onAdministradorAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AdministradorAgregadoPayload.class);

            logger.info(AdministradorKey.LOG_AGREGADO_RECIBIDO,
                    payload.idEvento(), payload.usuario(), UtilTexto.enmascararCorreo(payload.email()));

            var resultado = agregarAdministradorInteractor.ejecutar(AgregarAdministradorCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case AgregacionAdministradorResult.Agregada agregada ->
                        logger.info(AdministradorKey.LOG_AGREGADO, agregada.administrador());
                case AgregacionAdministradorResult.Reactivada reactivada ->
                        logger.info(AdministradorKey.LOG_REACTIVADO, reactivada.administrador());
                case AgregacionAdministradorResult.Duplicada duplicada ->
                        logger.info(AdministradorKey.LOG_DUPLICADO, duplicada.administrador());
                case AgregacionAdministradorResult.Descartada descartada ->
                        logger.debug(AdministradorKey.LOG_DESCARTADO,
                                descartada.administrador(), descartada.ocurridoEnVigente());
            }
        });
    }
}
