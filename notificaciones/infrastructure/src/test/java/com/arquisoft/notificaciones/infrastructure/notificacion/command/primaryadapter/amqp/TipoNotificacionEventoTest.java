package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp;

import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class TipoNotificacionEventoTest {

    @Test
    void debeResolverContraElCatalogoDeDominio_cuandoSeRecorreCadaCodigo() {
        // Assert — el codigo que el consumidor envia tiene que ser resoluble por el Command
        for (TipoNotificacionEvento tipo : TipoNotificacionEvento.values()) {
            assertThat(TipoNotificacion.desde(tipo.getCodigo()))
                    .isEqualTo(TipoNotificacion.valueOf(tipo.name()));
        }
    }

    @Test
    void debeDeclararLosMismosTiposQueElDominio_cuandoSeComparanAmbasTablas() {
        // Arrange
        Set<String> enInfraestructura = Arrays.stream(TipoNotificacionEvento.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
        Set<String> enDominio = Arrays.stream(TipoNotificacion.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        // Assert — si el dominio gana un tipo, esta tabla tiene que ganarlo tambien
        assertThat(enInfraestructura).isEqualTo(enDominio);
    }
}
