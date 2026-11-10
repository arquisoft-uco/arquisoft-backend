package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.CoordinadorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.CoordinadorProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
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
class CoordinadorProyectoGradoQueryOutputAdapterTest {

    @Autowired
    private CoordinadorProyectoGradoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private CoordinadorProyectoGradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CoordinadorProyectoGradoQueryOutputAdapter(repository);
    }

    private UUID estudiante(String identificador, Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EstudianteJpaEntity(id, identificador, "Estudiante " + identificador,
                identificador + "@soyuco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn));
        return id;
    }

    private UUID coordinador(String nombre, String identificador, Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new CoordinadorJpaEntity(id, identificador, nombre,
                identificador + "@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn));
        return id;
    }

    private UUID proyecto(UUID id, UUID coordinador, String titulo) {
        entityManager.persist(new ProyectoGradoJpaEntity(id, coordinador, UtilUUID.generarNuevoUUID(), titulo));
        return id;
    }

    private UUID proyecto(UUID coordinador, String titulo) {
        return proyecto(UtilUUID.generarNuevoUUID(), coordinador, titulo);
    }

    private void vincular(UUID estudiante, UUID proyecto) {
        entityManager.persist(new EstudianteProyectoGradoJpaEntity(UtilUUID.generarNuevoUUID(), estudiante, proyecto));
    }

    private List<CoordinadorProyectoGradoReadModel> consultar(UUID estudiante) {
        entityManager.flush();
        entityManager.clear();
        return adapter.consultarPorEstudiante(new CoordinadorProyectoGradoEstudianteCriteria(estudiante));
    }

    @Test
    void debeOrdenarPorTituloYProyecto_cuandoHayVariosProyectosConCoordinadoresDistintos() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var ana = coordinador("Ana", "1001", null);
        var beto = coordinador("Beto", "1002", null);
        var proyectoB = proyecto(beto, "B proyecto");
        var proyectoA2 = proyecto(UUID.fromString("00000000-0000-0000-0000-0000000000a2"), ana, "A proyecto");
        var proyectoA1 = proyecto(UUID.fromString("00000000-0000-0000-0000-0000000000a1"), ana, "A proyecto");
        vincular(estudiante, proyectoB);
        vincular(estudiante, proyectoA2);
        vincular(estudiante, proyectoA1);

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).extracting(r -> r.proyectoGradoId())
                .containsExactly(proyectoA1, proyectoA2, proyectoB);
        var ultimo = resultado.getLast();
        assertThat(ultimo.tituloProyecto()).isEqualTo("B proyecto");
        assertThat(ultimo.coordinadorId()).isEqualTo(beto);
        assertThat(ultimo.identificador()).isEqualTo("1002");
        assertThat(ultimo.nombre()).isEqualTo("Beto");
        assertThat(ultimo.email()).isEqualTo("1002@uco.edu.co");
    }

    @Test
    void debeDevolverUnaFilaPorProyecto_cuandoElMismoCoordinadorLideraDosProyectos() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var ana = coordinador("Ana", "1001", null);
        var primero = proyecto(ana, "Primero");
        var segundo = proyecto(ana, "Segundo");
        vincular(estudiante, primero);
        vincular(estudiante, segundo);

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).extracting(r -> r.proyectoGradoId()).containsExactly(primero, segundo);
        assertThat(resultado).extracting(r -> r.coordinadorId()).containsExactly(ana, ana);
    }

    @Test
    void debeOmitirProyecto_cuandoElCoordinadorEstaDadoDeBaja() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var vigente = coordinador("Ana", "1001", null);
        var baja = coordinador("Beto", "1002", Instant.parse("2026-10-10T10:00:00Z"));
        var proyectoVigente = proyecto(vigente, "Vigente");
        var proyectoBaja = proyecto(baja, "Baja");
        vincular(estudiante, proyectoVigente);
        vincular(estudiante, proyectoBaja);

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).extracting(r -> r.proyectoGradoId()).containsExactly(proyectoVigente);
    }

    @Test
    void debeRetornarVacio_cuandoElEstudianteNoTieneProyectos() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var ajeno = estudiante("2002", null);
        vincular(ajeno, proyecto(coordinador("Ana", "1001", null), "Ajeno"));

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoElEstudianteEstaDadoDeBaja() {
        // Arrange
        var estudiante = estudiante("2001", Instant.parse("2026-10-10T10:00:00Z"));
        vincular(estudiante, proyecto(coordinador("Ana", "1001", null), "Proyecto"));

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeOmitirProyecto_cuandoElEstudianteFueDesvinculado() {
        // Arrange
        var estudiante = estudiante("2001", null);
        var ana = coordinador("Ana", "1001", null);
        var vinculado = proyecto(ana, "Vinculado");
        proyecto(ana, "Desvinculado");
        vincular(estudiante, vinculado);

        // Act
        var resultado = consultar(estudiante);

        // Assert
        assertThat(resultado).extracting(r -> r.proyectoGradoId()).containsExactly(vinculado);
    }

    @Test
    void debeFiltrarPorEstudiante_cuandoVariosEstudiantesComparten() {
        // Arrange
        var uno = estudiante("2001", null);
        var dos = estudiante("2002", null);
        var ana = coordinador("Ana", "1001", null);
        var compartido = proyecto(ana, "Compartido");
        var soloDos = proyecto(ana, "Solo del segundo");
        vincular(uno, compartido);
        vincular(dos, compartido);
        vincular(dos, soloDos);

        // Act
        var delUno = consultar(uno);
        var delDos = consultar(dos);

        // Assert
        assertThat(delUno).extracting(r -> r.proyectoGradoId()).containsExactly(compartido);
        assertThat(delDos).extracting(r -> r.proyectoGradoId()).containsExactly(compartido, soloDos);
    }
}
