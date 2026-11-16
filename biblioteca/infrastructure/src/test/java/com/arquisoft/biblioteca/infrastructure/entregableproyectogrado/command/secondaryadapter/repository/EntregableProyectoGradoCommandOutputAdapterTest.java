package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EntregableProyectoGradoCommandOutputAdapterTest {

    @Autowired
    private EntregableProyectoGradoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EntregableProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EntregableProyectoGradoCommandOutputAdapter(repository);
    }

    private UUID sembrar(String bucket, String clave) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(EntregableProyectoGradoJpaEntity.builder()
                .id(id).proyectoGradoId(UtilUUID.generarNuevoUUID()).versionEntregable(1)
                .asesorId(UtilUUID.generarNuevoUUID()).estadoEntregableNombre("APROBADO")
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
}
