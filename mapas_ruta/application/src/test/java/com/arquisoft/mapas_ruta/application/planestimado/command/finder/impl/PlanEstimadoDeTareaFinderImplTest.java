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
class PlanEstimadoDeTareaFinderImplTest {

    @Mock
    private PlanEstimadoOutputPort planEstimadoOutputPort;

    @InjectMocks
    private PlanEstimadoDeTareaFinderImpl finder;

    @Test
    void debeDevolverDominio_cuandoExiste() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var creadoEn = Instant.parse("2026-11-01T10:00:00Z");
        var entity = new PlanEstimadoEntity(UtilUUID.generarNuevoUUID(), tarea, 4, "desc", creadoEn);
        when(planEstimadoOutputPort.obtenerPorTarea(tarea)).thenReturn(Optional.of(entity));

        // Act
        var plan = finder.obtener(tarea);

        // Assert
        assertThat(plan.esVacio()).isFalse();
        assertThat(plan.getId()).isEqualTo(entity.id());
        assertThat(plan.getDescripcionEstimado()).isEqualTo("desc");
        assertThat(plan.getCreadoEn()).isEqualTo(creadoEn);
    }

    @Test
    void debeDevolverVacio_cuandoAusente() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        when(planEstimadoOutputPort.obtenerPorTarea(tarea)).thenReturn(Optional.empty());

        // Act
        var plan = finder.obtener(tarea);

        // Assert
        assertThat(plan).isSameAs(PlanEstimadoDomain.VACIO);
    }
}
