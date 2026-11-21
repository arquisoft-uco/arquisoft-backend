package com.arquisoft.biblioteca.domain.entregableproyectogrado;

import com.arquisoft.biblioteca.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregableProyectoGradoDomainTest {

    @Test
    void debeCrearConLosSeisCampos_cuandoTodosVienen() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var entregable = EntregableProyectoGradoDomain.crear(
                id, proyecto, 2, "APROBADO", "a/b.zip", "b.zip");

        // Assert
        assertThat(entregable.esVacio()).isFalse();
        assertThat(entregable.getId()).isEqualTo(id);
        assertThat(entregable.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(entregable.getVersionEntregable()).isEqualTo(2);
        assertThat(entregable.getEstadoEntregable()).isEqualTo(EstadoEntregable.APROBADO);
        assertThat(entregable.getClaveObjeto()).isEqualTo("a/b.zip");
        assertThat(entregable.getNombreArchivo()).isEqualTo("b.zip");
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanTodosLosCampos() {
        // Act & Assert
        assertThatThrownBy(() -> EntregableProyectoGradoDomain.crear(null, null, null, " ", "", " "))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> assertThat(
                        ex.getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(
                                BibliotecaFields.EntregableProyectoGrado.ID,
                                BibliotecaFields.EntregableProyectoGrado.PROYECTO_GRADO,
                                BibliotecaFields.EntregableProyectoGrado.VERSION_ENTREGABLE,
                                BibliotecaFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                                BibliotecaFields.EntregableProyectoGrado.CLAVE_OBJETO,
                                BibliotecaFields.EntregableProyectoGrado.NOMBRE_ARCHIVO));
    }

    @Test
    void debeAcumularErrorDeEstadoRequerido_cuandoElEstadoEstaEnBlanco() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> EntregableProyectoGradoDomain.crear(
                id, proyecto, 1, "  ", "a/b.zip", "b.zip"))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> assertThat(
                        ex.getValidationResult().getErrores())
                        .extracting(e -> e.codigoError())
                        .containsExactly(BibliotecaCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO));
    }

    @Test
    void debeAcumularErrorDeEstadoInvalido_cuandoElEstadoEstaFueraDelCatalogo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> EntregableProyectoGradoDomain.crear(
                id, proyecto, 1, "Aprobado", "a/b.zip", "b.zip"))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> assertThat(
                        ex.getValidationResult().getErrores())
                        .satisfiesExactly(e -> {
                            assertThat(e.campo()).isEqualTo(BibliotecaFields.EntregableProyectoGrado.ESTADO_ENTREGABLE);
                            assertThat(e.codigoError())
                                    .isEqualTo(BibliotecaCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_INVALIDO);
                        }));
    }

    @Test
    void debeRechazarLaVersion_cuandoEsMenorQueUno() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> EntregableProyectoGradoDomain.crear(
                id, proyecto, 0, "APROBADO", "a/b.zip", "b.zip"))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> assertThat(
                        ex.getValidationResult().getErrores())
                        .extracting(e -> e.codigoError())
                        .containsExactly(BibliotecaCodes.EntregableProyectoGrado.VERSION_INVALIDA));
    }

    @Test
    void debeReconstruirSinRevalidarYSerVacioElCentinela_cuandoSeUsaCadaFabrica() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var entregable = EntregableProyectoGradoDomain.reconstruir(
                id, proyecto, 0, EstadoEntregable.CERRADO, "a/b.zip", "b.zip");

        // Assert
        assertThat(entregable.getVersionEntregable()).isZero();
        assertThat(entregable.getEstadoEntregable()).isEqualTo(EstadoEntregable.CERRADO);
        assertThat(entregable.getNombreArchivo()).isEqualTo("b.zip");
        assertThat(EntregableProyectoGradoDomain.VACIO.esVacio()).isTrue();
        assertThat(EntregableProyectoGradoDomain.VACIO.getEstadoEntregable()).isEqualTo(EstadoEntregable.VACIO);
        assertThat(EntregableProyectoGradoDomain.VACIO.getVersionEntregable()).isZero();
    }
}
