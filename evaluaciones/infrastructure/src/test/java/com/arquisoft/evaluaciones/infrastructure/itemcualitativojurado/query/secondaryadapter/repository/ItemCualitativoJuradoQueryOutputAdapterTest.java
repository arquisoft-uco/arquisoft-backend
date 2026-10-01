package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.entity.ItemCualitativoJuradoJpaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemCualitativoJuradoQueryOutputAdapterTest {

    @Mock
    private ItemCualitativoJuradoQueryRepository repository;

    @InjectMocks
    private ItemCualitativoJuradoQueryOutputAdapter adapter;

    @Test
    void debeInvocarFindAllByOrderByNombreAsc_yPreservarElOrdenRetornado() {
        // Arrange
        UUID idClaridad = UUID.randomUUID();
        UUID idRigor = UUID.randomUUID();
        ItemCualitativoJuradoJpaQueryEntity claridad = ItemCualitativoJuradoJpaQueryEntity.builder()
                .id(idClaridad)
                .nombre("Claridad")
                .descripcion("Evalúa la claridad conceptual")
                .build();
        ItemCualitativoJuradoJpaQueryEntity rigor = ItemCualitativoJuradoJpaQueryEntity.builder()
                .id(idRigor)
                .nombre("Rigor")
                .descripcion("Evalúa el rigor metodológico")
                .build();
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(claridad, rigor));

        // Act
        List<ItemCualitativoJuradoReadModel> resultado = adapter.consultarTodos();

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
        ItemCualitativoJuradoJpaQueryEntity entity = ItemCualitativoJuradoJpaQueryEntity.builder()
                .id(id)
                .nombre("Coherencia")
                .descripcion("Evalúa la coherencia argumentativa")
                .build();
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(entity));

        // Act
        List<ItemCualitativoJuradoReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).hasSize(1);
        ItemCualitativoJuradoReadModel readModel = resultado.get(0);
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.nombre()).isEqualTo("Coherencia");
        assertThat(readModel.descripcion()).isEqualTo("Evalúa la coherencia argumentativa");
    }

    @Test
    void debeRetornarListaVacia_cuandoElRepositorioNoTieneRegistros() {
        // Arrange
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of());

        // Act
        List<ItemCualitativoJuradoReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findAllByOrderByNombreAsc();
    }

    @Test
    void noDebeReferenciarTiposDelLadoCommand_enElAdapterNiEnLaEntidadDeConsulta() {
        // Arrange
        Class<?> jpaEntityDeCommand = ItemCualitativoJuradoJpaEntity.class;

        // Act
        boolean adapterReferenciaCommand = referenciaTipo(ItemCualitativoJuradoQueryOutputAdapter.class, jpaEntityDeCommand);
        boolean queryEntityReferenciaCommand = referenciaTipo(ItemCualitativoJuradoJpaQueryEntity.class, jpaEntityDeCommand);

        // Assert
        assertThat(adapterReferenciaCommand)
                .as("ItemCualitativoJuradoQueryOutputAdapter no debe depender del JpaEntity de command")
                .isFalse();
        assertThat(queryEntityReferenciaCommand)
                .as("ItemCualitativoJuradoJpaQueryEntity no debe depender del JpaEntity de command")
                .isFalse();
    }

    private static boolean referenciaTipo(Class<?> clase, Class<?> tipoBuscado) {
        boolean enCampos = Arrays.stream(clase.getDeclaredFields())
                .map(Field::getType)
                .anyMatch(tipoBuscado::equals);
        boolean enMetodos = Arrays.stream(clase.getDeclaredMethods())
                .anyMatch(metodo -> esReferenciadoPor(metodo, tipoBuscado));
        return enCampos || enMetodos;
    }

    private static boolean esReferenciadoPor(Method metodo, Class<?> tipoBuscado) {
        return metodo.getReturnType().equals(tipoBuscado)
                || Arrays.asList(metodo.getParameterTypes()).contains(tipoBuscado);
    }
}
