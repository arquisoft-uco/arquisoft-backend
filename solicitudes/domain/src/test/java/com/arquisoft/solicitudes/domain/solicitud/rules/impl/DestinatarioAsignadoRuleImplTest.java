package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.DestinatarioNoAsignadoException;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaAsignacionResponsable;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DestinatarioAsignadoRuleImplTest {

    private final DestinatarioAsignadoRuleImpl regla = new DestinatarioAsignadoRuleImpl();

    @Test
    void debeNoLanzar_cuandoElDestinatarioEstaAsignadoAlEstudiante() {
        // Act & Assert
        assertThatCode(() -> regla.validar(
                new ExistenciaAsignacionResponsable(UUID.randomUUID(), UUID.randomUUID(), true)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDestinatarioNoAsignado_cuandoElDestinatarioNoEstaAsignado() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(
                new ExistenciaAsignacionResponsable(estudiante, destinatario, false)))
                .isInstanceOf(DestinatarioNoAsignadoException.class)
                .hasMessageContaining(destinatario.toString())
                .hasMessageContaining(estudiante.toString());
    }
}
