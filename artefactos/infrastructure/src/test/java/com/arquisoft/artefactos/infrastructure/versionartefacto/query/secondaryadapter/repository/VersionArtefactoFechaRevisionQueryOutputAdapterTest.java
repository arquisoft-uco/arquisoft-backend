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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
class VersionArtefactoFechaRevisionQueryOutputAdapterTest {

    private static final Instant FECHA_ANTIGUA = Instant.parse("2026-10-02T10:00:00Z");
    private static final Instant FECHA_INTERMEDIA = Instant.parse("2026-10-03T10:00:00Z");
    private static final Instant FECHA_RECIENTE = Instant.parse("2026-10-05T10:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VersionArtefactoAsesorQueryRepository repository;

    @Autowired
    private VersionArtefactoEstudianteQueryRepository estudianteRepository;

    private VersionArtefactoQueryOutputAdapter adapter;

    private final UUID asesor = UtilUUID.generarNuevoUUID();
    private final UUID estudiante = UtilUUID.generarNuevoUUID();

    private UUID versionConDosRevisiones;
    private UUID versionSinRevision;
    private UUID versionConUnaRevision;

    @BeforeEach
    void setUp() {
        adapter = new VersionArtefactoQueryOutputAdapter(
                repository, new VersionArtefactoAsesorJpaSpecification(),
                estudianteRepository, new VersionArtefactoEstudianteJpaSpecification());
    }

    private UUID sembrarArtefacto(String proyecto, String documento) {
        var proyectoId = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(proyectoId, proyecto));
        var documentoId = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionRepositorioArtefactoJpaEntity(
                documentoId, documento, 1, UtilUUID.generarNuevoUUID(), "Repo",
                "archivo.pdf", "application/pdf", 10L));
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(artefacto, proyectoId, documentoId, asesor, null));
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefacto, estudiante));
        return artefacto;
    }

    private UUID sembrarVersion(UUID artefacto, int numero) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionArtefactoTestEntity(
                id, artefacto, numero, Instant.parse("2026-10-01T10:00:00Z"), "artefactos/" + id + ".pdf"));
        return id;
    }

    private void sembrarRevision(UUID version, Instant fecha) {
        entityManager.persist(new RevisionAsesorTestEntity(UtilUUID.generarNuevoUUID(), version, "PENDIENTE", fecha));
    }

    private void sembrarEscenario() {
        entityManager.persist(new AsesorJpaEntity(asesor, "ID-Ana", "Ana", "ana@uco.edu.co", Instant.now(), null));
        var artefactoUno = sembrarArtefacto("Alfa", "Anteproyecto");
        var artefactoDos = sembrarArtefacto("Beta", "Informe");
        versionConDosRevisiones = sembrarVersion(artefactoUno, 1);
        versionSinRevision = sembrarVersion(artefactoUno, 2);
        versionConUnaRevision = sembrarVersion(artefactoDos, 1);
        sembrarRevision(versionConDosRevisiones, FECHA_ANTIGUA);
        sembrarRevision(versionConDosRevisiones, FECHA_RECIENTE);
        sembrarRevision(versionConUnaRevision, FECHA_INTERMEDIA);
        entityManager.flush();
        entityManager.clear();
    }

    private VersionArtefactoAsesorCriteria criteriaAsesor(NodoFiltro raiz, List<SortOrder> orden) {
        var criterio = ConsultaCriteriaQuery.crear(0, 10, orden, raiz);
        return ConsultarVersionesArtefactoAsesorMapper.toCriteria(
                ConsultarVersionesArtefactoAsesorQuery.crear(asesor, criterio));
    }

    private VersionArtefactoEstudianteCriteria criteriaEstudiante(NodoFiltro raiz, List<SortOrder> orden) {
        var criterio = ConsultaCriteriaQuery.crear(0, 10, orden, raiz);
        return ConsultarVersionesArtefactoEstudianteMapper.toCriteria(
                ConsultarVersionesArtefactoEstudianteQuery.crear(estudiante, criterio));
    }

    private static List<UUID> ids(List<VersionArtefactoReadModel> contenido) {
        return contenido.stream().map(VersionArtefactoReadModel::id).toList();
    }

    @Test
    void debeProyectarFechaDeLaRevisionMasRecienteYNulaSinRevision_cuandoConsultaComoAsesor() {
        // Arrange
        sembrarEscenario();

        // Act
        var resultado = adapter.consultarTodasAsesor(criteriaAsesor(null, List.of()));

        // Assert
        assertThat(resultado.getContent())
                .extracting(VersionArtefactoReadModel::id, VersionArtefactoReadModel::fechaCreacionRevision)
                .containsExactlyInAnyOrder(
                        tuple(versionConDosRevisiones, FECHA_RECIENTE),
                        tuple(versionConUnaRevision, FECHA_INTERMEDIA),
                        tuple(versionSinRevision, null));
    }

    @Test
    void debeProyectarFechaDeLaRevisionMasRecienteYNulaSinRevision_cuandoConsultaComoEstudiante() {
        // Arrange
        sembrarEscenario();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteriaEstudiante(null, List.of()));

        // Assert
        assertThat(resultado.getContent())
                .extracting(VersionArtefactoReadModel::id, VersionArtefactoReadModel::fechaCreacionRevision)
                .containsExactlyInAnyOrder(
                        tuple(versionConDosRevisiones, FECHA_RECIENTE),
                        tuple(versionConUnaRevision, FECHA_INTERMEDIA),
                        tuple(versionSinRevision, null));
    }

    @Test
    void debeFiltrarPorRangoYPorNulidadDeFechaCreacionRevision_cuandoConsultaComoAsesor() {
        // Arrange
        sembrarEscenario();
        var desde = NodoFiltro.predicado(
                "fechaCreacionRevision", FiltroOperador.MAYOR_IGUAL_QUE, "2026-10-04T00:00:00");
        var sinRevision = NodoFiltro.predicado("fechaCreacionRevision", FiltroOperador.ES_NULO, null);

        // Act
        var enRango = adapter.consultarTodasAsesor(criteriaAsesor(desde, List.of()));
        var sinFecha = adapter.consultarTodasAsesor(criteriaAsesor(sinRevision, List.of()));

        // Assert
        assertThat(ids(enRango.getContent())).containsExactly(versionConDosRevisiones);
        assertThat(ids(sinFecha.getContent())).containsExactly(versionSinRevision);
    }

    @Test
    void debeFiltrarPorRangoYPorNulidadDeFechaCreacionRevision_cuandoConsultaComoEstudiante() {
        // Arrange
        sembrarEscenario();
        var hasta = NodoFiltro.predicado(
                "fechaCreacionRevision", FiltroOperador.MENOR_IGUAL_QUE, "2026-10-04T00:00:00");
        var sinRevision = NodoFiltro.predicado("fechaCreacionRevision", FiltroOperador.ES_NULO, null);

        // Act
        var enRango = adapter.consultarTodasEstudiante(criteriaEstudiante(hasta, List.of()));
        var sinFecha = adapter.consultarTodasEstudiante(criteriaEstudiante(sinRevision, List.of()));

        // Assert
        assertThat(ids(enRango.getContent())).containsExactly(versionConUnaRevision);
        assertThat(ids(sinFecha.getContent())).containsExactly(versionSinRevision);
    }

    @Test
    void debeOrdenarPorFechaCreacionRevision_ascYDesc() {
        // Arrange
        sembrarEscenario();
        var conRevision = NodoFiltro.predicado("fechaCreacionRevision", FiltroOperador.NO_ES_NULO, null);
        var asc = List.of(SortOrder.of("fechaCreacionRevision", SortDirection.ASC));
        var desc = List.of(SortOrder.of("fechaCreacionRevision", SortDirection.DESC));

        // Act
        var asesorAsc = adapter.consultarTodasAsesor(criteriaAsesor(conRevision, asc));
        var asesorDesc = adapter.consultarTodasAsesor(criteriaAsesor(conRevision, desc));
        var estudianteAsc = adapter.consultarTodasEstudiante(criteriaEstudiante(conRevision, asc));
        var estudianteDesc = adapter.consultarTodasEstudiante(criteriaEstudiante(conRevision, desc));

        // Assert
        assertThat(ids(asesorAsc.getContent())).containsExactly(versionConUnaRevision, versionConDosRevisiones);
        assertThat(ids(asesorDesc.getContent())).containsExactly(versionConDosRevisiones, versionConUnaRevision);
        assertThat(ids(estudianteAsc.getContent())).containsExactly(versionConUnaRevision, versionConDosRevisiones);
        assertThat(ids(estudianteDesc.getContent())).containsExactly(versionConDosRevisiones, versionConUnaRevision);
    }
}
