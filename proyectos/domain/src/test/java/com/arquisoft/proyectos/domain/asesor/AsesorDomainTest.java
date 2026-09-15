package com.arquisoft.proyectos.domain.asesor;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorDomainTest {

    @Test
    void debeCrearAsesor_cuandoDatosValidos() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var asesor = AsesorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(asesor.getId()).isEqualTo(id);
        assertThat(asesor.getIdentificador()).isEqualTo("20161020123");
        assertThat(asesor.getNombre()).isEqualTo("Ana Perez");
        assertThat(asesor.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(asesor.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(asesor.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorDomain.crear(null, " ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    var errores = validationEx.getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Asesor.ID);
                        assertThat(error.codigoError()).isEqualTo(ProyectosCodes.Asesor.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Asesor.IDENTIFICADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.Asesor.IDENTIFICADOR_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Asesor.NOMBRE);
                        assertThat(error.codigoError()).isEqualTo(ProyectosCodes.Asesor.NOMBRE_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Asesor.EMAIL);
                        assertThat(error.codigoError()).isEqualTo(ProyectosCodes.Asesor.EMAIL_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Asesor.OCURRIDO_EN);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.Asesor.OCURRIDO_EN_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruir_sinValidar() {
        // Act
        var asesor = AsesorDomain.reconstruir(null, null, null, null, null);

        // Assert
        assertThat(asesor.getId()).isNull();
        assertThat(asesor.esVacio()).isFalse();
    }

    @Test
    void debeExponerElCentinelaVacio() {
        // Assert
        assertThat(AsesorDomain.VACIO.esVacio()).isTrue();
    }
}
