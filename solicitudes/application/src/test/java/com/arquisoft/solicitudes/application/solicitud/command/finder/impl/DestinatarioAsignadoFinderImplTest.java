package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.asignacionproyecto.command.secondaryport.AsignacionProyectoOutputPort;
import com.arquisoft.solicitudes.domain.solicitud.model.ConsultaAsignacionResponsable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DestinatarioAsignadoFinderImplTest {

    @Mock
    private AsignacionProyectoOutputPort asignacionProyectoOutputPort;

    @InjectMocks
    private DestinatarioAsignadoFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_cuandoConsultaLaAsignacion() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();
        when(asignacionProyectoOutputPort.esResponsableAsignado(estudiante, coordinador)).thenReturn(true);

        // Act & Assert
        assertThat(finder.obtener(new ConsultaAsignacionResponsable(estudiante, coordinador))).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElPuertoIndicaQueNoEstaAsignado() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();
        when(asignacionProyectoOutputPort.esResponsableAsignado(estudiante, coordinador)).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener(new ConsultaAsignacionResponsable(estudiante, coordinador))).isFalse();
    }
}
