package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.infrastructure.autor.command.secondaryadapter.entity.AutorJpaEntity;
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
class AutorDocumentacionQueryOutputAdapterTest {

    private static final Instant BASE = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired
    private AutorDocumentacionQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private AutorDocumentacionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AutorDocumentacionQueryOutputAdapter(repository);
        var em = entityManager.getEntityManager();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS autor_documentacion (id UUID PRIMARY KEY, "
                + "documentacion_id UUID, autor_id UUID)").executeUpdate();
        em.createNativeQuery("DELETE FROM autor_documentacion").executeUpdate();
    }

    private UUID sembrarAutor(String nombre, Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(AutorJpaEntity.builder()
                .id(id).identificador("ID-" + nombre).nombre(nombre).email(nombre + "@uco.edu.co")
                .ocurridoEn(BASE).eliminadoEn(eliminadoEn).build());
        return id;
    }

    private void vincular(UUID documentacion, UUID autor) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO autor_documentacion (id, documentacion_id, autor_id) "
                        + "VALUES (?1, ?2, ?3)")
                .setParameter(1, UtilUUID.generarNuevoUUID()).setParameter(2, documentacion)
                .setParameter(3, autor)
                .executeUpdate();
    }

    private void confirmar() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeRetornarSoloLosAutoresDeLaDocumentacion_cuandoHayVariasDocumentaciones() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var otra = UtilUUID.generarNuevoUUID();
        var ana = sembrarAutor("Ana", null);
        var beto = sembrarAutor("Beto", null);
        vincular(documentacion, ana);
        vincular(otra, ana);
        vincular(otra, beto);
        confirmar();

        // Act
        var resultado = adapter.consultarPorDocumentacion(documentacion);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).id()).isEqualTo(ana);
        assertThat(resultado.get(0).identificador()).isEqualTo("ID-Ana");
        assertThat(resultado.get(0).nombre()).isEqualTo("Ana");
        assertThat(resultado.get(0).email()).isEqualTo("Ana@uco.edu.co");
    }

    @Test
    void debeOrdenarPorNombreAscendente_cuandoVariosAutoresSeVinculanEnOtroOrden() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var carla = sembrarAutor("Carla", null);
        var ana = sembrarAutor("Ana", null);
        var beto = sembrarAutor("Beto", null);
        vincular(documentacion, carla);
        vincular(documentacion, ana);
        vincular(documentacion, beto);
        confirmar();

        // Act
        var resultado = adapter.consultarPorDocumentacion(documentacion);

        // Assert
        assertThat(resultado).extracting(r -> r.nombre()).containsExactly("Ana", "Beto", "Carla");
    }

    @Test
    void debeIncluirAlAutorDadoDeBaja_cuandoTieneEliminadoEnInformado() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var vigente = sembrarAutor("Ana", null);
        var dadoDeBaja = sembrarAutor("Beto", BASE);
        vincular(documentacion, vigente);
        vincular(documentacion, dadoDeBaja);
        confirmar();

        // Act
        var resultado = adapter.consultarPorDocumentacion(documentacion);

        // Assert
        assertThat(resultado).extracting(r -> r.id()).containsExactly(vigente, dadoDeBaja);
    }

    @Test
    void debeRetornarVacio_cuandoLaDocumentacionNoTieneAutoresOEsDesconocida() {
        // Arrange
        var conAutor = UtilUUID.generarNuevoUUID();
        vincular(conAutor, sembrarAutor("Ana", null));
        confirmar();

        // Act
        var desconocida = adapter.consultarPorDocumentacion(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(desconocida).isEmpty();
    }
}
