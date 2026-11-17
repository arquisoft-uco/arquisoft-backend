package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionDelJuradoCriteria;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Mismo DDL de soporte que EvaluacionQueryOutputAdapterTest para evaluacion, entregable y estado_evaluacion
// (el CREATE TABLE IF NOT EXISTS se comparte en el mismo H2); evaluacion_jurado la crea Hibernate desde su JpaEntity de comando.
// @DirtiesContext: el CREATE TABLE nativo hace commit implicito y rompe el rollback entre clases.
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EvaluacionDelJuradoQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionDelJuradoQueryRepository repository;

    private EvaluacionDelJuradoQueryOutputAdapter adapter;

    private String sufijo;

    private UUID jurado;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionDelJuradoQueryOutputAdapter(repository, new EvaluacionDelJuradoJpaSpecification());
        sufijo = UtilUUID.generarNuevoUUID().toString();
        jurado = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS entregable (
                    id UUID NOT NULL, proyecto VARCHAR(200) NOT NULL,
                    version_entregable INTEGER NOT NULL, PRIMARY KEY (id))
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

    private UUID sembrarEvaluacion(UUID entregable, String proyecto, int version, String estado, UUID dueno) {
        entityManager.createNativeQuery(
                        "INSERT INTO entregable (id, proyecto, version_entregable, proyecto_grado_id) VALUES (?, ?, ?, ?)")
                .setParameter(1, entregable)
                .setParameter(2, proyecto + " " + sufijo)
                .setParameter(3, version)
                .setParameter(4, UtilUUID.generarNuevoUUID())
                .executeUpdate();
        var evaluacion = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacion)
                .setParameter(2, entregable)
                .setParameter(3, estado)
                .executeUpdate();
        if (dueno != null) {
            asignarJurado(evaluacion, dueno);
        }
        entityManager.flush();
        return evaluacion;
    }

    private void asignarJurado(UUID evaluacion, UUID dueno) {
        entityManager.createNativeQuery("""
                        INSERT INTO evaluacion_jurado
                            (id, evaluacion_id, jurado_id, estado_evaluacion_asignada_jurado_id)
                        VALUES (?, ?, ?, ?)
                        """)
                .setParameter(1, UtilUUID.generarNuevoUUID())
                .setParameter(2, evaluacion)
                .setParameter(3, dueno)
                .setParameter(4, "PENDIENTE_POR_EVALUAR")
                .executeUpdate();
    }

    private UUID sembrarEvaluacionDelJurado(String proyecto, String estado) {
        return sembrarEvaluacion(UtilUUID.generarNuevoUUID(), proyecto, 1, estado, jurado);
    }

    private NodoFiltro delJurado(UUID id) {
        return NodoFiltro.predicado("juradoId", FiltroOperador.ES, id.toString());
    }

    private NodoFiltro delJuradoEnEstaPrueba(NodoFiltro... adicionales) {
        var hijos = new ArrayList<NodoFiltro>();
        hijos.add(delJurado(jurado));
        hijos.add(NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, sufijo));
        hijos.addAll(List.of(adicionales));
        return NodoFiltro.grupo(FiltroConector.AND, hijos);
    }

    @Test
    void debeMapearEntregableYEstado_yRetornarSoloLasEvaluacionesDelJurado() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var propia = sembrarEvaluacion(entregable, "Robot seguidor", 2, "EN_PROGRESO", jurado);
        sembrarEvaluacion(UtilUUID.generarNuevoUUID(), "Ajena", 1, "PENDIENTE", UtilUUID.generarNuevoUUID());
        sembrarEvaluacion(UtilUUID.generarNuevoUUID(), "Sin vinculo", 1, "PENDIENTE", null);

        // Act
        var resultado = adapter.consultarPorJurado(EvaluacionDelJuradoCriteria.builder()
                .raiz(delJuradoEnEstaPrueba())
                .build());

        // Assert
        assertThat(resultado.getContent()).containsExactly(new EvaluacionReadModel(
                propia,
                new EvaluacionReadModel.Entregable(entregable, "Robot seguidor " + sufijo, 2),
                new EvaluacionReadModel.Estado("EN_PROGRESO", "En progreso")));
        assertThat(resultado.getTotalElements()).isEqualTo(1);
    }

    @Test
    void debeFiltrarPorEstadoProyectoYEntregableId_dentroDeLasEvaluacionesDelJurado() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var objetivo = sembrarEvaluacion(entregable, "Brazo robotico", 1, "PENDIENTE", jurado);
        sembrarEvaluacionDelJurado("Sistema contable", "FINALIZADA");

        // Act
        var porEstado = adapter.consultarPorJurado(EvaluacionDelJuradoCriteria.builder()
                .raiz(delJuradoEnEstaPrueba(NodoFiltro.predicadoMultivalor(
                        "estado", FiltroOperador.IN, List.of("PENDIENTE", "EN_PROGRESO"))))
                .build());
        var porProyecto = adapter.consultarPorJurado(EvaluacionDelJuradoCriteria.builder()
                .raiz(delJuradoEnEstaPrueba(NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, "robot")))
                .build());
        var porEntregable = adapter.consultarPorJurado(EvaluacionDelJuradoCriteria.builder()
                .raiz(delJuradoEnEstaPrueba(
                        NodoFiltro.predicado("entregableId", FiltroOperador.ES, entregable.toString())))
                .build());

        // Assert
        assertThat(porEstado.getContent()).extracting(EvaluacionReadModel::id).containsExactly(objetivo);
        assertThat(porProyecto.getContent()).extracting(EvaluacionReadModel::id).containsExactly(objetivo);
        assertThat(porEntregable.getContent()).extracting(EvaluacionReadModel::id).containsExactly(objetivo);
    }

    @Test
    void debeOrdenarPorProyectoYPorEstado() {
        // Arrange
        sembrarEvaluacionDelJurado("Alfa", "EN_PROGRESO");
        sembrarEvaluacionDelJurado("Beta", "FINALIZADA");
        sembrarEvaluacionDelJurado("Gamma", "PENDIENTE");

        // Act
        var porProyecto = adapter.consultarPorJurado(EvaluacionDelJuradoCriteria.builder()
                .raiz(delJuradoEnEstaPrueba())
                .ordenamiento(List.of(SortOrder.of("proyecto", SortDirection.DESC)))
                .build());
        var porEstado = adapter.consultarPorJurado(EvaluacionDelJuradoCriteria.builder()
                .raiz(delJuradoEnEstaPrueba())
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
        sembrarEvaluacionDelJurado("Alfa", "PENDIENTE");
        sembrarEvaluacionDelJurado("Beta", "PENDIENTE");
        sembrarEvaluacionDelJurado("Gamma", "PENDIENTE");

        // Act
        var resultado = adapter.consultarPorJurado(EvaluacionDelJuradoCriteria.builder()
                .raiz(delJuradoEnEstaPrueba())
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
    void debeRetornarPaginaVacia_cuandoElFiltroDeJuradoEsAjeno() {
        // Arrange
        sembrarEvaluacionDelJurado("Alfa", "PENDIENTE");
        var ajeno = UtilUUID.generarNuevoUUID();

        // Act
        var resultado = adapter.consultarPorJurado(EvaluacionDelJuradoCriteria.builder()
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(delJurado(jurado), delJurado(ajeno))))
                .build());

        // Assert
        assertThat(resultado.isEmpty()).isTrue();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeLanzarFiltroInvalidoException_cuandoJuradoIdNoEsUnUuid() {
        // Arrange
        var criteria = EvaluacionDelJuradoCriteria.builder()
                .raiz(NodoFiltro.predicado("juradoId", FiltroOperador.ES, "no-es-un-uuid"))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultarPorJurado(criteria))
                .isInstanceOf(FiltroInvalidoException.class);
    }

    @Test
    void debeRetornarUnaVezLaEvaluacion_cuandoHayVariosJuradosAsignados() {
        // Arrange
        var evaluacion = sembrarEvaluacionDelJurado("Alfa", "PENDIENTE");
        asignarJurado(evaluacion, UtilUUID.generarNuevoUUID());
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorJurado(EvaluacionDelJuradoCriteria.builder()
                .raiz(delJuradoEnEstaPrueba())
                .build());

        // Assert
        assertThat(resultado.getContent()).extracting(EvaluacionReadModel::id).containsExactly(evaluacion);
        assertThat(resultado.getTotalElements()).isEqualTo(1);
    }
}
