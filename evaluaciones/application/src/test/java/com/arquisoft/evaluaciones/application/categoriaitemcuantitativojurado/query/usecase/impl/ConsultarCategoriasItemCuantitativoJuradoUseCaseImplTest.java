package com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.criteria.CategoriaItemCuantitativoJuradoCriteria;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.readmodel.CategoriaItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.secondaryport.CategoriaItemCuantitativoJuradoQueryOutputPort;
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
class ConsultarCategoriasItemCuantitativoJuradoUseCaseImplTest {

    @Mock
    private CategoriaItemCuantitativoJuradoQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    private final CategoriaItemCuantitativoJuradoCriteria criteriaSinFiltro =
            new CategoriaItemCuantitativoJuradoCriteria(null);

    @InjectMocks
    private ConsultarCategoriasItemCuantitativoJuradoUseCaseImpl useCase;

    @Test
    void debeRetornarTodosLosReadModel_cuandoNoHayFiltro() {
        // Arrange
        var esperados = List.of(
                new CategoriaItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Puntualidad", "Evalúa la puntualidad"),
                new CategoriaItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Rigor", "Evalúa el rigor"));
        when(queryOutputPort.consultar(criteriaSinFiltro)).thenReturn(esperados);

        // Act
        var resultado = useCase.ejecutar(criteriaSinFiltro);

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(queryOutputPort, times(1)).consultar(criteriaSinFiltro);
        verifyNoMoreInteractions(queryOutputPort);
    }

    @Test
    void debeRetornarListaFiltrada_cuandoSeConsultaConNombre() {
        // Arrange
        var criteria = new CategoriaItemCuantitativoJuradoCriteria("Puntualidad");
        var esperados = List.of(
                new CategoriaItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Puntualidad", "Evalúa la puntualidad"));
        when(queryOutputPort.consultar(criteria)).thenReturn(esperados);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(queryOutputPort, times(1)).consultar(criteria);
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoEncuentraRegistros() {
        // Arrange
        when(queryOutputPort.consultar(any())).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new CategoriaItemCuantitativoJuradoCriteria("inexistente"));

        // Assert
        assertThat(resultado).isEmpty();
    }
}
