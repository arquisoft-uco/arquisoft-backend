package com.arquisoft.shared.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainEventTest {

    private static final String TIPO = "EventoDePrueba";

    private static final class EventoDePrueba extends DomainEvent {
        private EventoDePrueba(String tema) {
            super(tema, TIPO);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "usuarios.usuario.creado",
            "fichas.ficha_perfil.asesor_cambiado"
    })
    void debeAceptarElTema_cuandoSigueElFormatoDeTresSegmentos(String tema) {
        // Act & Assert
        assertThatCode(() -> new EventoDePrueba(tema)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "Usuarios.usuario.creado",
            "usuarios.usuario",
            "usuarios.usuario.creado.extra",
            "usuarios.usuario.creado ",
            "usuarios..creado",
            "_usuarios.usuario.creado",
            "usuarios.usuario.creado1",
            ""
    })
    void debeRechazarElTema_cuandoNoSigueElFormato(String tema) {
        // Act & Assert
        assertThatThrownBy(() -> new EventoDePrueba(tema))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("{contexto}.{entidad}.{accion}");
    }

    @Test
    void debeGenerarIdentidadPropia_cuandoSeConstruyeElEvento() {
        // Act
        var primero = new EventoDePrueba("usuarios.usuario.creado");
        var segundo = new EventoDePrueba("usuarios.usuario.creado");

        // Assert
        assertThat(primero.getIdEvento()).isNotBlank().isNotEqualTo(segundo.getIdEvento());
        assertThat(primero.getOcurridoEn()).isNotNull();
        assertThat(primero.getTipoEvento()).isEqualTo(TIPO);
        assertThat(primero.getTemaEvento()).isEqualTo("usuarios.usuario.creado");
    }
}
