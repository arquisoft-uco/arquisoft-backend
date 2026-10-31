package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.mapper.ConsultarArtefactosEstudianteMapper;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosEstudianteQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoEstudianteReadModel;
import com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.mapper.ConsultarArtefactosAsesorMapper;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosAsesorQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
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

@DataJpaTest
class ArtefactoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArtefactoAsesorQueryRepository repository;

    @Autowired
    private ArtefactoEstudianteQueryRepository estudianteRepository;

    private ArtefactoQueryOutputAdapter adapter;

    private final UUID asesorA = UtilUUID.generarNuevoUUID();
    private final UUID asesorB = UtilUUID.generarNuevoUUID();
    private final UUID estudianteX = UtilUUID.generarNuevoUUID();
    private final UUID estudianteY = UtilUUID.generarNuevoUUID();

    private UUID proyectoAlfa;
    private UUID proyectoBeta;
    private UUID versionAlfaDoc1;
    private UUID versionAlfaDoc2;
    private UUID versionBetaDoc1;
    private UUID artefactoAlfa1;
    private UUID artefactoAlfa2;
    private UUID artefactoBeta1;
    private UUID artefactoDeB;

    @BeforeEach
    void setUp() {
        adapter = new ArtefactoQueryOutputAdapter(repository, new ArtefactoAsesorJpaSpecification(),
                estudianteRepository, new ArtefactoEstudianteJpaSpecification());
    }

    private UUID sembrarProyecto(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(id, nombre));
        return id;
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

    private void sembrarEscenario() {
        proyectoAlfa = sembrarProyecto("Alfa");
        proyectoBeta = sembrarProyecto("Beta");
        versionAlfaDoc1 = sembrarVersion("Anteproyecto", 1, "Repo Uno");
        versionAlfaDoc2 = sembrarVersion("Informe", 2, "Repo Dos");
        versionBetaDoc1 = sembrarVersion("Anteproyecto", 3, "Repo Uno");

        artefactoAlfa1 = sembrarArtefacto(proyectoAlfa, versionAlfaDoc2, asesorA);
        artefactoAlfa2 = sembrarArtefacto(proyectoAlfa, versionAlfaDoc1, asesorA);
        artefactoBeta1 = sembrarArtefacto(proyectoBeta, versionBetaDoc1, asesorA);
        artefactoDeB = sembrarArtefacto(proyectoAlfa, versionAlfaDoc1, asesorB);
        entityManager.flush();
        entityManager.clear();
    }

    private ArtefactoAsesorCriteria criteria(
            UUID asesor, NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarArtefactosAsesorMapper.toCriteria(
                ConsultarArtefactosAsesorQuery.crear(asesor, criterio));
    }

    private ArtefactoAsesorCriteria criteria(UUID asesor, NodoFiltro raiz) {
        return criteria(asesor, raiz, List.of(), 0, 10);
    }

    private static List<UUID> ids(List<ArtefactoReadModel> contenido) {
        return contenido.stream().map(ArtefactoReadModel::id).toList();
    }

    @Test
    void debeDevolverSoloArtefactosDelAsesor_cuandoHayDeOtros() {
        // Arrange
        sembrarEscenario();

        // Act
        var deA = adapter.consultarTodosAsesor(criteria(asesorA, null));
        var deB = adapter.consultarTodosAsesor(criteria(asesorB, null));

        // Assert
        assertThat(deA.getTotalElements()).isEqualTo(3L);
        assertThat(ids(deA.getContent())).containsExactlyInAnyOrder(artefactoAlfa1, artefactoAlfa2, artefactoBeta1);
        assertThat(ids(deB.getContent())).containsExactly(artefactoDeB);
    }

    @Test
    void debeNoPermitirSustituirAlAsesorDelToken_cuandoElBodyFiltraPorOtroAsesorId() {
        // Arrange
        sembrarEscenario();
        var raizCliente = NodoFiltro.predicado("asesorId", FiltroOperador.ES, asesorB.toString());

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, raizCliente));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void debeOrdenarPorNombreProyectoYDocumento_cuandoOrdenPorDefecto() {
        // Arrange
        sembrarEscenario();

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, null));

        // Assert
        assertThat(ids(resultado.getContent()))
                .containsExactly(artefactoAlfa2, artefactoAlfa1, artefactoBeta1);
    }

    @Test
    void debeFiltrarPorNombreProyecto_cuandoContiene() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("nombreProyecto", FiltroOperador.CONTIENE, "bet");

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(artefactoBeta1);
    }

    @Test
    void debeFiltrarPorVersionDocumento_cuandoEs() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("versionDocumento", FiltroOperador.ES, "2");

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(artefactoAlfa1);
    }

    @Test
    void debeFiltrarPorNombreRepositorio_cuandoContiene() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("nombreRepositorio", FiltroOperador.CONTIENE, "dos");

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(artefactoAlfa1);
    }

    @Test
    void debeFiltrarPorProyectoGradoYVersionRepositorio_cuandoEs() {
        // Arrange
        sembrarEscenario();
        var porProyecto = NodoFiltro.predicado("proyectoGrado", FiltroOperador.ES, proyectoBeta.toString());
        var porVersion = NodoFiltro.predicado("versionRepositorioArtefacto", FiltroOperador.ES,
                versionAlfaDoc1.toString());

        // Act
        var conProyecto = adapter.consultarTodosAsesor(criteria(asesorA, porProyecto));
        var conVersion = adapter.consultarTodosAsesor(criteria(asesorA, porVersion));

        // Assert
        assertThat(ids(conProyecto.getContent())).containsExactly(artefactoBeta1);
        assertThat(ids(conVersion.getContent())).containsExactly(artefactoAlfa2);
    }

    @Test
    void debeOrdenarPorVersionDocumento_cuandoElClienteLoPide() {
        // Arrange
        sembrarEscenario();
        var orden = List.of(SortOrder.of("versionDocumento", SortDirection.DESC));

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, null, orden, 0, 10));

        // Assert
        assertThat(resultado.getContent()).extracting(ArtefactoReadModel::versionDocumento)
                .containsExactly(3, 2, 1);
    }

    @Test
    void debeDevolverPaginaVacia_cuandoNoHayCoincidencias() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("nombreProyecto", FiltroOperador.CONTIENE, "inexistente");

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeMapearTodosLosCampos_cuandoHayResultado() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("versionDocumento", FiltroOperador.ES, "3");

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var artefacto = resultado.getContent().get(0);
        assertThat(artefacto.id()).isEqualTo(artefactoBeta1);
        assertThat(artefacto.proyectoGrado()).isEqualTo(proyectoBeta);
        assertThat(artefacto.nombreProyecto()).isEqualTo("Beta");
        assertThat(artefacto.versionRepositorioArtefacto()).isEqualTo(versionBetaDoc1);
        assertThat(artefacto.nombreDocumento()).isEqualTo("Anteproyecto");
        assertThat(artefacto.versionDocumento()).isEqualTo(3);
        assertThat(artefacto.nombreRepositorio()).isEqualTo("Repo Uno");
    }

    private void sembrarAsesor(UUID id, String nombre, Instant eliminadoEn) {
        entityManager.persist(new AsesorJpaEntity(id, "ID-" + nombre, nombre, nombre + "@uco.edu.co",
                Instant.now(), eliminadoEn));
    }

    private void sembrarVinculo(UUID estudiante, UUID proyecto, Instant destituidoEn) {
        entityManager.persist(new EstudianteProyectoGradoJpaEntity(
                UtilUUID.generarNuevoUUID(), estudiante, proyecto, Instant.now(), destituidoEn));
    }

    private ArtefactoEstudianteCriteria criteriaEstudiante(
            UUID estudiante, NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarArtefactosEstudianteMapper.toCriteria(
                ConsultarArtefactosEstudianteQuery.crear(estudiante, criterio));
    }

    private ArtefactoEstudianteCriteria criteriaEstudiante(UUID estudiante, NodoFiltro raiz) {
        return criteriaEstudiante(estudiante, raiz, List.of(), 0, 10);
    }

    private static List<UUID> idsEstudiante(List<ArtefactoEstudianteReadModel> contenido) {
        return contenido.stream().map(ArtefactoEstudianteReadModel::id).toList();
    }

    private void sembrarEscenarioEstudiante() {
        sembrarEscenario();
        sembrarAsesor(asesorA, "Ana", null);
        sembrarAsesor(asesorB, "Beto", Instant.now());
        sembrarVinculo(estudianteX, proyectoAlfa, null);
        sembrarVinculo(estudianteX, proyectoBeta, Instant.now());
        sembrarVinculo(estudianteY, proyectoBeta, null);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeDevolverSoloArtefactosDeProyectosConVinculoVigente_cuandoHayVinculosDestituidosYAjenos() {
        // Arrange
        sembrarEscenarioEstudiante();

        // Act
        var deX = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, null));
        var deY = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteY, null));
        var deAjeno = adapter.consultarTodosEstudiante(
                criteriaEstudiante(UtilUUID.generarNuevoUUID(), null));

        // Assert
        assertThat(idsEstudiante(deX.getContent()))
                .containsExactlyInAnyOrder(artefactoAlfa1, artefactoAlfa2, artefactoDeB);
        assertThat(idsEstudiante(deY.getContent())).containsExactly(artefactoBeta1);
        assertThat(deAjeno.getContent()).isEmpty();
    }

    @Test
    void debeMarcarOmitidoEnLosListadosDeAsesorYEstudiante_cuandoElArtefactoFueOmitido() {
        // Arrange
        sembrarEscenarioEstudiante();
        var omitido = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(omitido, proyectoAlfa, versionAlfaDoc1, asesorA, Instant.now()));
        entityManager.flush();
        entityManager.clear();

        // Act
        var deAsesor = adapter.consultarTodosAsesor(criteria(asesorA, null));
        var deEstudiante = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, null));

        // Assert
        assertThat(deAsesor.getContent()).filteredOn(ArtefactoReadModel::omitido)
                .extracting(ArtefactoReadModel::id).containsExactly(omitido);
        assertThat(deAsesor.getContent()).filteredOn(a -> !a.omitido()).hasSize(3);
        assertThat(deEstudiante.getContent()).filteredOn(ArtefactoEstudianteReadModel::omitido)
                .extracting(ArtefactoEstudianteReadModel::id).containsExactly(omitido);
        assertThat(deEstudiante.getContent()).filteredOn(a -> !a.omitido()).hasSize(3);
    }

    @Test
    void debeIncluirNombreYIdDelAsesor_cuandoElAsesorFueDadoDeBaja() {
        // Arrange
        sembrarEscenarioEstudiante();

        // Act
        var resultado = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, null));

        // Assert
        var deBaja = resultado.getContent().stream()
                .filter(a -> a.id().equals(artefactoDeB)).findFirst().orElseThrow();
        assertThat(deBaja.nombreAsesor()).isEqualTo("Beto");
        assertThat(deBaja.asesor()).isEqualTo(asesorB);
    }

    @Test
    void debeMapearTodosLosCamposDelEstudiante_cuandoHayResultado() {
        // Arrange
        sembrarEscenarioEstudiante();
        var raiz = NodoFiltro.predicado("versionDocumento", FiltroOperador.ES, "2");

        // Act
        var resultado = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, raiz));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var artefacto = resultado.getContent().get(0);
        assertThat(artefacto.id()).isEqualTo(artefactoAlfa1);
        assertThat(artefacto.proyectoGrado()).isEqualTo(proyectoAlfa);
        assertThat(artefacto.nombreProyecto()).isEqualTo("Alfa");
        assertThat(artefacto.versionRepositorioArtefacto()).isEqualTo(versionAlfaDoc2);
        assertThat(artefacto.nombreDocumento()).isEqualTo("Informe");
        assertThat(artefacto.versionDocumento()).isEqualTo(2);
        assertThat(artefacto.nombreRepositorio()).isEqualTo("Repo Dos");
        assertThat(artefacto.asesor()).isEqualTo(asesorA);
        assertThat(artefacto.nombreAsesor()).isEqualTo("Ana");
    }

    @Test
    void debeFiltrarPorNombreAsesorDocumentoEIdAsesor_cuandoElCriterioLoPide() {
        // Arrange
        sembrarEscenarioEstudiante();
        var porAsesor = NodoFiltro.predicado("nombreAsesor", FiltroOperador.CONTIENE, "bet");
        var porDocumento = NodoFiltro.predicado("nombreDocumento", FiltroOperador.CONTIENE, "informe");
        var porIdAsesor = NodoFiltro.predicado("asesor", FiltroOperador.ES, asesorA.toString());

        // Act
        var conAsesor = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, porAsesor));
        var conDocumento = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, porDocumento));
        var conIdAsesor = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, porIdAsesor));

        // Assert
        assertThat(idsEstudiante(conAsesor.getContent())).containsExactly(artefactoDeB);
        assertThat(idsEstudiante(conDocumento.getContent())).containsExactly(artefactoAlfa1);
        assertThat(idsEstudiante(conIdAsesor.getContent()))
                .containsExactlyInAnyOrder(artefactoAlfa1, artefactoAlfa2);
    }

    @Test
    void debeOrdenarPorNombreAsesor_cuandoElClienteLoPide() {
        // Arrange
        sembrarEscenarioEstudiante();
        var orden = List.of(SortOrder.of("nombreAsesor", SortDirection.DESC));

        // Act
        var resultado = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, null, orden, 0, 10));

        // Assert
        assertThat(resultado.getContent().get(0).nombreAsesor()).isEqualTo("Beto");
    }

    @Test
    void debePaginar_cuandoTamanioEsMenorAlTotal() {
        // Arrange
        sembrarEscenarioEstudiante();

        // Act
        var resultado = adapter.consultarTodosEstudiante(criteriaEstudiante(estudianteX, null, List.of(), 1, 2));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
        assertThat(resultado.getContent()).hasSize(1);
    }
}
