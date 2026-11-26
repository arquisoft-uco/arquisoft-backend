package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.mapper.ConsultarParticipacionesArtefactoMapper;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.entity.EstudianteArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstadoRevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.RevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ParticipacionArtefactoFiltrosQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstudianteArtefactoQueryRepository repository;

    @Autowired
    private ParticipacionArtefactoQueryRepository participacionRepository;

    private EstudianteArtefactoQueryOutputAdapter adapter;

    private UUID pendiente;
    private UUID cerrada;
    private UUID sinRevision;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteArtefactoQueryOutputAdapter(
                repository, participacionRepository, new ParticipacionArtefactoJpaSpecification());
    }

    private UUID sembrarProyecto(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(id, nombre, null, "EN_PROCESO", null));
        return id;
    }

    private UUID sembrarVersion(String documento, int version, String repositorio) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionRepositorioArtefactoJpaEntity(
                id, documento, version, UtilUUID.generarNuevoUUID(), repositorio,
                "archivo.pdf", "application/pdf", 10L));
        return id;
    }

    private UUID sembrarAsesor(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new AsesorJpaEntity(id, "ID-" + nombre, nombre, nombre.toLowerCase() + "@uco.edu.co",
                Instant.now(), null));
        return id;
    }

    private UUID sembrarParticipacion(UUID estudiante, UUID proyecto, UUID version) {
        return sembrarParticipacion(estudiante, proyecto, version, sembrarAsesor("Ana"));
    }

    private UUID sembrarParticipacion(UUID estudiante, UUID proyecto, UUID version, UUID asesor) {
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(artefacto, proyecto, version, asesor, null));
        entityManager.persist(new EstudianteArtefactoJpaEntity(UtilUUID.generarNuevoUUID(), artefacto, estudiante));
        return artefacto;
    }

    private void sembrarEstado(String id, String nombre) {
        entityManager.persist(new EstadoRevisionAsesorTestEntity(id, nombre));
    }

    private void sembrarRevision(UUID artefacto, String estadoId, long diasAtras) {
        var version = UtilUUID.generarNuevoUUID();
        var fecha = Instant.now().minus(diasAtras, ChronoUnit.DAYS);
        entityManager.persist(new VersionArtefactoTestEntity(version, artefacto, 1, fecha, "artefactos/" + version));
        entityManager.persist(new RevisionAsesorTestEntity(UtilUUID.generarNuevoUUID(), version, estadoId, fecha));
    }

    private UUID sembrarProyectoConEstado(String nombre, String estado) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(id, nombre, null, estado, Instant.now()));
        return id;
    }

    private static ParticipacionArtefactoCriteria criteria(
            UUID estudiante, NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarParticipacionesArtefactoMapper.toCriteria(
                ConsultarParticipacionesArtefactoQuery.crear(estudiante, criterio));
    }

    private static ParticipacionArtefactoCriteria criteria(UUID estudiante, NodoFiltro raiz) {
        return criteria(estudiante, raiz, List.of(), 0, 10);
    }

    private static List<UUID> ids(PaginatedResult<ParticipacionArtefactoReadModel> pagina) {
        return pagina.getContent().stream().map(ParticipacionArtefactoReadModel::id).toList();
    }

    @Test
    void debeRetornarEstadoDeLaUltimaRevisionYNuloSinRevisiones() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = sembrarProyecto("Alfa");
        var conRevisiones = sembrarParticipacion(estudiante, proyecto, sembrarVersion("Informe", 1, "Repo"));
        var sinRevisiones = sembrarParticipacion(estudiante, proyecto, sembrarVersion("Anexo", 1, "Repo"));
        sembrarEstado("PENDIENTE", "Pendiente");
        sembrarEstado("CERRADA", "Cerrada");
        sembrarRevision(conRevisiones, "CERRADA", 90);
        sembrarRevision(conRevisiones, "PENDIENTE", 30);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarParticipaciones(criteria(estudiante, null));

        // Assert
        var porId = resultado.getContent().stream()
                .collect(Collectors.toMap(ParticipacionArtefactoReadModel::id,
                        participacion -> Optional.ofNullable(participacion.estadoUltimaRevision())));
        assertThat(porId.get(conRevisiones)).contains("Pendiente");
        assertThat(porId.get(sinRevisiones)).isEmpty();
    }

    private void sembrarEscenarioRevisiones(UUID estudiante) {
        var proyecto = sembrarProyecto("Alfa");
        pendiente = sembrarParticipacion(estudiante, proyecto, sembrarVersion("Anteproyecto", 1, "Repo"));
        cerrada = sembrarParticipacion(estudiante, proyecto, sembrarVersion("Informe", 1, "Repo"));
        sinRevision = sembrarParticipacion(estudiante, proyecto, sembrarVersion("Anexo", 1, "Repo"));
        sembrarEstado("PENDIENTE", "Pendiente");
        sembrarEstado("CERRADA", "Cerrada");
        sembrarRevision(pendiente, "PENDIENTE", 30);
        sembrarRevision(cerrada, "CERRADA", 20);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeFiltrarPorEstadoUltimaRevision_conESNOESEIN() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        sembrarEscenarioRevisiones(estudiante);

        // Act
        var es = adapter.consultarParticipaciones(criteria(estudiante,
                NodoFiltro.predicado("estadoUltimaRevision", FiltroOperador.ES, "Pendiente")));
        var noEs = adapter.consultarParticipaciones(criteria(estudiante,
                NodoFiltro.predicado("estadoUltimaRevision", FiltroOperador.NO_ES, "Pendiente")));
        var in = adapter.consultarParticipaciones(criteria(estudiante,
                NodoFiltro.predicadoMultivalor("estadoUltimaRevision", FiltroOperador.IN,
                        List.of("Pendiente", "Cerrada"))));

        // Assert
        assertThat(ids(es)).containsExactly(pendiente);
        assertThat(ids(noEs)).contains(cerrada).doesNotContain(pendiente);
        assertThat(ids(in)).containsExactlyInAnyOrder(pendiente, cerrada);
    }

    @Test
    void debeSepararConYSinRevision_conESNULOYNOESNULO() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        sembrarEscenarioRevisiones(estudiante);

        // Act
        var sin = adapter.consultarParticipaciones(criteria(estudiante,
                NodoFiltro.predicado("estadoUltimaRevision", FiltroOperador.ES_NULO, null)));
        var con = adapter.consultarParticipaciones(criteria(estudiante,
                NodoFiltro.predicado("estadoUltimaRevision", FiltroOperador.NO_ES_NULO, null)));

        // Assert
        assertThat(ids(sin)).containsExactly(sinRevision);
        assertThat(ids(con)).containsExactlyInAnyOrder(pendiente, cerrada);
    }

    @Test
    void debeOrdenarPorEstadoUltimaRevision() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        sembrarEscenarioRevisiones(estudiante);
        var conRevision = NodoFiltro.predicado("estadoUltimaRevision", FiltroOperador.NO_ES_NULO, null);

        // Act
        var asc = adapter.consultarParticipaciones(criteria(estudiante, conRevision,
                List.of(SortOrder.of("estadoUltimaRevision", SortDirection.ASC)), 0, 10));
        var desc = adapter.consultarParticipaciones(criteria(estudiante, conRevision,
                List.of(SortOrder.of("estadoUltimaRevision", SortDirection.DESC)), 0, 10));

        // Assert
        assertThat(ids(asc)).containsExactly(cerrada, pendiente);
        assertThat(ids(desc)).containsExactly(pendiente, cerrada);
    }

    @Test
    void debeFiltrarPorOmitido() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = sembrarProyecto("Alfa");
        var version = sembrarVersion("Informe", 1, "Repo");
        var activo = sembrarParticipacion(estudiante, proyecto, version);
        var omitido = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(
                omitido, proyecto, version, sembrarAsesor("Beto"), Instant.now()));
        entityManager.persist(new EstudianteArtefactoJpaEntity(UtilUUID.generarNuevoUUID(), omitido, estudiante));
        entityManager.flush();
        entityManager.clear();

        // Act
        var soloOmitidos = adapter.consultarParticipaciones(criteria(estudiante,
                NodoFiltro.predicado("omitido", FiltroOperador.ES, "true")));
        var soloActivos = adapter.consultarParticipaciones(criteria(estudiante,
                NodoFiltro.predicado("omitido", FiltroOperador.ES, "false")));

        // Assert
        assertThat(ids(soloOmitidos)).containsExactly(omitido);
        assertThat(ids(soloActivos)).containsExactly(activo);
    }

    @Test
    void debeFiltrarPorProyectoFinalizadoYCancelado() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var enProceso = sembrarParticipacion(estudiante, sembrarProyectoConEstado("Uno", "EN_PROCESO"),
                sembrarVersion("Informe", 1, "Repo"));
        var finalizado = sembrarParticipacion(estudiante, sembrarProyectoConEstado("Dos", "FINALIZADO"),
                sembrarVersion("Informe", 1, "Repo"));
        var cancelado = sembrarParticipacion(estudiante, sembrarProyectoConEstado("Tres", "CANCELADO"),
                sembrarVersion("Informe", 1, "Repo"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var finalizados = adapter.consultarParticipaciones(criteria(estudiante,
                NodoFiltro.predicado("proyectoFinalizado", FiltroOperador.ES, "true")));
        var cancelados = adapter.consultarParticipaciones(criteria(estudiante,
                NodoFiltro.predicado("proyectoCancelado", FiltroOperador.ES, "true")));
        var todos = adapter.consultarParticipaciones(criteria(estudiante, null));

        // Assert
        assertThat(ids(finalizados)).containsExactly(finalizado);
        assertThat(ids(cancelados)).containsExactly(cancelado);
        assertThat(todos.getContent()).filteredOn(ParticipacionArtefactoReadModel::proyectoFinalizado)
                .extracting("id").containsExactly(finalizado);
        assertThat(ids(todos)).contains(enProceso);
    }

    @Test
    void debeFiltrarPorNombreAsesor() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = sembrarProyecto("Alfa");
        var deDiana = sembrarParticipacion(estudiante, proyecto, sembrarVersion("Anteproyecto", 1, "Repo"),
                sembrarAsesor("Diana"));
        sembrarParticipacion(estudiante, proyecto, sembrarVersion("Informe", 1, "Repo"), sembrarAsesor("Elena"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarParticipaciones(criteria(estudiante,
                NodoFiltro.predicado("nombreAsesor", FiltroOperador.ES, "Diana")));

        // Assert
        assertThat(ids(resultado)).containsExactly(deDiana);
    }

    @Test
    void debeMantenerTotalSobreElConjunto_cuandoFiltraYPagina() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        sembrarEscenarioRevisiones(estudiante);
        var conRevision = NodoFiltro.predicado("estadoUltimaRevision", FiltroOperador.NO_ES_NULO, null);

        // Act
        var pagina = adapter.consultarParticipaciones(criteria(estudiante, conRevision, List.of(), 0, 1));

        // Assert
        assertThat(pagina.getTotalElements()).isEqualTo(2L);
        assertThat(pagina.getContent()).hasSize(1);
    }

}
