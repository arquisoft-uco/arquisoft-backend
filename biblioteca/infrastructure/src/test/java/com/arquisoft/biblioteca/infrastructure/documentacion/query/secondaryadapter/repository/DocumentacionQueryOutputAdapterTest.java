package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DocumentacionCriteria;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class DocumentacionQueryOutputAdapterTest {

    private static final Instant BASE = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired
    private DocumentacionQueryRepository repository;

    @Autowired
    private AutorDeDocumentacionQueryRepository autorRepository;

    @Autowired
    private TestEntityManager entityManager;

    private DocumentacionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new DocumentacionQueryOutputAdapter(repository, autorRepository, new DocumentacionJpaSpecification());
        var em = entityManager.getEntityManager();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS estado_documentacion (id VARCHAR(60) PRIMARY KEY, "
                + "nombre VARCHAR(60), descripcion VARCHAR(300))").executeUpdate();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS bibliotecario (id UUID PRIMARY KEY, "
                + "identificador VARCHAR(30), nombre VARCHAR(50), email VARCHAR(50), "
                + "ocurrido_en TIMESTAMP WITH TIME ZONE, eliminado_en TIMESTAMP WITH TIME ZONE)").executeUpdate();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS entregable_proyecto_grado (id UUID PRIMARY KEY, "
                + "proyecto_grado_id UUID, version_entregable INTEGER, "
                + "estado_entregable_nombre VARCHAR(60), bucket VARCHAR(63), clave_objeto VARCHAR(2000), "
                + "nombre_archivo VARCHAR(255))").executeUpdate();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS documentacion (id UUID PRIMARY KEY, url VARCHAR(2000), "
                + "fecha_publicacion TIMESTAMP WITH TIME ZONE, entregable_proyecto_grado_id UUID, "
                + "bibliotecario_id UUID, estado_documentacion_id VARCHAR(60))").executeUpdate();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS autor_documentacion (id UUID PRIMARY KEY, "
                + "documentacion_id UUID, autor_id UUID)").executeUpdate();
        em.createNativeQuery("DELETE FROM autor_documentacion").executeUpdate();
        em.createNativeQuery("DELETE FROM autor").executeUpdate();
        em.createNativeQuery("DELETE FROM proyecto_grado").executeUpdate();
        em.createNativeQuery("DELETE FROM documentacion").executeUpdate();
        em.createNativeQuery("DELETE FROM entregable_proyecto_grado").executeUpdate();
        em.createNativeQuery("DELETE FROM bibliotecario").executeUpdate();
        em.createNativeQuery("DELETE FROM estado_documentacion").executeUpdate();
        em.createNativeQuery("INSERT INTO estado_documentacion (id, nombre, descripcion) VALUES "
                + "('PENDIENTE', 'Pendiente', 'd'), ('PUBLICADO', 'Publicado', 'd'), "
                + "('NO_PUBLICADO', 'No Publicado', 'd')").executeUpdate();
    }

    private UUID sembrarBibliotecario(String nombre, Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO bibliotecario (id, identificador, nombre, email, ocurrido_en, "
                        + "eliminado_en) VALUES (?1, ?2, ?3, ?4, ?5, ?6)")
                .setParameter(1, id).setParameter(2, "ID-" + nombre).setParameter(3, nombre)
                .setParameter(4, nombre + "@uco.edu.co").setParameter(5, BASE).setParameter(6, eliminadoEn)
                .executeUpdate();
        return id;
    }

    private UUID sembrarEntregable(UUID proyecto, int version, String estado) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO entregable_proyecto_grado (id, proyecto_grado_id, "
                        + "version_entregable, estado_entregable_nombre) VALUES (?1, ?2, ?3, ?4)")
                .setParameter(1, id).setParameter(2, proyecto).setParameter(3, version)
                .setParameter(4, estado)
                .executeUpdate();
        return id;
    }

    private UUID sembrarDocumentacion(UUID entregable, UUID bibliotecario, String estado, Instant fecha) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO documentacion (id, url, fecha_publicacion, "
                        + "entregable_proyecto_grado_id, bibliotecario_id, estado_documentacion_id) "
                        + "VALUES (?1, ?2, ?3, ?4, ?5, ?6)")
                .setParameter(1, id).setParameter(2, "https://biblioteca.test/" + id)
                .setParameter(3, fecha).setParameter(4, entregable).setParameter(5, bibliotecario)
                .setParameter(6, estado)
                .executeUpdate();
        return id;
    }

    private void sembrarProyecto(UUID id, String titulo) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO proyecto_grado (id, titulo, ocurrido_en) VALUES (?1, ?2, ?3)")
                .setParameter(1, id).setParameter(2, titulo).setParameter(3, BASE)
                .executeUpdate();
    }

    private UUID sembrarAutor(String nombre, Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO autor (id, identificador, nombre, email, ocurrido_en, "
                        + "eliminado_en) VALUES (?1, ?2, ?3, ?4, ?5, ?6)")
                .setParameter(1, id).setParameter(2, "AUT-" + nombre).setParameter(3, nombre)
                .setParameter(4, nombre + "@uco.edu.co").setParameter(5, BASE).setParameter(6, eliminadoEn)
                .executeUpdate();
        return id;
    }

    private UUID vincularAutor(UUID autor, UUID documentacion) {
        return vincularAutor(UtilUUID.generarNuevoUUID(), autor, documentacion);
    }

    private UUID vincularAutor(UUID id, UUID autor, UUID documentacion) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO autor_documentacion (id, documentacion_id, autor_id) "
                        + "VALUES (?1, ?2, ?3)")
                .setParameter(1, id).setParameter(2, documentacion).setParameter(3, autor)
                .executeUpdate();
        return id;
    }

    private void confirmar() {
        entityManager.flush();
        entityManager.clear();
    }

    private static DocumentacionCriteria criteria(NodoFiltro raiz) {
        return DocumentacionCriteria.builder().pagina(0).tamanio(10).raiz(raiz).build();
    }

    @Test
    void debeRetornarTodasLasColumnas_cuandoExisteUnaDocumentacion() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var bibliotecario = sembrarBibliotecario("Ana", null);
        var entregable = sembrarEntregable(proyecto, 3, "Aprobado");
        var id = sembrarDocumentacion(entregable, bibliotecario, "PUBLICADO", BASE);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
        var leido = resultado.getContent().get(0);
        assertThat(leido.id()).isEqualTo(id);
        assertThat(leido.tituloProyecto()).isNull();
        assertThat(leido.autores()).isEmpty();
        assertThat(leido.fechaPublicacion()).isEqualTo(BASE);
        assertThat(leido.entregableProyectoGrado().id()).isEqualTo(entregable);
        assertThat(leido.entregableProyectoGrado().proyectoGrado()).isEqualTo(proyecto);
        assertThat(leido.entregableProyectoGrado().versionEntregable()).isEqualTo(3);
        assertThat(leido.entregableProyectoGrado().estadoEntregable()).isEqualTo("Aprobado");
        assertThat(leido.bibliotecario().id()).isEqualTo(bibliotecario);
        assertThat(leido.bibliotecario().nombre()).isEqualTo("Ana");
        assertThat(leido.estadoDocumentacion().id()).isEqualTo("PUBLICADO");
        assertThat(leido.estadoDocumentacion().nombre()).isEqualTo("Publicado");
    }

    @Test
    void debeFiltrarPorEstado_cuandoElPredicadoEsEstadoDocumentacion() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var bibliotecario = sembrarBibliotecario("Ana", null);
        var publicada = sembrarDocumentacion(sembrarEntregable(proyecto, 1, "Aprobado"), bibliotecario,
                "PUBLICADO", BASE);
        sembrarDocumentacion(sembrarEntregable(proyecto, 2, "Aprobado"), bibliotecario, "PENDIENTE", BASE);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(
                NodoFiltro.predicado("estadoDocumentacion", FiltroOperador.ES, "PUBLICADO")));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(publicada);
    }

    @Test
    void debeFiltrarPorEntregable_cuandoElPredicadoEsEntregableProyectoGrado() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var bibliotecario = sembrarBibliotecario("Ana", null);
        var entregable = sembrarEntregable(proyecto, 1, "Aprobado");
        var esperada = sembrarDocumentacion(entregable, bibliotecario, "PUBLICADO", BASE);
        sembrarDocumentacion(sembrarEntregable(proyecto, 2, "Aprobado"), bibliotecario, "PUBLICADO", BASE);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(
                NodoFiltro.predicado("entregableProyectoGrado", FiltroOperador.ES, entregable.toString())));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(esperada);
    }

    @Test
    void debeFiltrarPorBibliotecario_cuandoElPredicadoEsBibliotecario() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var ana = sembrarBibliotecario("Ana", null);
        var luis = sembrarBibliotecario("Luis", null);
        var esperada = sembrarDocumentacion(sembrarEntregable(proyecto, 1, "Aprobado"), ana, "PUBLICADO", BASE);
        sembrarDocumentacion(sembrarEntregable(proyecto, 2, "Aprobado"), luis, "PUBLICADO", BASE);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(
                NodoFiltro.predicado("bibliotecario", FiltroOperador.ES, ana.toString())));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(esperada);
    }

    @Test
    void debePaginarYContarElTotal_cuandoHayMasFilasQueElTamanio() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var bibliotecario = sembrarBibliotecario("Ana", null);
        for (var version = 1; version <= 5; version++) {
            sembrarDocumentacion(sembrarEntregable(proyecto, version, "Aprobado"), bibliotecario, "PUBLICADO",
                    BASE.plusSeconds(version));
        }
        confirmar();
        var segundaPagina = DocumentacionCriteria.builder().pagina(1).tamanio(2).build();

        // Act
        var resultado = adapter.consultar(segundaPagina);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(5L);
        assertThat(resultado.getContent()).hasSize(2);
    }

    @Test
    void debeOrdenarPorFechaDescendenteYAscendente_cuandoElClienteLoPide() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var bibliotecario = sembrarBibliotecario("Ana", null);
        var antigua = sembrarDocumentacion(sembrarEntregable(proyecto, 1, "Aprobado"), bibliotecario,
                "PUBLICADO", BASE);
        var reciente = sembrarDocumentacion(sembrarEntregable(proyecto, 2, "Aprobado"), bibliotecario,
                "PUBLICADO", BASE.plusSeconds(60));
        confirmar();

        // Act
        var descendente = adapter.consultar(DocumentacionCriteria.builder().pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("fechaPublicacion", SortDirection.DESC))).build());
        var ascendente = adapter.consultar(DocumentacionCriteria.builder().pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("fechaPublicacion", SortDirection.ASC))).build());

        // Assert
        assertThat(descendente.getContent()).extracting("id").containsExactly(reciente, antigua);
        assertThat(ascendente.getContent()).extracting("id").containsExactly(antigua, reciente);
    }

    @Test
    void debeIncluirDocumentacionDeBibliotecarioDadoDeBaja_cuandoSeConsulta() {
        // Arrange
        var bibliotecario = sembrarBibliotecario("Ana", BASE.plusSeconds(1));
        var id = sembrarDocumentacion(sembrarEntregable(UtilUUID.generarNuevoUUID(), 1, "Aprobado"),
                bibliotecario, "PUBLICADO", BASE);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(id);
        assertThat(resultado.getContent().get(0).bibliotecario().nombre()).isEqualTo("Ana");
    }

    @Test
    void debeRetornarElTitulo_cuandoElProyectoEstaEnLaReplica() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        sembrarProyecto(proyecto, "Proyecto Alfa");
        var bibliotecario = sembrarBibliotecario("Ana", null);
        sembrarDocumentacion(sembrarEntregable(proyecto, 1, "Aprobado"), bibliotecario, "PUBLICADO", BASE);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).tituloProyecto()).isEqualTo("Proyecto Alfa");
    }

    @Test
    void debeRetornarTituloNuloYLaDocumentacion_cuandoElProyectoNoEstaEnLaReplica() {
        // Arrange
        var bibliotecario = sembrarBibliotecario("Ana", null);
        var id = sembrarDocumentacion(sembrarEntregable(UtilUUID.generarNuevoUUID(), 1, "Aprobado"),
                bibliotecario, "PUBLICADO", BASE);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
        assertThat(resultado.getContent()).extracting("id").containsExactly(id);
        assertThat(resultado.getContent().get(0).tituloProyecto()).isNull();
    }

    @Test
    void debeRetornarAutoresOrdenadosPorNombreYLuegoPorId_cuandoHayVarios() {
        // Arrange
        var bibliotecario = sembrarBibliotecario("Ana", null);
        var documentacion = sembrarDocumentacion(sembrarEntregable(UtilUUID.generarNuevoUUID(), 1, "Aprobado"),
                bibliotecario, "PUBLICADO", BASE);
        var zoe = sembrarAutor("Zoe", null);
        var beto = sembrarAutor("Beto", null);
        var primerCarlos = sembrarAutor("Carlos", null);
        var segundoCarlos = sembrarAutor("Carlos", null);
        vincularAutor(zoe, documentacion);
        vincularAutor(beto, documentacion);
        vincularAutor(UtilUUID.generarUUIDDesdeTexto("00000000-0000-4000-8000-000000000002"), primerCarlos,
                documentacion);
        vincularAutor(UtilUUID.generarUUIDDesdeTexto("00000000-0000-4000-8000-000000000001"), segundoCarlos,
                documentacion);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        var autores = resultado.getContent().get(0).autores();
        assertThat(autores).extracting("nombre").containsExactly("Beto", "Carlos", "Carlos", "Zoe");
        assertThat(autores).extracting("id")
                .containsExactly(beto, segundoCarlos, primerCarlos, zoe);
        assertThat(autores.get(0).identificador()).isEqualTo("AUT-Beto");
    }

    @Test
    void debeIncluirAlAutorEliminado_cuandoSeConsulta() {
        // Arrange
        var bibliotecario = sembrarBibliotecario("Ana", null);
        var documentacion = sembrarDocumentacion(sembrarEntregable(UtilUUID.generarNuevoUUID(), 1, "Aprobado"),
                bibliotecario, "PUBLICADO", BASE);
        var vigente = sembrarAutor("Beto", null);
        var eliminado = sembrarAutor("Carlos", BASE.plusSeconds(1));
        vincularAutor(vigente, documentacion);
        vincularAutor(eliminado, documentacion);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        assertThat(resultado.getContent().get(0).autores()).extracting("id")
                .containsExactly(vigente, eliminado);
    }

    @Test
    void debeRetornarAutoresVacio_cuandoNoHayVinculos() {
        // Arrange
        var bibliotecario = sembrarBibliotecario("Ana", null);
        sembrarDocumentacion(sembrarEntregable(UtilUUID.generarNuevoUUID(), 1, "Aprobado"), bibliotecario,
                "PUBLICADO", BASE);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        assertThat(resultado.getContent().get(0).autores()).isNotNull().isEmpty();
    }

    @Test
    void debeAsignarCadaAutorASuDocumentacion_cuandoLaPaginaTraeVarias() {
        // Arrange
        var bibliotecario = sembrarBibliotecario("Ana", null);
        var proyecto = UtilUUID.generarNuevoUUID();
        var docA = sembrarDocumentacion(sembrarEntregable(proyecto, 1, "Aprobado"), bibliotecario, "PUBLICADO",
                BASE);
        var docB = sembrarDocumentacion(sembrarEntregable(proyecto, 2, "Aprobado"), bibliotecario, "PUBLICADO",
                BASE.plusSeconds(1));
        var autorA = sembrarAutor("Beto", null);
        var autorB = sembrarAutor("Carlos", null);
        vincularAutor(autorA, docA);
        vincularAutor(autorB, docB);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        var porId = resultado.getContent().stream()
                .collect(Collectors.toMap(d -> d.id(), d -> d.autores()));
        assertThat(porId.get(docA)).extracting("id").containsExactly(autorA);
        assertThat(porId.get(docB)).extracting("id").containsExactly(autorB);
    }

    @Test
    void debeEjecutarTresSentenciasSinImportarElTamanoDeLaPagina_cuandoHayAutores() {
        // Arrange
        var bibliotecario = sembrarBibliotecario("Ana", null);
        var proyecto = UtilUUID.generarNuevoUUID();
        for (var version = 1; version <= 5; version++) {
            var documentacion = sembrarDocumentacion(sembrarEntregable(proyecto, version, "Aprobado"),
                    bibliotecario, "PUBLICADO", BASE.plusSeconds(version));
            vincularAutor(sembrarAutor("Autor" + version + "a", null), documentacion);
            vincularAutor(sembrarAutor("Autor" + version + "b", null), documentacion);
        }
        confirmar();
        var estadisticas = entityManager.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        var paginaDeCinco = DocumentacionCriteria.builder().pagina(0).tamanio(5).build();
        var paginaDeUna = DocumentacionCriteria.builder().pagina(0).tamanio(1).build();

        // Act
        estadisticas.clear();
        var resultadoCinco = adapter.consultar(paginaDeCinco);
        var sentenciasCinco = estadisticas.getPrepareStatementCount();
        estadisticas.clear();
        var resultadoUna = adapter.consultar(paginaDeUna);
        var sentenciasUna = estadisticas.getPrepareStatementCount();

        // Assert
        assertThat(resultadoCinco.getContent()).hasSize(5);
        assertThat(resultadoUna.getContent()).hasSize(1);
        assertThat(sentenciasCinco).isEqualTo(3L);
        assertThat(sentenciasUna).isEqualTo(3L);
    }

    @Test
    void debeNoContarMasFilas_cuandoUnaDocumentacionTieneVariosAutores() {
        // Arrange
        var bibliotecario = sembrarBibliotecario("Ana", null);
        var documentacion = sembrarDocumentacion(sembrarEntregable(UtilUUID.generarNuevoUUID(), 1, "Aprobado"),
                bibliotecario, "PUBLICADO", BASE);
        vincularAutor(sembrarAutor("Beto", null), documentacion);
        vincularAutor(sembrarAutor("Carlos", null), documentacion);
        vincularAutor(sembrarAutor("Dora", null), documentacion);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).autores()).hasSize(3);
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFilas() {
        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeLanzarFiltroInvalido_cuandoElUuidEstaMalFormado() {
        // Arrange
        var criteria = criteria(NodoFiltro.predicado("bibliotecario", FiltroOperador.ES, "no-es-uuid"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultar(criteria)).isInstanceOf(FiltroInvalidoException.class);
    }
}
