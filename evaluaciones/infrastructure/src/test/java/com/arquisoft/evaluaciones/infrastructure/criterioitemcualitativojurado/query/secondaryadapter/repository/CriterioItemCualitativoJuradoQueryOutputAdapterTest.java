package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriterioItemCualitativoJuradoQueryOutputAdapterTest {

    @Mock
    private CriterioItemCualitativoJuradoQueryRepository repository;

    @InjectMocks
    private CriterioItemCualitativoJuradoQueryOutputAdapter adapter;

    @Test
    void debeInvocarFindAllByOrderByNombreAsc_yPreservarElOrdenRetornado() {
        // Arrange
        UUID idClaridad = UUID.randomUUID();
        UUID idRigor = UUID.randomUUID();
        CriterioItemCualitativoJuradoJpaQueryEntity claridad = CriterioItemCualitativoJuradoJpaQueryEntity.builder()
                .id(idClaridad)
                .nombre("Claridad")
                .descripcion("Evalúa la claridad conceptual")
                .build();
        CriterioItemCualitativoJuradoJpaQueryEntity rigor = CriterioItemCualitativoJuradoJpaQueryEntity.builder()
                .id(idRigor)
                .nombre("Rigor")
                .descripcion("Evalúa el rigor metodológico")
                .build();
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(claridad, rigor));

        // Act
        List<CriterioItemCualitativoJuradoReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).id()).isEqualTo(idClaridad);
        assertThat(resultado.get(1).id()).isEqualTo(idRigor);
        verify(repository, times(1)).findAllByOrderByNombreAsc();
    }

    @Test
    void debeMapearIdNombreYDescripcion_deTodosLosElementos() {
        // Arrange
        UUID id = UUID.randomUUID();
        CriterioItemCualitativoJuradoJpaQueryEntity entity = CriterioItemCualitativoJuradoJpaQueryEntity.builder()
                .id(id)
                .nombre("Coherencia")
                .descripcion("Evalúa la coherencia argumentativa")
                .build();
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(entity));

        // Act
        List<CriterioItemCualitativoJuradoReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).hasSize(1);
        CriterioItemCualitativoJuradoReadModel readModel = resultado.get(0);
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.nombre()).isEqualTo("Coherencia");
        assertThat(readModel.descripcion()).isEqualTo("Evalúa la coherencia argumentativa");
    }

    @Test
    void debeRetornarListaVacia_cuandoElRepositorioNoTieneRegistros() {
        // Arrange
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of());

        // Act
        List<CriterioItemCualitativoJuradoReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findAllByOrderByNombreAsc();
    }
}
