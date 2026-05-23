package com.arquisoft.seguridad.application.usuario.command;

import com.arquisoft.seguridad.domain.model.UsuarioAggregate;
import com.arquisoft.seguridad.domain.model.UsuarioRole;
import com.arquisoft.seguridad.domain.port.out.UsuarioOutputPort;
import com.arquisoft.shared.events.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementación del caso de uso de creación de usuario.
 *
 * <p>Orquesta el patrón aggregate-driven de publicación de eventos:
 * <ol>
 *   <li>El aggregate {@link UsuarioAggregate#crear} decide qué evento emitir y lo acumula
 *       internamente — sin saber que existe RabbitMQ.</li>
 *   <li>Este use case persiste el aggregate.</li>
 *   <li>Drena mecánicamente los eventos acumulados y los entrega a {@link EventPublisher}
 *       (implementado en infraestructura por {@code RabbitMQEventPublisher}).</li>
 * </ol>
 *
 * <p>La decisión de <i>qué</i> publicar y <i>cuándo</i> la tomó el aggregate.
 * Este use case solo orquesta: crea → persiste → drena.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CrearUsuarioUseCaseImpl implements CrearUsuarioInputPort {

    private final UsuarioOutputPort usuarioOutputPort;
    private final EventPublisher eventPublisher;

    @Override
    public UUID crear(String email, UsuarioRole rol) {
        // 1. El aggregate valida invariantes y acumula UsuarioCreadoEvent en memoria
        UsuarioAggregate usuario = UsuarioAggregate.crear(email, rol);

        // 2. Persistir el aggregate (mock en memoria por ahora)
        usuarioOutputPort.save(usuario);

        // 3. Drenar y publicar los eventos que el aggregate acumuló — operación atómica:
        //    drainUnPublishedEvents() retorna y limpia en un solo paso, evitando
        //    que se olvide la limpieza o que se limpie sin publicar.
        //
        //    DEUDA TÉCNICA (Outbox Pattern): los pasos 2 y 3 no son atómicos.
        //    Si el broker RabbitMQ está caído y los 3 reintentos de RabbitMQEventPublisher
        //    se agotan, el usuario quedará persistido pero fichas no recibirá el evento.
        //    Solución correcta: persistir el evento en tabla domain_events dentro de la
        //    misma transacción JDBC (Outbox Pattern), y publicar desde un scheduler/CDC.
        //    Pendiente de implementar cuando UsuarioOutputAdapter sea reemplazado por JPA.
        usuario.drainUnPublishedEvents().forEach(eventPublisher::publish);

        log.info("Usuario creado: id={} email={} rol={}", usuario.getId(), email, rol.getCode());
        return usuario.getId();
    }
}
