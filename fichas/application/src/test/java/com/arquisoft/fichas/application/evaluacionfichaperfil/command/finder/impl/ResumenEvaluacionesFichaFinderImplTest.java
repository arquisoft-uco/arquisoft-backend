package com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.ConteoEvaluacionesPorEstadoEntity;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ConteoEvaluacionesPorEstado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumenEvaluacionesFichaFinderImplTest {

    @Mock
    private EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @InjectMocks
    private ResumenEvaluacionesFichaFinderImpl finder;

    @Test
    void debeResolverElEstadoDelCatalogoYEnvolverLosConteos_cuandoHayEvaluaciones() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        when(evaluacionFichaPerfilOutputPort.contarEvaluacionesDeFichaPorEstadoEvaluacionActual(fichaPerfil))
                .thenReturn(List.of(
                        new ConteoEvaluacionesPorEstadoEntity("APROBADA", 2, 1),
                        new ConteoEvaluacionesPorEstadoEntity("DESCARTADA", 1, 1)));

        // Act
        var resumen = finder.obtener(fichaPerfil);

        // Assert
        assertThat(resumen.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(resumen.conteos()).containsExactly(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA, 2, 1),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.DESCARTADA, 1, 1));
    }

    @Test
    void debeDevolverResumenSinConteos_cuandoLaFichaNoTieneEvaluaciones() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        when(evaluacionFichaPerfilOutputPort.contarEvaluacionesDeFichaPorEstadoEvaluacionActual(fichaPerfil))
                .thenReturn(List.of());

        // Act
        var resumen = finder.obtener(fichaPerfil);

        // Assert
        assertThat(resumen).isNotNull();
        assertThat(resumen.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(resumen.conteos()).isEmpty();
        assertThat(resumen.finalizadas()).isZero();
    }
}
