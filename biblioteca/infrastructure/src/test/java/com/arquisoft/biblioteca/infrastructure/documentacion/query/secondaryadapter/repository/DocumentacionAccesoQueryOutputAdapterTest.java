package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DocumentacionAccesoQueryOutputAdapterTest {

    @Autowired
    private DocumentacionQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private DocumentacionAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new DocumentacionAccesoQueryOutputAdapter(repository);
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

    private UUID sembrarDocumentacion(String estado) {
        var em = entityManager.getEntityManager();
        var bibliotecario = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        em.createNativeQuery("INSERT INTO bibliotecario (id, identificador, nombre, email, ocurrido_en, "
                        + "eliminado_en) VALUES (?1, 'ID', 'Ana', 'ana@uco.edu.co', ?2, NULL)")
                .setParameter(1, bibliotecario).setParameter(2, Instant.parse("2026-11-10T10:00:00Z"))
                .executeUpdate();
        em.createNativeQuery("INSERT INTO entregable_proyecto_grado (id, proyecto_grado_id, "
                        + "version_entregable, asesor_id, estado_entregable_nombre) VALUES (?1, ?2, 1, ?3, 'Aprobado')")
                .setParameter(1, entregable).setParameter(2, UtilUUID.generarNuevoUUID())
                .setParameter(3, UtilUUID.generarNuevoUUID()).executeUpdate();
        em.createNativeQuery("INSERT INTO documentacion (id, url, fecha_publicacion, "
                        + "entregable_proyecto_grado_id, bibliotecario_id, estado_documentacion_id) "
                        + "VALUES (?1, 'https://biblioteca.test/x', ?2, ?3, ?4, ?5)")
                .setParameter(1, id).setParameter(2, Instant.parse("2026-11-10T10:00:00Z"))
                .setParameter(3, entregable).setParameter(4, bibliotecario).setParameter(5, estado)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return id;
    }

    @Test
    void debeDevolverEstado_cuandoLaDocumentacionExiste() {
        // Arrange
        var id = sembrarDocumentacion("NO_PUBLICADO");

        // Act
        var estado = adapter.obtenerEstadoPorId(id);

        // Assert
        assertThat(estado).contains("NO_PUBLICADO");
    }

    @Test
    void debeDevolverVacio_cuandoLaDocumentacionNoExiste() {
        // Act
        var estado = adapter.obtenerEstadoPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(estado).isEmpty();
    }
}
