package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilCoordinadorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.entity.RepresentanteComiteJpaEntity;
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
class EvaluacionFichaPerfilQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EvaluacionFichaPerfilQueryRepository repository;

    @Autowired
    private EvaluacionFichaPerfilEstudianteQueryRepository estudianteRepository;

    @Autowired
    private EvaluacionFichaPerfilCoordinadorQueryRepository coordinadorRepository;

    private EvaluacionFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionFichaPerfilQueryOutputAdapter(repository, estudianteRepository, coordinadorRepository);
        persistirEstado("EN_EVALUACION", "En Evaluación");
        persistirEstado("APROBADA", "Aprobada");
    }

    @Test
    void debeRetornarTodasLasEvaluacionesDeLaFicha_ordenadasPorFechaCreacionAscendente() {
        // Arrange
        var ficha = UUID.randomUUID();
        var representante = persistirRepresentante("María Gómez", null);
        var base = Instant.now().minus(10, ChronoUnit.DAYS);
        var reciente = persistirEvaluacion(ficha, representante, base.plus(2, ChronoUnit.DAYS));
        var antigua = persistirEvaluacion(ficha, representante, base);
        var media = persistirEvaluacion(ficha, representante, base.plus(1, ChronoUnit.DAYS));
        persistirTrazabilidad(antigua, "EN_EVALUACION", base);
        persistirTrazabilidad(antigua, "APROBADA", base.plus(1, ChronoUnit.HOURS));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado).extracting(EvaluacionFichaPerfilCoordinadorReadModel::id)
                .containsExactly(antigua, media, reciente);
        assertThat(resultado.get(0)).satisfies(e -> {
            assertThat(e.fichaPerfil()).isEqualTo(ficha);
            assertThat(e.fechaCreacion()).isNotNull();
            assertThat(e.estadoEvaluacion()).isEqualTo("APROBADA");
            assertThat(e.estadoEvaluacionNombre()).isEqualTo("Aprobada");
            assertThat(e.representanteComite().id()).isEqualTo(representante);
            assertThat(e.representanteComite().nombre()).isEqualTo("María Gómez");
        });
    }

    @Test
    void debeIncluirEvaluacionEnEvaluacionYDescartada_conSuEstado() {
        // Arrange
        persistirEstado("DESCARTADA", "Descartada");
        var ficha = UUID.randomUUID();
        var representante = persistirRepresentante("María Gómez", null);
        var base = Instant.now().minus(2, ChronoUnit.HOURS);
        var enEvaluacion = persistirEvaluacion(ficha, representante, base);
        var descartada = persistirEvaluacion(ficha, representante, base.plus(1, ChronoUnit.MINUTES));
        persistirTrazabilidad(enEvaluacion, "EN_EVALUACION", base);
        persistirTrazabilidad(descartada, "EN_EVALUACION", base);
        persistirTrazabilidad(descartada, "DESCARTADA", base.plus(1, ChronoUnit.HOURS));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado).extracting(EvaluacionFichaPerfilCoordinadorReadModel::estadoEvaluacion)
                .containsExactly("EN_EVALUACION", "DESCARTADA");
        assertThat(resultado.get(1).estadoEvaluacionNombre()).isEqualTo("Descartada");
    }

    @Test
    void debeIncluirEvaluacion_cuandoRepresentanteDadoDeBajaYSinEstados() {
        // Arrange
        var ficha = UUID.randomUUID();
        var representante = persistirRepresentante("Carlos Ruiz", Instant.now());
        var evaluacion = persistirEvaluacion(ficha, representante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado).singleElement().satisfies(e -> {
            assertThat(e.id()).isEqualTo(evaluacion);
            assertThat(e.representanteComite().nombre()).isEqualTo("Carlos Ruiz");
            assertThat(e.estadoEvaluacion()).isNull();
            assertThat(e.estadoEvaluacionNombre()).isNull();
        });
    }

    @Test
    void debeNoMezclarEvaluaciones_deOtraFicha() {
        // Arrange
        var ficha = UUID.randomUUID();
        var representante = persistirRepresentante("María Gómez", null);
        var propia = persistirEvaluacion(ficha, representante, Instant.now());
        persistirEvaluacion(UUID.randomUUID(), representante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado).extracting(EvaluacionFichaPerfilCoordinadorReadModel::id)
                .containsExactly(propia);
    }

    @Test
    void debeRetornarListaVacia_cuandoFichaNoExisteONoTieneEvaluaciones() {
        // Act
        var resultado = adapter.consultarPorFicha(UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDevolverEvaluacionConUltimoEstado_cuandoHayVariasFilasDeTrazabilidad() {
        // Arrange
        var ficha = UUID.randomUUID();
        var representante = UUID.randomUUID();
        var evaluacionId = persistirEvaluacion(ficha, representante, Instant.now());
        var base = Instant.now().minus(2, ChronoUnit.HOURS);
        persistirTrazabilidad(evaluacionId, "EN_EVALUACION", base);
        persistirTrazabilidad(evaluacionId, "APROBADA", base.plus(1, ChronoUnit.HOURS));
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionFichaPerfilReadModel> resultado =
                adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).singleElement().satisfies(e -> {
            assertThat(e.id()).isEqualTo(evaluacionId);
            assertThat(e.fichaPerfilId()).isEqualTo(ficha);
            assertThat(e.fechaCreacion()).isNotNull();
            assertThat(e.estadoEvaluacion()).isEqualTo("APROBADA");
            assertThat(e.estadoEvaluacionNombre()).isEqualTo("Aprobada");
        });
    }

    @Test
    void debeDevolverEstadoNulo_cuandoLaEvaluacionNoTieneTrazabilidad() {
        // Arrange
        var ficha = UUID.randomUUID();
        var representante = UUID.randomUUID();
        var evaluacionId = persistirEvaluacion(ficha, representante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionFichaPerfilReadModel> resultado =
                adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).singleElement().satisfies(e -> {
            assertThat(e.id()).isEqualTo(evaluacionId);
            assertThat(e.estadoEvaluacion()).isNull();
            assertThat(e.estadoEvaluacionNombre()).isNull();
        });
    }

    @Test
    void debeFiltrarPorRepresentante_ignorandoEvaluacionesDeOtro() {
        // Arrange
        var ficha = UUID.randomUUID();
        var representante = UUID.randomUUID();
        var otroRepresentante = UUID.randomUUID();
        persistirEvaluacion(ficha, representante, Instant.now());
        persistirEvaluacion(ficha, otroRepresentante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionFichaPerfilReadModel> resultado =
                adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).singleElement()
                .satisfies(e -> assertThat(e.fichaPerfilId()).isEqualTo(ficha));
    }

    @Test
    void debeFiltrarPorFicha_ignorandoOtrasFichasDelMismoRepresentante() {
        // Arrange
        var fichaPedida = UUID.randomUUID();
        var otraFicha = UUID.randomUUID();
        var representante = UUID.randomUUID();
        var evaluacionPedida = persistirEvaluacion(fichaPedida, representante, Instant.now());
        persistirEvaluacion(otraFicha, representante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionFichaPerfilReadModel> resultado =
                adapter.consultarPorFichaYRepresentante(fichaPedida, representante);

        // Assert
        assertThat(resultado).singleElement()
                .satisfies(e -> assertThat(e.id()).isEqualTo(evaluacionPedida));
    }

    @Test
    void debeDevolverListaVacia_cuandoNoHayEvaluacionesPropiasEnEsaFicha() {
        // Act & Assert
        assertThat(adapter.consultarPorFichaYRepresentante(UUID.randomUUID(), UUID.randomUUID()))
                .isEmpty();
    }

    @Test
    void debeRetornarEvaluacionesConEstadoActualYRepresentante_cuandoEstudianteVinculado() {
        // Arrange
        var ficha = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var companero = UUID.randomUUID();
        persistirVinculo(ficha, estudiante);
        persistirVinculo(ficha, companero);
        var representante = persistirRepresentante("María Gómez", null);
        var base = Instant.now().minus(10, ChronoUnit.DAYS);
        var evaluacionReciente = persistirEvaluacion(ficha, representante, base.plus(2, ChronoUnit.DAYS));
        var evaluacionAntigua = persistirEvaluacion(ficha, representante, base);
        persistirTrazabilidad(evaluacionAntigua, "EN_EVALUACION", base);
        persistirTrazabilidad(evaluacionAntigua, "APROBADA", base.plus(1, ChronoUnit.HOURS));
        persistirTrazabilidad(evaluacionReciente, "EN_EVALUACION", base.plus(2, ChronoUnit.DAYS));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorFichaYEstudiante(ficha, estudiante);

        // Assert
        assertThat(resultado).extracting(EvaluacionFichaPerfilEstudianteReadModel::id)
                .containsExactly(evaluacionAntigua, evaluacionReciente);
        assertThat(resultado.get(0)).satisfies(e -> {
            assertThat(e.fichaPerfil()).isEqualTo(ficha);
            assertThat(e.fechaCreacion()).isNotNull();
            assertThat(e.estadoEvaluacion()).isEqualTo("APROBADA");
            assertThat(e.estadoEvaluacionNombre()).isEqualTo("Aprobada");
            assertThat(e.representanteComite().id()).isEqualTo(representante);
            assertThat(e.representanteComite().nombre()).isEqualTo("María Gómez");
        });
        assertThat(resultado.get(1).estadoEvaluacion()).isEqualTo("EN_EVALUACION");
    }

    @Test
    void debeIncluirEvaluacionDescartada_cuandoUltimoEstadoEsDescartada() {
        // Arrange
        persistirEstado("DESCARTADA", "Descartada");
        var ficha = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        persistirVinculo(ficha, estudiante);
        var representante = persistirRepresentante("María Gómez", null);
        var base = Instant.now().minus(2, ChronoUnit.HOURS);
        var evaluacion = persistirEvaluacion(ficha, representante, base);
        persistirTrazabilidad(evaluacion, "EN_EVALUACION", base);
        persistirTrazabilidad(evaluacion, "DESCARTADA", base.plus(1, ChronoUnit.HOURS));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorFichaYEstudiante(ficha, estudiante);

        // Assert
        assertThat(resultado).singleElement().satisfies(e -> {
            assertThat(e.id()).isEqualTo(evaluacion);
            assertThat(e.estadoEvaluacion()).isEqualTo("DESCARTADA");
            assertThat(e.estadoEvaluacionNombre()).isEqualTo("Descartada");
        });
    }

    @Test
    void debeRetornarListaVacia_cuandoEstudianteNoVinculadoALaFicha() {
        // Arrange
        var ficha = UUID.randomUUID();
        var otraFicha = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        persistirVinculo(ficha, UUID.randomUUID());
        persistirVinculo(otraFicha, estudiante);
        var representante = persistirRepresentante("María Gómez", null);
        persistirEvaluacion(ficha, representante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorFichaYEstudiante(ficha, estudiante);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeIncluirEvaluacion_cuandoRepresentanteDadoDeBaja() {
        // Arrange
        var ficha = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        persistirVinculo(ficha, estudiante);
        var representante = persistirRepresentante("Carlos Ruiz", Instant.now());
        var evaluacion = persistirEvaluacion(ficha, representante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorFichaYEstudiante(ficha, estudiante);

        // Assert
        assertThat(resultado).singleElement().satisfies(e -> {
            assertThat(e.id()).isEqualTo(evaluacion);
            assertThat(e.representanteComite().id()).isEqualTo(representante);
            assertThat(e.representanteComite().nombre()).isEqualTo("Carlos Ruiz");
        });
    }

    @Test
    void debeRetornarEstadoNulo_cuandoEvaluacionSinEstados() {
        // Arrange
        var ficha = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        persistirVinculo(ficha, estudiante);
        var representante = persistirRepresentante("María Gómez", null);
        var evaluacion = persistirEvaluacion(ficha, representante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorFichaYEstudiante(ficha, estudiante);

        // Assert
        assertThat(resultado).singleElement().satisfies(e -> {
            assertThat(e.id()).isEqualTo(evaluacion);
            assertThat(e.estadoEvaluacion()).isNull();
            assertThat(e.estadoEvaluacionNombre()).isNull();
        });
    }

    private void persistirVinculo(UUID fichaId, UUID estudianteId) {
        entityManager.persist(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaId)
                .estudianteId(estudianteId)
                .build());
    }

    private UUID persistirRepresentante(String nombre, Instant eliminadoEn) {
        var representante = RepresentanteComiteJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("RC-" + nombre.length())
                .nombre(nombre)
                .email("representante@uco.edu.co")
                .ocurridoEn(Instant.now())
                .eliminadoEn(eliminadoEn)
                .build();
        entityManager.persist(representante);
        return representante.getId();
    }

    private void persistirEstado(String id, String nombre) {
        entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion("Descripción de " + nombre)
                .build());
    }

    private UUID persistirEvaluacion(UUID fichaId, UUID representanteId, Instant fechaCreacion) {
        var evaluacion = EvaluacionFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaId)
                .representanteComiteId(representanteId)
                .fechaCreacion(fechaCreacion)
                .build();
        entityManager.persist(evaluacion);
        return evaluacion.getId();
    }

    private void persistirTrazabilidad(UUID evaluacionId, String estadoId, Instant fechaActualizacion) {
        var evaluacionRef = entityManager.getEntityManager()
                .getReference(EvaluacionFichaPerfilJpaEntity.class, evaluacionId);
        var estadoRef = entityManager.getEntityManager()
                .getReference(EstadoEvaluacionJpaEntity.class, estadoId);
        entityManager.persist(EstadoEvaluacionFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .evaluacionFichaPerfil(evaluacionRef)
                .estadoEvaluacion(estadoRef)
                .fechaActualizacion(fechaActualizacion)
                .build());
    }
}
