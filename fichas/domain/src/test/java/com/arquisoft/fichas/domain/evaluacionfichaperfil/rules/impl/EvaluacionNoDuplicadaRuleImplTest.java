package com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.EvaluacionRepresentanteFichaCriteria;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
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
class EvaluacionNoDuplicadaRuleImplTest {

    @Mock
    private EvaluacionFichaPerfilOutputPort puerto;

    @InjectMocks
    private EvaluacionNoDuplicadaRuleImpl regla;

    @Test
    void debeLanzarExcepcion_cuandoLaReglaNoSeCumple() {
        // Arrange
        var entrada = new EvaluacionRepresentanteFichaCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(puerto.existePorRepresentanteYFicha(entrada.representanteComite(), entrada.fichaPerfil())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(EvaluacionFichaPerfilDuplicadaException.class);
    }

    @Test
    void debePasar_cuandoLaReglaSeCumple() {
        // Arrange
        var entrada = new EvaluacionRepresentanteFichaCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(puerto.existePorRepresentanteYFicha(entrada.representanteComite(), entrada.fichaPerfil())).thenReturn(false);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
