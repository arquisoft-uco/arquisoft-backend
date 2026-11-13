package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DocumentacionCriteria;
import com.arquisoft.biblioteca.infrastructure.documentacion.exception.DocumentacionNoDisponibleException;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class DocumentacionQueryOutputAdapterTest {

    private static final Instant BASE = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired
    private DocumentacionQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private DocumentacionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new DocumentacionQueryOutputAdapter(repository, new DocumentacionJpaSpecification());
        var em = entityManager.getEntityManager();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS estado_documentacion (id VARCHAR(60) PRIMARY KEY, "
                + "nombre VARCHAR(60), descripcion VARCHAR(300))").executeUpdate();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS bibliotecario (id UUID PRIMARY KEY, "
                + "identificador VARCHAR(30), nombre VARCHAR(50), email VARCHAR(50), "
                + "ocurrido_en TIMESTAMP WITH TIME ZONE, eliminado_en TIMESTAMP WITH TIME ZONE)").executeUpdate();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS entregable_proyecto_grado (id UUID PRIMARY KEY, "
                + "proyecto_grado_id UUID, version_entregable INTEGER, asesor_id UUID, "
                + "estado_entregable_nombre VARCHAR(60))").executeUpdate();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS documentacion (id UUID PRIMARY KEY, url VARCHAR(2000), "
                + "fecha_publicacion TIMESTAMP WITH TIME ZONE, entregable_proyecto_grado_id UUID, "
                + "bibliotecario_id UUID, estado_documentacion_id VARCHAR(60))").executeUpdate();
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
                        + "version_entregable, asesor_id, estado_entregable_nombre) VALUES (?1, ?2, ?3, ?4, ?5)")
                .setParameter(1, id).setParameter(2, proyecto).setParameter(3, version)
                .setParameter(4, UtilUUID.generarNuevoUUID()).setParameter(5, estado)
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
        assertThat(leido.url()).isEqualTo("https://biblioteca.test/" + id);
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

    @Test
    void debeRetornarElReadModelCompleto_cuandoConsultaPorIdUnaDocumentacionExistente() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var bibliotecario = sembrarBibliotecario("Ana", null);
        var entregable = sembrarEntregable(proyecto, 3, "Aprobado");
        var id = sembrarDocumentacion(entregable, bibliotecario, "PUBLICADO", BASE);
        sembrarDocumentacion(sembrarEntregable(proyecto, 4, "Aprobado"), bibliotecario, "PUBLICADO", BASE);
        confirmar();

        // Act
        var leido = adapter.consultarPorId(id);

        // Assert
        assertThat(leido.id()).isEqualTo(id);
        assertThat(leido.entregableProyectoGrado().id()).isEqualTo(entregable);
        assertThat(leido.entregableProyectoGrado().versionEntregable()).isEqualTo(3);
        assertThat(leido.bibliotecario().id()).isEqualTo(bibliotecario);
        assertThat(leido.bibliotecario().nombre()).isEqualTo("Ana");
        assertThat(leido.estadoDocumentacion().id()).isEqualTo("PUBLICADO");
    }

    @Test
    void debeLanzarNoDisponible_cuandoConsultaPorIdUnaDocumentacionInexistente() {
        // Arrange
        var inexistente = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultarPorId(inexistente))
                .isInstanceOf(DocumentacionNoDisponibleException.class)
                .extracting("codigoError")
                .isEqualTo(BibliotecaCodes.Documentacion.NO_DISPONIBLE);
    }
}
