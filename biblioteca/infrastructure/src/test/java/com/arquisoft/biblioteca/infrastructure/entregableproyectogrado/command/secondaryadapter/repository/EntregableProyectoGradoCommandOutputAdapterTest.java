package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EntregableProyectoGradoKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EntregableProyectoGradoCommandOutputAdapterTest {

    @Autowired
    private EntregableProyectoGradoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private EntregableProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EntregableProyectoGradoCommandOutputAdapter(repository, logger);
    }

    private UUID sembrar(String bucket, String clave) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(EntregableProyectoGradoJpaEntity.builder()
                .id(id).proyectoGradoId(UtilUUID.generarNuevoUUID()).versionEntregable(1)
                .estadoEntregableNombre("APROBADO")
                .bucket(bucket).claveObjeto(clave).nombreArchivo("a.zip").build());
        entityManager.clear();
        return id;
    }

    @Test
    void debeDevolverProyectoBucketYClave_cuandoElEntregableExiste() {
        // Arrange
        var id = sembrar("entregables", "proyectos/a.zip");

        // Act
        var resultado = adapter.obtenerBloqueadoPorId(id);
        var ausente = adapter.obtenerBloqueadoPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(id);
            assertThat(entity.bucket()).isEqualTo("entregables");
            assertThat(entity.claveObjeto()).isEqualTo("proyectos/a.zip");
        });
        assertThat(ausente).isEmpty();
    }

    @Test
    void debeMapearBucketYClaveNulosAVacio_cuandoElArchivoAunNoEstaDisponible() {
        // Arrange
        var id = sembrar(null, null);

        // Act
        var resultado = adapter.obtenerBloqueadoPorId(id);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.bucket()).isEqualTo(UtilTexto.VACIO);
            assertThat(entity.claveObjeto()).isEqualTo(UtilTexto.VACIO);
        });
    }

    @Test
    void debeInsertarConLosSieteCampos_cuandoSeGuardaUnEntregableNuevo() {
        // Arrange
        var entregable = new EntregableProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                3, "Aprobado Con Observaciones", "entregables", "paquetes/a.zip", "a.zip");

        // Act
        adapter.guardar(entregable);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var jpa = entityManager.find(EntregableProyectoGradoJpaEntity.class, entregable.id());
        assertThat(jpa.getProyectoGradoId()).isEqualTo(entregable.proyectoGrado());
        assertThat(jpa.getVersionEntregable()).isEqualTo(3);
        assertThat(jpa.getEstadoEntregableNombre()).isEqualTo("Aprobado Con Observaciones");
        assertThat(jpa.getBucket()).isEqualTo("entregables");
        assertThat(jpa.getClaveObjeto()).isEqualTo("paquetes/a.zip");
        assertThat(jpa.getNombreArchivo()).isEqualTo("a.zip");
        verify(logger).debug(EntregableProyectoGradoKey.LOG_GUARDADO, entregable.id());
    }

    @Test
    void debeResponderPorExistencia_cuandoElIdEstaONoEnLaReplica() {
        // Arrange
        var existente = sembrar("entregables", "proyectos/a.zip");

        // Act & Assert
        assertThat(adapter.existePorId(existente)).isTrue();
        assertThat(adapter.existePorId(UtilUUID.generarNuevoUUID())).isFalse();
    }
}
