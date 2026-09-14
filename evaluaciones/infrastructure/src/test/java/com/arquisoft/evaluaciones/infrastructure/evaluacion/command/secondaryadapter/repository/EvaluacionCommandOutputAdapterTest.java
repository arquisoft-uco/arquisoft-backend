package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionCommandOutputAdapterTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private EvaluacionCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionCommandOutputAdapter();
        ReflectionTestUtils.setField(adapter, "entityManager", entityManager);
    }

    @Test
    void debeEjecutarElUpdateConLosParametrosCorrectos_cuandoActualizaElEstado() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);

        // Act
        adapter.actualizarEstado(evaluacion, "EN_PROGRESO");

        // Assert
        verify(query).setParameter("estado", "EN_PROGRESO");
        verify(query).setParameter("evaluacion", evaluacion);
        verify(query).executeUpdate();
    }
}
