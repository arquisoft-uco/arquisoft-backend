package com.arquisoft.seguridad.infrastructure.config;

import com.arquisoft.seguridad.domain.usuario.event.UsuarioCreadoEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.jackson.JacksonMixin;

import java.util.UUID;

/**
 * Jackson Mixin para {@link UsuarioCreadoEvent}.
 *
 * <p>Resuelve la discrepancia entre el JSON persistido por Spring Modulith
 * ({@code aggregateId}) y el nombre del parámetro del constructor del evento
 * ({@code usuarioId}). Sin este mixin, Jackson pasa {@code null} al constructor
 * al republicar eventos pendientes en el arranque, causando NPE.
 *
 * <p>Spring Boot 4.x detecta automáticamente las clases anotadas con
 * {@code @JacksonMixin} y las registra en el {@code ObjectMapper} global,
 * que es el mismo que usa {@code JacksonEventSerializer} de Spring Modulith.
 */
@JacksonMixin(type = UsuarioCreadoEvent.class)
abstract class UsuarioCreadoEventMixin {

    @JsonCreator
    UsuarioCreadoEventMixin(
            @JsonProperty("aggregateId") UUID usuarioId,
            @JsonProperty("email") String email,
            @JsonProperty("rol") String rol) {
    }
}
