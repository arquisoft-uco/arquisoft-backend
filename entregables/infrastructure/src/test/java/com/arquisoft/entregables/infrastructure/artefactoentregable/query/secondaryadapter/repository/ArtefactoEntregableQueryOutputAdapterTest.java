package com.arquisoft.entregables.infrastructure.artefactoentregable.query.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.entity.ArtefactoEntregableJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ArtefactoEntregableQueryOutputAdapter.class)
class ArtefactoEntregableQueryOutputAdapterTest {

    @Autowired
    private ArtefactoEntregableQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    private UUID sembrar(UUID entregable, UUID id, String nombreDocumento) {
        var version = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoEntregableJpaEntity.builder()
                .id(id).artefacto(UtilUUID.generarNuevoUUID()).entregableId(entregable)
                .uriArchivo("uri/" + nombreDocumento).versionArtefacto(version).numeroVersion(3)
                .nombreDocumento(nombreDocumento).versionRepositorioArtefacto(UtilUUID.generarNuevoUUID())
                .repositorio(UtilUUID.generarNuevoUUID()).nombreRepositorio("Repo").build());
        entityManager.flush();
        entityManager.clear();
        return version;
    }

    @Test
    void debeRetornarLosCamposDelArtefacto_cuandoElEntregableTieneArtefactos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var vinculo = UtilUUID.generarNuevoUUID();
        var artefacto = sembrar(entregable, vinculo, "Anteproyecto");

        // Act
        var resultado = adapter.consultarPorEntregable(entregable);

        // Assert
        assertThat(resultado).hasSize(1);
        var readModel = resultado.get(0);
        assertThat(readModel.id()).isEqualTo(vinculo);
        assertThat(readModel.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(readModel.versionArtefacto()).isEqualTo(artefacto);
        assertThat(readModel.numeroVersion()).isEqualTo(3);
        assertThat(readModel.uriArchivo()).isEqualTo("uri/Anteproyecto");
        assertThat(readModel.nombreDocumento()).isEqualTo("Anteproyecto");
        assertThat(readModel.nombreRepositorio()).isEqualTo("Repo");
        assertThat(readModel.artefacto()).isNotNull();
        assertThat(readModel.versionRepositorioArtefacto()).isNotNull();
        assertThat(readModel.repositorio()).isNotNull();
    }

    @Test
    void debeOrdenarPorNombreDocumento_yDesempatarPorId() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var idBajo = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var idAlto = UUID.fromString("00000000-0000-0000-0000-000000000002");
        sembrar(entregable, UtilUUID.generarNuevoUUID(), "Zeta");
        sembrar(entregable, idAlto, "Alfa");
        sembrar(entregable, idBajo, "Alfa");

        // Act
        var resultado = adapter.consultarPorEntregable(entregable);

        // Assert
        assertThat(resultado).extracting("nombreDocumento").containsExactly("Alfa", "Alfa", "Zeta");
        assertThat(resultado).extracting("id").startsWith(idBajo, idAlto);
    }

    @Test
    void debeRetornarSoloLosDelEntregable_cuandoOtroEntregableTambienTieneArtefactos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        sembrar(entregable, UtilUUID.generarNuevoUUID(), "Propio");
        sembrar(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Ajeno");

        // Act
        var resultado = adapter.consultarPorEntregable(entregable);

        // Assert
        assertThat(resultado).extracting("nombreDocumento").containsExactly("Propio");
    }

    @Test
    void debeRetornarListaVacia_cuandoElEntregableNoTieneArtefactos() {
        // Arrange
        sembrar(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Ajeno");

        // Act
        var resultado = adapter.consultarPorEntregable(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
