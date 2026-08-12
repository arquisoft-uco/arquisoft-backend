package com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.representantecomite.command.finder.impl.RepresentanteComiteExisteFinderImpl;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionesFinderTest {

    @Mock
    private EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Mock
    private RepresentanteComiteOutputPort representanteComiteOutputPort;

    @InjectMocks
    private EvaluacionDeRepresentanteExisteFinderImpl evaluacionDeRepresentanteFinder;

    @InjectMocks
    private RepresentanteComiteExisteFinderImpl representanteExisteFinder;

    private final UUID representante = UUID.randomUUID();
    private final UUID ficha = UUID.randomUUID();

    @Test
    void debeTrasladarQueElRepresentanteYaEvaluoLaFicha_cuandoElPuertoResponde() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        when(evaluacionFichaPerfilOutputPort.existePorRepresentanteYFicha(representante, ficha))
                .thenReturn(true);

        // Act & Assert
        assertThat(evaluacionDeRepresentanteFinder.obtener(evaluacion)).isTrue();
    }

    @Test
    void debeTrasladarQueNoHayEvaluacionPrevia_cuandoElPuertoResponde() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        when(evaluacionFichaPerfilOutputPort.existePorRepresentanteYFicha(representante, ficha))
                .thenReturn(false);

        // Act & Assert
        assertThat(evaluacionDeRepresentanteFinder.obtener(evaluacion)).isFalse();
    }

    @Test
    void debeTrasladarLaExistenciaDelRepresentante_cuandoExiste() {
        // Arrange
        when(representanteComiteOutputPort.existePorId(representante)).thenReturn(true);

        // Act & Assert
        assertThat(representanteExisteFinder.obtener(representante)).isTrue();
    }

    @Test
    void debeTrasladarLaAusenciaDelRepresentante_cuandoNoExiste() {
        // Arrange
        when(representanteComiteOutputPort.existePorId(representante)).thenReturn(false);

        // Act & Assert
        assertThat(representanteExisteFinder.obtener(representante)).isFalse();
    }
}
