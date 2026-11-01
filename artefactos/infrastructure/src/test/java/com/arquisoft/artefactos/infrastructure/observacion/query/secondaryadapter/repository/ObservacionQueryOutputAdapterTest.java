package com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.observacion.support.EstadoObservacionTestEntity;
import com.arquisoft.artefactos.infrastructure.observacion.support.ObservacionTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.ArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstudianteArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.RevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
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

    @Autowired
    private ObservacionEstudianteQueryRepository estudianteRepository;

    private ObservacionQueryOutputAdapter adapter;

    private final UUID asesor = UtilUUID.generarNuevoUUID();
    private final UUID otroAsesor = UtilUUID.generarNuevoUUID();
    private final UUID estudiante = UtilUUID.generarNuevoUUID();
    private final UUID otroEstudiante = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        adapter = new ObservacionQueryOutputAdapter(repository, estudianteRepository);
        entityManager.persist(new EstadoObservacionTestEntity("NUEVA", "Nueva"));
        entityManager.persist(new EstadoObservacionTestEntity("CERRADA", "Cerrada"));
    }

    private UUID sembrarArtefacto(UUID asesorDelArtefacto, UUID... estudiantes) {
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoTestEntity(artefacto, asesorDelArtefacto));
        for (var participante : estudiantes) {
            entityManager.persist(
                    new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefacto, participante));
        }
        return artefacto;
    }

    private UUID sembrarRevision(UUID artefacto) {
        var version = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionArtefactoTestEntity(version, artefacto, 1));
        var revision = UtilUUID.generarNuevoUUID();
        entityManager.persist(new RevisionAsesorTestEntity(revision, version, "PENDIENTE"));
        return revision;
    }

    private UUID sembrarObservacion(UUID revision, String descripcion, String estado) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ObservacionTestEntity(id, revision, descripcion, estado));
        return id;
    }

    @Test
    void debeRetornarOrdenadasPorDescripcionYId_cuandoElAsesorEsDelArtefacto() {
        // Arrange
        var revision = sembrarRevision(sembrarArtefacto(asesor));
        sembrarObservacion(revision, "Zeta", "NUEVA");
        sembrarObservacion(revision, "Alfa", "NUEVA");
        sembrarObservacion(revision, "Media", "CERRADA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarDeAsesor(revision, asesor);

        // Assert
        assertThat(resultado).extracting("descripcion").containsExactly("Alfa", "Media", "Zeta");
    }

    @Test
    void debeResolverNombreDelEstado_cuandoConsultaElAsesor() {
        // Arrange
        var revision = sembrarRevision(sembrarArtefacto(asesor));
        var id = sembrarObservacion(revision, "Obs", "CERRADA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarDeAsesor(revision, asesor);

        // Assert
        assertThat(resultado).hasSize(1);
        var observacion = resultado.get(0);
        assertThat(observacion.id()).isEqualTo(id);
        assertThat(observacion.revisionAsesor()).isEqualTo(revision);
        assertThat(observacion.estado()).isEqualTo("CERRADA");
        assertThat(observacion.estadoNombre()).isEqualTo("Cerrada");
    }

    @Test
    void debeExcluirObservacionesDeOtraRevision_cuandoConsultaElAsesor() {
        // Arrange
        var revision = sembrarRevision(sembrarArtefacto(asesor));
        var otra = sembrarRevision(sembrarArtefacto(asesor));
        var propia = sembrarObservacion(revision, "Propia", "NUEVA");
        sembrarObservacion(otra, "Ajena", "NUEVA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarDeAsesor(revision, asesor);

        // Assert
        assertThat(resultado).extracting("id").containsExactly(propia);
    }

    @Test
    void debeRetornarVacio_cuandoElAsesorNoEsDelArtefactoDeLaRevision() {
        // Arrange
        var revision = sembrarRevision(sembrarArtefacto(otroAsesor));
        sembrarObservacion(revision, "Obs", "NUEVA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarDeAsesor(revision, asesor);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoLaRevisionDelAsesorNoExisteOEstaSinObservaciones() {
        // Arrange
        var revision = sembrarRevision(sembrarArtefacto(asesor));
        entityManager.flush();
        entityManager.clear();

        // Act
        var sinObservaciones = adapter.consultarDeAsesor(revision, asesor);
        var inexistente = adapter.consultarDeAsesor(UtilUUID.generarNuevoUUID(), asesor);

        // Assert
        assertThat(sinObservaciones).isEmpty();
        assertThat(inexistente).isEmpty();
    }

    @Test
    void debeRetornarOrdenadasPorDescripcionYId_cuandoElEstudianteParticipaEnElArtefacto() {
        // Arrange
        var revision = sembrarRevision(sembrarArtefacto(asesor, estudiante, otroEstudiante));
        sembrarObservacion(revision, "Zeta", "NUEVA");
        sembrarObservacion(revision, "Alfa", "NUEVA");
        sembrarObservacion(revision, "Media", "CERRADA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarDeEstudiante(revision, estudiante);

        // Assert
        assertThat(resultado).extracting("descripcion").containsExactly("Alfa", "Media", "Zeta");
    }

    @Test
    void debeResolverNombreDelEstado_cuandoConsultaElEstudiante() {
        // Arrange
        var revision = sembrarRevision(sembrarArtefacto(asesor, estudiante));
        var id = sembrarObservacion(revision, "Obs", "CERRADA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarDeEstudiante(revision, estudiante);

        // Assert
        assertThat(resultado).hasSize(1);
        var observacion = resultado.get(0);
        assertThat(observacion.id()).isEqualTo(id);
        assertThat(observacion.revisionAsesor()).isEqualTo(revision);
        assertThat(observacion.estado()).isEqualTo("CERRADA");
        assertThat(observacion.estadoNombre()).isEqualTo("Cerrada");
    }

    @Test
    void debeRetornarVacio_cuandoElEstudianteNoParticipaEnElArtefactoDeLaRevision() {
        // Arrange
        var revision = sembrarRevision(sembrarArtefacto(asesor, otroEstudiante));
        sembrarObservacion(revision, "Obs", "NUEVA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarDeEstudiante(revision, estudiante);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoElEstudianteFueRemovidoDelArtefacto() {
        // Arrange
        var revision = sembrarRevision(sembrarArtefacto(asesor, otroEstudiante));
        sembrarObservacion(revision, "Obs", "NUEVA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var delRemovido = adapter.consultarDeEstudiante(revision, estudiante);
        var delVigente = adapter.consultarDeEstudiante(revision, otroEstudiante);

        // Assert
        assertThat(delRemovido).isEmpty();
        assertThat(delVigente).hasSize(1);
    }

    @Test
    void debeRetornarVacio_cuandoLaRevisionDelEstudianteNoExiste() {
        // Arrange
        sembrarRevision(sembrarArtefacto(asesor, estudiante));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarDeEstudiante(UtilUUID.generarNuevoUUID(), estudiante);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
