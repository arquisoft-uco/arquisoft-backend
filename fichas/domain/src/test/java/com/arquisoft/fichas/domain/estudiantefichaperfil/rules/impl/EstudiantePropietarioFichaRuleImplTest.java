package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropietarioFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
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
class EstudiantePropietarioFichaRuleImplTest {

    @Mock
    private EstudianteFichaPerfilOutputPort puerto;

    @InjectMocks
    private EstudiantePropietarioFichaRuleImpl regla;

    @Test
    void debeLanzarExcepcion_cuandoLaReglaNoSeCumple() {
        // Arrange
        var entrada = new PropietarioFicha(UUID.randomUUID(), UUID.randomUUID());
        when(puerto.existePorFichaYEstudiante(entrada.fichaPerfil(), entrada.estudiante())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(FichaNoPropietarioException.class);
    }

    @Test
    void debePasar_cuandoLaReglaSeCumple() {
        // Arrange
        var entrada = new PropietarioFicha(UUID.randomUUID(), UUID.randomUUID());
        when(puerto.existePorFichaYEstudiante(entrada.fichaPerfil(), entrada.estudiante())).thenReturn(true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
