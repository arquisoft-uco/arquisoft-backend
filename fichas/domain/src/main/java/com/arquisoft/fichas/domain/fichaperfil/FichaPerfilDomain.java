package com.arquisoft.fichas.domain.fichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public final class FichaPerfilDomain {

    public static final FichaPerfilDomain VACIO = new FichaPerfilDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilTexto.VACIO,
            UtilUUID.obtenerUUIDPorDefecto());

    private UUID id;
    private String tituloProyecto;
    private UUID asesorFicha;

    private FichaPerfilDomain() {}

    private FichaPerfilDomain(UUID id, String tituloProyecto, UUID asesorFicha) {
        this.id = id;
        this.tituloProyecto = tituloProyecto;
        this.asesorFicha = asesorFicha;
    }

    public static FichaPerfilDomain crear(String titulo, UUID asesorFicha) {
        var ficha = new FichaPerfilDomain();
        var result = new ValidationResult();

        ficha.setId();
        ficha.setTituloProyecto(titulo, result);
        ficha.setAsesorFicha(asesorFicha, result);

        result.lanzarSiTieneErrores();
        return ficha;
    }

    public static FichaPerfilDomain reconstruir(UUID id, String titulo, UUID asesorFichaId) {
        return new FichaPerfilDomain(id, titulo, asesorFichaId);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setTituloProyecto(String titulo, ValidationResult result) {
        if (!DomainValidator.noEnBlanco(titulo,
                FichasFields.FichaPerfil.TITULO,
                FichasCodes.FichaPerfil.TITULO_REQUERIDO, result)) {
            return;
        }
        if (!DomainValidator.longitudMaxima(titulo, FichasLimits.FichaPerfil.TITULO_MAX,
                FichasFields.FichaPerfil.TITULO,
                FichasCodes.FichaPerfil.TITULO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.tituloProyecto = UtilTexto.aplicarTrim(titulo);
    }

    private void setAsesorFicha(UUID asesorFicha, ValidationResult result) {
        if (!DomainValidator.noNulo(asesorFicha,
                FichasFields.FichaPerfil.ASESOR_FICHA,
                FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesorFicha = asesorFicha;
    }

    public UUID getId() {
        return id;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public UUID getAsesorFicha() {
        return asesorFicha;
    }

    public boolean esVacio() {
        return this != VACIO;
    }
}
