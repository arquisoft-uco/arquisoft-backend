package com.arquisoft.proyectos.domain.coordinador;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class CoordinadorDomain {

    public static final CoordinadorDomain VACIO = CoordinadorDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO, Instant.EPOCH);

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
    private Instant ocurridoEn;

    private CoordinadorDomain() {}

    public static CoordinadorDomain crear(UUID id, String identificador, String nombre, String email,
                                           Instant ocurridoEn) {
        var coordinador = new CoordinadorDomain();
        var result = new ValidationResult();

        coordinador.setId(id, result);
        coordinador.setIdentificador(identificador, result);
        coordinador.setNombre(nombre, result);
        coordinador.setEmail(email, result);
        coordinador.setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        return coordinador;
    }

    public static CoordinadorDomain reconstruir(UUID id, String identificador, String nombre, String email,
                                                 Instant ocurridoEn) {
        var coordinador = new CoordinadorDomain();
        coordinador.id = id;
        coordinador.identificador = identificador;
        coordinador.nombre = nombre;
        coordinador.email = email;
        coordinador.ocurridoEn = ocurridoEn;
        return coordinador;
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
                ProyectosFields.Coordinador.ID,
                ProyectosCodes.Coordinador.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setIdentificador(String identificador, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(identificador,
                ProyectosFields.Coordinador.IDENTIFICADOR,
                ProyectosCodes.Coordinador.IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        this.identificador = identificador;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombre,
                ProyectosFields.Coordinador.NOMBRE,
                ProyectosCodes.Coordinador.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
    }

    private void setEmail(String email, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(email,
                ProyectosFields.Coordinador.EMAIL,
                ProyectosCodes.Coordinador.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = email;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                ProyectosFields.Coordinador.OCURRIDO_EN,
                ProyectosCodes.Coordinador.OCURRIDO_EN_REQUERIDO, result)) {
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
