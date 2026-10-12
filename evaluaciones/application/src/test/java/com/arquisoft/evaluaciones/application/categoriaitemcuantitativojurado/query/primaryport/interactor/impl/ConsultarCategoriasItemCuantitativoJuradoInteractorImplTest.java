package com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.criteria.CategoriaItemCuantitativoJuradoCriteria;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.model.ConsultarCategoriasItemCuantitativoJuradoQuery;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.readmodel.CategoriaItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.usecase.ConsultarCategoriasItemCuantitativoJuradoUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarCategoriasItemCuantitativoJuradoInteractorImplTest {

    @Mock
    private ConsultarCategoriasItemCuantitativoJuradoUseCase useCase;

    @InjectMocks
    private ConsultarCategoriasItemCuantitativoJuradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElCriteriaDerivadoDelQuery() {
        // Arrange
        var query = ConsultarCategoriasItemCuantitativoJuradoQuery.crear("Puntualidad");
        var criteria = new CategoriaItemCuantitativoJuradoCriteria(query.nombre());
        var esperados = List.of(
                new CategoriaItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Puntualidad", "Evalúa la puntualidad"));
        when(useCase.ejecutar(criteria)).thenReturn(esperados);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(useCase).ejecutar(criteria);
    }

    @Test
    void debePropagarListaVacia_cuandoElUseCaseNoEncuentraRegistros() {
        // Arrange
        var query = ConsultarCategoriasItemCuantitativoJuradoQuery.crear(null);
        var criteria = new CategoriaItemCuantitativoJuradoCriteria(query.nombre());
        when(useCase.ejecutar(criteria)).thenReturn(List.of());

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isEmpty();
        verify(useCase).ejecutar(criteria);
    }
}
