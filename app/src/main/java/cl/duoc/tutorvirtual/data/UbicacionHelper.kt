package cl.duoc.tutorvirtual.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat

// ==================================================
// UBICACION GPS
// TUTOR VIRTUAL ESUCAR - PLAN B
//
// Consulta la ubicacion actual del dispositivo.
// Usa el servicio de ubicacion de Android,
// sin librerias externas.
//
// Solo se consulta cuando el estudiante lo solicita.
// ==================================================

object UbicacionHelper {

    // Verificar permiso de ubicacion (aproximada o precisa)
    fun hayPermiso(context: Context): Boolean {

        val precisa = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val aproximada = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return precisa || aproximada
    }

    private fun permisoPreciso(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Verificar que la ubicacion este activada en el telefono
    fun ubicacionActivada(context: Context): Boolean {

        val manager = context.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        return LocationManagerCompat.isLocationEnabled(manager)
    }

    // Solicitar la ubicacion actual.
    // Devuelve null si no se pudo obtener.
    @SuppressLint("MissingPermission")
    fun obtenerUbicacion(
        context: Context,
        onResultado: (Location?) -> Unit
    ) {

        if (!hayPermiso(context) || !ubicacionActivada(context)) {
            onResultado(null)
            return
        }

        val manager = context.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        // Con permiso preciso se intenta primero el GPS
        val proveedores = buildList {

            if (permisoPreciso(context) &&
                manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            ) {
                add(LocationManager.GPS_PROVIDER)
            }

            if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                add(LocationManager.NETWORK_PROVIDER)
            }
        }

        if (proveedores.isEmpty()) {
            onResultado(null)
            return
        }

        consultarProveedor(
            context = context,
            manager = manager,
            proveedores = proveedores,
            indice = 0,
            onResultado = onResultado
        )
    }

    // Probar cada proveedor hasta obtener una posicion
    @SuppressLint("MissingPermission")
    private fun consultarProveedor(
        context: Context,
        manager: LocationManager,
        proveedores: List<String>,
        indice: Int,
        onResultado: (Location?) -> Unit
    ) {

        if (indice >= proveedores.size) {
            onResultado(null)
            return
        }

        try {

            LocationManagerCompat.getCurrentLocation(
                manager,
                proveedores[indice],
                null,
                ContextCompat.getMainExecutor(context)
            ) { ubicacion ->

                if (ubicacion != null) {
                    onResultado(ubicacion)
                } else {
                    consultarProveedor(
                        context,
                        manager,
                        proveedores,
                        indice + 1,
                        onResultado
                    )
                }
            }

        } catch (e: SecurityException) {
            onResultado(null)
        }
    }
}
