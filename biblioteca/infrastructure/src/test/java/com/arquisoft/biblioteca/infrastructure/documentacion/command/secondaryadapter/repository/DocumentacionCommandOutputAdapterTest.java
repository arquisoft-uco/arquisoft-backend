package com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.entity.DocumentacionJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class DocumentacionCommandOutputAdapterTest {

    private static final Instant FECHA = Instant.parse("2026-11-15T10:00:00Z");

    @Autowired
    private DocumentacionCommandRepository documentacionCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private DocumentacionCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new DocumentacionCommandOutputAdapter(documentacionCommandRepository, logger);
    }

    private DocumentacionJpaEntity sembrar(UUID id, String url, UUID entregable, UUID bibliotecario, String estado) {
        return entityManager.persistAndFlush(DocumentacionJpaEntity.builder()
                .id(id).url(url).fechaPublicacion(FECHA).entregableProyectoGradoId(entregable)
                .bibliotecarioId(bibliotecario).estadoDocumentacion(estado).build());
    }

    @Test
    void debeObtenerLaDocumentacionConSusSeisColumnas_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var bibliotecario = UtilUUID.generarNuevoUUID();
        sembrar(id, "https://minio.test/a.zip", entregable, bibliotecario, "PUBLICADO");
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(id);
            assertThat(entity.url()).isEqualTo("https://minio.test/a.zip");
            assertThat(entity.fechaPublicacion()).isEqualTo(FECHA);
            assertThat(entity.entregableProyectoGrado()).isEqualTo(entregable);
            assertThat(entity.bibliotecario()).isEqualTo(bibliotecario);
            assertThat(entity.estadoDocumentacion()).isEqualTo("PUBLICADO");
        });
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Act
        var resultado = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeActualizarSoloElEstadoDeEsaDocumentacion_cuandoSeCambiaElEstado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var otra = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var bibliotecario = UtilUUID.generarNuevoUUID();
        sembrar(id, "https://minio.test/a.zip", entregable, bibliotecario, "PENDIENTE");
        sembrar(otra, "https://minio.test/b.zip", UtilUUID.generarNuevoUUID(), bibliotecario, "PENDIENTE");

        // Act
        adapter.actualizarEstado(id, "PUBLICADO");

        // Assert
        entityManager.clear();
        assertThat(adapter.obtenerPorId(id)).hasValueSatisfying(entity -> {
            assertThat(entity.estadoDocumentacion()).isEqualTo("PUBLICADO");
            assertThat(entity.url()).isEqualTo("https://minio.test/a.zip");
            assertThat(entity.fechaPublicacion()).isEqualTo(FECHA);
            assertThat(entity.entregableProyectoGrado()).isEqualTo(entregable);
            assertThat(entity.bibliotecario()).isEqualTo(bibliotecario);
        });
        assertThat(adapter.obtenerPorId(otra))
                .hasValueSatisfying(intacta -> assertThat(intacta.estadoDocumentacion()).isEqualTo("PENDIENTE"));
        verify(logger).debug(DocumentacionKey.LOG_ESTADO_ACTUALIZADO, id);
    }

    @Test
    void noDebeCrearNiModificarNada_cuandoLaDocumentacionNoExiste() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        sembrar(existente, "https://minio.test/a.zip", UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "NO_PUBLICADO");

        // Act
        adapter.actualizarEstado(UtilUUID.generarNuevoUUID(), "PUBLICADO");

        // Assert
        entityManager.clear();
        assertThat(documentacionCommandRepository.count()).isEqualTo(1);
        assertThat(adapter.obtenerPorId(existente))
                .hasValueSatisfying(entity -> assertThat(entity.estadoDocumentacion()).isEqualTo("NO_PUBLICADO"));
    }
}
