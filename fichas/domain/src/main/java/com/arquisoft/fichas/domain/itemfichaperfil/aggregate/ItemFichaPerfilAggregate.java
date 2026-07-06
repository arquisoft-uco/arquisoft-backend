package com.arquisoft.fichas.domain.itemfichaperfil.aggregate;

import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import com.arquisoft.shared.message.FichasMessages;
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

    private ItemFichaPerfilAggregate() {}

    // ─── Factory: crear (entidad nueva — valida invariantes) ─────────────────

    public static ItemFichaPerfilAggregate crear(UUID fichaPerfilId, String tipoItem, String contenido) {
        var itemFichaPerfilAggregate = new ItemFichaPerfilAggregate();
        var result = new ValidationResult();

        itemFichaPerfilAggregate.setId();
        itemFichaPerfilAggregate.setFichaPerfilId(fichaPerfilId, result);
        itemFichaPerfilAggregate.setTipoItem(tipoItem, result);
        itemFichaPerfilAggregate.setContenido(contenido, result);

        result.throwIfHasErrors();
        return itemFichaPerfilAggregate;
    }

    // ─── Factory: reconstruir (desde persistencia — dato confiable) ──────────

    public static ItemFichaPerfilAggregate reconstruir(UUID id, UUID fichaPerfilId, TipoItem tipoItem,
                                                       String contenido) {
        var itemFichaPerfilAggregate = new ItemFichaPerfilAggregate();
        itemFichaPerfilAggregate.id = id;
        itemFichaPerfilAggregate.fichaPerfilId = fichaPerfilId;
        itemFichaPerfilAggregate.tipoItem = tipoItem;
        itemFichaPerfilAggregate.contenido = contenido;
        return itemFichaPerfilAggregate;
    }

    // ─── Private setters ──────────────────────────────────────────────────────

    private void setId() {
        this.id = UtilUUID.generateNewUUID();
    }

    private void setFichaPerfilId(UUID fichaPerfilId, ValidationResult result) {
        if (!DomainValidator.notNull(fichaPerfilId,
                FichasMessages.ItemFichaPerfil.CAMPO_FICHA_PERFIL_ID,
                FichasMessages.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfilId = fichaPerfilId;
    }

    private void setTipoItem(String tipoItem, ValidationResult result) {
        if (!DomainValidator.notBlank(tipoItem,
                FichasMessages.ItemFichaPerfil.CAMPO_TIPO_ITEM,
                FichasMessages.ItemFichaPerfil.TIPO_ITEM_REQUERIDO, result)) {
            return;
        }
        try {
            this.tipoItem = TipoItem.valueOf(UtilText.applyTrim(tipoItem));
        } catch (IllegalArgumentException e) {
            result.addError(
                    FichasMessages.ItemFichaPerfil.CAMPO_TIPO_ITEM,
                    FichasMessages.ItemFichaPerfil.TIPO_ITEM_INVALIDO,
                    FichasMessages.ItemFichaPerfil.TIPO_ITEM_INVALIDO_MSG.formatted(tipoItem));
        }
    }

    private void setContenido(String contenido, ValidationResult result) {
        if (!DomainValidator.notBlank(contenido,
                FichasMessages.ItemFichaPerfil.CAMPO_CONTENIDO,
                FichasMessages.ItemFichaPerfil.CONTENIDO_REQUERIDO, result)) {
            return;
        }
        if (!DomainValidator.maxLength(contenido,
                FichasMessages.ItemFichaPerfil.CONTENIDO_MAX,
                FichasMessages.ItemFichaPerfil.CAMPO_CONTENIDO,
                FichasMessages.ItemFichaPerfil.CONTENIDO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.contenido = UtilText.applyTrim(contenido);
    }

    // ─── Método de negocio: modificar contenido ───────────────────────────────

    public void modificarContenido(String nuevoContenido) {
        var result = new ValidationResult();
        setContenido(nuevoContenido, result);
        result.throwIfHasErrors();
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
