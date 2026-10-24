package com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.primaryadapter.amqp.proyectos.asesorproyectogrado;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.interactor.FinalizarAsesorProyectoGradoInteractor;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.model.FinalizarAsesorProyectoGradoCommand;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.FinalizacionAsesorProyectoGradoResult;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosProyectosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AsesorProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AsesorProyectoGradoFinalizadoConsumer extends AbstractEventConsumer {

    private final FinalizarAsesorProyectoGradoInteractor finalizarAsesorProyectoGradoInteractor;
    private final AppLogger logger;

    public AsesorProyectoGradoFinalizadoConsumer(
            FinalizarAsesorProyectoGradoInteractor finalizarAsesorProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.finalizarAsesorProyectoGradoInteractor = finalizarAsesorProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosProyectosQueueConfig.ASESOR_PROYECTO_GRADO_FINALIZADO_QUEUE)
    public void onAsesorProyectoGradoFinalizado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AsesorProyectoGradoFinalizadoPayload.class);
            logger.info(AsesorProyectoGradoKey.LOG_FINALIZADO_RECIBIDO,
                    payload.idEvento(), payload.proyectoGradoId(), payload.asesorId(), payload.ocurridoEn());

            var resultado = finalizarAsesorProyectoGradoInteractor.ejecutar(
                    FinalizarAsesorProyectoGradoCommand.crear(
                            payload.asesorId(), payload.proyectoGradoId(), payload.ocurridoEn()));

            switch (resultado) {
                case FinalizacionAsesorProyectoGradoResult.Finalizada finalizada ->
                        logger.info(AsesorProyectoGradoKey.LOG_FINALIZADO,
                                finalizada.asesor(), finalizada.proyectoGrado());
                case FinalizacionAsesorProyectoGradoResult.Lapida lapida ->
                        logger.info(AsesorProyectoGradoKey.LOG_LAPIDA, lapida.asesor(), lapida.proyectoGrado());
                case FinalizacionAsesorProyectoGradoResult.Descartada descartada ->
                        logger.debug(AsesorProyectoGradoKey.LOG_FINALIZACION_DESCARTADA,
                                descartada.asesor(), descartada.proyectoGrado(), descartada.ocurridoEnVigente());
            }
        });
    }
}
