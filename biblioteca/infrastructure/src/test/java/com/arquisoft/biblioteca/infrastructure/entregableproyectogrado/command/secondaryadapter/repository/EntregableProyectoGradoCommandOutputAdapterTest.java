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

    private UUID sembrar(String clave) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(EntregableProyectoGradoJpaEntity.builder()
                .id(id).proyectoGradoId(UtilUUID.generarNuevoUUID()).versionEntregable(1)
                .estadoEntregableId("APROBADO")
                .claveObjeto(clave).nombreArchivo("a.zip").build());
        entityManager.clear();
        return id;
    }

    @Test
    void debeDevolverClave_cuandoElEntregableExiste() {
        // Arrange
        var id = sembrar("proyectos/a.zip");

        // Act
        var resultado = adapter.obtenerBloqueadoPorId(id);
        var ausente = adapter.obtenerBloqueadoPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(id);
            assertThat(entity.claveObjeto()).isEqualTo("proyectos/a.zip");
        });
        assertThat(ausente).isEmpty();
    }

    @Test
    void debeMapearClaveNulaAVacio_cuandoElArchivoAunNoEstaDisponible() {
        // Arrange
        var id = sembrar(null);

        // Act
        var resultado = adapter.obtenerBloqueadoPorId(id);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.claveObjeto()).isEqualTo(UtilTexto.VACIO);
        });
    }

    @Test
    void debeInsertarConLosSeisCampos_cuandoSeGuardaUnEntregableNuevo() {
        // Arrange
        var entregable = new EntregableProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                3, "APROBADO_CON_OBSERVACIONES", "paquetes/a.zip", "a.zip");

        // Act
        adapter.guardar(entregable);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var jpa = entityManager.find(EntregableProyectoGradoJpaEntity.class, entregable.id());
        assertThat(jpa.getProyectoGradoId()).isEqualTo(entregable.proyectoGrado());
        assertThat(jpa.getVersionEntregable()).isEqualTo(3);
        assertThat(jpa.getEstadoEntregableId()).isEqualTo("APROBADO_CON_OBSERVACIONES");
        assertThat(jpa.getClaveObjeto()).isEqualTo("paquetes/a.zip");
        assertThat(jpa.getNombreArchivo()).isEqualTo("a.zip");
        verify(logger).debug(EntregableProyectoGradoKey.LOG_GUARDADO, entregable.id());
    }

    @Test
    void debeResponderPorExistencia_cuandoElIdEstaONoEnLaReplica() {
        // Arrange
        var existente = sembrar("proyectos/a.zip");

        // Act & Assert
        assertThat(adapter.existePorId(existente)).isTrue();
        assertThat(adapter.existePorId(UtilUUID.generarNuevoUUID())).isFalse();
    }
}
