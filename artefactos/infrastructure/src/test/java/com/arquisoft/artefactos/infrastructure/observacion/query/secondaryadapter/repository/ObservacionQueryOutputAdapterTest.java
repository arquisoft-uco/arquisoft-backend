package com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.observacion.support.EstadoObservacionTestEntity;
import com.arquisoft.artefactos.infrastructure.observacion.support.ObservacionTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.RevisionAsesorTestEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ObservacionQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObservacionQueryRepository repository;

    private ObservacionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ObservacionQueryOutputAdapter(repository);
        entityManager.persist(new EstadoObservacionTestEntity("NUEVA", "Nueva"));
        entityManager.persist(new EstadoObservacionTestEntity("CERRADA", "Cerrada"));
    }

    private UUID sembrarRevision() {
        var revision = UtilUUID.generarNuevoUUID();
        entityManager.persist(new RevisionAsesorTestEntity(revision, UtilUUID.generarNuevoUUID(), "PENDIENTE"));
        return revision;
    }

    private UUID sembrarObservacion(UUID revision, String descripcion, String estado) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ObservacionTestEntity(id, revision, descripcion, estado));
        return id;
    }

    @Test
    void debeRetornarOrdenadasPorDescripcionYId_cuandoHayVarias() {
        // Arrange
        var revision = sembrarRevision();
        sembrarObservacion(revision, "Zeta", "NUEVA");
        sembrarObservacion(revision, "Alfa", "NUEVA");
        sembrarObservacion(revision, "Media", "CERRADA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorRevisionAsesor(revision);

        // Assert
        assertThat(resultado).extracting("descripcion").containsExactly("Alfa", "Media", "Zeta");
    }

    @Test
    void debeResolverNombreDelEstado_cuandoConsulta() {
        // Arrange
        var revision = sembrarRevision();
        var id = sembrarObservacion(revision, "Obs", "CERRADA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorRevisionAsesor(revision);

        // Assert
        assertThat(resultado).hasSize(1);
        var observacion = resultado.get(0);
        assertThat(observacion.id()).isEqualTo(id);
        assertThat(observacion.revisionAsesor()).isEqualTo(revision);
        assertThat(observacion.estado()).isEqualTo("CERRADA");
        assertThat(observacion.estadoNombre()).isEqualTo("Cerrada");
    }

    @Test
    void debeExcluirObservacionesDeOtraRevision_cuandoConsulta() {
        // Arrange
        var revision = sembrarRevision();
        var otra = sembrarRevision();
        var propia = sembrarObservacion(revision, "Propia", "NUEVA");
        sembrarObservacion(otra, "Ajena", "NUEVA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorRevisionAsesor(revision);

        // Assert
        assertThat(resultado).extracting("id").containsExactly(propia);
    }

    @Test
    void debeRetornarVacio_cuandoRevisionSinObservaciones() {
        // Arrange
        var revision = sembrarRevision();
        sembrarObservacion(sembrarRevision(), "Ajena", "NUEVA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var conRevisionVacia = adapter.consultarPorRevisionAsesor(revision);
        var inexistente = adapter.consultarPorRevisionAsesor(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(conRevisionVacia).isEmpty();
        assertThat(inexistente).isEmpty();
    }
}
