package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.RemocionEstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
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
class VinculoEstudianteFichaExisteRuleImplTest {

    @Mock
    private EstudianteFichaPerfilOutputPort puerto;

    @InjectMocks
    private VinculoEstudianteFichaExisteRuleImpl regla;

    @Test
    void debeLanzarExcepcion_cuandoLaReglaNoSeCumple() {
        // Arrange
        var entrada = RemocionEstudianteFichaPerfilDomain.crear(UUID.randomUUID(), UUID.randomUUID());
        when(puerto.existePorFichaYEstudiante(entrada.getFichaPerfil(), entrada.getEstudiante())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(EstudianteFichaPerfilNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoLaReglaSeCumple() {
        // Arrange
        var entrada = RemocionEstudianteFichaPerfilDomain.crear(UUID.randomUUID(), UUID.randomUUID());
        when(puerto.existePorFichaYEstudiante(entrada.getFichaPerfil(), entrada.getEstudiante())).thenReturn(true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
