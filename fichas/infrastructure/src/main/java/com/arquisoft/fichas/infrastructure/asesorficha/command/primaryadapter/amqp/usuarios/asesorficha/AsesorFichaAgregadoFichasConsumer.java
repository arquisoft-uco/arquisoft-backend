package com.arquisoft.fichas.infrastructure.asesorficha.command.primaryadapter.amqp.usuarios.asesorficha;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.AgregarAsesorFichaFichasInteractor;
import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.AgregarAsesorFichaCommand;
import com.arquisoft.fichas.application.asesorficha.command.result.AgregacionAsesorFichaResult;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.AsesorFichaKey;
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
public class AsesorFichaAgregadoFichasConsumer extends AbstractEventConsumer {

    private final AgregarAsesorFichaFichasInteractor agregarAsesorFichaFichasInteractor;
    private final AppLogger logger;

    public AsesorFichaAgregadoFichasConsumer(
            AgregarAsesorFichaFichasInteractor agregarAsesorFichaFichasInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.agregarAsesorFichaFichasInteractor = agregarAsesorFichaFichasInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = FichasUsuariosQueueConfig.ASESOR_FICHA_AGREGADO_QUEUE)
    public void onAsesorFichaAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AsesorFichaAgregadoPayload.class);

            logger.info(AsesorFichaKey.LOG_AGREGADO_RECIBIDO,
                    payload.idEvento(), payload.usuario(), UtilTexto.enmascararCorreo(payload.email()));

            var resultado = agregarAsesorFichaFichasInteractor.ejecutar(AgregarAsesorFichaCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case AgregacionAsesorFichaResult.Agregada agregada ->
                        logger.info(AsesorFichaKey.LOG_AGREGADO, agregada.asesorFicha());
                case AgregacionAsesorFichaResult.Duplicada duplicada ->
                        logger.info(AsesorFichaKey.LOG_DUPLICADO, duplicada.asesorFicha());
                case AgregacionAsesorFichaResult.Descartada descartada ->
                        logger.debug(AsesorFichaKey.LOG_DESCARTADO,
                                descartada.asesorFicha(), descartada.ocurridoEnVigente());
            }
        });
    }
}
