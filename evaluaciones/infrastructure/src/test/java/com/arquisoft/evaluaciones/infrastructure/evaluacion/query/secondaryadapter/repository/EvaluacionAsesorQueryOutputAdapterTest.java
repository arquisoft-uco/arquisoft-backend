package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionAsesorCriteria;
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
// (el CREATE TABLE IF NOT EXISTS se comparte en el mismo H2); evaluacion_asesor se crea aqui sin FK.
// @DirtiesContext: el CREATE TABLE nativo hace commit implicito y rompe el rollback entre clases.
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EvaluacionAsesorQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionAsesorQueryRepository repository;

    private EvaluacionAsesorQueryOutputAdapter adapter;

    private String sufijo;

    private UUID asesor;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionAsesorQueryOutputAdapter(repository, new EvaluacionAsesorJpaSpecification());
        sufijo = UtilUUID.generarNuevoUUID().toString();
        asesor = UtilUUID.generarNuevoUUID();
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
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion_asesor (
                    id UUID NOT NULL, evaluacion_id UUID NOT NULL,
                    asesor_id UUID NOT NULL, PRIMARY KEY (id))
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
                        "INSERT INTO entregable (id, proyecto, version_entregable) VALUES (?, ?, ?)")
                .setParameter(1, entregable)
                .setParameter(2, proyecto + " " + sufijo)
                .setParameter(3, version)
                .executeUpdate();
        var evaluacion = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacion)
                .setParameter(2, entregable)
                .setParameter(3, estado)
                .executeUpdate();
        if (dueno != null) {
            entityManager.createNativeQuery(
                            "INSERT INTO evaluacion_asesor (id, evaluacion_id, asesor_id) VALUES (?, ?, ?)")
                    .setParameter(1, UtilUUID.generarNuevoUUID())
                    .setParameter(2, evaluacion)
                    .setParameter(3, dueno)
                    .executeUpdate();
        }
        entityManager.flush();
        return evaluacion;
    }

    private UUID sembrarEvaluacionDelAsesor(String proyecto, String estado) {
        return sembrarEvaluacion(UtilUUID.generarNuevoUUID(), proyecto, 1, estado, asesor);
    }

    private NodoFiltro delAsesor(UUID id) {
        return NodoFiltro.predicado("asesorId", FiltroOperador.ES, id.toString());
    }

    private NodoFiltro delAsesorEnEstaPrueba(NodoFiltro... adicionales) {
        var hijos = new ArrayList<NodoFiltro>();
        hijos.add(delAsesor(asesor));
        hijos.add(NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, sufijo));
        hijos.addAll(List.of(adicionales));
        return NodoFiltro.grupo(FiltroConector.AND, hijos);
    }

    @Test
    void debeMapearEntregableYEstado_yRetornarSoloLasEvaluacionesDelAsesor() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var propia = sembrarEvaluacion(entregable, "Robot seguidor", 2, "EN_PROGRESO", asesor);
        sembrarEvaluacion(UtilUUID.generarNuevoUUID(), "Ajena", 1, "PENDIENTE", UtilUUID.generarNuevoUUID());
        sembrarEvaluacion(UtilUUID.generarNuevoUUID(), "Sin vinculo", 1, "PENDIENTE", null);

        // Act
        var resultado = adapter.consultarPorAsesor(EvaluacionAsesorCriteria.builder()
                .raiz(delAsesorEnEstaPrueba())
                .build());

        // Assert
        assertThat(resultado.getContent()).containsExactly(new EvaluacionReadModel(
                propia,
                new EvaluacionReadModel.Entregable(entregable, "Robot seguidor " + sufijo, 2),
                new EvaluacionReadModel.Estado("EN_PROGRESO", "En progreso")));
        assertThat(resultado.getTotalElements()).isEqualTo(1);
    }

    @Test
    void debeFiltrarPorEstadoProyectoYEntregableId_dentroDeLasEvaluacionesDelAsesor() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var objetivo = sembrarEvaluacion(entregable, "Brazo robotico", 1, "PENDIENTE", asesor);
        sembrarEvaluacionDelAsesor("Sistema contable", "FINALIZADA");

        // Act
        var porEstado = adapter.consultarPorAsesor(EvaluacionAsesorCriteria.builder()
                .raiz(delAsesorEnEstaPrueba(NodoFiltro.predicadoMultivalor(
                        "estado", FiltroOperador.IN, List.of("PENDIENTE", "EN_PROGRESO"))))
                .build());
        var porProyecto = adapter.consultarPorAsesor(EvaluacionAsesorCriteria.builder()
                .raiz(delAsesorEnEstaPrueba(NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, "robot")))
                .build());
        var porEntregable = adapter.consultarPorAsesor(EvaluacionAsesorCriteria.builder()
                .raiz(delAsesorEnEstaPrueba(
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
        sembrarEvaluacionDelAsesor("Alfa", "EN_PROGRESO");
        sembrarEvaluacionDelAsesor("Beta", "FINALIZADA");
        sembrarEvaluacionDelAsesor("Gamma", "PENDIENTE");

        // Act
        var porProyecto = adapter.consultarPorAsesor(EvaluacionAsesorCriteria.builder()
                .raiz(delAsesorEnEstaPrueba())
                .ordenamiento(List.of(SortOrder.of("proyecto", SortDirection.DESC)))
                .build());
        var porEstado = adapter.consultarPorAsesor(EvaluacionAsesorCriteria.builder()
                .raiz(delAsesorEnEstaPrueba())
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
        sembrarEvaluacionDelAsesor("Alfa", "PENDIENTE");
        sembrarEvaluacionDelAsesor("Beta", "PENDIENTE");
        sembrarEvaluacionDelAsesor("Gamma", "PENDIENTE");

        // Act
        var resultado = adapter.consultarPorAsesor(EvaluacionAsesorCriteria.builder()
                .raiz(delAsesorEnEstaPrueba())
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
    void debeRetornarPaginaVacia_cuandoElFiltroDeAsesorEsAjeno() {
        // Arrange
        sembrarEvaluacionDelAsesor("Alfa", "PENDIENTE");
        var ajeno = UtilUUID.generarNuevoUUID();

        // Act
        var resultado = adapter.consultarPorAsesor(EvaluacionAsesorCriteria.builder()
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(delAsesor(asesor), delAsesor(ajeno))))
                .build());

        // Assert
        assertThat(resultado.isEmpty()).isTrue();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeLanzarFiltroInvalidoException_cuandoAsesorIdNoEsUnUuid() {
        // Arrange
        var criteria = EvaluacionAsesorCriteria.builder()
                .raiz(NodoFiltro.predicado("asesorId", FiltroOperador.ES, "no-es-un-uuid"))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultarPorAsesor(criteria))
                .isInstanceOf(FiltroInvalidoException.class);
    }
}
