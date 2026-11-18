package com.arquisoft.entregables.infrastructure.artefacto.command.primaryadapter.amqp.artefactos.revisionasesor;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.RegistrarVersionCerradaArtefactoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.RegistrarVersionCerradaArtefactoCommand;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.RegistrarVersionCerradaArtefactoCommand.DatosRepositorio;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.RegistrarVersionCerradaArtefactoCommand.DatosVersion;
import com.arquisoft.entregables.application.artefacto.command.result.RegistroVersionCerradaArtefactoResult;
import com.arquisoft.entregables.infrastructure.artefacto.exception.ArchivoOrigenArtefactoNoDisponibleException;
import com.arquisoft.entregables.infrastructure.config.EntregablesArtefactosQueueConfig;
import com.arquisoft.shared.amqp.RabbitListenerConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class RevisionAsesorCerradaConsumer extends AbstractEventConsumer {

    private final RegistrarVersionCerradaArtefactoInteractor registrarVersionCerradaArtefactoInteractor;
    private final AppLogger logger;

    public RevisionAsesorCerradaConsumer(
            RegistrarVersionCerradaArtefactoInteractor registrarVersionCerradaArtefactoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.registrarVersionCerradaArtefactoInteractor = registrarVersionCerradaArtefactoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EntregablesArtefactosQueueConfig.REVISION_ASESOR_CERRADA_QUEUE,
            concurrency = RabbitListenerConfig.UN_SOLO_CONSUMIDOR)
    public void onRevisionAsesorCerrada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, RevisionAsesorCerradaPayload.class);
            logger.info(ArtefactoKey.LOG_VERSION_CERRADA_RECIBIDA,
                    payload.idEvento(), payload.artefacto(), payload.proyectoGrado(), payload.numeroVersion());

            var resultado = registrarVersionCerradaArtefactoInteractor.ejecutar(
                    RegistrarVersionCerradaArtefactoCommand.crear(
                            payload.artefacto(), payload.proyectoGrado(), payload.documento(),
                            new DatosVersion(payload.versionArtefacto(), payload.numeroVersion(),
                                    payload.uriArchivo(), payload.fechaVersion()),
                            new DatosRepositorio(payload.versionRepositorioArtefacto(), payload.repositorio(),
                                    payload.nombreRepositorio())));

            switch (resultado) {
                case RegistroVersionCerradaArtefactoResult.Registrada registrada ->
                        logger.info(ArtefactoKey.LOG_REGISTRADA, registrada.artefacto());
                case RegistroVersionCerradaArtefactoResult.Obsoleta obsoleta ->
                        logger.info(ArtefactoKey.LOG_OBSOLETA, obsoleta.artefacto());
                case RegistroVersionCerradaArtefactoResult.ProyectoCerrado cerrado ->
                        logger.info(ArtefactoKey.LOG_PROYECTO_CERRADO, cerrado.artefacto());
                case RegistroVersionCerradaArtefactoResult.OrigenNoDisponible origen ->
                        throw new ArchivoOrigenArtefactoNoDisponibleException(
                                origen.artefacto(), origen.nombreDocumento(), origen.claveOrigen());
            }
        });
    }
}
