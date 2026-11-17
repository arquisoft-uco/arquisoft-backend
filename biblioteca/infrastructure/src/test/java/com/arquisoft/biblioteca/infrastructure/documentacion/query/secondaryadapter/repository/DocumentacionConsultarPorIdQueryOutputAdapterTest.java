package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.infrastructure.documentacion.exception.DocumentacionNoDisponibleException;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class DocumentacionConsultarPorIdQueryOutputAdapterTest {

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
                + "proyecto_grado_id UUID, version_entregable INTEGER, asesor_id UUID, "
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

    private UUID sembrarBibliotecario(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO bibliotecario (id, identificador, nombre, email, ocurrido_en, "
                        + "eliminado_en) VALUES (?1, ?2, ?3, ?4, ?5, NULL)")
                .setParameter(1, id).setParameter(2, "ID-" + nombre).setParameter(3, nombre)
                .setParameter(4, nombre + "@uco.edu.co").setParameter(5, BASE)
                .executeUpdate();
        return id;
    }

    private UUID sembrarEntregable(UUID proyecto, int version) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO entregable_proyecto_grado (id, proyecto_grado_id, "
                        + "version_entregable, asesor_id, estado_entregable_nombre) VALUES (?1, ?2, ?3, ?4, ?5)")
                .setParameter(1, id).setParameter(2, proyecto).setParameter(3, version)
                .setParameter(4, UtilUUID.generarNuevoUUID()).setParameter(5, "Aprobado")
                .executeUpdate();
        return id;
    }

    private UUID sembrarDocumentacion(UUID entregable, UUID bibliotecario) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO documentacion (id, url, fecha_publicacion, "
                        + "entregable_proyecto_grado_id, bibliotecario_id, estado_documentacion_id) "
                        + "VALUES (?1, ?2, ?3, ?4, ?5, ?6)")
                .setParameter(1, id).setParameter(2, "https://biblioteca.test/" + id)
                .setParameter(3, BASE).setParameter(4, entregable).setParameter(5, bibliotecario)
                .setParameter(6, "PUBLICADO")
                .executeUpdate();
        return id;
    }

    @Test
    void debeRetornarElReadModelCompleto_cuandoConsultaPorIdUnaDocumentacionExistente() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var bibliotecario = sembrarBibliotecario("Ana");
        var entregable = sembrarEntregable(proyecto, 3);
        var id = sembrarDocumentacion(entregable, bibliotecario);
        sembrarDocumentacion(sembrarEntregable(proyecto, 4), bibliotecario);
        entityManager.flush();
        entityManager.clear();

        // Act
        var leido = adapter.consultarPorId(id);

        // Assert
        assertThat(leido.id()).isEqualTo(id);
        assertThat(leido.entregableProyectoGrado().id()).isEqualTo(entregable);
        assertThat(leido.entregableProyectoGrado().versionEntregable()).isEqualTo(3);
        assertThat(leido.bibliotecario().id()).isEqualTo(bibliotecario);
        assertThat(leido.bibliotecario().nombre()).isEqualTo("Ana");
        assertThat(leido.estadoDocumentacion().id()).isEqualTo("PUBLICADO");
        assertThat(leido.autores()).isEmpty();
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
