package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProveedoresAutenticacion {

    private final List<ProveedorAutenticacion> proveedores;

    public ProveedoresAutenticacion(List<ProveedorAutenticacion> proveedores) {
        this.proveedores = List.copyOf(proveedores);
    }

    public List<ProveedorAutenticacion> todos() {
        return proveedores;
    }

    public List<ProveedorAutenticacion> habilitados() {
        return proveedores.stream().filter(ProveedorAutenticacion::habilitado).toList();
    }

    public List<ProveedorAutenticacion> aplicables(boolean rutaNoDeNegocio) {
        return habilitados().stream()
                .filter(proveedor -> !rutaNoDeNegocio || proveedor.actuaEnRutasNoDeNegocio())
                .toList();
    }

    public Optional<ProveedorAutenticacion> porId(String id) {
        return habilitados().stream().filter(proveedor -> proveedor.id().equalsIgnoreCase(id)).findFirst();
    }

    public Optional<ProveedorAutenticacion> porEmisor(String emisor) {
        return habilitados().stream().filter(proveedor -> proveedor.autorizacion(emisor).isPresent()).findFirst();
    }

    public Optional<ModoAutorizacion> modoDe(String emisor) {
        return porEmisor(emisor).flatMap(proveedor -> proveedor.autorizacion(emisor));
    }

    public Optional<PresentadorCredencial> presentadorDe(String emisor) {
        return porEmisor(emisor).<PresentadorCredencial>map(proveedor -> proveedor);
    }

    public boolean contiene(String id) {
        return proveedores.stream().anyMatch(proveedor -> proveedor.id().equalsIgnoreCase(id));
    }
}
