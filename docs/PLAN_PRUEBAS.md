# Plan de Pruebas - Mi Formación CTMA

| ID | HU/CA | Técnica | Precondición | Pasos | Esperado |
|---|---|---|---|---|---|
| TC-01.1 | HU-01 / CA-01.1 | Visual | Lista con datos | Cargar pantalla principal | Tarjetas muestran color según prioridad (Rojo/Amarillo/Verde). |
| TC-01.2 | HU-01 / CA-01.2 | Límite | Actividad a 1 día de vencer | Ver tarjeta de actividad | Texto de días restantes resaltado en rojo. |
| TC-02.1 | HU-02 / CA-02.1 | Lógica | Formulario creación | Ingresar Fecha Fin < Fecha Inicio | Mostrar error: "La fecha fin no puede ser anterior". |
| TC-03.1 | HU-03 / CA-03.1 | Formato | Formulario evidencia | Ingresar URL sin http/https | Mostrar error: "Formato de URL inválido". |
| TC-04.1 | HU-04 / CA-04.1 | Funcional | App en pantalla Bitácora | Llenar campos válidos y pulsar Guardar | Lista actualizada y Snackbar de éxito. |
| TC-04.2 | HU-04 / CA-04.2 | Límite | App en pantalla Bitácora | Ingresar '0' o '-1' en horas | El botón Guardar se deshabilita o muestra error. |
| TC-05.1 | HU-05 / CA-05.2 | Lógica | Registro asistencia | Seleccionar fecha futura | Sistema impide el registro. |
| TC-06.1 | HU-06 / CA-06.1 | Sintáctica | Pantalla Login | Ingresar correo @gmail.com | Error: "Debe ser un correo institucional SENA". |
| TC-07.1 | HU-07 / CA-07.1 | Integración | Actividades variadas | Ver Dashboard | El gráfico refleja el % real de actividades con progreso=100. |
| TC-08.1 | HU-08 / CA-08.1 | Funcional | Actividad a 24h | Esperar notificación | Se dispara alerta en la barra de notificaciones. |
| TC-09.1 | HU-09 / CA-09.1 | Caja Negra | Lista con 10 items | Escribir término en buscador | La lista se filtra en tiempo real (< 500ms). |
| TC-10.1 | HU-10 / CA-10.1 | Offline | Sin conexión | Crear Bitácora | El registro se guarda localmente (Room) con marca "Pendiente". |
| TC-11.1 | HU-11 / CA-11.1 | Visual | Comentario nuevo | Ver lista evidencias | Badge rojo visible sobre el item de la evidencia. |
| TC-12.1 | HU-12 / CA-12.1 | Visual | Mes con entregas | Ver Calendario | Días con actividades muestran puntos de color. |
| TC-13.1 | HU-13 / CA-13.1 | Salida | Certificado generado | Pulsar Descargar | Se abre visor de PDF con el documento generado. |
| TC-14.1 | HU-14 / CA-14.1 | Funcional | Formulario Novedad | Adjuntar imagen y enviar | Estado de la novedad aparece como "Enviado". |
| TC-15.1 | HU-15 / CA-15.1 | Accesibilidad | Tema sistema: Dark | Abrir App | La interfaz cambia automáticamente a colores oscuros. |
