package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
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
class AsesorProyectoGradoQueryOutputAdapterTest {

    private static final String ASIGNADO = "ASIGNADO";
    private static final String FINALIZADO = "FINALIZADO";

    @Autowired
    private AsesorProyectoGradoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private AsesorProyectoGradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsesorProyectoGradoQueryOutputAdapter(repository);
        entityManager.persist(new EstadoAsesorFixtureJpaEntity(ASIGNADO, "Asignado", "Asesor asignado"));
        entityManager.persist(new EstadoAsesorFixtureJpaEntity(FINALIZADO, "Finalizado", "Asesor finalizado"));
    }

    private UUID asesor(String nombre, String identificador, Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new AsesorJpaEntity(id, identificador, nombre, identificador + "@soyuco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn));
        return id;
    }

    private UUID vincular(UUID proyectoGrado, UUID asesor, String estado, Instant fechaAsignado) {
        return vincular(UtilUUID.generarNuevoUUID(), proyectoGrado, asesor, estado, fechaAsignado);
    }

    private UUID vincular(UUID id, UUID proyectoGrado, UUID asesor, String estado, Instant fechaAsignado) {
        entityManager.persist(new AsesorProyectoGradoJpaEntity(id, asesor, proyectoGrado, estado, fechaAsignado));
        return id;
    }

    @Test
    void debeOrdenarPorFechaAsignadoDescendenteYProyectarCampos_cuandoHayFinalizados() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var antiguoAsesor = asesor("Ana", "1001", null);
        var recienteAsesor = asesor("Beto", "1002", null);
        var antiguo = vincular(proyectoGrado, antiguoAsesor, FINALIZADO, Instant.parse("2026-09-10T10:00:00Z"));
        var reciente = vincular(proyectoGrado, recienteAsesor, ASIGNADO, Instant.parse("2026-10-05T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).extracting(r -> r.id()).containsExactly(reciente, antiguo);
        var primero = resultado.getFirst();
        assertThat(primero.proyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(primero.asesorId()).isEqualTo(recienteAsesor);
        assertThat(primero.identificador()).isEqualTo("1002");
        assertThat(primero.nombre()).isEqualTo("Beto");
        assertThat(primero.email()).isEqualTo("1002@soyuco.edu.co");
        assertThat(primero.estadoAsesor()).isEqualTo(ASIGNADO);
        assertThat(primero.estadoAsesorNombre()).isEqualTo("Asignado");
        assertThat(primero.fechaAsignado()).isEqualTo(Instant.parse("2026-10-05T10:00:00Z"));
        assertThat(primero.vigente()).isTrue();
        var ultimo = resultado.getLast();
        assertThat(ultimo.estadoAsesor()).isEqualTo(FINALIZADO);
        assertThat(ultimo.estadoAsesorNombre()).isEqualTo("Finalizado");
    }

    @Test
    void debeDesempatarPorIdDescendente_cuandoMismaFecha() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-10-05T10:00:00Z");
        var uno = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var dos = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var tres = UUID.fromString("00000000-0000-0000-0000-000000000003");
        vincular(uno, proyectoGrado, asesor("Ana", "1001", null), FINALIZADO, fecha);
        vincular(tres, proyectoGrado, asesor("Carla", "1003", null), ASIGNADO, fecha);
        vincular(dos, proyectoGrado, asesor("Beto", "1002", null), FINALIZADO, fecha);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).extracting(r -> r.id()).containsExactly(tres, dos, uno);
    }

    @Test
    void debeMarcarNoVigente_cuandoAsesorDadoDeBaja() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vigente = asesor("Ana", "1001", null);
        var baja = asesor("Beto", "1002", Instant.parse("2026-09-20T10:00:00Z"));
        vincular(proyectoGrado, vigente, ASIGNADO, Instant.parse("2026-10-05T10:00:00Z"));
        vincular(proyectoGrado, baja, FINALIZADO, Instant.parse("2026-09-10T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).filteredOn(r -> r.asesorId().equals(vigente)).singleElement()
                .satisfies(r -> assertThat(r.vigente()).isTrue());
        assertThat(resultado).filteredOn(r -> r.asesorId().equals(baja)).singleElement()
                .satisfies(r -> assertThat(r.vigente()).isFalse());
    }

    @Test
    void debeExcluirAsesoresDeOtroProyecto_cuandoHayVarios() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var propio = asesor("Ana", "1001", null);
        vincular(proyectoGrado, propio, ASIGNADO, Instant.parse("2026-10-05T10:00:00Z"));
        vincular(UtilUUID.generarNuevoUUID(), asesor("Beto", "1002", null), ASIGNADO,
                Instant.parse("2026-10-06T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).extracting(r -> r.asesorId()).containsExactly(propio);
    }

    @Test
    void debeRetornarVacio_cuandoProyectoSinAsesores() {
        // Act
        var resultado = adapter.consultarPorProyecto(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
