package com.arquisoft.biblioteca.domain.entregableproyectogrado;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregableProyectoGradoDomainTest {

    @Test
    void debeCrearConLosSieteCampos_cuandoTodosVienen() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var entregable = EntregableProyectoGradoDomain.crear(
                id, proyecto, 2, "Aprobado", "entregables", "a/b.zip", "b.zip");

        // Assert
        assertThat(entregable.esVacio()).isFalse();
        assertThat(entregable.getId()).isEqualTo(id);
        assertThat(entregable.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(entregable.getVersionEntregable()).isEqualTo(2);
        assertThat(entregable.getEstadoEntregableNombre()).isEqualTo("Aprobado");
        assertThat(entregable.getBucket()).isEqualTo("entregables");
        assertThat(entregable.getClaveObjeto()).isEqualTo("a/b.zip");
        assertThat(entregable.getNombreArchivo()).isEqualTo("b.zip");
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanTodosLosCampos() {
        // Act & Assert
        assertThatThrownBy(() -> EntregableProyectoGradoDomain.crear(null, null, null, " ", null, "", " "))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> assertThat(
                        ex.getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(
                                BibliotecaFields.EntregableProyectoGrado.ID,
                                BibliotecaFields.EntregableProyectoGrado.PROYECTO_GRADO,
                                BibliotecaFields.EntregableProyectoGrado.VERSION_ENTREGABLE,
                                BibliotecaFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                                BibliotecaFields.EntregableProyectoGrado.BUCKET,
                                BibliotecaFields.EntregableProyectoGrado.CLAVE_OBJETO,
                                BibliotecaFields.EntregableProyectoGrado.NOMBRE_ARCHIVO));
    }

    @Test
    void debeRechazarLaVersion_cuandoEsMenorQueUno() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> EntregableProyectoGradoDomain.crear(
                id, proyecto, 0, "Aprobado", "entregables", "a/b.zip", "b.zip"))
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
                id, proyecto, 0, "", "entregables", "a/b.zip", "b.zip");

        // Assert
        assertThat(entregable.getVersionEntregable()).isZero();
        assertThat(entregable.getEstadoEntregableNombre()).isEmpty();
        assertThat(entregable.getNombreArchivo()).isEqualTo("b.zip");
        assertThat(EntregableProyectoGradoDomain.VACIO.esVacio()).isTrue();
        assertThat(EntregableProyectoGradoDomain.VACIO.getBucket()).isEqualTo(UtilTexto.VACIO);
        assertThat(EntregableProyectoGradoDomain.VACIO.getVersionEntregable()).isZero();
    }
}
