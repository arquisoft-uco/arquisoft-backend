package com.arquisoft.fichas.domain.itemfichaperfil;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ItemFichaPerfilDomain {

    private UUID id;
    private UUID fichaPerfilId;
    private TipoItem tipoItem;
    private String contenido;

    private ItemFichaPerfilDomain(UUID id, UUID fichaPerfilId, TipoItem tipoItem, String contenido) {
        this.id = id;
        this.fichaPerfilId = fichaPerfilId;
        this.tipoItem = tipoItem;
        this.contenido = contenido;
    }

    private ItemFichaPerfilDomain() {}

    public static ItemFichaPerfilDomain crear(UUID fichaPerfilId, String tipoItem, String contenido) {
        var itemFichaPerfilDomain = new ItemFichaPerfilDomain();
        var result = new ValidationResult();

        itemFichaPerfilDomain.setId();
        itemFichaPerfilDomain.setFichaPerfilId(fichaPerfilId, result);
        itemFichaPerfilDomain.setTipoItem(tipoItem, result);
        itemFichaPerfilDomain.setContenido(contenido, result);

        result.lanzarSiTieneErrores();
        return itemFichaPerfilDomain;
    }

    public static ItemFichaPerfilDomain reconstruir(UUID id, UUID fichaPerfilId, TipoItem tipoItem,
                                                       String contenido) {
        return new ItemFichaPerfilDomain(id, fichaPerfilId,  tipoItem, contenido);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setFichaPerfilId(UUID fichaPerfilId, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(fichaPerfilId,
                FichasFields.ItemFichaPerfil.FICHA_PERFIL,
                FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfilId = fichaPerfilId;
    }

    private void setTipoItem(String tipoItem, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(tipoItem,
                FichasFields.ItemFichaPerfil.TIPO_ITEM,
                FichasCodes.ItemFichaPerfil.TIPO_ITEM_REQUERIDO, result)) {
            return;
        }
        if (!TipoItem.esValido(tipoItem)) {
            result.agregarError(
                    FichasFields.ItemFichaPerfil.TIPO_ITEM,
                    FichasCodes.ItemFichaPerfil.TIPO_ITEM_INVALIDO,
                    Mensajes.formatear(ItemFichaPerfilKey.ERROR_TIPO_INVALIDO, tipoItem));
            return;
        }
        this.tipoItem = TipoItem.desde(tipoItem);
    }

    private void setContenido(String contenido, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(contenido,
                FichasFields.ItemFichaPerfil.CONTENIDO,
                FichasCodes.ItemFichaPerfil.CONTENIDO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(contenido,
                FichasLimits.ItemFichaPerfil.CONTENIDO_MAX,
                FichasFields.ItemFichaPerfil.CONTENIDO,
                FichasCodes.ItemFichaPerfil.CONTENIDO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.contenido = UtilTexto.aplicarTrim(contenido);
    }

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
