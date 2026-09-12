package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.exception.ContactosEvaluacionNoDisponiblesException;
import com.arquisoft.shared.logger.AppLogger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// SELECT ... uuid columns via SQL nativo: probado con mocks de EntityManager/Query porque el
// driver H2 de @DataJpaTest devuelve byte[] para columnas UUID en consultas nativas (no
// java.util.UUID, como sí hace el driver real de PostgreSQL), lo que produciria un falso negativo
// aqui. Ver PLAN-HU-215.md seccion 12: "no H2/mocks para demostrar" comportamiento especifico de
// PostgreSQL.
@ExtendWith(MockitoExtension.class)
class ProyectoEstudianteAccesoCommandOutputAdapterAccesoTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private ProyectoEstudianteAccesoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProyectoEstudianteAccesoCommandOutputAdapter(
                mock(ProyectoEstudianteAccesoCommandRepository.class), mock(AppLogger.class));
        ReflectionTestUtils.setField(adapter, "entityManager", entityManager);
    }

    @Test
    void debeRetornarEstudiantes_cuandoHayAccesoActivoParaElEntregable() {
        // Arrange
        UUID entregable = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(estudiante));

        // Act
        Set<UUID> resultado = adapter.obtenerEstudiantesConAccesoPorEntregable(entregable);

        // Assert
        assertThat(resultado).containsExactly(estudiante);
        verify(query).setParameter("entregable", entregable);
    }

    @Test
    void debeLanzarExcepcion_cuandoNoHayNingunEstudianteActivoParaElEntregable() {
        // Arrange
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> adapter.obtenerEstudiantesConAccesoPorEntregable(UUID.randomUUID()))
                .isInstanceOf(ContactosEvaluacionNoDisponiblesException.class);
    }
}
