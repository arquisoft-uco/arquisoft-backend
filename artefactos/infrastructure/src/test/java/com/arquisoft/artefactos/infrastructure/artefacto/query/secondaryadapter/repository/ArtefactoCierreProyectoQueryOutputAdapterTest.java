package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.mapper.ConsultarArtefactosAsesorMapper;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.mapper.ConsultarArtefactosEstudianteMapper;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosAsesorQuery;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosEstudianteQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoEstudianteReadModel;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ArtefactoCierreProyectoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArtefactoAsesorQueryRepository repository;

    @Autowired
    private ArtefactoEstudianteQueryRepository estudianteRepository;

    @Autowired
    private ArtefactoSinAvanceQueryRepository sinAvanceRepository;

    private ArtefactoQueryOutputAdapter adapter;

    private final UUID asesorA = UtilUUID.generarNuevoUUID();
    private final UUID estudianteX = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        adapter = new ArtefactoQueryOutputAdapter(repository, new ArtefactoAsesorJpaSpecification(),
                estudianteRepository, new ArtefactoEstudianteJpaSpecification(),
                sinAvanceRepository, new ArtefactoSinAvanceJpaSpecification());
    }

    private ArtefactoAsesorCriteria criteria(UUID asesor, NodoFiltro raiz) {
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz);
        return ConsultarArtefactosAsesorMapper.toCriteria(ConsultarArtefactosAsesorQuery.crear(asesor, criterio));
    }

    private ArtefactoEstudianteCriteria criteriaEstudiante(UUID estudiante, NodoFiltro raiz) {
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz);
        return ConsultarArtefactosEstudianteMapper.toCriteria(
                ConsultarArtefactosEstudianteQuery.crear(estudiante, criterio));
    }

    private static List<UUID> ids(List<ArtefactoReadModel> contenido) {
        return contenido.stream().map(ArtefactoReadModel::id).toList();
    }

    private static List<UUID> idsEstudiante(List<ArtefactoEstudianteReadModel> contenido) {
        return contenido.stream().map(ArtefactoEstudianteReadModel::id).toList();
    }

    private UUID sembrarVersion(String documento, int version, String repositorio) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionRepositorioArtefactoJpaEntity(
                id, documento, version, UtilUUID.generarNuevoUUID(), repositorio,
                "archivo.pdf", "application/pdf", 10L));
        return id;
    }

    private UUID sembrarArtefacto(UUID proyecto, UUID version, UUID asesor) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(id, proyecto, version, asesor, null));
        return id;
    }

    private void sembrarVinculo(UUID estudiante, UUID proyecto, Instant destituidoEn) {
        entityManager.persist(new EstudianteProyectoGradoJpaEntity(
                UtilUUID.generarNuevoUUID(), estudiante, proyecto, Instant.now(), destituidoEn));
    }

    private void sembrarAsesor(UUID id, String nombre, Instant eliminadoEn) {
        entityManager.persist(new AsesorJpaEntity(id, "ID-" + nombre, nombre, nombre + "@uco.edu.co",
                Instant.now(), eliminadoEn));
    }

    private UUID artefactoActivo;
    private UUID artefactoFinalizado;
    private UUID artefactoCancelado;

    private UUID sembrarArtefactoDeProyecto(String nombre, Instant finalizadoEn, Instant canceladoEn) {
        return sembrarArtefactoDeProyectoConEstado(
                nombre, estadoDe(finalizadoEn, canceladoEn), ocurridoEnDe(finalizadoEn, canceladoEn));
    }

    private UUID sembrarArtefactoDeProyectoConEstado(String nombre, String estado, Instant ocurridoEn) {
        var proyecto = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(proyecto, nombre, null, estado, ocurridoEn));
        var version = sembrarVersion("Documento " + nombre, 1, "Repo " + nombre);
        sembrarVinculo(estudianteX, proyecto, null);
        return sembrarArtefacto(proyecto, version, asesorA);
    }

    @Test
    void debeNoMarcarCierre_cuandoElProyectoEstaAtrasado() {
        // Arrange
        sembrarAsesor(asesorA, "Ana", null);
        var atrasado = sembrarArtefactoDeProyectoConEstado("Atrasado", "ATRASADO", Instant.now());
        entityManager.flush();

        // Act
        var deAsesor = adapter.consultarTodosAsesor(criteria(asesorA, null));
        var deEstudiante = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, null));

        // Assert
        assertThat(deAsesor.getContent()).filteredOn(a -> a.id().equals(atrasado)).singleElement()
                .satisfies(a -> {
                    assertThat(a.proyectoFinalizado()).isFalse();
                    assertThat(a.proyectoCancelado()).isFalse();
                });
        assertThat(deEstudiante.getContent()).filteredOn(a -> a.id().equals(atrasado)).singleElement()
                .satisfies(a -> {
                    assertThat(a.proyectoFinalizado()).isFalse();
                    assertThat(a.proyectoCancelado()).isFalse();
                });
    }

    private void sembrarEscenarioCierre() {
        sembrarAsesor(asesorA, "Ana", null);
        artefactoActivo = sembrarArtefactoDeProyecto("Activo", null, null);
        artefactoFinalizado = sembrarArtefactoDeProyecto("Finalizado", Instant.now(), null);
        artefactoCancelado = sembrarArtefactoDeProyecto("Cancelado", null, Instant.now());
        entityManager.flush();
        entityManager.clear();
    }

    private static NodoFiltro ocultarCerrados() {
        return NodoFiltro.grupo(FiltroConector.AND, List.of(
                NodoFiltro.predicado("proyectoFinalizado", FiltroOperador.ES, "false"),
                NodoFiltro.predicado("proyectoCancelado", FiltroOperador.ES, "false")));
    }

    @Test
    void debeMarcarProyectoFinalizadoYCancelado_cuandoElProyectoTieneLasFechas() {
        // Arrange
        sembrarEscenarioCierre();

        // Act
        var deAsesor = adapter.consultarTodosAsesor(criteria(asesorA, null));
        var deEstudiante = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, null));

        // Assert
        assertThat(deAsesor.getContent()).filteredOn(a -> a.id().equals(artefactoActivo))
                .singleElement().satisfies(a -> {
                    assertThat(a.proyectoFinalizado()).isFalse();
                    assertThat(a.proyectoCancelado()).isFalse();
                });
        assertThat(deAsesor.getContent()).filteredOn(a -> a.id().equals(artefactoFinalizado))
                .singleElement().satisfies(a -> {
                    assertThat(a.proyectoFinalizado()).isTrue();
                    assertThat(a.proyectoCancelado()).isFalse();
                });
        assertThat(deAsesor.getContent()).filteredOn(a -> a.id().equals(artefactoCancelado))
                .singleElement().satisfies(a -> {
                    assertThat(a.proyectoFinalizado()).isFalse();
                    assertThat(a.proyectoCancelado()).isTrue();
                });
        assertThat(deEstudiante.getContent()).filteredOn(a -> a.id().equals(artefactoActivo))
                .singleElement().satisfies(a -> {
                    assertThat(a.proyectoFinalizado()).isFalse();
                    assertThat(a.proyectoCancelado()).isFalse();
                });
        assertThat(deEstudiante.getContent()).filteredOn(a -> a.id().equals(artefactoFinalizado))
                .singleElement().satisfies(a -> assertThat(a.proyectoFinalizado()).isTrue());
        assertThat(deEstudiante.getContent()).filteredOn(a -> a.id().equals(artefactoCancelado))
                .singleElement().satisfies(a -> assertThat(a.proyectoCancelado()).isTrue());
    }

    @Test
    void debeRetornarTodosLosArtefactos_cuandoNoFiltraPorCierreDelProyecto() {
        // Arrange
        sembrarEscenarioCierre();

        // Act
        var deAsesor = adapter.consultarTodosAsesor(criteria(asesorA, null));
        var deEstudiante = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, null));

        // Assert
        assertThat(ids(deAsesor.getContent()))
                .containsExactlyInAnyOrder(artefactoActivo, artefactoFinalizado, artefactoCancelado);
        assertThat(idsEstudiante(deEstudiante.getContent()))
                .containsExactlyInAnyOrder(artefactoActivo, artefactoFinalizado, artefactoCancelado);
    }

    @Test
    void debeOcultarArtefactosDeProyectosCerrados_cuandoFiltraAmbosEnFalsePorAnd() {
        // Arrange
        sembrarEscenarioCierre();

        // Act
        var deAsesor = adapter.consultarTodosAsesor(criteria(asesorA, ocultarCerrados()));
        var deEstudiante = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, ocultarCerrados()));

        // Assert
        assertThat(ids(deAsesor.getContent())).containsExactly(artefactoActivo);
        assertThat(deAsesor.getTotalElements()).isEqualTo(1L);
        assertThat(idsEstudiante(deEstudiante.getContent())).containsExactly(artefactoActivo);
    }

    @Test
    void debeRetornarSoloCancelados_cuandoFiltraProyectoCanceladoEnTrue() {
        // Arrange
        sembrarEscenarioCierre();
        var soloCancelados = NodoFiltro.predicado("proyectoCancelado", FiltroOperador.ES, "true");
        var noFinalizados = NodoFiltro.predicado("proyectoFinalizado", FiltroOperador.NO_ES, "true");

        // Act
        var cancelados = adapter.consultarTodosAsesor(criteria(asesorA, soloCancelados));
        var sinFinalizados = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, noFinalizados));

        // Assert
        assertThat(ids(cancelados.getContent())).containsExactly(artefactoCancelado);
        assertThat(idsEstudiante(sinFinalizados.getContent()))
                .containsExactlyInAnyOrder(artefactoActivo, artefactoCancelado);
    }

    @Test
    void debeLanzarFiltroInvalidoException_cuandoOperadorNoAplicaAProyectoFinalizado() {
        // Arrange
        sembrarEscenarioCierre();
        var criterio = criteria(asesorA, NodoFiltro.predicado("proyectoFinalizado", FiltroOperador.CONTIENE, "true"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultarTodosAsesor(criterio))
                .isInstanceOf(FiltroInvalidoException.class);
    }
}
