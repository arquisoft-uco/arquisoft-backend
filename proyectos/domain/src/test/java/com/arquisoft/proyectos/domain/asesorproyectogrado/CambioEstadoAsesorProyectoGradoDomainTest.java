package com.arquisoft.proyectos.domain.asesorproyectogrado;

import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioEstadoAsesorProyectoGradoDomainTest {

    @Test
    void debeCrearConElEstadoConvertido_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioEstadoAsesorProyectoGradoDomain.crear(
                proyectoGrado, asesor, coordinador, "FINALIZADO");

        // Assert
        assertThat(cambio.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(cambio.getAsesor()).isEqualTo(asesor);
        assertThat(cambio.getCoordinador()).isEqualTo(coordinador);
        assertThat(cambio.getEstadoNuevo()).isEqualTo(EstadoAsesor.FINALIZADO);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanIdsYEstado() {
        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoAsesorProyectoGradoDomain.crear(null, null, null, " "))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.AsesorProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.AsesorProyectoGrado.ASESOR_ID_REQUERIDO,
                                ProyectosCodes.AsesorProyectoGrado.COORDINADOR_ID_REQUERIDO,
                                ProyectosCodes.AsesorProyectoGrado.ESTADO_ASESOR_REQUERIDO));
    }

    @Test
    void debeLanzarEstadoInvalido_cuandoElEstadoNoEstaEnElCatalogoNiEsVacio() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoAsesorProyectoGradoDomain.crear(
                proyectoGrado, asesor, coordinador, "INEXISTENTE"))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.AsesorProyectoGrado.ESTADO_ASESOR_INVALIDO)));
        assertThatThrownBy(() -> CambioEstadoAsesorProyectoGradoDomain.crear(
                proyectoGrado, asesor, coordinador, EstadoAsesor.VACIO.getId()))
                .isInstanceOf(DomainValidationException.class);
    }
}
