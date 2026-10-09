package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.readmodel.CategoriaItemCuantitativoJuradoReadModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaItemCuantitativoJuradoQueryOutputAdapterTest {

    @Mock
    private CategoriaItemCuantitativoJuradoQueryRepository repository;

    @InjectMocks
    private CategoriaItemCuantitativoJuradoQueryOutputAdapter adapter;

    @Test
    void debeInvocarFindAllByOrderByNombreAsc_cuandoNombreEsNulo() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = CategoriaItemCuantitativoJuradoJpaQueryEntity.builder()
                .id(id)
                .nombre("Puntualidad")
                .descripcion("Evalúa la puntualidad")
                .build();
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(entity));

        // Act
        var resultado = adapter.consultar(null);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).id()).isEqualTo(id);
        verify(repository, times(1)).findAllByOrderByNombreAsc();
        verify(repository, never()).findByNombreContainingIgnoreCaseOrderByNombreAsc(anyString());
    }

    @Test
    void debeInvocarFindByNombreContaining_cuandoNombreNoEsNulo() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = CategoriaItemCuantitativoJuradoJpaQueryEntity.builder()
                .id(id)
                .nombre("Puntualidad")
                .descripcion("Evalúa la puntualidad")
                .build();
        when(repository.findByNombreContainingIgnoreCaseOrderByNombreAsc("Puntual")).thenReturn(List.of(entity));

        // Act
        var resultado = adapter.consultar("Puntual");

        // Assert
        assertThat(resultado).hasSize(1);
        verify(repository, times(1)).findByNombreContainingIgnoreCaseOrderByNombreAsc("Puntual");
        verify(repository, never()).findAllByOrderByNombreAsc();
    }

    @Test
    void debeMapearIdNombreYDescripcion_deTodosLosElementos() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = CategoriaItemCuantitativoJuradoJpaQueryEntity.builder()
                .id(id)
                .nombre("Coherencia")
                .descripcion("Evalúa la coherencia argumentativa")
                .build();
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(entity));

        // Act
        var resultado = adapter.consultar(null);

        // Assert
        CategoriaItemCuantitativoJuradoReadModel readModel = resultado.get(0);
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.nombre()).isEqualTo("Coherencia");
        assertThat(readModel.descripcion()).isEqualTo("Evalúa la coherencia argumentativa");
    }

    @Test
    void debeRetornarListaVacia_cuandoElRepositorioNoTieneRegistros() {
        // Arrange
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of());

        // Act
        var resultado = adapter.consultar(null);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
