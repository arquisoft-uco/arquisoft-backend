package com.arquisoft.fichas.domain.asesorficha;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class AsesorFichaDomain {

    public static final AsesorFichaDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO, Instant.EPOCH);

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
    private Instant ocurridoEn;

    private AsesorFichaDomain() {}

    public static AsesorFichaDomain crear(UUID id, String identificador, String nombre, String email,
                                           Instant ocurridoEn) {
        var asesorFicha = new AsesorFichaDomain();
        var result = new ValidationResult();

        asesorFicha.setId(id, result);
        asesorFicha.setIdentificador(identificador, result);
        asesorFicha.setNombre(nombre, result);
        asesorFicha.setEmail(email, result);
        asesorFicha.setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        return asesorFicha;
    }

    public static AsesorFichaDomain reconstruir(UUID id, String identificador, String nombre, String email,
                                                 Instant ocurridoEn) {
        var asesorFicha = new AsesorFichaDomain();
        asesorFicha.id = id;
        asesorFicha.identificador = identificador;
        asesorFicha.nombre = nombre;
        asesorFicha.email = email;
        asesorFicha.ocurridoEn = ocurridoEn;
        return asesorFicha;
    }

    public void actualizar(String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        setIdentificador(identificador, result);
        setNombre(nombre, result);
        setEmail(email, result);
        setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                FichasFields.AsesorFicha.ID,
                FichasCodes.AsesorFicha.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setIdentificador(String identificador, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(identificador,
                FichasFields.AsesorFicha.IDENTIFICADOR,
                FichasCodes.AsesorFicha.IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        this.identificador = identificador;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombre,
                FichasFields.AsesorFicha.NOMBRE,
                FichasCodes.AsesorFicha.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
    }

    private void setEmail(String email, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(email,
                FichasFields.AsesorFicha.EMAIL,
                FichasCodes.AsesorFicha.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = email;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                FichasFields.AsesorFicha.OCURRIDO_EN,
                FichasCodes.AsesorFicha.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    public UUID getId() {
        return id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
