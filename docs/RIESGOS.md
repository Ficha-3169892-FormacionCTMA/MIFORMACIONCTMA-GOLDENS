# Registro de Riesgos - Mi Formación CTMA

| ID | Riesgo | Probabilidad | Impacto | Nivel | Tratamiento |
|---|---|---|---|---|---|
| R-01 | Inconsistencia visual en estados de actividad | Baja | Media | Bajo | Uso de Temas de Material 3 y estados centralizados. |
| R-02 | Ingreso de fechas inconsistentes por el usuario | Alta | Alta | Alto | Validación lógica en ViewModel antes de procesar. |
| R-03 | Enlaces de evidencias rotos o mal formados | Media | Media | Medio | Expresiones regulares para validación de URL. |
| R-04 | Registro duplicado de bitácora por latencia | Media | Baja | Bajo | Deshabilitar botón durante el estado de carga. |
| R-05 | Falsificación de asistencia manual | Media | Alta | Alto | Registro de geolocalización o validación por instructor. |
| R-06 | Fuga de datos por sesión no protegida | Baja | Alta | Medio | Implementación de Cifrado en DataStore/SharedPreferences. |
| R-07 | Dashboard con información desactualizada | Alta | Media | Medio | Implementar Swipe-to-refresh y caché con Room. |
| R-08 | Notificaciones no recibidas por ahorro de energía | Alta | Media | Medio | Notificar al usuario sobre permisos de segundo plano. |
| R-09 | Rendimiento lento en búsqueda/filtros | Baja | Baja | Bajo | Uso de operadores de Flow (Debounce/DistinctUntilChanged). |
| R-10 | Conflicto de sincronización en modo offline | Alta | Media | Medio | Estrategia "Last Write Wins" o resolución manual. |
| R-11 | Comentarios de retroalimentación no cargados | Media | Media | Medio | Implementar reintentos (Retry) en la capa de red. |
| R-12 | Superposición de eventos en calendario | Media | Baja | Bajo | Algoritmo de renderizado para múltiples eventos por día. |
| R-13 | Error en la generación o descarga de certificados | Baja | Alta | Medio | Validación de integridad de archivo tras descarga. |
| R-14 | Archivos de novedad (excusa) demasiado pesados | Media | Media | Medio | Compresión de imagen antes de la subida. |
| R-15 | Falta de contraste en Modo Oscuro | Baja | Baja | Bajo | Verificación con inspectores de accesibilidad de Android. |
