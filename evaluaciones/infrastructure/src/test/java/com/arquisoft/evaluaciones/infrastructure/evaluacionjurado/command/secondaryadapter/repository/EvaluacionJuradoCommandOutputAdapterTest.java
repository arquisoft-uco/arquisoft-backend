package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.ContextoRegistroEvaluacionJuradoEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionJuradoCommandOutputAdapterTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private EvaluacionJuradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionJuradoCommandOutputAdapter();
        ReflectionTestUtils.setField(adapter, "entityManager", entityManager);
    }

    @Test
    void debeMapearElContexto_cuandoLaEvaluacionDeJuradoExiste() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID evaluacion = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID entregable = UUID.randomUUID();
        Object[] fila = {evaluacionJurado, evaluacion, jurado, "PENDIENTE", entregable, "Proyecto X", 2};
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.<Object[]>of(fila));

        // Act
        Optional<ContextoRegistroEvaluacionJuradoEntity> resultado = adapter.obtenerContextoBloqueado(evaluacionJurado);

        // Assert
        verify(query).setParameter("evaluacionJurado", evaluacionJurado);
        assertThat(resultado).hasValueSatisfying(contexto -> {
            assertThat(contexto.id()).isEqualTo(evaluacionJurado);
            assertThat(contexto.evaluacion()).isEqualTo(evaluacion);
            assertThat(contexto.jurado()).isEqualTo(jurado);
            assertThat(contexto.estado()).isEqualTo("PENDIENTE");
            assertThat(contexto.entregable()).isEqualTo(entregable);
            assertThat(contexto.proyecto()).isEqualTo("Proyecto X");
            assertThat(contexto.versionEntregable()).isEqualTo(2);
        });
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionDeJuradoNoExiste() {
        // Arrange
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        // Act
        Optional<ContextoRegistroEvaluacionJuradoEntity> resultado = adapter.obtenerContextoBloqueado(UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
