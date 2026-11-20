package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArchivoDocumentacionAccesoQueryOutputAdapterTest {

    @Autowired
    private ArchivoDocumentacionQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private ArchivoDocumentacionAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ArchivoDocumentacionAccesoQueryOutputAdapter(repository);
        var em = entityManager.getEntityManager();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS entregable_proyecto_grado (id UUID PRIMARY KEY, "
                + "proyecto_grado_id UUID, version_entregable INTEGER, "
                + "estado_entregable_nombre VARCHAR(60), bucket VARCHAR(63), clave_objeto VARCHAR(2000), "
                + "nombre_archivo VARCHAR(255))").executeUpdate();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS documentacion (id UUID PRIMARY KEY, url VARCHAR(2000), "
                + "fecha_publicacion TIMESTAMP WITH TIME ZONE, entregable_proyecto_grado_id UUID, "
                + "bibliotecario_id UUID, estado_documentacion_id VARCHAR(60))").executeUpdate();
        em.createNativeQuery("DELETE FROM documentacion").executeUpdate();
        em.createNativeQuery("DELETE FROM entregable_proyecto_grado").executeUpdate();
    }

    private UUID sembrar(String estado, String bucket, String clave, String nombre) {
        var em = entityManager.getEntityManager();
        var entregable = UtilUUID.generarNuevoUUID();
        em.createNativeQuery("INSERT INTO entregable_proyecto_grado (id, proyecto_grado_id, version_entregable, "
                        + "estado_entregable_nombre, bucket, clave_objeto, nombre_archivo) "
                        + "VALUES (?1, ?2, 1, 'Aprobado', ?3, ?4, ?5)")
                .setParameter(1, entregable).setParameter(2, UtilUUID.generarNuevoUUID())
                .setParameter(3, bucket).setParameter(4, clave).setParameter(5, nombre)
                .executeUpdate();
        var id = UtilUUID.generarNuevoUUID();
        em.createNativeQuery("INSERT INTO documentacion (id, url, fecha_publicacion, "
                        + "entregable_proyecto_grado_id, bibliotecario_id, estado_documentacion_id) "
                        + "VALUES (?1, ?2, CURRENT_TIMESTAMP, ?3, ?4, ?5)")
                .setParameter(1, id).setParameter(2, "https://biblioteca.test/" + id)
                .setParameter(3, entregable).setParameter(4, UtilUUID.generarNuevoUUID())
                .setParameter(5, estado)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return id;
    }

    @Test
    void debeDevolverUbicacionYEstado_cuandoLaDocumentacionExiste() {
        // Arrange
        var id = sembrar("PUBLICADO", "entregables", "paquetes/a.zip", "a.zip");

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().documentacion()).isEqualTo(id);
        assertThat(resultado.get().estado()).isEqualTo("PUBLICADO");
        assertThat(resultado.get().bucket()).isEqualTo("entregables");
        assertThat(resultado.get().claveObjeto()).isEqualTo("paquetes/a.zip");
        assertThat(resultado.get().nombreArchivo()).isEqualTo("a.zip");
    }

    @Test
    void debeDevolverColumnasNulas_cuandoLaFilaNoTieneUbicacion() {
        // Arrange
        var id = sembrar("PENDIENTE", null, null, null);

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().estado()).isEqualTo("PENDIENTE");
        assertThat(resultado.get().bucket()).isNull();
        assertThat(resultado.get().claveObjeto()).isNull();
        assertThat(resultado.get().nombreArchivo()).isNull();
    }

    @Test
    void debeDevolverVacio_cuandoLaDocumentacionNoExiste() {
        // Act
        var resultado = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
