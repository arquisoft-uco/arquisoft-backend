package com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.readmodel.CategoriaItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.secondaryport.CategoriaItemCuantitativoAsesorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarCategoriasItemCuantitativoAsesorUseCaseImplTest {

    @Mock
    private CategoriaItemCuantitativoAsesorQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarCategoriasItemCuantitativoAsesorUseCaseImpl useCase;

    @Test
    void debeRetornarTodosLosReadModel_cuandoNoHayFiltro() {
        // Arrange
        var esperados = List.of(
                new CategoriaItemCuantitativoAsesorReadModel(UUID.randomUUID(), "Puntualidad", "Evalúa la puntualidad"),
                new CategoriaItemCuantitativoAsesorReadModel(UUID.randomUUID(), "Rigor", "Evalúa el rigor"));
        when(queryOutputPort.consultar(null)).thenReturn(esperados);

        // Act
        var resultado = useCase.ejecutar(null);

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(queryOutputPort, times(1)).consultar(null);
        verifyNoMoreInteractions(queryOutputPort);
    }

    @Test
    void debeRetornarListaFiltrada_cuandoSeConsultaConNombre() {
        // Arrange
        var nombre = "Puntualidad";
        var esperados = List.of(
                new CategoriaItemCuantitativoAsesorReadModel(UUID.randomUUID(), "Puntualidad", "Evalúa la puntualidad"));
        when(queryOutputPort.consultar(nombre)).thenReturn(esperados);

        // Act
        var resultado = useCase.ejecutar(nombre);

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(queryOutputPort, times(1)).consultar(nombre);
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoEncuentraRegistros() {
        // Arrange
        when(queryOutputPort.consultar(any())).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar("inexistente");

        // Assert
        assertThat(resultado).isEmpty();
    }
}
