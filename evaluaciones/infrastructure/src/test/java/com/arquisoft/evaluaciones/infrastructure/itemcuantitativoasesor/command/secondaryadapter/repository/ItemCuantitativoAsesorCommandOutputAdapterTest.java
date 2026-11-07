package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.entity.ItemCuantitativoAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.entity.ItemCuantitativoAsesorJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Import(ItemCuantitativoAsesorCommandOutputAdapter.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ItemCuantitativoAsesorCommandOutputAdapterTest {

    @Autowired
    private ItemCuantitativoAsesorCommandOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @MockitoBean
    private AppLogger logger;

    @Test
    void debeRetornarValorPorIdSoloDeLosSembrados_cuandoConsultaValoresMaximos() {
        // Arrange
        var categoria = UtilUUID.generarNuevoUUID();
        var item1 = sembrar("Item uno", categoria, 40);
        var item2 = sembrar("Item dos", categoria, 60);
        sembrar("Item no pedido", categoria, 99);
        var inexistente = UtilUUID.generarNuevoUUID();
        entityManager.flush();

        // Act
        var resultado = adapter.consultarValoresMaximosPorIds(Set.of(item1, item2, inexistente));

        // Assert
        assertThat(resultado).containsOnly(
                java.util.Map.entry(item1, 40),
                java.util.Map.entry(item2, 60));
    }

    @Test
    void debeRetornarMapaVacio_cuandoNingunIdExiste() {
        // Act
        var resultado = adapter.consultarValoresMaximosPorIds(Set.of(UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debePersistirYLoguear_cuandoRegistraUnItem() {
        // Arrange
        var entity = new ItemCuantitativoAsesorEntity(
                UtilUUID.generarNuevoUUID(), "Avance", "Porcentaje", UtilUUID.generarNuevoUUID(), 80);

        // Act
        adapter.registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var persistido = entityManager.find(ItemCuantitativoAsesorJpaEntity.class, entity.id());
        assertThat(persistido.getNombre()).isEqualTo("Avance");
        assertThat(persistido.getDescripcion()).isEqualTo("Porcentaje");
        assertThat(persistido.getCategoria()).isEqualTo(entity.categoriaId());
        assertThat(persistido.getValor()).isEqualTo(80);
        verify(logger).debug(any(ClaveMensaje.class), eq(entity.id()));
    }

    @Test
    void debeDetectarNombreIgnorandoMayusculasSoloEnLaMismaCategoria_cuandoConsultaExistencia() {
        // Arrange
        var categoria = UtilUUID.generarNuevoUUID();
        var otraCategoria = UtilUUID.generarNuevoUUID();
        sembrar("Avance", categoria, 10);
        entityManager.flush();

        // Act
        var mismaCategoriaOtraCapitalizacion =
                adapter.existePorNombreYCategoriaIgnorandoMayusculas("aVANCE", categoria);
        var otraCategoriaMismoNombre = adapter.existePorNombreYCategoriaIgnorandoMayusculas("Avance", otraCategoria);
        var otroNombre = adapter.existePorNombreYCategoriaIgnorandoMayusculas("Otro", categoria);

        // Assert
        assertThat(mismaCategoriaOtraCapitalizacion).isTrue();
        assertThat(otraCategoriaMismoNombre).isFalse();
        assertThat(otroNombre).isFalse();
    }

    private UUID sembrar(String nombre, UUID categoria, int valor) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ItemCuantitativoAsesorJpaEntity.builder()
                .id(id).nombre(nombre).descripcion("descripcion").categoria(categoria).valor(valor).build());
        return id;
    }
}
