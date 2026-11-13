package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.entity.ItemCualitativoAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.entity.ItemCualitativoAsesorJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Import(ItemCualitativoAsesorCommandOutputAdapter.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ItemCualitativoAsesorCommandOutputAdapterTest {

    @Autowired
    private ItemCualitativoAsesorCommandOutputAdapter adapter;

    @Autowired
    private ItemCualitativoAsesorCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private AppLogger logger;

    @Test
    void debeRetornarSoloIdsSembrados_cuandoConsultaUnConjuntoMixto() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        repository.saveAndFlush(ItemCualitativoAsesorJpaEntity.builder()
                .id(existente)
                .nombre("Claridad")
                .descripcion("Evalua la claridad")
                .build());

        // Act
        var resultado = adapter.consultarIdsExistentes(Set.of(existente, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).containsExactly(existente);
    }

    @Test
    void debePersistirLosCampos_cuandoRegistra() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = new ItemCualitativoAsesorEntity(id, "Claridad", "Evalua la claridad");

        // Act
        adapter.registrar(entity);
        repository.flush();

        // Assert
        var guardado = repository.findById(id).orElseThrow();
        assertThat(guardado.getNombre()).isEqualTo("Claridad");
        assertThat(guardado.getDescripcion()).isEqualTo("Evalua la claridad");
    }

    @Test
    void debeEncontrarNombre_cuandoCambiaMayusculasYMinusculas() {
        // Arrange
        repository.saveAndFlush(ItemCualitativoAsesorJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .nombre("Claridad Conceptual")
                .descripcion("Evalua la claridad")
                .build());

        // Act
        var existe = adapter.existePorNombreIgnorandoMayusculas("claridad CONCEPTUAL");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoNombreNoExiste() {
        // Arrange
        var nombre = "Inexistente";

        // Act
        var existe = adapter.existePorNombreIgnorandoMayusculas(nombre);

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeRetornarSoloElSembrado_cuandoConsultaExistenciaPorId() {
        // Arrange
        var sembrado = UtilUUID.generarNuevoUUID();
        repository.saveAndFlush(ItemCualitativoAsesorJpaEntity.builder()
                .id(sembrado)
                .nombre("Claridad")
                .descripcion("Evalua la claridad")
                .build());

        // Act
        var existe = adapter.existePorId(sembrado);
        var noExiste = adapter.existePorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }

    @Test
    void debeActualizarSoloDescripcionYLoguear_cuandoActualizaDescripcion() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        repository.saveAndFlush(ItemCualitativoAsesorJpaEntity.builder()
                .id(id)
                .nombre("Claridad")
                .descripcion("Descripción original")
                .build());
        entityManager.clear();

        // Act
        adapter.actualizarDescripcion(id, "Descripción nueva");
        entityManager.clear();

        // Assert
        var persistido = repository.findById(id).orElseThrow();
        assertThat(persistido.getDescripcion()).isEqualTo("Descripción nueva");
        assertThat(persistido.getNombre()).isEqualTo("Claridad");
        verify(logger).debug(any(ClaveMensaje.class), eq(id));
    }

    @Test
    void debeEliminarElItemYSoloEse_cuandoEliminaPorId() {
        // Arrange
        var aEliminar = UtilUUID.generarNuevoUUID();
        var aConservar = UtilUUID.generarNuevoUUID();
        repository.saveAndFlush(ItemCualitativoAsesorJpaEntity.builder()
                .id(aEliminar).nombre("Claridad").descripcion("Evalua la claridad").build());
        repository.saveAndFlush(ItemCualitativoAsesorJpaEntity.builder()
                .id(aConservar).nombre("Coherencia").descripcion("Evalua la coherencia").build());
        entityManager.clear();

        // Act
        adapter.eliminar(aEliminar);
        repository.flush();
        entityManager.clear();

        // Assert
        assertThat(repository.findById(aEliminar)).isEmpty();
        assertThat(repository.findById(aConservar)).isPresent();
        verify(logger).debug(any(ClaveMensaje.class), eq(aEliminar));
    }
}
