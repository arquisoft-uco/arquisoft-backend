package com.arquisoft.seguridad.application.usecase;

import com.arquisoft.seguridad.domain.model.Usuario;
import com.arquisoft.seguridad.domain.model.UsuarioRole;
import com.arquisoft.seguridad.domain.port.in.CrearUsuarioUseCase;
import com.arquisoft.seguridad.domain.port.out.UsuarioRepositoryPort;
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
 *   <li>El aggregate {@link Usuario#crear} decide qué evento emitir y lo acumula
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
public class CrearUsuarioUseCaseImpl implements CrearUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final EventPublisher eventPublisher;

    @Override
    public UUID crear(String email, UsuarioRole rol) {
        // 1. El aggregate valida invariantes y acumula UsuarioCreadoEvent en memoria
        Usuario usuario = Usuario.crear(email, rol);

        // 2. Persistir el aggregate (mock en memoria por ahora)
        usuarioRepository.save(usuario);

        // 3. Drenar y publicar los eventos que el aggregate acumuló
        //    El aggregate sabe QUÉ emitir; el use case solo ejecuta el drenaje mecánico
        usuario.getUnPublishedEvents().forEach(eventPublisher::publish);
        usuario.clearUnPublishedEvents();

        log.info("Usuario creado: id={} email={} rol={}", usuario.getId(), email, rol.getCode());
        return usuario.getId();
    }
}
