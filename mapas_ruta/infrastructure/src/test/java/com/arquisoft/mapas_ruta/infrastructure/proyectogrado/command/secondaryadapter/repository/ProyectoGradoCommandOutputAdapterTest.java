package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ProyectoGradoCommandOutputAdapter.class)
class ProyectoGradoCommandOutputAdapterTest {

    @Autowired
    private ProyectoGradoCommandOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void debeRetornarLaEntity_cuandoElProyectoExiste() {
        // Arrange
        var proyecto = entityManager.persistFlushFind(ProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estadoProyectoGradoId("EN_PROCESO")
                .coordinadorId(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(UtilUUID.generarNuevoUUID())
                .tituloProyecto("Titulo del proyecto")
                .build());

        // Act
        var resultado = adapter.obtenerPorId(proyecto.getId());

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(proyecto.getId());
            assertThat(entity.estadoProyectoGrado()).isEqualTo("EN_PROCESO");
            assertThat(entity.coordinador()).isEqualTo(proyecto.getCoordinadorId());
            assertThat(entity.fichaPerfil()).isEqualTo(proyecto.getFichaPerfilId());
            assertThat(entity.tituloProyecto()).isEqualTo("Titulo del proyecto");
        });
    }

    @Test
    void debeRetornarVacio_cuandoElProyectoNoExiste() {
        // Act
        var resultado = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
