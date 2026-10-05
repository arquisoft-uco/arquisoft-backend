package com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.model.ConsultarCategoriasItemCuantitativoAsesorQuery;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.readmodel.CategoriaItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.usecase.ConsultarCategoriasItemCuantitativoAsesorUseCase;
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
class ConsultarCategoriasItemCuantitativoAsesorInteractorImplTest {

    @Mock
    private ConsultarCategoriasItemCuantitativoAsesorUseCase useCase;

    @InjectMocks
    private ConsultarCategoriasItemCuantitativoAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElNombreDelQuery() {
        // Arrange
        var query = ConsultarCategoriasItemCuantitativoAsesorQuery.crear("Puntualidad");
        var esperados = List.of(
                new CategoriaItemCuantitativoAsesorReadModel(UUID.randomUUID(), "Puntualidad", "Evalúa la puntualidad"));
        when(useCase.ejecutar(query.nombre())).thenReturn(esperados);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(useCase).ejecutar(query.nombre());
    }
}
