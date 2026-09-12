package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.webclient;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.DestinatarioEvaluacionEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContactoEstudianteOutputAdapterTest {

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ContactoEstudianteOutputAdapter adapter;

    @Test
    void debeFabricarUnContactoPorCadaEstudianteSolicitado_cuandoConsultaContactos() {
        // Arrange
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        Set<UUID> estudiantes = Set.of(estudiante1, estudiante2);

        // Act
        List<DestinatarioEvaluacionEntity> resultado = adapter.consultarContactos(estudiantes);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(DestinatarioEvaluacionEntity::estudiante)
                .containsExactlyInAnyOrder(estudiante1, estudiante2);
        assertThat(resultado).allSatisfy(contacto ->
                assertThat(contacto.email()).isEqualTo(contacto.estudiante() + "@stub.local"));
        assertThat(resultado).allSatisfy(contacto ->
                assertThat(contacto.email()).hasSizeLessThanOrEqualTo(50));
        verify(logger).warn(any(ClaveMensaje.class), eq(2));
    }

    @Test
    void debeRetornarListaVacia_cuandoNoSeSolicitaNingunEstudiante() {
        // Act
        List<DestinatarioEvaluacionEntity> resultado = adapter.consultarContactos(Set.of());

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).warn(any(ClaveMensaje.class), eq(0));
    }
}
