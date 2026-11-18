package com.arquisoft.mapas_ruta.application.planestimado.command.finder.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.entity.PlanEstimadoEntity;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanEstimadoPorIdFinderImplTest {

    @Mock
    private PlanEstimadoOutputPort planEstimadoOutputPort;

    @InjectMocks
    private PlanEstimadoPorIdFinderImpl finder;

    @Test
    void debeDevolverDominio_cuandoExiste() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var creadoEn = Instant.parse("2026-11-01T10:00:00Z");
        var entity = new PlanEstimadoEntity(id, tarea, 4, "desc", creadoEn);
        when(planEstimadoOutputPort.obtenerPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var plan = finder.obtener(id);

        // Assert
        assertThat(plan.esVacio()).isFalse();
        assertThat(plan.getId()).isEqualTo(id);
        assertThat(plan.getTarea()).isEqualTo(tarea);
        assertThat(plan.getDescripcionEstimado()).isEqualTo("desc");
        assertThat(plan.getCreadoEn()).isEqualTo(creadoEn);
    }

    @Test
    void debeDevolverVacio_cuandoAusente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(planEstimadoOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var plan = finder.obtener(id);

        // Assert
        assertThat(plan).isSameAs(PlanEstimadoDomain.VACIO);
    }
}
