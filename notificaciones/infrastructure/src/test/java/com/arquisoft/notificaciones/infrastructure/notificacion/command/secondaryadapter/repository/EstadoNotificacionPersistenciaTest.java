package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.repository;

import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoNotificacionPersistenciaTest {

    @Test
    void debeResolverContraElCatalogoDeDominio_cuandoSeRecorreCadaCodigo() {
        // Assert — el codigo que viaja a la consulta tiene que ser el que el dominio persiste
        for (EstadoNotificacionPersistencia estado : EstadoNotificacionPersistencia.values()) {
            assertThat(EstadoNotificacion.desde(estado.getCodigo()).getId())
                    .isEqualTo(EstadoNotificacion.valueOf(estado.name()).getId());
        }
    }

    @Test
    void debeDeclararLosMismosEstadosQueElDominio_cuandoSeComparanAmbasTablas() {
        // Arrange
        Set<String> enInfraestructura = Arrays.stream(EstadoNotificacionPersistencia.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
        Set<String> enDominio = Arrays.stream(EstadoNotificacion.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        // Assert — si el dominio gana un estado, esta tabla tiene que ganarlo tambien
        assertThat(enInfraestructura).isEqualTo(enDominio);
    }
}
