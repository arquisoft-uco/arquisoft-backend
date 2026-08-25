package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Los tres finders de estudiantefichaperfil comparten puerto, asi que se prueban
 * juntos: cada uno solo traslada el resultado sin interpretarlo.
 */
@ExtendWith(MockitoExtension.class)
class EstudianteFichaPerfilConsultasFinderTest {

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @InjectMocks
    private VinculoEstudianteFichaExisteFinderImpl vinculoFinder;

    @InjectMocks
    private EstudiantesYaVinculadosFinderImpl yaVinculadosFinder;

    @InjectMocks
    private EstudiantesVinculadosContadorFinderImpl contadorFinder;

    private final UUID fichaPerfil = UUID.randomUUID();
    private final UUID estudiante = UUID.randomUUID();

    @Test
    void debeTrasladarLaExistenciaDelVinculo_cuandoExiste() {
        // Arrange
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfil, estudiante))
                .thenReturn(true);

        // Act & Assert
        assertThat(vinculoFinder.obtener(new VinculoEstudianteFicha(fichaPerfil, estudiante))).isTrue();
    }

    @Test
    void debeTrasladarLaAusenciaDelVinculo_cuandoNoExiste() {
        // Arrange
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfil, estudiante))
                .thenReturn(false);

        // Act & Assert
        assertThat(vinculoFinder.obtener(new VinculoEstudianteFicha(fichaPerfil, estudiante))).isFalse();
    }

    @Test
    void debeDevolverSoloLosEstudiantesYaVinculados_cuandoAlgunoLoEsta() {
        // Arrange
        UUID otroEstudiante = UUID.randomUUID();
        var relaciones = EstudianteFichaPerfilDomain.crear(fichaPerfil, List.of(estudiante, otroEstudiante));
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfil, estudiante))
                .thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfil, otroEstudiante))
                .thenReturn(false);

        // Act
        List<UUID> resultado = yaVinculadosFinder.obtener(relaciones);

        // Assert
        assertThat(resultado).containsExactly(estudiante);
    }

    @Test
    void debeDevolverListaVacia_cuandoNoHayRelaciones() {
        // Act
        List<UUID> resultado = yaVinculadosFinder.obtener(List.of());

        // Assert
        assertThat(resultado).isEmpty();
        verifyNoInteractions(estudianteFichaPerfilOutputPort);
    }

    @Test
    void debeTrasladarElConteoDeVinculados_cuandoElPuertoResponde() {
        // Arrange
        when(estudianteFichaPerfilOutputPort.contarPorFichaPerfilId(fichaPerfil)).thenReturn(2L);

        // Act & Assert
        assertThat(contadorFinder.obtener(fichaPerfil)).isEqualTo(2L);
    }
}
