package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.asesorficha.port.out.AsesorFichaOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;

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
class AsesorFichaExisteRuleImplTest {

    @Mock
    private AsesorFichaOutputPort puerto;

    @InjectMocks
    private AsesorFichaExisteRuleImpl regla;

    @Test
    void debeLanzarExcepcion_cuandoLaReglaNoSeCumple() {
        // Arrange
        UUID entrada = UUID.randomUUID();
        when(puerto.existePorId(entrada)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);
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
