package com.arquisoft.fichas.infrastructure.tipoitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.tipoitem.query.readmodel.TipoItemReadModel;
import com.arquisoft.fichas.infrastructure.tipoitem.command.secondaryadapter.entity.TipoItemJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TipoItemQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TipoItemQueryRepository repository;

    private TipoItemQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TipoItemQueryOutputAdapter(repository);
    }

    @Test
    void debeRetornarLosTiposDelCatalogo_cuandoExistenEnBD() {
        // Arrange
        persistirTipo("OBJETIVO_GENERAL", "Objetivo General", "Proposito principal del proyecto.");
        persistirTipo("ANTECEDENTES", "Antecedentes", "Estudios previos que contextualizan el proyecto.");
        entityManager.flush();

        // Act
        List<TipoItemReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado)
                .extracting(TipoItemReadModel::id)
                .containsExactlyInAnyOrder("OBJETIVO_GENERAL", "ANTECEDENTES");
    }

    @Test
    void debeProyectarTodasLasColumnas_cuandoLeeUnTipo() {
        // Arrange
        persistirTipo("REFERENCIAS", "Referencias", "Fuentes bibliograficas en formato de citacion estandar.");
        entityManager.flush();

        // Act
        List<TipoItemReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).singleElement().satisfies(tipo -> {
            assertThat(tipo.id()).isEqualTo("REFERENCIAS");
            assertThat(tipo.nombre()).isEqualTo("Referencias");
            assertThat(tipo.descripcion()).isEqualTo("Fuentes bibliograficas en formato de citacion estandar.");
        });
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFilasEnBD() {
        // Act & Assert
        assertThat(adapter.consultarTodos()).isEmpty();
    }

    @Test
    void debeTolerarDescripcionLarga_cuandoOcupaCasiElLimite() {
        // Arrange
        String descripcionLarga = "d".repeat(500);
        persistirTipo("JUSTIFICACION", "Justificacion", descripcionLarga);
        entityManager.flush();

        // Act
        List<TipoItemReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).singleElement()
                .extracting(TipoItemReadModel::descripcion)
                .isEqualTo(descripcionLarga);
    }

    private void persistirTipo(String id, String nombre, String descripcion) {
        entityManager.persist(TipoItemJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .build());
    }
}
