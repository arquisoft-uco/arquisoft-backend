package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
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
class EstudianteProyectoGradoQueryOutputAdapterTest {

    @Autowired
    private EstudianteProyectoGradoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EstudianteProyectoGradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteProyectoGradoQueryOutputAdapter(repository);
    }

    private UUID vincular(UUID proyectoGrado, String nombre, String identificador, Instant eliminadoEn) {
        var estudiante = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EstudianteJpaEntity(estudiante, identificador, nombre,
                identificador + "@soyuco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn));
        entityManager.persist(new EstudianteProyectoGradoJpaEntity(UtilUUID.generarNuevoUUID(), estudiante,
                proyectoGrado));
        return estudiante;
    }

    @Test
    void debeRetornarEstudiantesOrdenadosPorNombre_cuandoProyectoTieneVinculos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var carla = vincular(proyectoGrado, "Carla", "2022", null);
        var ana = vincular(proyectoGrado, "Ana", "2020", null);
        var beto = vincular(proyectoGrado, "Beto", "2021", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).extracting(r -> r.nombre()).containsExactly("Ana", "Beto", "Carla");
        assertThat(resultado).extracting(r -> r.estudianteId()).containsExactly(ana, beto, carla);
        var primero = resultado.getFirst();
        assertThat(primero.proyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(primero.identificador()).isEqualTo("2020");
        assertThat(primero.email()).isEqualTo("2020@soyuco.edu.co");
        assertThat(primero.id()).isNotNull();
        assertThat(resultado).allMatch(r -> r.vigente());
    }

    @Test
    void debeMarcarNoVigente_cuandoEstudianteDadoDeBaja() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vigente = vincular(proyectoGrado, "Ana", "2020", null);
        var baja = vincular(proyectoGrado, "Beto", "2021", Instant.parse("2026-09-10T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).filteredOn(r -> r.estudianteId().equals(vigente)).singleElement()
                .satisfies(r -> assertThat(r.vigente()).isTrue());
        assertThat(resultado).filteredOn(r -> r.estudianteId().equals(baja)).singleElement()
                .satisfies(r -> assertThat(r.vigente()).isFalse());
    }

    @Test
    void debeExcluirVinculosDeOtroProyecto_cuandoHayVarios() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var propio = vincular(proyectoGrado, "Ana", "2020", null);
        vincular(UtilUUID.generarNuevoUUID(), "Beto", "2021", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).extracting(r -> r.estudianteId()).containsExactly(propio);
    }

    @Test
    void debeRetornarVacio_cuandoProyectoSinEstudiantes() {
        // Act
        var resultado = adapter.consultarPorProyecto(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarCompanerosVigentesOrdenadosPorNombre_cuandoHayBajasYOtrosProyectos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var carla = vincular(proyectoGrado, "Carla", "2022", null);
        var ana = vincular(proyectoGrado, "Ana", "2020", null);
        vincular(proyectoGrado, "Beto", "2021", Instant.parse("2026-09-10T10:00:00Z"));
        vincular(UtilUUID.generarNuevoUUID(), "Diego", "2023", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarCompanerosVigentesPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).extracting(r -> r.estudianteId()).containsExactly(ana, carla);
        var primero = resultado.getFirst();
        assertThat(primero.identificador()).isEqualTo("2020");
        assertThat(primero.nombre()).isEqualTo("Ana");
        assertThat(primero.email()).isEqualTo("2020@soyuco.edu.co");
    }

    @Test
    void debeRetornarSoloVigentesOrdenadosPorNombre_cuandoConsultaAsesorados() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var carla = vincular(proyectoGrado, "Carla", "2022", null);
        var ana = vincular(proyectoGrado, "Ana", "2020", null);
        vincular(proyectoGrado, "Beto", "2021", Instant.parse("2026-09-10T10:00:00Z"));
        vincular(UtilUUID.generarNuevoUUID(), "Diego", "2023", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarAsesoradosVigentesPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).extracting(r -> r.estudianteId()).containsExactly(ana, carla);
        var primero = resultado.getFirst();
        assertThat(primero.identificador()).isEqualTo("2020");
        assertThat(primero.nombre()).isEqualTo("Ana");
        assertThat(primero.email()).isEqualTo("2020@soyuco.edu.co");
    }

    @Test
    void debeRetornarVacio_cuandoSinEstudiantesVigentes() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        vincular(proyectoGrado, "Beto", "2021", Instant.parse("2026-09-10T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarAsesoradosVigentesPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoProyectoSinCompaneros() {
        // Act
        var resultado = adapter.consultarCompanerosVigentesPorProyecto(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
