package com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.interactor.impl;

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
    void debeDelegarEnElUseCase_conElNombreDelQuery() {
        // Arrange
        var query = ConsultarCategoriasItemCuantitativoJuradoQuery.crear("Puntualidad");
        var esperados = List.of(
                new CategoriaItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Puntualidad", "Evalúa la puntualidad"));
        when(useCase.ejecutar(query.nombre())).thenReturn(esperados);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(useCase).ejecutar(query.nombre());
    }

    @Test
    void debePropagarListaVacia_cuandoElUseCaseNoEncuentraRegistros() {
        // Arrange
        var query = ConsultarCategoriasItemCuantitativoJuradoQuery.crear(null);
        when(useCase.ejecutar(query.nombre())).thenReturn(List.of());

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isEmpty();
        verify(useCase).ejecutar(query.nombre());
    }
}
