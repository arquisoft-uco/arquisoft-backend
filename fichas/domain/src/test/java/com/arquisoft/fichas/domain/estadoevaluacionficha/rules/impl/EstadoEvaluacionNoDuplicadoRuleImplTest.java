package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.secondaryport.EstadoEvaluacionFichaOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoEvaluacionNoDuplicadoRuleImplTest {

    @Mock
    private EstadoEvaluacionFichaOutputPort puerto;

    @InjectMocks
    private EstadoEvaluacionNoDuplicadoRuleImpl regla;

    @Test
    void debeLanzarExcepcion_cuandoLaReglaNoSeCumple() {
        // Arrange
        var entrada = AgregacionEstadoEvaluacionFichaDomain.crear(UUID.randomUUID(), "APROBADA", UUID.randomUUID());
        when(puerto.existePorEvaluacionYEstado(entrada.getEvaluacionFichaPerfil(), entrada.getEstadoEvaluacion().getId()))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(EstadoEvaluacionDuplicadoException.class);
    }

    @Test
    void debePasar_cuandoLaReglaSeCumple() {
        // Arrange
        var entrada = AgregacionEstadoEvaluacionFichaDomain.crear(UUID.randomUUID(), "APROBADA", UUID.randomUUID());
        when(puerto.existePorEvaluacionYEstado(entrada.getEvaluacionFichaPerfil(), entrada.getEstadoEvaluacion().getId()))
                .thenReturn(false);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
