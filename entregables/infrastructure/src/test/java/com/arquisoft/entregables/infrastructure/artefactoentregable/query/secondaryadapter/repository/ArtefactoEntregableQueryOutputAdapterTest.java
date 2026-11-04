package com.arquisoft.entregables.infrastructure.artefactoentregable.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void prepararTablas() {
        var em = entityManager.getEntityManager();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS artefacto (id UUID PRIMARY KEY, uri_archivo VARCHAR(2000), "
                + "id_artefacto UUID, id_repositorio_artefacto UUID, nombre_documento VARCHAR(100), "
                + "id_repositorio UUID, nombre_repositorio VARCHAR(100))").executeUpdate();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS artefacto_entregable (id UUID PRIMARY KEY, "
                + "artefacto_id UUID, entregable_id UUID)").executeUpdate();
        em.createNativeQuery("DELETE FROM artefacto_entregable").executeUpdate();
        em.createNativeQuery("DELETE FROM artefacto").executeUpdate();
    }

    private UUID sembrar(UUID entregable, UUID id, String nombreDocumento) {
        var em = entityManager.getEntityManager();
        var artefacto = UtilUUID.generarNuevoUUID();
        em.createNativeQuery("INSERT INTO artefacto (id, uri_archivo, id_artefacto, id_repositorio_artefacto, "
                        + "nombre_documento, id_repositorio, nombre_repositorio) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7)")
                .setParameter(1, artefacto).setParameter(2, "uri/" + nombreDocumento)
                .setParameter(3, UtilUUID.generarNuevoUUID()).setParameter(4, UtilUUID.generarNuevoUUID())
                .setParameter(5, nombreDocumento).setParameter(6, UtilUUID.generarNuevoUUID())
                .setParameter(7, "Repo").executeUpdate();
        em.createNativeQuery("INSERT INTO artefacto_entregable (id, artefacto_id, entregable_id) VALUES (?1, ?2, ?3)")
                .setParameter(1, id).setParameter(2, artefacto).setParameter(3, entregable).executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return artefacto;
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
