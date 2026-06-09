package com.arquisoft.seguridad.application.usuario.command;

import com.arquisoft.seguridad.application.usuario.command.model.CrearUsuarioCommand;
import com.arquisoft.seguridad.application.usuario.command.port.in.CrearUsuarioInputPort;
import com.arquisoft.seguridad.domain.usuario.aggregate.UsuarioAggregate;
import com.arquisoft.seguridad.domain.usuario.port.out.UsuarioOutputPort;
import com.arquisoft.shared.events.EventPublisher;
import com.arquisoft.shared.message.SeguridadMessages;
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
 * <p>La anotación {@code @Transactional} garantiza que el {@code save} del aggregate y la
 * inserción en {@code event_publication} sean atómicos (usando {@code seguridadTransactionManager}).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CrearUsuarioUseCase implements CrearUsuarioInputPort {

    private final UsuarioOutputPort usuarioOutputPort;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public UUID ejecutar(CrearUsuarioCommand command) {
        // 1. El aggregate valida invariantes y acumula UsuarioCreadoEvent en memoria
        UsuarioAggregate usuario = UsuarioAggregate.crear(command.email(), command.rol());

        // 2. Persistir el aggregate (mock en memoria por ahora)
        usuarioOutputPort.save(usuario);

        // 3. Drenar y entregar eventos a SpringModulithEventPublisher, que los persiste en
        //    event_publication (misma transacción). Spring Modulith los publica al broker
        //    tras el commit — si falla, los reintenta desde BD automáticamente.
        usuario.drainUnPublishedEvents().forEach(eventPublisher::publish);

        log.info(SeguridadMessages.Usuario.LOG_CREADO, usuario.getId(), command.email(), command.rol().getCode());
        return usuario.getId();
    }
}
