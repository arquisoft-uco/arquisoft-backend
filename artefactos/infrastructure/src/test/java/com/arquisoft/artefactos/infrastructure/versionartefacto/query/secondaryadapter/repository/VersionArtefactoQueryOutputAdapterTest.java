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
class VersionArtefactoQueryOutputAdapterTest {

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

    private UUID proyectoAlfa;
    private UUID proyectoBeta;
    private UUID artefactoA1;
    private UUID artefactoA2;
    private UUID artefactoB;
    private UUID versionA1v1;
    private UUID versionA1v2;
    private UUID versionA2v1;
    private UUID versionB;

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

    private UUID sembrarProyecto(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(id, nombre));
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

    private void sembrarEscenario() {
        proyectoAlfa = sembrarProyecto("Alfa");
        proyectoBeta = sembrarProyecto("Beta");
        entityManager.persist(new AsesorJpaEntity(asesorA, "ID-Ana", "Ana", "ana@uco.edu.co", Instant.now(), null));
        entityManager.persist(new AsesorJpaEntity(
                asesorB, "ID-Beto", "Beto", "beto@uco.edu.co", Instant.now(), Instant.now()));
        var docAnteproyecto = sembrarVersionRepositorio("Anteproyecto", 1, "Repo Uno");
        var docInforme = sembrarVersionRepositorio("Informe", 2, "Repo Dos");
        artefactoA1 = sembrarArtefacto(proyectoAlfa, docAnteproyecto, asesorA);
        artefactoA2 = sembrarArtefacto(proyectoBeta, docInforme, asesorA);
        artefactoB = sembrarArtefacto(proyectoAlfa, docAnteproyecto, asesorB);

        versionA1v1 = sembrarVersion(artefactoA1, 1, "2026-09-01T10:00:00Z");
        versionA1v2 = sembrarVersion(artefactoA1, 2, "2026-10-20T10:00:00Z");
        versionA2v1 = sembrarVersion(artefactoA2, 1, "2026-10-10T10:00:00Z");
        versionB = sembrarVersion(artefactoB, 1, "2026-10-25T10:00:00Z");
        entityManager.flush();
        entityManager.clear();
    }

    private VersionArtefactoAsesorCriteria criteria(
            UUID asesor, NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarVersionesArtefactoAsesorMapper.toCriteria(
                ConsultarVersionesArtefactoAsesorQuery.crear(asesor, criterio));
    }

    private VersionArtefactoAsesorCriteria criteria(UUID asesor, NodoFiltro raiz) {
        return criteria(asesor, raiz, List.of(), 0, 10);
    }

    private static List<UUID> ids(List<VersionArtefactoReadModel> contenido) {
        return contenido.stream().map(VersionArtefactoReadModel::id).toList();
    }

    @Test
    void debeRetornarVacio_cuandoAsesorSinArtefactos() {
        // Arrange
        sembrarEscenario();

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(UtilUUID.generarNuevoUUID(), null));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeRetornarSoloVersionesDeArtefactosDelAsesor_ordenadasPorFechaCreacionDescPorDefecto() {
        // Arrange
        sembrarEscenario();

        // Act
        var deA = adapter.consultarTodasAsesor(criteria(asesorA, null));
        var deB = adapter.consultarTodasAsesor(criteria(asesorB, null));

        // Assert
        assertThat(deA.getTotalElements()).isEqualTo(3L);
        assertThat(ids(deA.getContent())).containsExactly(versionA1v2, versionA2v1, versionA1v1);
        assertThat(ids(deB.getContent())).containsExactly(versionB);
    }

    @Test
    void debeFiltrarPorArtefacto_yNoMostrarVersionesAjenas_cuandoFiltraPorArtefactoDeOtroAsesor() {
        // Arrange
        sembrarEscenario();

        // Act
        var propio = adapter.consultarTodasAsesor(criteria(asesorA,
                NodoFiltro.predicado("artefacto", FiltroOperador.ES, artefactoA2.toString())));
        var ajeno = adapter.consultarTodasAsesor(criteria(asesorA,
                NodoFiltro.predicado("artefacto", FiltroOperador.ES, artefactoB.toString())));

        // Assert
        assertThat(ids(propio.getContent())).containsExactly(versionA2v1);
        assertThat(ajeno.getContent()).isEmpty();
        assertThat(ajeno.getTotalElements()).isZero();
    }

    @Test
    void debeNoPermitirSustituirAlAsesorDelToken_cuandoElBodyFiltraPorOtroAsesorId() {
        // Arrange
        sembrarEscenario();
        var raizCliente = NodoFiltro.predicado("asesorId", FiltroOperador.ES, asesorB.toString());

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(asesorA, raizCliente));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void debeFiltrarPorVersion_yPorFechaCreacion() {
        // Arrange
        sembrarEscenario();
        var porVersion = NodoFiltro.predicado("version", FiltroOperador.ES, "2");
        var porFecha = NodoFiltro.predicado("fechaCreacion", FiltroOperador.MAYOR_QUE, "2026-10-05T00:00:00");

        // Act
        var conVersion = adapter.consultarTodasAsesor(criteria(asesorA, porVersion));
        var conFecha = adapter.consultarTodasAsesor(criteria(asesorA, porFecha));

        // Assert
        assertThat(ids(conVersion.getContent())).containsExactly(versionA1v2);
        assertThat(ids(conFecha.getContent())).containsExactly(versionA1v2, versionA2v1);
    }

    @Test
    void debeOrdenarPorVersion_cuandoElClienteLoPide() {
        // Arrange
        sembrarEscenario();
        var orden = List.of(SortOrder.of("version", SortDirection.DESC));

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(asesorA, null, orden, 0, 10));

        // Assert
        assertThat(resultado.getContent()).extracting(VersionArtefactoReadModel::version)
                .containsExactly(2, 1, 1);
    }

    @Test
    void debePaginar_cuandoHayMasVersionesQueElTamanioDePagina() {
        // Arrange
        sembrarEscenario();

        // Act
        var primera = adapter.consultarTodasAsesor(criteria(asesorA, null, List.of(), 0, 2));
        var segunda = adapter.consultarTodasAsesor(criteria(asesorA, null, List.of(), 1, 2));

        // Assert
        assertThat(primera.getContent()).hasSize(2);
        assertThat(segunda.getContent()).hasSize(1);
        assertThat(segunda.getTotalElements()).isEqualTo(3L);
        assertThat(ids(primera.getContent())).doesNotContainAnyElementsOf(ids(segunda.getContent()));
    }

    @Test
    void debeProyectarTodosLosCampos_incluidoUriArchivo() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("version", FiltroOperador.ES, "2");

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var version = resultado.getContent().get(0);
        assertThat(version.id()).isEqualTo(versionA1v2);
        assertThat(version.artefacto()).isEqualTo(artefactoA1);
        assertThat(version.version()).isEqualTo(2);
        assertThat(version.fechaCreacion()).isEqualTo(Instant.parse("2026-10-20T10:00:00Z"));
        assertThat(version.uriArchivo()).isEqualTo("artefactos/" + versionA1v2 + ".pdf");
    }

    @Test
    void debeRetornarNombresDeProyectoDocumentoRepositorioYAsesor_cuandoConsultaComoAsesor() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("artefacto", FiltroOperador.ES, artefactoA2.toString());

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var version = resultado.getContent().get(0);
        assertThat(version.nombreProyecto()).isEqualTo("Beta");
        assertThat(version.nombreDocumento()).isEqualTo("Informe");
        assertThat(version.versionDocumento()).isEqualTo(2);
        assertThat(version.nombreRepositorio()).isEqualTo("Repo Dos");
        assertThat(version.nombreAsesor()).isEqualTo("Ana");
    }

    @Test
    void debeFiltrarPorProyectoGrado_cuandoConsultaComoAsesor() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("proyectoGrado", FiltroOperador.ES, proyectoBeta.toString());

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(versionA2v1);
    }

    @Test
    void debeFiltrarPorNombreProyectoInsensibleAMayusculasYPorNombreDocumento_cuandoConsultaComoAsesorUsaContiene() {
        // Arrange
        sembrarEscenario();
        var porProyecto = NodoFiltro.predicado("nombreProyecto", FiltroOperador.CONTIENE, "ALF");
        var porDocumento = NodoFiltro.predicado("nombreDocumento", FiltroOperador.CONTIENE, "inform");

        // Act
        var conProyecto = adapter.consultarTodasAsesor(criteria(asesorA, porProyecto));
        var conDocumento = adapter.consultarTodasAsesor(criteria(asesorA, porDocumento));

        // Assert
        assertThat(ids(conProyecto.getContent())).containsExactly(versionA1v2, versionA1v1);
        assertThat(ids(conDocumento.getContent())).containsExactly(versionA2v1);
    }

    @Test
    void debeNoMostrarVersionesAjenas_cuandoFiltraPorProyectoGradoDeOtroAsesor() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("proyectoGrado", FiltroOperador.ES, proyectoAlfa.toString());

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(versionA1v2, versionA1v1)
                .doesNotContain(versionB);
    }

    @Test
    void debeIncluirVersiones_cuandoElAsesorEstaDadoDeBaja() {
        // Arrange
        sembrarEscenario();

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(asesorB, null));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(versionB);
        assertThat(resultado.getContent().get(0).nombreAsesor()).isEqualTo("Beto");
    }

    @Test
    void debeNoDuplicarVersiones_cuandoElArtefactoTieneVariosEstudiantes() {
        // Arrange
        sembrarEscenarioEstudiante();

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(asesorA, null));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
        assertThat(ids(resultado.getContent())).containsExactly(versionA1v2, versionA2v1, versionA1v1);
    }

    private void sembrarEscenarioEstudiante() {
        sembrarEscenario();
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefactoA1, estudianteX));
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefactoA2, estudianteX));
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefactoA1, estudianteY));
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefactoB, estudianteY));
        entityManager.flush();
        entityManager.clear();
    }

    private VersionArtefactoEstudianteCriteria criteriaEstudiante(
            UUID estudiante, NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarVersionesArtefactoEstudianteMapper.toCriteria(
                ConsultarVersionesArtefactoEstudianteQuery.crear(estudiante, criterio));
    }

    private VersionArtefactoEstudianteCriteria criteriaEstudiante(UUID estudiante, NodoFiltro raiz) {
        return criteriaEstudiante(estudiante, raiz, List.of(), 0, 10);
    }

    @Test
    void debeRetornarSoloVersionesDeArtefactosDelEstudiante_sinDuplicarPorOtrosParticipantes() {
        // Arrange
        sembrarEscenarioEstudiante();

        // Act
        var deX = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, null));
        var deY = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteY, null));

        // Assert
        assertThat(deX.getTotalElements()).isEqualTo(3L);
        assertThat(ids(deX.getContent())).containsExactly(versionA1v2, versionA2v1, versionA1v1);
        assertThat(ids(deY.getContent())).containsExactly(versionB, versionA1v2, versionA1v1);
    }

    @Test
    void debeRetornarVacio_cuandoEstudianteSinArtefactos() {
        // Arrange
        sembrarEscenarioEstudiante();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteriaEstudiante(UtilUUID.generarNuevoUUID(), null));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeFiltrarPorArtefacto_yExcluirArtefactosAjenos_yNoPermitirSustituirAlEstudiante() {
        // Arrange
        sembrarEscenarioEstudiante();
        var propio = NodoFiltro.predicado("artefacto", FiltroOperador.ES, artefactoA2.toString());
        var ajeno = NodoFiltro.predicado("artefacto", FiltroOperador.ES, artefactoB.toString());
        var otroEstudiante = NodoFiltro.predicado("estudianteId", FiltroOperador.ES, estudianteY.toString());

        // Act
        var conPropio = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, propio));
        var conAjeno = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, ajeno));
        var sustituido = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, otroEstudiante));

        // Assert
        assertThat(ids(conPropio.getContent())).containsExactly(versionA2v1);
        assertThat(conAjeno.getContent()).isEmpty();
        assertThat(sustituido.getContent()).isEmpty();
    }

    @Test
    void debeFiltrarPorVersion_yOrdenarPorVersionDescendente() {
        // Arrange
        sembrarEscenarioEstudiante();
        var porVersion = NodoFiltro.predicado("version", FiltroOperador.ES, "2");
        var orden = List.of(SortOrder.of("version", SortDirection.DESC));

        // Act
        var filtrado = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, porVersion));
        var ordenado = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, null, orden, 0, 10));

        // Assert
        assertThat(ids(filtrado.getContent())).containsExactly(versionA1v2);
        assertThat(ordenado.getContent()).extracting(VersionArtefactoReadModel::version)
                .containsExactly(2, 1, 1);
    }

    @Test
    void debePaginarYProyectarTodosLosCampos_cuandoHayMasVersionesQueElTamanioDePagina() {
        // Arrange
        sembrarEscenarioEstudiante();

        // Act
        var primera = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, null, List.of(), 0, 2));
        var segunda = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, null, List.of(), 1, 2));

        // Assert
        assertThat(primera.getContent()).hasSize(2);
        assertThat(segunda.getContent()).hasSize(1);
        assertThat(segunda.getTotalElements()).isEqualTo(3L);
        var version = primera.getContent().get(0);
        assertThat(version.id()).isEqualTo(versionA1v2);
        assertThat(version.artefacto()).isEqualTo(artefactoA1);
        assertThat(version.version()).isEqualTo(2);
        assertThat(version.fechaCreacion()).isEqualTo(Instant.parse("2026-10-20T10:00:00Z"));
        assertThat(version.uriArchivo()).isEqualTo("artefactos/" + versionA1v2 + ".pdf");
    }

    @Test
    void debeRetornarNombresDeProyectoDocumentoRepositorioYAsesor_cuandoConsultaComoEstudiante() {
        // Arrange
        sembrarEscenarioEstudiante();
        var raiz = NodoFiltro.predicado("artefacto", FiltroOperador.ES, artefactoA2.toString());

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, raiz));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var version = resultado.getContent().get(0);
        assertThat(version.nombreProyecto()).isEqualTo("Beta");
        assertThat(version.nombreDocumento()).isEqualTo("Informe");
        assertThat(version.versionDocumento()).isEqualTo(2);
        assertThat(version.nombreRepositorio()).isEqualTo("Repo Dos");
        assertThat(version.nombreAsesor()).isEqualTo("Ana");
    }

    @Test
    void debeFiltrarPorProyectoGrado_cuandoConsultaComoEstudiante() {
        // Arrange
        sembrarEscenarioEstudiante();
        var raiz = NodoFiltro.predicado("proyectoGrado", FiltroOperador.ES, proyectoBeta.toString());

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, raiz));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(versionA2v1);
    }

    @Test
    void debeFiltrarPorNombreProyectoInsensibleAMayusculasYPorNombreDocumento_cuandoConsultaComoEstudianteUsaContiene() {
        // Arrange
        sembrarEscenarioEstudiante();
        var porProyecto = NodoFiltro.predicado("nombreProyecto", FiltroOperador.CONTIENE, "BET");
        var porDocumento = NodoFiltro.predicado("nombreDocumento", FiltroOperador.CONTIENE, "anteproy");

        // Act
        var conProyecto = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, porProyecto));
        var conDocumento = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, porDocumento));

        // Assert
        assertThat(ids(conProyecto.getContent())).containsExactly(versionA2v1);
        assertThat(ids(conDocumento.getContent())).containsExactly(versionA1v2, versionA1v1);
    }
}
