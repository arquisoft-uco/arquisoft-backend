package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionAsesorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionEstudianteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.entity.ObservacionEvaluacionJpaEntity;
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
class ObservacionEvaluacionQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObservacionEvaluacionEstudianteQueryRepository repository;

    @Autowired
    private ObservacionEvaluacionAsesorQueryRepository asesorRepository;

    private ObservacionEvaluacionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ObservacionEvaluacionQueryOutputAdapter(repository, asesorRepository);
    }

    @Test
    void debeDevolverObservacionesOrdenadas_cuandoElEstudianteEstaVinculadoALaFicha() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        persistirVinculo(ficha, estudiante);
        var evaluacion = persistirEvaluacion(ficha);
        var revisarMetodologia = persistirObservacion(evaluacion.getId(), "Revisar la metodología");
        var marcoTeorico = persistirObservacion(evaluacion.getId(), "El marco teórico es insuficiente");
        var alcance = persistirObservacion(evaluacion.getId(), "Falta delimitar el alcance");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYEstudiante(criteria(evaluacion.getId(), estudiante));

        // Assert
        assertThat(resultado)
                .extracting(ObservacionEvaluacionReadModel::id)
                .containsExactly(marcoTeorico, alcance, revisarMetodologia);
        assertThat(resultado.getFirst()).isEqualTo(new ObservacionEvaluacionReadModel(
                marcoTeorico, evaluacion.getId(), "El marco teórico es insuficiente"));
    }

    @Test
    void debeDevolverListaVacia_cuandoElEstudianteNoEstaVinculadoALaFicha() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var vinculado = UtilUUID.generarNuevoUUID();
        var ajeno = UtilUUID.generarNuevoUUID();
        persistirVinculo(ficha, vinculado);
        persistirVinculo(UtilUUID.generarNuevoUUID(), ajeno);
        var evaluacion = persistirEvaluacion(ficha);
        persistirObservacion(evaluacion.getId(), "El marco teórico es insuficiente");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYEstudiante(criteria(evaluacion.getId(), ajeno));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDevolverListaVacia_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        persistirVinculo(ficha, estudiante);
        var evaluacion = persistirEvaluacion(ficha);
        persistirObservacion(evaluacion.getId(), "El marco teórico es insuficiente");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYEstudiante(
                criteria(UtilUUID.generarNuevoUUID(), estudiante));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeIgnorarObservacionesDeOtraEvaluacion_cuandoLaFichaTieneVarias() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        persistirVinculo(ficha, estudiante);
        var pedida = persistirEvaluacion(ficha);
        var otra = persistirEvaluacion(ficha);
        var buscada = persistirObservacion(pedida.getId(), "Falta delimitar el alcance");
        persistirObservacion(otra.getId(), "Observación de otra evaluación");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYEstudiante(criteria(pedida.getId(), estudiante));

        // Assert
        assertThat(resultado).extracting(ObservacionEvaluacionReadModel::id).containsExactly(buscada);
    }

    @Test
    void debeDevolverCadaObservacionUnaVez_cuandoLaFichaTieneVariosEstudiantes() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var estudianteUno = UtilUUID.generarNuevoUUID();
        var estudianteDos = UtilUUID.generarNuevoUUID();
        persistirVinculo(ficha, estudianteUno);
        persistirVinculo(ficha, estudianteDos);
        var evaluacion = persistirEvaluacion(ficha);
        var compartida = persistirObservacion(evaluacion.getId(), "Observación compartida");
        sincronizar();

        // Act
        var paraUno = adapter.consultarPorEvaluacionYEstudiante(criteria(evaluacion.getId(), estudianteUno));
        var paraDos = adapter.consultarPorEvaluacionYEstudiante(criteria(evaluacion.getId(), estudianteDos));

        // Assert
        assertThat(paraUno).extracting(ObservacionEvaluacionReadModel::id).containsExactly(compartida);
        assertThat(paraDos).extracting(ObservacionEvaluacionReadModel::id).containsExactly(compartida);
    }

    @Test
    void debeIncluirObservaciones_cuandoLaEvaluacionEstaDescartada() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        persistirVinculo(ficha, estudiante);
        var evaluacion = persistirEvaluacion(ficha);
        var descartada = entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id("DESCARTADA").nombre("Descartada").descripcion("La evaluación fue descartada").build());
        entityManager.persist(EstadoEvaluacionFichaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfil(evaluacion)
                .estadoEvaluacion(descartada)
                .fechaActualizacion(Instant.now())
                .build());
        var observacion = persistirObservacion(evaluacion.getId(), "El marco teórico es insuficiente");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYEstudiante(criteria(evaluacion.getId(), estudiante));

        // Assert
        assertThat(resultado).extracting(ObservacionEvaluacionReadModel::id).containsExactly(observacion);
    }

    @Test
    void debeDevolverObservacionesOrdenadas_cuandoElAsesorEsElDeLaFicha() {
        // Arrange
        var asesor = persistirAsesor();
        var ficha = persistirFicha(asesor);
        var evaluacion = persistirEvaluacion(ficha);
        var revisarMetodologia = persistirObservacion(evaluacion.getId(), "Revisar la metodología");
        var marcoTeorico = persistirObservacion(evaluacion.getId(), "El marco teórico es insuficiente");
        var alcance = persistirObservacion(evaluacion.getId(), "Falta delimitar el alcance");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYAsesorFicha(criteriaAsesor(evaluacion.getId(), asesor));

        // Assert
        assertThat(resultado)
                .extracting(ObservacionEvaluacionReadModel::id)
                .containsExactly(marcoTeorico, alcance, revisarMetodologia);
        assertThat(resultado.getFirst()).isEqualTo(new ObservacionEvaluacionReadModel(
                marcoTeorico, evaluacion.getId(), "El marco teórico es insuficiente"));
    }

    @Test
    void debeDevolverListaVacia_cuandoLaFichaEsDeOtroAsesor() {
        // Arrange
        var asesorDeLaFicha = persistirAsesor();
        var otroAsesor = persistirAsesor();
        var ficha = persistirFicha(asesorDeLaFicha);
        persistirFicha(otroAsesor);
        var evaluacion = persistirEvaluacion(ficha);
        persistirObservacion(evaluacion.getId(), "El marco teórico es insuficiente");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYAsesorFicha(criteriaAsesor(evaluacion.getId(), otroAsesor));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDevolverListaVacia_cuandoLaEvaluacionDelAsesorNoExiste() {
        // Arrange
        var asesor = persistirAsesor();
        var ficha = persistirFicha(asesor);
        var evaluacion = persistirEvaluacion(ficha);
        persistirObservacion(evaluacion.getId(), "El marco teórico es insuficiente");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYAsesorFicha(
                criteriaAsesor(UtilUUID.generarNuevoUUID(), asesor));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeIgnorarObservacionesDeOtraEvaluacion_cuandoLaFichaDelAsesorTieneVarias() {
        // Arrange
        var asesor = persistirAsesor();
        var ficha = persistirFicha(asesor);
        var pedida = persistirEvaluacion(ficha);
        var otra = persistirEvaluacion(ficha);
        var buscada = persistirObservacion(pedida.getId(), "Falta delimitar el alcance");
        persistirObservacion(otra.getId(), "Observación de otra evaluación");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYAsesorFicha(criteriaAsesor(pedida.getId(), asesor));

        // Assert
        assertThat(resultado).extracting(ObservacionEvaluacionReadModel::id).containsExactly(buscada);
    }

    @Test
    void debeDevolverListaVacia_cuandoLaEvaluacionDelAsesorNoTieneObservaciones() {
        // Arrange
        var asesor = persistirAsesor();
        var ficha = persistirFicha(asesor);
        var sinObservaciones = persistirEvaluacion(ficha);
        var conObservaciones = persistirEvaluacion(ficha);
        persistirObservacion(conObservaciones.getId(), "El marco teórico es insuficiente");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYAsesorFicha(
                criteriaAsesor(sinObservaciones.getId(), asesor));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeIncluirObservaciones_cuandoLaEvaluacionDelAsesorEstaDescartada() {
        // Arrange
        var asesor = persistirAsesor();
        var ficha = persistirFicha(asesor);
        var evaluacion = persistirEvaluacion(ficha);
        var descartada = entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id("DESCARTADA").nombre("Descartada").descripcion("La evaluación fue descartada").build());
        entityManager.persist(EstadoEvaluacionFichaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfil(evaluacion)
                .estadoEvaluacion(descartada)
                .fechaActualizacion(Instant.now())
                .build());
        var observacion = persistirObservacion(evaluacion.getId(), "El marco teórico es insuficiente");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYAsesorFicha(criteriaAsesor(evaluacion.getId(), asesor));

        // Assert
        assertThat(resultado).extracting(ObservacionEvaluacionReadModel::id).containsExactly(observacion);
    }

    private static ObservacionEvaluacionAsesorCriteria criteriaAsesor(UUID evaluacionFichaPerfil, UUID asesorFicha) {
        return new ObservacionEvaluacionAsesorCriteria(evaluacionFichaPerfil, asesorFicha);
    }

    private UUID persistirAsesor() {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(AsesorFichaJpaEntity.builder()
                .id(id)
                .identificador(id.toString().substring(0, 8))
                .nombre("Asesor de prueba")
                .email("asesor." + id.toString().substring(0, 8) + "@uco.edu.co")
                .ocurridoEn(Instant.now())
                .build());
        return id;
    }

    private UUID persistirFicha(UUID asesorFichaId) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(FichaPerfilJpaEntity.builder()
                .id(id)
                .tituloProyecto("Proyecto " + id)
                .asesorFicha(entityManager.find(AsesorFichaJpaEntity.class, asesorFichaId))
                .build());
        return id;
    }

    private static ObservacionEvaluacionEstudianteCriteria criteria(UUID evaluacionFichaPerfil, UUID estudiante) {
        return new ObservacionEvaluacionEstudianteCriteria(evaluacionFichaPerfil, estudiante);
    }

    private void persistirVinculo(UUID fichaPerfilId, UUID estudianteId) {
        entityManager.persist(EstudianteFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(fichaPerfilId)
                .estudianteId(estudianteId)
                .build());
    }

    private EvaluacionFichaPerfilJpaEntity persistirEvaluacion(UUID fichaPerfilId) {
        return entityManager.persist(EvaluacionFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .representanteComiteId(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(fichaPerfilId)
                .fechaCreacion(Instant.now())
                .build());
    }

    private UUID persistirObservacion(UUID evaluacionFichaPerfilId, String texto) {
        return entityManager.persist(ObservacionEvaluacionJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfilId(evaluacionFichaPerfilId)
                .observacion(texto)
                .build()).getId();
    }

    private void sincronizar() {
        entityManager.flush();
        entityManager.clear();
    }
}
