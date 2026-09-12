package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.command.secondaryadapter.entity.EstadoFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.entity.EstadoFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoFichaPerfilQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstadoFichaPerfilEstudianteQueryRepository repository;

    private EstadoFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoFichaPerfilQueryOutputAdapter(repository);

        persistirEstadoFicha("EN_CONSTRUCCION", "En Construccion");
        persistirEstadoFicha("DISPONIBLE_PARA_EVALUACION", "Disponible Para Evaluacion");
        persistirEstadoFicha("APROBADA", "Aprobada");
    }

    @Test
    void debeDevolverEstadosOrdenadosPorFechaDesc_cuandoEstudianteVinculado() {
        // Arrange
        UUID estudiante = persistirEstudiante("EST-001", "Juan Perez", "juan.perez@uco.edu.co");
        UUID asesor = persistirAsesor("DOC-001", "Ana Ruiz", "ana.ruiz@uco.edu.co");
        UUID ficha = persistirFicha("Proyecto A", asesor);
        persistirEstudianteFichaPerfil(ficha, estudiante);

        var t0 = Instant.now().minus(2, ChronoUnit.HOURS);
        persistirTransicion(ficha, "EN_CONSTRUCCION", t0);
        persistirTransicion(ficha, "DISPONIBLE_PARA_EVALUACION", t0.plus(1, ChronoUnit.HOURS));
        persistirTransicion(ficha, "APROBADA", t0.plus(2, ChronoUnit.HOURS));
        entityManager.flush();

        // Act
        List<EstadoFichaPerfilReadModel> resultado = adapter.consultarPorFichaYEstudiante(ficha, estudiante);

        // Assert
        assertThat(resultado)
                .extracting(EstadoFichaPerfilReadModel::id)
                .containsExactly("APROBADA", "DISPONIBLE_PARA_EVALUACION", "EN_CONSTRUCCION");
        assertThat(resultado.get(0).nombre()).isEqualTo("Aprobada");
    }

    @Test
    void debeDevolverListaVacia_cuandoEstudianteNoVinculadoALaFicha() {
        // Arrange
        UUID estudianteVinculado = persistirEstudiante("EST-010", "Juan Perez", "juan10@uco.edu.co");
        UUID otroEstudiante = persistirEstudiante("EST-011", "Maria Diaz", "maria11@uco.edu.co");
        UUID asesor = persistirAsesor("DOC-010", "Ana Ruiz", "ana10@uco.edu.co");
        UUID ficha = persistirFicha("Proyecto B", asesor);
        persistirEstudianteFichaPerfil(ficha, estudianteVinculado);
        persistirTransicion(ficha, "EN_CONSTRUCCION", Instant.now());
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.consultarPorFichaYEstudiante(ficha, otroEstudiante)).isEmpty();
    }

    @Test
    void debeDevolverListaVacia_cuandoFichaSinTransiciones() {
        // Arrange
        UUID estudiante = persistirEstudiante("EST-020", "Juan Perez", "juan20@uco.edu.co");
        UUID asesor = persistirAsesor("DOC-020", "Ana Ruiz", "ana20@uco.edu.co");
        UUID ficha = persistirFicha("Proyecto C", asesor);
        persistirEstudianteFichaPerfil(ficha, estudiante);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.consultarPorFichaYEstudiante(ficha, estudiante)).isEmpty();
    }

    @Test
    void debeDevolverSoloLasTransicionesDeLaFichaIndicada() {
        // Arrange
        UUID estudiante = persistirEstudiante("EST-030", "Juan Perez", "juan30@uco.edu.co");
        UUID asesor = persistirAsesor("DOC-030", "Ana Ruiz", "ana30@uco.edu.co");
        UUID fichaPedida = persistirFicha("Proyecto D", asesor);
        UUID otraFicha = persistirFicha("Proyecto E", asesor);
        persistirEstudianteFichaPerfil(fichaPedida, estudiante);
        persistirEstudianteFichaPerfil(otraFicha, estudiante);
        persistirTransicion(fichaPedida, "EN_CONSTRUCCION", Instant.now());
        persistirTransicion(otraFicha, "APROBADA", Instant.now());
        entityManager.flush();

        // Act
        List<EstadoFichaPerfilReadModel> resultado = adapter.consultarPorFichaYEstudiante(fichaPedida, estudiante);

        // Assert
        assertThat(resultado).singleElement().satisfies(estado ->
                assertThat(estado.id()).isEqualTo("EN_CONSTRUCCION"));
    }

    private void persistirEstadoFicha(String id, String nombre) {
        entityManager.persist(EstadoFichaJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion("Descripcion de " + nombre)
                .build());
    }

    private UUID persistirEstudiante(String identificador, String nombre, String email) {
        EstudianteJpaEntity estudiante = EstudianteJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .ocurridoEn(Instant.now())
                .build();
        entityManager.persist(estudiante);
        return estudiante.getId();
    }

    private void persistirEstudianteFichaPerfil(UUID fichaId, UUID estudianteId) {
        entityManager.persist(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaId)
                .estudianteId(estudianteId)
                .build());
    }

    private UUID persistirAsesor(String identificador, String nombre, String email) {
        AsesorFichaJpaEntity asesor = AsesorFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .build();
        entityManager.persist(asesor);
        return asesor.getId();
    }

    private UUID persistirFicha(String titulo, UUID asesorId) {
        AsesorFichaJpaEntity asesorRef = entityManager.getEntityManager()
                .getReference(AsesorFichaJpaEntity.class, asesorId);
        FichaPerfilJpaEntity ficha = FichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto(titulo)
                .asesorFicha(asesorRef)
                .build();
        entityManager.persist(ficha);
        return ficha.getId();
    }

    private void persistirTransicion(UUID fichaId, String estadoId, Instant fechaActualizacion) {
        EstadoFichaJpaEntity estadoRef = entityManager.getEntityManager()
                .getReference(EstadoFichaJpaEntity.class, estadoId);
        entityManager.persist(EstadoFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaId)
                .estadoFicha(estadoRef)
                .fechaActualizacion(fechaActualizacion)
                .build());
    }
}
