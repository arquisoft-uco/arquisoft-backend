package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.amqp.fichas.fichaperfil;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.RegistrarProyectoGradoInteractor;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.RegistrarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.result.RegistroProyectoGradoResult;
import com.arquisoft.proyectos.infrastructure.config.ProyectosFichasQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
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
public class FichaPerfilAprobadaConsumer extends AbstractEventConsumer {

    private final RegistrarProyectoGradoInteractor registrarProyectoGradoInteractor;
    private final AppLogger logger;

    public FichaPerfilAprobadaConsumer(
            RegistrarProyectoGradoInteractor registrarProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.registrarProyectoGradoInteractor = registrarProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ProyectosFichasQueueConfig.FICHA_PERFIL_APROBADA_QUEUE)
    public void onFichaPerfilAprobada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, FichaPerfilAprobadaPayload.class);
            var estudiantes = UtilColeccion.aplicarPorDefecto(payload.estudiantes()).stream()
                    .map(FichaPerfilAprobadaPayload.IntegrantePayload::estudiante)
                    .toList();

            logger.info(ProyectoGradoKey.LOG_FICHA_APROBADA_RECIBIDA,
                    payload.idEvento(), payload.fichaPerfilId(), estudiantes.size());

            var resultado = registrarProyectoGradoInteractor.ejecutar(RegistrarProyectoGradoCommand.crear(
                    payload.fichaPerfilId(), payload.tituloProyecto(), payload.coordinadorId(), estudiantes));

            switch (resultado) {
                case RegistroProyectoGradoResult.Registrado registrado ->
                        logger.info(ProyectoGradoKey.LOG_REGISTRADO,
                                registrado.proyectoGrado(), registrado.fichaPerfil());
                case RegistroProyectoGradoResult.Duplicado duplicado ->
                        logger.info(ProyectoGradoKey.LOG_DUPLICADO, duplicado.fichaPerfil());
            }
        });
    }
}
