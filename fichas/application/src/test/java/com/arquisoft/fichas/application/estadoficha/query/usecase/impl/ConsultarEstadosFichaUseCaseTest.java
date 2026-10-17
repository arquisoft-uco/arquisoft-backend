package com.arquisoft.fichas.application.estadoficha.query.usecase.impl;

import com.arquisoft.fichas.application.estadoficha.query.criteria.EstadoFichaCriteria;
import com.arquisoft.fichas.application.estadoficha.query.secondaryport.EstadoFichaQueryOutputPort;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosFichaUseCaseTest {

    @Mock
    private EstadoFichaQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosFichaUseCaseImpl useCase;

    @Test
    void debeRetornarEstadosDelPuerto_cuandoElRolTieneEstadosHabilitados() {
        // Arrange
        var roles = List.of("ASESOR_FICHA");
        var estadosEsperados = List.of(
                new EstadoFichaReadModel("EN_CONSTRUCCION", "En Construccion", "Ficha en desarrollo"),
                new EstadoFichaReadModel("DISPONIBLE_PARA_EVALUACION", "Disponible Para Evaluacion", "Lista para evaluar")
        );
        when(queryOutputPort.consultarPorRoles(roles)).thenReturn(estadosEsperados);

        // Act
        var resultado = useCase.ejecutar(new EstadoFichaCriteria(roles));

        // Assert
        assertThat(resultado).containsExactlyElementsOf(estadosEsperados);
        verify(queryOutputPort, times(1)).consultarPorRoles(roles);
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoDevuelveEstados() {
        // Arrange
        var roles = List.<String>of();
        when(queryOutputPort.consultarPorRoles(roles)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new EstadoFichaCriteria(roles));

        // Assert
        assertThat(resultado).isEmpty();
        verify(queryOutputPort, times(1)).consultarPorRoles(roles);
    }
}
