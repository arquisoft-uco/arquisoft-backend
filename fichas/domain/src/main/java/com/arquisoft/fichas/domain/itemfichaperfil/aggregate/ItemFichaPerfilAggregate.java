package com.arquisoft.fichas.domain.itemfichaperfil.aggregate;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import com.arquisoft.shared.util.UtilText;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public final class ItemFichaPerfilAggregate {

    private UUID id;
    private UUID fichaPerfilId;
    private TipoItem tipoItem;
    private String contenido;

    private ItemFichaPerfilAggregate(UUID id, UUID fichaPerfilId, TipoItem tipoItem, String contenido) {
        this.id = id;
        this.fichaPerfilId = fichaPerfilId;
        this.tipoItem = tipoItem;
        this.contenido = contenido;
    }

    private ItemFichaPerfilAggregate() {}

    // ─── Factory: crear (entidad nueva — valida invariantes) ─────────────────

    public static ItemFichaPerfilAggregate crear(UUID fichaPerfilId, String tipoItem, String contenido) {
        var itemFichaPerfilAggregate = new ItemFichaPerfilAggregate();
        var result = new ValidationResult();

        itemFichaPerfilAggregate.setId();
        itemFichaPerfilAggregate.setFichaPerfilId(fichaPerfilId, result);
        itemFichaPerfilAggregate.setTipoItem(tipoItem, result);
        itemFichaPerfilAggregate.setContenido(contenido, result);

        result.lanzarSiTieneErrores();
        return itemFichaPerfilAggregate;
    }

    // ─── Factory: reconstruir (desde persistencia — dato confiable) ──────────

    public static ItemFichaPerfilAggregate reconstruir(UUID id, UUID fichaPerfilId, TipoItem tipoItem,
                                                       String contenido) {
        return new ItemFichaPerfilAggregate(id, fichaPerfilId,  tipoItem, contenido);
    }

    // ─── Private setters ──────────────────────────────────────────────────────

    private void setId() {
        this.id = UtilUUID.generateNewUUID();
    }

    private void setFichaPerfilId(UUID fichaPerfilId, ValidationResult result) {
        if (!DomainValidator.noNulo(fichaPerfilId,
                FichasFields.ItemFichaPerfil.FICHA_PERFIL,
                FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfilId = fichaPerfilId;
    }

    private void setTipoItem(String tipoItem, ValidationResult result) {
        if (!DomainValidator.noEnBlanco(tipoItem,
                FichasFields.ItemFichaPerfil.TIPO_ITEM,
                FichasCodes.ItemFichaPerfil.TIPO_ITEM_REQUERIDO, result)) {
            return;
        }
        try {
            this.tipoItem = TipoItem.valueOf(UtilText.applyTrim(tipoItem));
        } catch (IllegalArgumentException e) {
            result.agregarError(
                    FichasFields.ItemFichaPerfil.TIPO_ITEM,
                    FichasCodes.ItemFichaPerfil.TIPO_ITEM_INVALIDO,
                    Messages.formatear(FichasKeys.ItemFichaPerfil.ERROR_TIPO_INVALIDO, tipoItem));
        }
    }

    private void setContenido(String contenido, ValidationResult result) {
        if (!DomainValidator.noEnBlanco(contenido,
                FichasFields.ItemFichaPerfil.CONTENIDO,
                FichasCodes.ItemFichaPerfil.CONTENIDO_REQUERIDO, result)) {
            return;
        }
        if (!DomainValidator.longitudMaxima(contenido,
                FichasLimits.ItemFichaPerfil.CONTENIDO_MAX,
                FichasFields.ItemFichaPerfil.CONTENIDO,
                FichasCodes.ItemFichaPerfil.CONTENIDO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.contenido = UtilText.applyTrim(contenido);
    }

    // ─── Método de negocio: modificar contenido ───────────────────────────────

    public void modificarContenido(String nuevoContenido, EstadoFicha estadoFichaActual) {
        var result = new ValidationResult();

        if (esFichaModificable(estadoFichaActual, result)) {
            setContenido(nuevoContenido, result);
        }

        result.lanzarSiTieneErrores();
    }

    // ─── Método de negocio: remover (valida invariante POL-05) ───────────────

    public void removerse(long totalRevisiones) {
        var result = new ValidationResult();
        if (totalRevisiones > 0) {
            result.agregarError(FichasFields.ItemFichaPerfil.REVISIONES,
                    FichasCodes.ItemFichaPerfil.ITEM_CON_REVISIONES,
                    Messages.formatear(FichasKeys.ItemFichaPerfil.ERROR_CON_REVISIONES, id));
        }
        result.lanzarSiTieneErrores();
    }

    private boolean esFichaModificable(EstadoFicha estadoFichaActual, ValidationResult result) {
        if (!DomainValidator.noNulo(estadoFichaActual,
                FichasFields.ItemFichaPerfil.ESTADO_FICHA,
                FichasCodes.ItemFichaPerfil.ESTADO_FICHA_REQUERIDO, result)) {
            return false;
        }
        if (!estadoFichaActual.permiteModificacion()) {
            result.agregarError(
                    FichasFields.ItemFichaPerfil.ESTADO_FICHA,
                    FichasCodes.ItemFichaPerfil.ESTADO_FICHA_NO_MODIFICABLE,
                    Messages.obtener(FichasKeys.ItemFichaPerfil.ERROR_ESTADO_FICHA_NO_MODIFICABLE)
                            .formatted(estadoFichaActual.getNombre()));
            return false;
        }
        return true;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public UUID getId() {
        return id;
    }

    public UUID getFichaPerfilId() {
        return fichaPerfilId;
    }

    public TipoItem getTipoItem() {
        return tipoItem;
    }

    public String getContenido() {
        return contenido;
    }
}
