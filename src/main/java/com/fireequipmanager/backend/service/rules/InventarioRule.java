package com.fireequipmanager.backend.service.rules;

import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.enumsEquipo.TipoInventario;

public final class InventarioRule {

    private InventarioRule() {
    }

    // Valida inventario individual o múltiple
    public static void validarInventario(
            TipoInventario tipoInventario,
            String codigoCgbvp,
            String numeroSerie,
            Integer stock) {

        if (tipoInventario == null) {
            throw new BusinessException("Debe indicar el tipo de inventario");
        }

        if (tipoInventario == TipoInventario.INDIVIDUAL) {

            if (codigoCgbvp == null || codigoCgbvp.isBlank()) {
                throw new BusinessException("Debe registrar el código CGBVP");
            }

            if (numeroSerie == null || numeroSerie.isBlank()) {
                throw new BusinessException("Debe registrar el número de serie");
            }

            if (stock == null || stock != 1) {
                throw new BusinessException("Un equipo individual solo puede tener stock 1");
            }

            return;
        }

        // Inventario múltiple

        if (codigoCgbvp != null && !codigoCgbvp.isBlank()) {
            throw new BusinessException("Los equipos múltiples no poseen código CGBVP");
        }

        if (numeroSerie != null && !numeroSerie.isBlank()) {
            throw new BusinessException("Los equipos múltiples no poseen número de serie");
        }

        if (stock == null || stock <= 0) {
            throw new BusinessException("Debe registrar un stock válido");
        }
    }
}