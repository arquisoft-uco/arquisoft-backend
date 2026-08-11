package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;
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
class RepresentantePropietarioEvaluacionRuleImplTest {

    @Mock
    private EvaluacionFichaPerfilOutputPort puerto;

    @InjectMocks
    private RepresentantePropietarioEvaluacionRuleImpl regla;

    @Test
    void debeLanzarExcepcion_cuandoLaReglaNoSeCumple() {
        // Arrange
        var entrada = AgregacionEstadoEvaluacionFichaDomain.crear(UUID.randomUUID(), "APROBADA", UUID.randomUUID());
        when(puerto.esRepresentantePropietario(entrada.getEvaluacionFichaPerfil(), entrada.getRepresentanteComite()))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);
    }

    @Test
    void debePasar_cuandoLaReglaSeCumple() {
        // Arrange
        var entrada = AgregacionEstadoEvaluacionFichaDomain.crear(UUID.randomUUID(), "APROBADA", UUID.randomUUID());
        when(puerto.esRepresentantePropietario(entrada.getEvaluacionFichaPerfil(), entrada.getRepresentanteComite()))
                .thenReturn(true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
