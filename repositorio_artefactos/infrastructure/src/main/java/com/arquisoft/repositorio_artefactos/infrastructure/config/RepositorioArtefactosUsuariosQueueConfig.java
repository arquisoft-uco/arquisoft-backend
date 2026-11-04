package com.arquisoft.repositorio_artefactos.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositorioArtefactosUsuariosQueueConfig {

    public static final String ADMINISTRADOR_AGREGADO_QUEUE =
            RepositorioArtefactosQueues.PREFIJO + EventTopics.Usuarios.ADMINISTRADOR_AGREGADO;

    @Bean
    public Declarables repositorioArtefactosAdministradorAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ADMINISTRADOR_AGREGADO_QUEUE,
                EventTopics.Usuarios.ADMINISTRADOR_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String ADMINISTRADOR_REMOVIDO_QUEUE =
            RepositorioArtefactosQueues.PREFIJO + EventTopics.Usuarios.ADMINISTRADOR_REMOVIDO;

    @Bean
    public Declarables repositorioArtefactosAdministradorRemovidoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ADMINISTRADOR_REMOVIDO_QUEUE,
                EventTopics.Usuarios.ADMINISTRADOR_REMOVIDO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String USUARIO_MODIFICADO_QUEUE =
            RepositorioArtefactosQueues.PREFIJO + EventTopics.Usuarios.USUARIO_MODIFICADO;

    @Bean
    public Declarables repositorioArtefactosUsuarioModificadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                USUARIO_MODIFICADO_QUEUE,
                EventTopics.Usuarios.USUARIO_MODIFICADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
