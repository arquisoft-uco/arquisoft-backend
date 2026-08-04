package com.arquisoft.fichas.infrastructure.estadoficha.query.adapter.out.persistence;

import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaRepository;
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
class EstadoFichaQueryOutputAdapterTest {

    @Mock
    private EstadoFichaRepository repository;

    @InjectMocks
    private EstadoFichaQueryOutputAdapter adapter;

    @Test
    void debeRetornarListaDeReadModels_cuandoFindAllEsInvocado() {
        // Arrange
        EstadoFichaEntity entity1 = new EstadoFichaEntity();
        entity1.setId("EN_CONSTRUCCION");
        entity1.setNombre("En Construccion");
        entity1.setDescripcion("Ficha en desarrollo");

        EstadoFichaEntity entity2 = new EstadoFichaEntity();
        entity2.setId("APROBADA");
        entity2.setNombre("Aprobada");
        entity2.setDescripcion("Ficha aprobada por el comite");

        EstadoFichaEntity entity3 = new EstadoFichaEntity();
        entity3.setId("NO_APROBADA");
        entity3.setNombre("No Aprobada");
        entity3.setDescripcion("Ficha rechazada");

        List<EstadoFichaEntity> entities = List.of(entity1, entity2, entity3);
        when(repository.findAll()).thenReturn(entities);

        // Act
        List<EstadoFichaReadModel> resultado = adapter.findAll();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(3);

        assertThat(resultado.get(0).id()).isEqualTo("EN_CONSTRUCCION");
        assertThat(resultado.get(0).nombre()).isEqualTo("En Construccion");
        assertThat(resultado.get(0).descripcion()).isEqualTo("Ficha en desarrollo");

        assertThat(resultado.get(1).id()).isEqualTo("APROBADA");
        assertThat(resultado.get(1).nombre()).isEqualTo("Aprobada");
        assertThat(resultado.get(1).descripcion()).isEqualTo("Ficha aprobada por el comite");

        assertThat(resultado.get(2).id()).isEqualTo("NO_APROBADA");
        assertThat(resultado.get(2).nombre()).isEqualTo("No Aprobada");
        assertThat(resultado.get(2).descripcion()).isEqualTo("Ficha rechazada");

        verify(repository, times(1)).findAll();
    }

    @Test
    void debeRetornarListaVacia_cuandoRepositorioRetornaVacio() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());

        // Act
        List<EstadoFichaReadModel> resultado = adapter.findAll();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findAll();
    }

    @Test
    void debeMaperarCorrectamente_cuandoConvierteEntityAReadModel() {
        // Arrange
        EstadoFichaEntity entity = new EstadoFichaEntity();
        entity.setId("DISPONIBLE_PARA_EVALUACION");
        entity.setNombre("Disponible para Evaluacion");
        entity.setDescripcion("Ficha lista para ser evaluada");

        when(repository.findAll()).thenReturn(List.of(entity));

        // Act
        List<EstadoFichaReadModel> resultado = adapter.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        EstadoFichaReadModel readModel = resultado.get(0);

        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.nombre()).isEqualTo(entity.getNombre());
        assertThat(readModel.descripcion()).isEqualTo(entity.getDescripcion());
    }
}
