package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper.ConsultarVersionesArtefactoAsesorMapper;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper.ConsultarVersionesArtefactoEstudianteMapper;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionesArtefactoAsesorQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionesArtefactoEstudianteQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstudianteArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.arquisoft.artefactos.infrastructure.proyectogrado.EstadoProyectoFixture.estadoDe;
import static com.arquisoft.artefactos.infrastructure.proyectogrado.EstadoProyectoFixture.ocurridoEnDe;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VersionArtefactoCierreProyectoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VersionArtefactoAsesorQueryRepository repository;

    @Autowired
    private VersionArtefactoEstudianteQueryRepository estudianteRepository;

    private VersionArtefactoQueryOutputAdapter adapter;

    private final UUID asesorA = UtilUUID.generarNuevoUUID();
    private final UUID asesorB = UtilUUID.generarNuevoUUID();
    private final UUID estudianteX = UtilUUID.generarNuevoUUID();
    private final UUID estudianteY = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        adapter = new VersionArtefactoQueryOutputAdapter(
                repository, new VersionArtefactoAsesorJpaSpecification(),
                estudianteRepository, new VersionArtefactoEstudianteJpaSpecification());
    }

    private UUID sembrarVersion(UUID artefacto, int numero, String fecha) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionArtefactoTestEntity(
                id, artefacto, numero, Instant.parse(fecha), "artefactos/" + id + ".pdf"));
        return id;
    }

    private UUID sembrarVersionRepositorio(String documento, int version, String repositorio) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionRepositorioArtefactoJpaEntity(
                id, documento, version, UtilUUID.generarNuevoUUID(), repositorio,
                "archivo.pdf", "application/pdf", 10L));
        return id;
    }

    private UUID sembrarArtefacto(UUID proyecto, UUID versionRepositorio, UUID asesor) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(id, proyecto, versionRepositorio, asesor, null));
        return id;
    }

    private VersionArtefactoAsesorCriteria criteria(UUID asesor, NodoFiltro raiz) {
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz);
        return ConsultarVersionesArtefactoAsesorMapper.toCriteria(
                ConsultarVersionesArtefactoAsesorQuery.crear(asesor, criterio));
    }

    private VersionArtefactoEstudianteCriteria criteriaEstudiante(UUID estudiante, NodoFiltro raiz) {
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz);
        return ConsultarVersionesArtefactoEstudianteMapper.toCriteria(
                ConsultarVersionesArtefactoEstudianteQuery.crear(estudiante, criterio));
    }

    private static List<UUID> ids(List<VersionArtefactoReadModel> contenido) {
        return contenido.stream().map(VersionArtefactoReadModel::id).toList();
    }

    private UUID versionActivo;
    private UUID versionFinalizado;
    private UUID versionCancelado;
    private UUID versionCanceladoAjeno;

    private UUID sembrarVersionDeProyecto(
            String nombre, Instant finalizadoEn, Instant canceladoEn, UUID asesor, UUID estudiante) {
        return sembrarVersionDeProyectoConEstado(
                nombre, estadoDe(finalizadoEn, canceladoEn), ocurridoEnDe(finalizadoEn, canceladoEn),
                asesor, estudiante);
    }

    private UUID sembrarVersionDeProyectoConEstado(
            String nombre, String estado, Instant ocurridoEn, UUID asesor, UUID estudiante) {
        var proyecto = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(proyecto, nombre, null, estado, ocurridoEn));
        var documento = sembrarVersionRepositorio("Documento " + nombre, 1, "Repo " + nombre);
        var artefacto = sembrarArtefacto(proyecto, documento, asesor);
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefacto, estudiante));
        return sembrarVersion(artefacto, 1, "2026-10-20T10:00:00Z");
    }

    private void sembrarEscenarioCierre() {
        entityManager.persist(new AsesorJpaEntity(asesorA, "ID-Ana", "Ana", "ana@uco.edu.co", Instant.now(), null));
        entityManager.persist(new AsesorJpaEntity(asesorB, "ID-Beto", "Beto", "beto@uco.edu.co", Instant.now(), null));
        versionActivo = sembrarVersionDeProyecto("Activo", null, null, asesorA, estudianteX);
        versionFinalizado = sembrarVersionDeProyecto("Finalizado", Instant.now(), null, asesorA, estudianteX);
        versionCancelado = sembrarVersionDeProyecto("Cancelado", null, Instant.now(), asesorA, estudianteX);
        versionCanceladoAjeno = sembrarVersionDeProyecto("Ajeno", null, Instant.now(), asesorB, estudianteY);
        entityManager.flush();
        entityManager.clear();
    }

    private static NodoFiltro ocultarCerrados() {
        return NodoFiltro.grupo(FiltroConector.AND, List.of(
                NodoFiltro.predicado("proyectoFinalizado", FiltroOperador.ES, "false"),
                NodoFiltro.predicado("proyectoCancelado", FiltroOperador.ES, "false")));
    }

    @Test
    void debeNoMarcarCierre_cuandoElProyectoEstaAtrasado() {
        // Arrange
        entityManager.persist(new AsesorJpaEntity(asesorA, "ID-Ana", "Ana", "ana@uco.edu.co", Instant.now(), null));
        var atrasada = sembrarVersionDeProyectoConEstado("Atrasado", "ATRASADO", Instant.now(), asesorA, estudianteX);
        entityManager.flush();
        entityManager.clear();

        // Act
        var deAsesor = adapter.consultarTodasAsesor(criteria(asesorA, null));
        var deEstudiante = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, null));

        // Assert
        for (var resultado : List.of(deAsesor, deEstudiante)) {
            assertThat(resultado.getContent()).filteredOn(v -> v.id().equals(atrasada))
                    .singleElement().satisfies(v -> {
                        assertThat(v.proyectoFinalizado()).isFalse();
                        assertThat(v.proyectoCancelado()).isFalse();
                    });
        }
    }

    @Test
    void debeMarcarProyectoFinalizadoYCancelado_cuandoElProyectoTieneLasFechas() {
        // Arrange
        sembrarEscenarioCierre();

        // Act
        var deAsesor = adapter.consultarTodasAsesor(criteria(asesorA, null));
        var deEstudiante = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, null));

        // Assert
        for (var resultado : List.of(deAsesor, deEstudiante)) {
            assertThat(resultado.getContent()).filteredOn(v -> v.id().equals(versionActivo))
                    .singleElement().satisfies(v -> {
                        assertThat(v.proyectoFinalizado()).isFalse();
                        assertThat(v.proyectoCancelado()).isFalse();
                    });
            assertThat(resultado.getContent()).filteredOn(v -> v.id().equals(versionFinalizado))
                    .singleElement().satisfies(v -> {
                        assertThat(v.proyectoFinalizado()).isTrue();
                        assertThat(v.proyectoCancelado()).isFalse();
                    });
            assertThat(resultado.getContent()).filteredOn(v -> v.id().equals(versionCancelado))
                    .singleElement().satisfies(v -> {
                        assertThat(v.proyectoFinalizado()).isFalse();
                        assertThat(v.proyectoCancelado()).isTrue();
                    });
        }
    }

    @Test
    void debeRetornarTodasLasVersiones_cuandoNoFiltraPorCierreDelProyecto() {
        // Arrange
        sembrarEscenarioCierre();

        // Act
        var deAsesor = adapter.consultarTodasAsesor(criteria(asesorA, null));
        var deEstudiante = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, null));

        // Assert
        assertThat(ids(deAsesor.getContent()))
                .containsExactlyInAnyOrder(versionActivo, versionFinalizado, versionCancelado);
        assertThat(ids(deEstudiante.getContent()))
                .containsExactlyInAnyOrder(versionActivo, versionFinalizado, versionCancelado);
    }

    @Test
    void debeOcultarVersionesDeProyectosCerrados_cuandoFiltraAmbosEnFalsePorAnd() {
        // Arrange
        sembrarEscenarioCierre();

        // Act
        var deAsesor = adapter.consultarTodasAsesor(criteria(asesorA, ocultarCerrados()));
        var deEstudiante = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, ocultarCerrados()));

        // Assert
        assertThat(ids(deAsesor.getContent())).containsExactly(versionActivo);
        assertThat(ids(deEstudiante.getContent())).containsExactly(versionActivo);
    }

    @Test
    void debeNoMostrarVersionesAjenas_cuandoFiltraPorProyectoCancelado() {
        // Arrange
        sembrarEscenarioCierre();
        var soloCancelados = NodoFiltro.predicado("proyectoCancelado", FiltroOperador.ES, "true");

        // Act
        var deAsesor = adapter.consultarTodasAsesor(criteria(asesorA, soloCancelados));
        var deEstudiante = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, soloCancelados));

        // Assert
        assertThat(ids(deAsesor.getContent())).containsExactly(versionCancelado)
                .doesNotContain(versionCanceladoAjeno);
        assertThat(ids(deEstudiante.getContent())).containsExactly(versionCancelado)
                .doesNotContain(versionCanceladoAjeno);
    }
}
