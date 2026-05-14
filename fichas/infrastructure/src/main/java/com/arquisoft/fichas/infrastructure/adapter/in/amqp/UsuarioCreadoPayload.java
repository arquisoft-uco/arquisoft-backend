package com.arquisoft.fichas.infrastructure.adapter.in.amqp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Payload local del evento {@code seguridad.usuario.creado} tal como llega por RabbitMQ.
 *
 * <p><b>Diseño intencional:</b> este record pertenece únicamente al contexto {@code fichas}
 * y NO importa ninguna clase de {@code seguridad:domain}. Esto mantiene el desacoplamiento
 * entre bounded contexts — fichas solo conoce la <em>forma del mensaje</em>, no el
 * modelo de dominio del productor.
 *
 * <p>{@code @JsonIgnoreProperties(ignoreUnknown = true)} garantiza que campos adicionales
 * del evento ({@code occurredAt}, {@code eventType}, {@code eventTopic}, etc.) sean ignorados
 * silenciosamente en lugar de causar errores de deserialización.
 *
 * <p>El campo {@code aggregateId} corresponde al {@code UUID} del usuario en el contexto
 * seguridad, serializado como String en el evento de dominio base.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioCreadoPayload(
        String eventId,
        String aggregateId,
        String email,
        String rol
) {
}
