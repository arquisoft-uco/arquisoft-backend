package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.command.secondaryadapter.repository;

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
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriterioItemCualitativoJuradoCommandOutputAdapterTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private CriterioItemCualitativoJuradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CriterioItemCualitativoJuradoCommandOutputAdapter();
        ReflectionTestUtils.setField(adapter, "entityManager", entityManager);
    }

    @Test
    void debeRetornarIdsExistentes_cuandoElCriterioEstaEnBaseDeDatos() {
        // Arrange
        UUID existente = UUID.randomUUID();
        UUID faltante = UUID.randomUUID();
        Set<UUID> solicitados = Set.of(existente, faltante);
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), anySet())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(existente));

        // Act
        Set<UUID> resultado = adapter.consultarIdsExistentes(solicitados);

        // Assert
        assertThat(resultado).containsExactly(existente);
        verify(query).setParameter("ids", solicitados);
    }

    @Test
    void debeRetornarVacioSinConsultar_cuandoNoSeSolicitaNingunId() {
        // Act
        Set<UUID> resultado = adapter.consultarIdsExistentes(Set.of());

        // Assert
        assertThat(resultado).isEmpty();
        verifyNoInteractions(entityManager);
    }
}
