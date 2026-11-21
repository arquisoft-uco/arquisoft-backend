package com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.entity.AutorDeEntregableEntity;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.biblioteca.infrastructure.autor.command.secondaryadapter.entity.AutorJpaEntity;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EstudianteProyectoGradoKey;
import com.arquisoft.shared.util.UtilFecha;
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
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EstudianteProyectoGradoCommandOutputAdapterTest {

    private static final Instant T1 = Instant.parse("2026-11-15T10:00:00Z");
    private static final Instant T2 = Instant.parse("2026-11-16T10:00:00Z");

    @Autowired
    private EstudianteProyectoGradoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private EstudianteProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteProyectoGradoCommandOutputAdapter(repository, logger);
    }

    private void sembrarVinculo(UUID proyecto, UUID autor, Instant eliminadoEn) {
        entityManager.persist(EstudianteProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).proyectoGradoId(proyecto).autorId(autor)
                .ocurridoEn(T1).eliminadoEn(eliminadoEn).build());
    }

    private void sembrarAutor(UUID id, Instant eliminadoEn) {
        entityManager.persist(AutorJpaEntity.builder().id(id).identificador("123").nombre("Ana")
                .email("ana@uco.edu.co").ocurridoEn(T1).eliminadoEn(eliminadoEn).build());
    }

    private UUID sembrarEntregable(UUID proyecto) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(EntregableProyectoGradoJpaEntity.builder()
                .id(id).proyectoGradoId(proyecto).versionEntregable(1)
                .estadoEntregableId("APROBADO").claveObjeto("a.zip")
                .nombreArchivo("a.zip").build());
        return id;
    }

    @Test
    void debeGuardarYLeerVinculosConLaBajaLogica_cuandoSeUsanLosDosMetodosDeEscritura() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        var retirado = UtilUUID.generarNuevoUUID();
        var unico = UtilUUID.generarNuevoUUID();

        // Act
        adapter.guardarTodos(List.of(
                new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), vigente, proyecto, T1, UtilFecha.VACIO),
                new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), retirado, proyecto, T1, T2)));
        adapter.guardar(new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), unico,
                UtilUUID.generarNuevoUUID(), T1, UtilFecha.VACIO));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(adapter.consultarPorProyectoGrado(proyecto))
                .extracting(EstudianteProyectoGradoEntity::autor, EstudianteProyectoGradoEntity::eliminadoEn)
                .containsExactlyInAnyOrder(
                        tuple(vigente, UtilFecha.VACIO),
                        tuple(retirado, T2));
        assertThat(adapter.obtenerPorAutorYProyectoGrado(vigente, proyecto)).isPresent();
        assertThat(adapter.obtenerPorAutorYProyectoGrado(unico, proyecto)).isEmpty();
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_GUARDADO, 2);
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_GUARDADO, 1);
    }

    @Test
    void debeConsultarLosAutoresVigentesDelProyectoDelEntregable_cuandoHayRetiradosAusentesYOtrosProyectos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var otroProyecto = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        var autorEliminado = UtilUUID.generarNuevoUUID();
        var autorAusente = UtilUUID.generarNuevoUUID();
        var vinculoRetirado = UtilUUID.generarNuevoUUID();
        var deOtroProyecto = UtilUUID.generarNuevoUUID();
        sembrarAutor(vigente, null);
        sembrarAutor(autorEliminado, T2);
        sembrarAutor(vinculoRetirado, null);
        sembrarAutor(deOtroProyecto, null);
        sembrarVinculo(proyecto, vigente, null);
        sembrarVinculo(proyecto, autorEliminado, null);
        sembrarVinculo(proyecto, autorAusente, null);
        sembrarVinculo(proyecto, vinculoRetirado, T2);
        sembrarVinculo(otroProyecto, deOtroProyecto, null);
        var entregable = sembrarEntregable(proyecto);
        entityManager.flush();
        entityManager.clear();

        // Act
        var autores = adapter.consultarAutoresPorEntregable(entregable);

        // Assert
        assertThat(autores).containsExactlyInAnyOrder(
                new AutorDeEntregableEntity(vigente, true),
                new AutorDeEntregableEntity(autorEliminado, false),
                new AutorDeEntregableEntity(autorAusente, false));
        assertThat(adapter.consultarAutoresPorEntregable(UtilUUID.generarNuevoUUID())).isEmpty();
    }
}
