package com.arquisoft.usuarios.application.usuario.command;

import com.arquisoft.shared.events.EventPublisher;
import com.arquisoft.shared.message.UsuariosMessages;
import com.arquisoft.usuarios.application.usuario.command.model.CrearUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.port.in.CrearUsuarioUseCase;
import com.arquisoft.usuarios.domain.usuario.aggregate.UsuarioAggregate;
import com.arquisoft.usuarios.domain.usuario.port.out.UsuarioOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementación del caso de uso de creación de usuario.
 *
 * <p>Orquesta el patrón aggregate-driven de publicación de eventos con Outbox Pattern:
 * <ol>
 *   <li>El aggregate {@link UsuarioAggregate#crear} decide qué evento emitir y lo acumula
 *       internamente — sin saber que existe RabbitMQ.</li>
 *   <li>Este use case persiste el aggregate.</li>
 *   <li>Drena los eventos y los entrega a {@link EventPublisher} ({@code SpringModulithEventPublisher}),
 *       que los persiste en {@code event_publication} dentro de la misma transacción.</li>
 *   <li>Tras el commit, Spring Modulith publica al exchange RabbitMQ de forma asíncrona.
 *       Si falla, el evento queda en BD y se reintenta automáticamente.</li>
 * </ol>
 *
 * <p>La anotación {@code @Transactional(transactionManager = "usuariosTransactionManager")}
 * garantiza que el {@code save} del aggregate y la inserción en {@code event_publication}
 * sean atómicos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CrearUsuarioUseCaseImpl implements CrearUsuarioUseCase {

    private final UsuarioOutputPort usuarioOutputPort;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public UUID ejecutar(CrearUsuarioCommand command) {
        UsuarioAggregate usuario = UsuarioAggregate.crear(command.email(), command.rol());

        usuarioOutputPort.save(usuario);

        usuario.drainUnPublishedEvents().forEach(eventPublisher::publish);

        log.info(UsuariosMessages.Usuario.LOG_CREADO, usuario.getId(), command.email(), command.rol().getCode());
        return usuario.getId();
    }
}
