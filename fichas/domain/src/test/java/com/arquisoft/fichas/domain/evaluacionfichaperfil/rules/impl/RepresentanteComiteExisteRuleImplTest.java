package com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.representantecomite.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;

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
class RepresentanteComiteExisteRuleImplTest {

    @Mock
    private RepresentanteComiteOutputPort puerto;

    @InjectMocks
    private RepresentanteComiteExisteRuleImpl regla;

    @Test
    void debeLanzarExcepcion_cuandoLaReglaNoSeCumple() {
        // Arrange
        UUID entrada = UUID.randomUUID();
        when(puerto.existePorId(entrada)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoLaReglaSeCumple() {
        // Arrange
        UUID entrada = UUID.randomUUID();
        when(puerto.existePorId(entrada)).thenReturn(true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
