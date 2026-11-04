package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// El CREATE TABLE IF NOT EXISTS se comparte en el mismo H2 con las demas pruebas de evaluaciones: la columna
// proyecto_grado_id se agrega con DEFAULT para no romper a quien siembra entregable sin ella.
// @DirtiesContext: el DDL nativo hace commit implicito y rompe el rollback entre clases.
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EvaluacionEstudianteQueryOutputAdapterTest {

    private static final UUID SIN_PROYECTO = UtilUUID.obtenerUUIDPorDefecto();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionEstudianteQueryRepository repository;

    private EvaluacionEstudianteQueryOutputAdapter adapter;

    private String sufijo;

    private UUID estudiante;

    private UUID proyectoGrado;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionEstudianteQueryOutputAdapter(repository, new EvaluacionEstudianteJpaSpecification());
        sufijo = UtilUUID.generarNuevoUUID().toString();
        estudiante = UtilUUID.generarNuevoUUID();
        proyectoGrado = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS entregable (
                    id UUID NOT NULL, proyecto VARCHAR(200) NOT NULL,
                    version_entregable INTEGER NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                ALTER TABLE entregable ADD COLUMN IF NOT EXISTS proyecto_grado_id UUID
                    DEFAULT '00000000-0000-0000-0000-000000000000' NOT NULL
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS estado_evaluacion (
                    id VARCHAR(60) NOT NULL, nombre VARCHAR(60) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion (
                    id UUID NOT NULL, entregable_id UUID NOT NULL,
                    estado_evaluacion_id VARCHAR(60) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        sembrarEstado("PENDIENTE", "Pendiente");
        sembrarEstado("EN_PROGRESO", "En progreso");
        sembrarEstado("FINALIZADA", "Finalizada");
        sembrarVinculo(estudiante, proyectoGrado, null);
        entityManager.flush();
    }

    private void sembrarEstado(String id, String nombre) {
        entityManager.createNativeQuery(
                        "MERGE INTO estado_evaluacion (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, nombre)
                .setParameter(3, nombre)
                .executeUpdate();
    }

    private void sembrarVinculo(UUID estudianteVinculado, UUID proyecto, Instant eliminadoEn) {
        entityManager.createNativeQuery("""
                        INSERT INTO estudiante_proyecto_grado
                            (id, estudiante_id, proyecto_grado_id, ocurrido_en, eliminado_en)
                        VALUES (?, ?, ?, ?, ?)
                        """)
                .setParameter(1, UtilUUID.generarNuevoUUID())
                .setParameter(2, estudianteVinculado)
                .setParameter(3, proyecto)
                .setParameter(4, Timestamp.from(Instant.parse("2026-10-20T10:00:00Z")))
                .setParameter(5, eliminadoEn == null ? null : Timestamp.from(eliminadoEn))
                .executeUpdate();
    }

    private UUID sembrarEvaluacion(UUID entregable, String proyecto, int version, String estado, UUID proyectoDelEntregable) {
        entityManager.createNativeQuery("""
                        INSERT INTO entregable (id, proyecto, version_entregable, proyecto_grado_id)
                        VALUES (?, ?, ?, ?)
                        """)
                .setParameter(1, entregable)
                .setParameter(2, proyecto + " " + sufijo)
                .setParameter(3, version)
                .setParameter(4, proyectoDelEntregable)
                .executeUpdate();
        var evaluacion = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacion)
                .setParameter(2, entregable)
                .setParameter(3, estado)
                .executeUpdate();
        entityManager.flush();
        return evaluacion;
    }

    private UUID sembrarEvaluacionDelProyecto(String proyecto, String estado) {
        return sembrarEvaluacion(UtilUUID.generarNuevoUUID(), proyecto, 1, estado, proyectoGrado);
    }

    private NodoFiltro delEstudiante(UUID id) {
        return NodoFiltro.predicado("estudianteId", FiltroOperador.ES, id.toString());
    }

    private NodoFiltro delEstudianteEnEstaPrueba(UUID id, NodoFiltro... adicionales) {
        var hijos = new ArrayList<NodoFiltro>();
        hijos.add(delEstudiante(id));
        hijos.add(NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, sufijo));
        hijos.addAll(List.of(adicionales));
        return NodoFiltro.grupo(FiltroConector.AND, hijos);
    }

    private EvaluacionEstudianteCriteria criteria(UUID id, NodoFiltro... adicionales) {
        return EvaluacionEstudianteCriteria.builder().raiz(delEstudianteEnEstaPrueba(id, adicionales)).build();
    }

    @Test
    void debeMapearEntregableYEstado_yRetornarSoloLasEvaluacionesDelProyectoDelEstudiante() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var propia = sembrarEvaluacion(entregable, "Robot seguidor", 2, "EN_PROGRESO", proyectoGrado);
        sembrarEvaluacion(UtilUUID.generarNuevoUUID(), "Ajena", 1, "PENDIENTE", UtilUUID.generarNuevoUUID());

        // Act
        var resultado = adapter.consultarPorEstudiante(criteria(estudiante));

        // Assert
        assertThat(resultado.getContent()).containsExactly(new EvaluacionReadModel(
                propia,
                new EvaluacionReadModel.Entregable(entregable, "Robot seguidor " + sufijo, 2),
                new EvaluacionReadModel.Estado("EN_PROGRESO", "En progreso")));
        assertThat(resultado.getTotalElements()).isEqualTo(1);
    }

    @Test
    void debeMostrarLasMismasEvaluaciones_cuandoDosEstudiantesComparten_elProyecto() {
        // Arrange
        var companero = UtilUUID.generarNuevoUUID();
        sembrarVinculo(companero, proyectoGrado, null);
        var evaluacion = sembrarEvaluacionDelProyecto("Alfa", "PENDIENTE");

        // Act
        var delPrimero = adapter.consultarPorEstudiante(criteria(estudiante));
        var delSegundo = adapter.consultarPorEstudiante(criteria(companero));

        // Assert
        assertThat(delPrimero.getContent()).extracting(EvaluacionReadModel::id).containsExactly(evaluacion);
        assertThat(delSegundo.getContent()).extracting(EvaluacionReadModel::id).containsExactly(evaluacion);
        assertThat(delPrimero.getTotalElements()).isEqualTo(1);
    }

    @Test
    void debeMostrarAmbosProyectos_cuandoElEstudianteTieneDosVinculos() {
        // Arrange
        var otroProyecto = UtilUUID.generarNuevoUUID();
        sembrarVinculo(estudiante, otroProyecto, null);
        var primera = sembrarEvaluacionDelProyecto("Alfa", "PENDIENTE");
        var segunda = sembrarEvaluacion(UtilUUID.generarNuevoUUID(), "Beta", 1, "PENDIENTE", otroProyecto);

        // Act
        var resultado = adapter.consultarPorEstudiante(criteria(estudiante));

        // Assert
        assertThat(resultado.getContent()).extracting(EvaluacionReadModel::id)
                .containsExactlyInAnyOrder(primera, segunda);
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElEstudianteNoTieneVinculo() {
        // Arrange
        sembrarEvaluacionDelProyecto("Alfa", "PENDIENTE");

        // Act
        var resultado = adapter.consultarPorEstudiante(criteria(UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado.isEmpty()).isTrue();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElVinculoEstaDadoDeBaja() {
        // Arrange
        var dadoDeBaja = UtilUUID.generarNuevoUUID();
        sembrarVinculo(dadoDeBaja, proyectoGrado, Instant.parse("2026-10-21T10:00:00Z"));
        sembrarEvaluacionDelProyecto("Alfa", "PENDIENTE");

        // Act
        var resultado = adapter.consultarPorEstudiante(criteria(dadoDeBaja));

        // Assert
        assertThat(resultado.isEmpty()).isTrue();
    }

    @Test
    void debeDejarDeVerElProyecto_cuandoSeDaDeBajaElVinculo() {
        // Arrange
        sembrarEvaluacionDelProyecto("Alfa", "PENDIENTE");
        var antes = adapter.consultarPorEstudiante(criteria(estudiante));

        // Act
        entityManager.createNativeQuery("""
                        UPDATE estudiante_proyecto_grado SET eliminado_en = ?, ocurrido_en = ?
                        WHERE estudiante_id = ? AND proyecto_grado_id = ?
                        """)
                .setParameter(1, Timestamp.from(Instant.parse("2026-10-21T10:00:00Z")))
                .setParameter(2, Timestamp.from(Instant.parse("2026-10-21T10:00:00Z")))
                .setParameter(3, estudiante)
                .setParameter(4, proyectoGrado)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        var despues = adapter.consultarPorEstudiante(criteria(estudiante));

        // Assert
        assertThat(antes.getTotalElements()).isEqualTo(1);
        assertThat(despues.isEmpty()).isTrue();
    }

    @Test
    void debeOcultarElEntregable_cuandoPerteneceAlProyectoCentinela() {
        // Arrange
        sembrarEvaluacion(UtilUUID.generarNuevoUUID(), "Huerfano", 1, "PENDIENTE", SIN_PROYECTO);
        var visible = sembrarEvaluacionDelProyecto("Visible", "PENDIENTE");

        // Act
        var resultado = adapter.consultarPorEstudiante(criteria(estudiante));

        // Assert
        assertThat(resultado.getContent()).extracting(EvaluacionReadModel::id).containsExactly(visible);
        assertThat(resultado.getContent()).extracting(modelo -> modelo.entregable().proyecto())
                .doesNotContain("Huerfano " + sufijo);
    }

    @Test
    void debeFiltrarPorProyectoGradoId_ySoloReducirElResultado() {
        // Arrange
        var evaluacion = sembrarEvaluacionDelProyecto("Alfa", "PENDIENTE");
        var propio = NodoFiltro.predicado("proyectoGradoId", FiltroOperador.ES, proyectoGrado.toString());
        var ajeno = NodoFiltro.predicado("proyectoGradoId", FiltroOperador.ES, UtilUUID.generarNuevoUUID().toString());

        // Act
        var delPropio = adapter.consultarPorEstudiante(criteria(estudiante, propio));
        var delAjeno = adapter.consultarPorEstudiante(criteria(estudiante, ajeno));

        // Assert
        assertThat(delPropio.getContent()).extracting(EvaluacionReadModel::id).containsExactly(evaluacion);
        assertThat(delAjeno.isEmpty()).isTrue();
    }

    @Test
    void debeNoAmpliarElResultado_cuandoElClienteFiltraPorUnEstudianteOProyectoAjeno() {
        // Arrange
        var ajeno = UtilUUID.generarNuevoUUID();
        var proyectoAjeno = UtilUUID.generarNuevoUUID();
        sembrarVinculo(ajeno, proyectoAjeno, null);
        var propia = sembrarEvaluacionDelProyecto("Propia", "PENDIENTE");
        sembrarEvaluacion(UtilUUID.generarNuevoUUID(), "Ajena", 1, "PENDIENTE", proyectoAjeno);
        var porEstudianteAjeno = NodoFiltro.predicado("estudianteId", FiltroOperador.ES, ajeno.toString());
        var oAjeno = NodoFiltro.grupo(FiltroConector.OR, List.of(
                NodoFiltro.predicado("estudianteId", FiltroOperador.ES, ajeno.toString()),
                NodoFiltro.predicado("proyectoGradoId", FiltroOperador.ES, proyectoAjeno.toString())));

        // Act
        var soloAjeno = adapter.consultarPorEstudiante(criteria(estudiante, porEstudianteAjeno));
        var conOr = adapter.consultarPorEstudiante(criteria(estudiante, oAjeno));

        // Assert
        assertThat(soloAjeno.isEmpty()).isTrue();
        assertThat(conOr.isEmpty()).isTrue();
        assertThat(adapter.consultarPorEstudiante(criteria(estudiante)).getContent())
                .extracting(EvaluacionReadModel::id).containsExactly(propia);
    }

    @Test
    void debeRetornarCadaEvaluacionUnaVez_cuandoElProyectoTieneTresEstudiantes() {
        // Arrange
        sembrarVinculo(UtilUUID.generarNuevoUUID(), proyectoGrado, null);
        sembrarVinculo(UtilUUID.generarNuevoUUID(), proyectoGrado, null);
        sembrarEvaluacionDelProyecto("Alfa", "PENDIENTE");
        sembrarEvaluacionDelProyecto("Beta", "PENDIENTE");

        // Act
        var resultado = adapter.consultarPorEstudiante(criteria(estudiante));

        // Assert
        assertThat(resultado.getContent()).hasSize(2).doesNotHaveDuplicates();
        assertThat(resultado.getTotalElements()).isEqualTo(2);
    }

    @Test
    void debeFiltrarPorEstadoProyectoYEntregableId_dentroDeLasEvaluacionesDelEstudiante() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var objetivo = sembrarEvaluacion(entregable, "Brazo robotico", 1, "PENDIENTE", proyectoGrado);
        sembrarEvaluacionDelProyecto("Sistema contable", "FINALIZADA");

        // Act
        var porEstado = adapter.consultarPorEstudiante(criteria(estudiante, NodoFiltro.predicadoMultivalor(
                "estado", FiltroOperador.IN, List.of("PENDIENTE", "EN_PROGRESO"))));
        var porProyecto = adapter.consultarPorEstudiante(criteria(estudiante,
                NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, "robot")));
        var porEntregable = adapter.consultarPorEstudiante(criteria(estudiante,
                NodoFiltro.predicado("entregableId", FiltroOperador.ES, entregable.toString())));

        // Assert
        assertThat(porEstado.getContent()).extracting(EvaluacionReadModel::id).containsExactly(objetivo);
        assertThat(porProyecto.getContent()).extracting(EvaluacionReadModel::id).containsExactly(objetivo);
        assertThat(porEntregable.getContent()).extracting(EvaluacionReadModel::id).containsExactly(objetivo);
    }

    @Test
    void debeOrdenarPorProyectoYPorEstado() {
        // Arrange
        sembrarEvaluacionDelProyecto("Alfa", "EN_PROGRESO");
        sembrarEvaluacionDelProyecto("Beta", "FINALIZADA");
        sembrarEvaluacionDelProyecto("Gamma", "PENDIENTE");

        // Act
        var porProyecto = adapter.consultarPorEstudiante(EvaluacionEstudianteCriteria.builder()
                .raiz(delEstudianteEnEstaPrueba(estudiante))
                .ordenamiento(List.of(SortOrder.of("proyecto", SortDirection.DESC)))
                .build());
        var porEstado = adapter.consultarPorEstudiante(EvaluacionEstudianteCriteria.builder()
                .raiz(delEstudianteEnEstaPrueba(estudiante))
                .ordenamiento(List.of(SortOrder.of("estado", SortDirection.DESC)))
                .build());

        // Assert
        assertThat(porProyecto.getContent())
                .extracting(modelo -> modelo.entregable().proyecto())
                .containsExactly("Gamma " + sufijo, "Beta " + sufijo, "Alfa " + sufijo);
        assertThat(porEstado.getContent())
                .extracting(modelo -> modelo.estado().nombre())
                .containsExactly("Pendiente", "Finalizada", "En progreso");
    }

    @Test
    void debePaginar_conElTotalDeLasCoincidencias() {
        // Arrange
        sembrarEvaluacionDelProyecto("Alfa", "PENDIENTE");
        sembrarEvaluacionDelProyecto("Beta", "PENDIENTE");
        sembrarEvaluacionDelProyecto("Gamma", "PENDIENTE");

        // Act
        var resultado = adapter.consultarPorEstudiante(EvaluacionEstudianteCriteria.builder()
                .raiz(delEstudianteEnEstaPrueba(estudiante))
                .pagina(1)
                .tamanio(2)
                .ordenamiento(List.of(SortOrder.of("proyecto", SortDirection.ASC)))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(modelo -> modelo.entregable().proyecto())
                .containsExactly("Gamma " + sufijo);
        assertThat(resultado.getTotalElements()).isEqualTo(3);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
    }

    @Test
    void debeLanzarFiltroInvalidoException_cuandoProyectoGradoIdNoEsUnUuid() {
        // Arrange
        var criteria = EvaluacionEstudianteCriteria.builder()
                .raiz(NodoFiltro.predicado("proyectoGradoId", FiltroOperador.ES, "no-es-un-uuid"))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultarPorEstudiante(criteria))
                .isInstanceOf(FiltroInvalidoException.class);
    }
}
