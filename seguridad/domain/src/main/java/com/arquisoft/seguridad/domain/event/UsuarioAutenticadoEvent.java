package com.arquisoft.seguridad.domain.event;

import com.arquisoft.shared.events.DomainEvent;

/**
 * Evento de dominio publicado cuando un usuario se autentica exitosamente.
 *
 * <p>El {@code aggregateId} es el email del usuario — identificador disponible
 * en el flujo de login sin necesidad de decodificar el JWT.
 * En una implementación futura se puede reemplazar por el {@code sub} (UUID de Keycloak)
 * extrayéndolo del access_token retornado por AuthResult.
 *
 * <p>Topic: {@code seguridad.usuario.autenticado}
 * Formato: {@code {contexto}.{entidad}.{accion}}
 */
public class UsuarioAutenticadoEvent extends DomainEvent {

    private final String email;

    public UsuarioAutenticadoEvent(String email) {
        super(email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getEventTopic() {
        return "seguridad.usuario.autenticado";
    }
}
