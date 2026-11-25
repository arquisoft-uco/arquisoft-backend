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
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstadoRevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.RevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArtefactoEstadoUltimaRevisionQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArtefactoAsesorQueryRepository asesorRepository;

    @Autowired
    private ArtefactoEstudianteQueryRepository estudianteRepository;

    @Autowired
    private ArtefactoSinAvanceQueryRepository sinAvanceRepository;

    private ArtefactoQueryOutputAdapter adapter;

    private final UUID asesorA = UtilUUID.generarNuevoUUID();
    private final UUID asesorB = UtilUUID.generarNuevoUUID();
    private final UUID estudianteX = UtilUUID.generarNuevoUUID();

    private UUID artefactoAlfa1;
    private UUID artefactoAlfa2;
    private UUID artefactoBeta1;
    private UUID artefactoDeB;

    @BeforeEach
    void setUp() {
        adapter = new ArtefactoQueryOutputAdapter(asesorRepository, new ArtefactoAsesorJpaSpecification(),
                estudianteRepository, new ArtefactoEstudianteJpaSpecification(),
                sinAvanceRepository, new ArtefactoSinAvanceJpaSpecification());
        sembrarEscenario();
    }

    private UUID sembrarProyecto(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(id, nombre, null, "EN_PROCESO", null));
        return id;
    }

    private UUID sembrarVersionRepositorio(String documento, int version, String repositorio) {
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

    private UUID sembrarVersionArtefacto(UUID artefacto, int numero, long diasAtras) {
        var id = UtilUUID.generarNuevoUUID();
        var fecha = Instant.now().minus(diasAtras, ChronoUnit.DAYS);
        entityManager.persist(new VersionArtefactoTestEntity(id, artefacto, numero, fecha, "artefactos/" + id));
        return id;
    }

    private void sembrarEscenario() {
        var proyectoAlfa = sembrarProyecto("Alfa");
        var proyectoBeta = sembrarProyecto("Beta");
        var versionAlfaDoc1 = sembrarVersionRepositorio("Anteproyecto", 1, "Repo Uno");
        var versionAlfaDoc2 = sembrarVersionRepositorio("Informe", 2, "Repo Dos");
        var versionBetaDoc1 = sembrarVersionRepositorio("Anteproyecto", 3, "Repo Uno");
        artefactoAlfa1 = sembrarArtefacto(proyectoAlfa, versionAlfaDoc2, asesorA);
        artefactoAlfa2 = sembrarArtefacto(proyectoAlfa, versionAlfaDoc1, asesorA);
        artefactoBeta1 = sembrarArtefacto(proyectoBeta, versionBetaDoc1, asesorA);
        artefactoDeB = sembrarArtefacto(proyectoAlfa, versionAlfaDoc1, asesorB);
        entityManager.persist(new AsesorJpaEntity(asesorA, "ID-Ana", "Ana", "ana@uco.edu.co", Instant.now(), null));
        entityManager.persist(new AsesorJpaEntity(asesorB, "ID-Beto", "Beto", "beto@uco.edu.co", Instant.now(), null));
        entityManager.persist(new EstudianteProyectoGradoJpaEntity(
                UtilUUID.generarNuevoUUID(), estudianteX, proyectoAlfa, Instant.now(), null));
        entityManager.persist(new EstadoRevisionAsesorTestEntity("PENDIENTE", "Pendiente"));
        entityManager.persist(new EstadoRevisionAsesorTestEntity("CERRADA", "Cerrada"));
        var versionVieja = sembrarVersionArtefacto(artefactoAlfa1, 1, 90);
        var versionNueva = sembrarVersionArtefacto(artefactoAlfa1, 2, 80);
        var versionBeta = sembrarVersionArtefacto(artefactoBeta1, 1, 70);
        sembrarVersionArtefacto(artefactoAlfa2, 1, 40);
        entityManager.persist(new RevisionAsesorTestEntity(UtilUUID.generarNuevoUUID(), versionVieja, "CERRADA",
                Instant.now().minus(85, ChronoUnit.DAYS)));
        entityManager.persist(new RevisionAsesorTestEntity(UtilUUID.generarNuevoUUID(), versionNueva, "PENDIENTE",
                Instant.now().minus(60, ChronoUnit.DAYS)));
        entityManager.persist(new RevisionAsesorTestEntity(UtilUUID.generarNuevoUUID(), versionBeta, "CERRADA",
                Instant.now().minus(50, ChronoUnit.DAYS)));
        entityManager.flush();
        entityManager.clear();
    }

    private ArtefactoAsesorCriteria criteria(NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarArtefactosAsesorMapper.toCriteria(
                ConsultarArtefactosAsesorQuery.crear(asesorA, criterio));
    }

    private ArtefactoAsesorCriteria criteria(NodoFiltro raiz) {
        return criteria(raiz, List.of(), 0, 10);
    }

    private ArtefactoEstudianteCriteria criteriaEstudiante(
            NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarArtefactosEstudianteMapper.toCriteria(
                ConsultarArtefactosEstudianteQuery.crear(estudianteX, criterio));
    }

    private ArtefactoEstudianteCriteria criteriaEstudiante(NodoFiltro raiz) {
        return criteriaEstudiante(raiz, List.of(), 0, 10);
    }

    private static List<UUID> ids(List<ArtefactoReadModel> contenido) {
        return contenido.stream().map(ArtefactoReadModel::id).toList();
    }

    private static List<UUID> idsEstudiante(List<ArtefactoEstudianteReadModel> contenido) {
        return contenido.stream().map(ArtefactoEstudianteReadModel::id).toList();
    }

    private static NodoFiltro estadoUltimaRevision(FiltroOperador operador, String valor) {
        return NodoFiltro.predicado("estadoUltimaRevision", operador, valor);
    }

    @Test
    void debeRetornarEstadoDeLaUltimaRevisionYNuloSinRevisiones_enAsesorYEstudiante() {
        // Act
        var deAsesor = adapter.consultarTodosAsesor(criteria(null));
        var deEstudiante = adapter.consultarTodosEstudiante(criteriaEstudiante(null));

        // Assert
        var alfa1Asesor = deAsesor.getContent().stream()
                .filter(a -> a.id().equals(artefactoAlfa1)).findFirst().orElseThrow();
        var alfa2Asesor = deAsesor.getContent().stream()
                .filter(a -> a.id().equals(artefactoAlfa2)).findFirst().orElseThrow();
        var alfa1Estudiante = deEstudiante.getContent().stream()
                .filter(a -> a.id().equals(artefactoAlfa1)).findFirst().orElseThrow();
        assertThat(alfa1Asesor.estadoUltimaRevision()).isEqualTo("Pendiente");
        assertThat(alfa2Asesor.estadoUltimaRevision()).isNull();
        assertThat(alfa1Estudiante.estadoUltimaRevision()).isEqualTo("Pendiente");
        assertThat(deAsesor.getTotalElements()).isEqualTo(3L);
    }

    @Test
    void debeFiltrarPorEstadoUltimaRevision_cuandoOperadorEsESOEsNOES() {
        // Act
        var esAsesor = adapter.consultarTodosAsesor(
                criteria(estadoUltimaRevision(FiltroOperador.ES, "Pendiente")));
        var esEstudiante = adapter.consultarTodosEstudiante(
                criteriaEstudiante(estadoUltimaRevision(FiltroOperador.ES, "Pendiente")));
        var noEsAsesor = adapter.consultarTodosAsesor(
                criteria(estadoUltimaRevision(FiltroOperador.NO_ES, "Pendiente")));

        // Assert
        assertThat(ids(esAsesor.getContent())).containsExactly(artefactoAlfa1);
        assertThat(idsEstudiante(esEstudiante.getContent())).containsExactly(artefactoAlfa1);
        assertThat(ids(noEsAsesor.getContent())).containsExactly(artefactoBeta1);
    }

    @Test
    void debeSepararArtefactosConYSinRevision_cuandoOperadorEsESNULOONOESNULO() {
        // Act
        var sinRevision = adapter.consultarTodosAsesor(
                criteria(estadoUltimaRevision(FiltroOperador.ES_NULO, null)));
        var conRevision = adapter.consultarTodosAsesor(
                criteria(estadoUltimaRevision(FiltroOperador.NO_ES_NULO, null)));
        var sinRevisionEstudiante = adapter.consultarTodosEstudiante(
                criteriaEstudiante(estadoUltimaRevision(FiltroOperador.ES_NULO, null)));

        // Assert
        assertThat(ids(sinRevision.getContent())).containsExactly(artefactoAlfa2);
        assertThat(ids(conRevision.getContent())).containsExactlyInAnyOrder(artefactoAlfa1, artefactoBeta1);
        assertThat(idsEstudiante(sinRevisionEstudiante.getContent()))
                .containsExactlyInAnyOrder(artefactoAlfa2, artefactoDeB);
    }

    @Test
    void debeOrdenarPorEstadoUltimaRevision_cuandoElClienteLoPide() {
        // Arrange
        var filtro = estadoUltimaRevision(FiltroOperador.NO_ES_NULO, null);
        var asc = List.of(SortOrder.of("estadoUltimaRevision", SortDirection.ASC));
        var desc = List.of(SortOrder.of("estadoUltimaRevision", SortDirection.DESC));

        // Act
        var ascendente = adapter.consultarTodosAsesor(criteria(filtro, asc, 0, 10));
        var descendente = adapter.consultarTodosAsesor(criteria(filtro, desc, 0, 10));
        var descendenteEstudiante = adapter.consultarTodosEstudiante(
                criteriaEstudiante(filtro, desc, 0, 10));

        // Assert
        assertThat(ids(ascendente.getContent())).containsExactly(artefactoBeta1, artefactoAlfa1);
        assertThat(ids(descendente.getContent())).containsExactly(artefactoAlfa1, artefactoBeta1);
        assertThat(idsEstudiante(descendenteEstudiante.getContent())).containsExactly(artefactoAlfa1);
    }

    @Test
    void debeMantenerTotalSobreElConjunto_cuandoFiltraPorEstadoUltimaRevisionYPagina() {
        // Arrange
        var filtro = estadoUltimaRevision(FiltroOperador.NO_ES_NULO, null);

        // Act
        var pagina = adapter.consultarTodosAsesor(criteria(filtro, List.of(), 0, 1));

        // Assert
        assertThat(pagina.getTotalElements()).isEqualTo(2L);
        assertThat(pagina.getContent()).hasSize(1);
    }
}
