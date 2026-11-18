package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorActualProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AsesorActualProyectoGradoQueryOutputAdapterTest {

    private static final String ASIGNADO = "ASIGNADO";
    private static final String FINALIZADO = "FINALIZADO";

    @Autowired
    private AsesorActualProyectoGradoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private AsesorActualProyectoGradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsesorActualProyectoGradoQueryOutputAdapter(repository);
        entityManager.persist(new EstadoAsesorFixtureJpaEntity(ASIGNADO, "Asignado", "Asesor asignado"));
        entityManager.persist(new EstadoAsesorFixtureJpaEntity(FINALIZADO, "Finalizado", "Asesor finalizado"));
    }

    private UUID estudiante(String identificador, Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EstudianteJpaEntity(id, identificador, "Estudiante " + identificador,
                identificador + "@soyuco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn));
        return id;
    }

    private UUID proyecto(String titulo) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(id, UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), titulo));
        return id;
    }

    private void vincularEstudiante(UUID estudiante, UUID proyecto) {
        entityManager.persist(new EstudianteProyectoGradoJpaEntity(UtilUUID.generarNuevoUUID(), estudiante, proyecto));
    }

    private UUID asesor(String nombre, String identificador, Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new AsesorJpaEntity(id, identificador, nombre, identificador + "@soyuco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn));
        return id;
    }

    private UUID asignar(UUID proyecto, UUID asesor, String estado, String fecha) {
        return asignar(UtilUUID.generarNuevoUUID(), proyecto, asesor, estado, fecha);
    }

    private UUID asignar(UUID id, UUID proyecto, UUID asesor, String estado, String fecha) {
        entityManager.persist(new AsesorProyectoGradoJpaEntity(id, asesor, proyecto, estado, Instant.parse(fecha)));
        return id;
    }

    private List<AsesorActualProyectoGradoReadModel> consultar(UUID estudiante) {
        entityManager.flush();
        entityManager.clear();
        return adapter.consultarPorEstudiante(new AsesorActualProyectoGradoEstudianteCriteria(estudiante));
    }

    @Test
    void debeOrdenarPorTituloYProyecto_cuandoHayVariosProyectos() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var proyectoB = proyecto("B proyecto");
        var proyectoA2 = UUID.fromString("00000000-0000-0000-0000-0000000000a2");
        var proyectoA1 = UUID.fromString("00000000-0000-0000-0000-0000000000a1");
        entityManager.persist(new ProyectoGradoJpaEntity(proyectoA2, UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "A proyecto"));
        entityManager.persist(new ProyectoGradoJpaEntity(proyectoA1, UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "A proyecto"));
        var ana = asesor("Ana", "1001", null);
        var beto = asesor("Beto", "1002", null);
        for (var proyecto : new UUID[] {proyectoB, proyectoA2, proyectoA1}) {
            vincularEstudiante(estudiante, proyecto);
        }
        asignar(proyectoB, beto, ASIGNADO, "2026-10-05T10:00:00Z");
        asignar(proyectoA2, ana, ASIGNADO, "2026-10-05T10:00:00Z");
        asignar(proyectoA1, ana, ASIGNADO, "2026-10-05T10:00:00Z");

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).extracting(r -> r.proyectoGradoId())
                .containsExactly(proyectoA1, proyectoA2, proyectoB);
        var ultimo = resultado.getLast();
        assertThat(ultimo.tituloProyecto()).isEqualTo("B proyecto");
        assertThat(ultimo.asesorId()).isEqualTo(beto);
        assertThat(ultimo.identificador()).isEqualTo("1002");
        assertThat(ultimo.nombre()).isEqualTo("Beto");
        assertThat(ultimo.email()).isEqualTo("1002@soyuco.edu.co");
    }

    @Test
    void debeOmitirProyecto_cuandoElUnicoAsesorEstaFinalizado() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var proyecto = proyecto("Proyecto");
        var ana = asesor("Ana", "1001", null);
        vincularEstudiante(estudiante, proyecto);
        asignar(proyecto, ana, ASIGNADO, "2026-09-10T10:00:00Z");
        asignar(proyecto, ana, FINALIZADO, "2026-10-01T10:00:00Z");

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDevolverSoloElAsignado_cuandoHayFinalizadoDeOtroAsesor() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var proyecto = proyecto("Proyecto");
        var ana = asesor("Ana", "1001", null);
        var beto = asesor("Beto", "1002", null);
        vincularEstudiante(estudiante, proyecto);
        asignar(proyecto, ana, ASIGNADO, "2026-09-10T10:00:00Z");
        asignar(proyecto, ana, FINALIZADO, "2026-09-20T10:00:00Z");
        asignar(proyecto, beto, ASIGNADO, "2026-10-01T10:00:00Z");

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).extracting(r -> r.asesorId()).containsExactly(beto);
    }

    @Test
    void debeDevolverUnaFila_cuandoElAsesorFueReasignado() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var proyecto = proyecto("Proyecto");
        var ana = asesor("Ana", "1001", null);
        vincularEstudiante(estudiante, proyecto);
        asignar(proyecto, ana, ASIGNADO, "2026-09-10T10:00:00Z");
        asignar(proyecto, ana, FINALIZADO, "2026-09-20T10:00:00Z");
        asignar(proyecto, ana, ASIGNADO, "2026-10-05T10:00:00Z");

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).extracting(r -> r.asesorId()).containsExactly(ana);
    }

    @Test
    void debeOmitirProyecto_cuandoNoTieneAsesores() {
        // Arrange
        var estudiante = estudiante("2001", null);
        vincularEstudiante(estudiante, proyecto("Proyecto"));

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeOmitirProyecto_cuandoElAsesorActualEstaDadoDeBaja() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var proyecto = proyecto("Proyecto");
        var baja = asesor("Ana", "1001", Instant.parse("2026-10-10T10:00:00Z"));
        vincularEstudiante(estudiante, proyecto);
        asignar(proyecto, baja, ASIGNADO, "2026-10-05T10:00:00Z");

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDevolverElAsesorVigente_cuandoOtroAsignadoEstaDadoDeBaja() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var proyecto = proyecto("Proyecto");
        var vigente = asesor("Ana", "1001", null);
        var baja = asesor("Beto", "1002", Instant.parse("2026-10-10T10:00:00Z"));
        vincularEstudiante(estudiante, proyecto);
        asignar(proyecto, vigente, ASIGNADO, "2026-09-10T10:00:00Z");
        asignar(proyecto, baja, ASIGNADO, "2026-10-05T10:00:00Z");

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).extracting(r -> r.asesorId()).containsExactly(vigente);
    }

    @Test
    void debeRetornarVacio_cuandoElEstudianteNoTieneProyectos() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var ajeno = estudiante("2002", null);
        var proyecto = proyecto("Proyecto");
        vincularEstudiante(ajeno, proyecto);
        asignar(proyecto, asesor("Ana", "1001", null), ASIGNADO, "2026-10-05T10:00:00Z");

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoElEstudianteEstaDadoDeBaja() {
        // Arrange
        var estudiante = estudiante("2001", Instant.parse("2026-10-10T10:00:00Z"));
        var proyecto = proyecto("Proyecto");
        vincularEstudiante(estudiante, proyecto);
        asignar(proyecto, asesor("Ana", "1001", null), ASIGNADO, "2026-10-05T10:00:00Z");

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeFiltrarPorEstudiante_cuandoVariosEstudiantesComparten() {
        // Arrange
        var uno = estudiante("2001", null);
        var dos = estudiante("2002", null);
        var compartido = proyecto("Compartido");
        var soloDos = proyecto("Solo del segundo");
        var ana = asesor("Ana", "1001", null);
        vincularEstudiante(uno, compartido);
        vincularEstudiante(dos, compartido);
        vincularEstudiante(dos, soloDos);
        asignar(compartido, ana, ASIGNADO, "2026-10-05T10:00:00Z");
        asignar(soloDos, ana, ASIGNADO, "2026-10-06T10:00:00Z");

        // Act
        var delUno = consultar(uno);
        var delDos = consultar(dos);

        // Assert
        assertThat(delUno).extracting(r -> r.proyectoGradoId()).containsExactly(compartido);
        assertThat(delDos).extracting(r -> r.proyectoGradoId()).containsExactly(compartido, soloDos);
    }

    @Test
    void debeDevolverUnaFilaPorProyecto_cuandoHayDosAsignados() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var proyecto = proyecto("Proyecto");
        var antiguo = asesor("Ana", "1001", null);
        var reciente = asesor("Beto", "1002", null);
        vincularEstudiante(estudiante, proyecto);
        asignar(proyecto, antiguo, ASIGNADO, "2026-09-10T10:00:00Z");
        asignar(proyecto, reciente, ASIGNADO, "2026-10-05T10:00:00Z");

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).extracting(r -> r.asesorId()).containsExactly(reciente);
    }

    @Test
    void debeDesempatarPorIdMayor_cuandoMismaFecha() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var proyecto = proyecto("Proyecto");
        var menor = asesor("Ana", "1001", null);
        var mayor = asesor("Beto", "1002", null);
        vincularEstudiante(estudiante, proyecto);
        asignar(UUID.fromString("00000000-0000-0000-0000-000000000001"), proyecto, menor, ASIGNADO,
                "2026-10-05T10:00:00Z");
        asignar(UUID.fromString("00000000-0000-0000-0000-000000000002"), proyecto, mayor, ASIGNADO,
                "2026-10-05T10:00:00Z");

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).extracting(r -> r.asesorId()).containsExactly(mayor);
    }
}
