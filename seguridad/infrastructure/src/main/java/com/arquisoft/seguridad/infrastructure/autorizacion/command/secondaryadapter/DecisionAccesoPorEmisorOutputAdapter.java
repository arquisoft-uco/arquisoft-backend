package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.DecisionAccesoOutputPort;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.infrastructure.autorizacion.exception.ProveedorAutorizacionNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.AutorizacionKey;
import com.arquisoft.shared.util.UtilObjeto;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Primary
@Component
public class DecisionAccesoPorEmisorOutputAdapter implements DecisionAccesoOutputPort {

    private final ProveedoresAutenticacion proveedores;
    private final Map<ModoAutorizacion, DecisionAccesoPorModo> adaptadoresPorModo = new EnumMap<>(ModoAutorizacion.class);

    public DecisionAccesoPorEmisorOutputAdapter(ProveedoresAutenticacion proveedores,
                                                List<DecisionAccesoPorModo> adaptadores) {
        this.proveedores = proveedores;
        adaptadores.forEach(this::registrar);
        var sinAdaptador = Arrays.stream(ModoAutorizacion.values())
                .filter(modo -> !adaptadoresPorModo.containsKey(modo))
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        if (!sinAdaptador.isEmpty()) {
            throw new IllegalStateException(
                    "Modos de autorizacion sin adaptador de decision de acceso: " + sinAdaptador);
        }
    }

    private void registrar(DecisionAccesoPorModo adaptador) {
        var previo = adaptadoresPorModo.putIfAbsent(adaptador.modo(), adaptador);
        if (UtilObjeto.noEsNulo(previo)) {
            throw new IllegalStateException("Modo de autorizacion " + adaptador.modo()
                    + " declarado por mas de un adaptador: " + previo.getClass().getSimpleName()
                    + " y " + adaptador.getClass().getSimpleName());
        }
    }

    @Override
    public DecisionAcceso decidir(SolicitudAcceso solicitud) {
        var modo = proveedores.modoDe(solicitud.emisor())
                .orElseThrow(() -> new ProveedorAutorizacionNoDisponibleException(
                        Mensajes.obtener(AutorizacionKey.ERROR_PROVEEDOR_NO_DISPONIBLE)));
        return adaptadoresPorModo.get(modo).decidir(solicitud);
    }
}
