# Mi Formación CTMA

## Descripción del proyecto

Mi Formación CTMA es una aplicación móvil desarrollada para Android con Kotlin y Jetpack Compose. Su propósito es ayudar a los aprendices del SENA a organizar actividades, compromisos y evidencias de su proceso formativo en un solo lugar. La aplicación permitirá llevar un mejor control de las tareas asignadas, consultar fechas importantes y facilitar el seguimiento del avance académico de manera sencilla y organizada.

---

# Problema

Actualmente los aprendices administran sus actividades, enlaces, evidencias y fechas en diferentes plataformas, lo que genera desorganización, pérdida de información y dificultad para hacer seguimiento a su proceso de formación. Mi Formación CTMA busca centralizar esta información en una sola aplicación móvil para mejorar la organización, la productividad y la trazabilidad del aprendizaje.

---

# Usuarios

## 1. Aprendiz
**Necesidad:** Consultar actividades, registrar evidencias, bitácoras y hacer seguimiento a su progreso académico.

## 2. Instructor
**Necesidad:** Publicar actividades, establecer fechas de entrega y realizar seguimiento al avance de los aprendices.

---

# Historias de Usuario (Product Backlog)

### HU-01 - Visualización y Estado de Actividades (Mejorada)
**Como** aprendiz, **quiero** consultar mis actividades pendientes con estados claros (Pendiente, En Proceso, Completada), **para** organizar mejor mi tiempo y cumplir con las fechas establecidas.
- **CA-01.1:** Lista con colores según prioridad y estado.
- **CA-01.2:** Indicador visual de días restantes.

### HU-02 - Creación de Actividades (Mejorada)
**Como** instructor, **quiero** publicar nuevas actividades con fechas de inicio/fin y prioridad, **para** que los aprendices puedan consultarlas desde la aplicación.
- **CA-02.1:** Validación de fecha fin posterior a inicio.
- **CA-02.2:** Selección de prioridad (Alta, Media, Baja).

### HU-03 - Entrega de Evidencias Digitales (Mejorada)
**Como** aprendiz, **quiero** registrar las evidencias de las actividades realizadas, **para** llevar un control de mi progreso durante la formación.
- **CA-03.1:** El aprendiz puede marcar una actividad como completada al subir el link.
- **CA-03.2:** Validación de formato URL para la evidencia.

### HU-04 - Registro de Bitácora Diaria
**Como** aprendiz, **quiero** registrar mis tareas diarias y horas dedicadas, **para** cumplir con el seguimiento de mi etapa productiva.
- **CA-04.1:** Registro de fecha, descripción y horas.
- **CA-04.2:** Validación de horas mayores a cero.

### HU-05 - Control de Asistencia QR
**Como** aprendiz, **quiero** registrar mi asistencia escaneando un código QR, **para** agilizar el proceso de control en el ambiente de formación.
- **CA-05.1:** Escaneo de código QR generado por el instructor.

### HU-06 - Autenticación y Perfil SENA
**Como** usuario, **quiero** iniciar sesión con mis credenciales institucionales, **para** acceder a mi información de forma segura.

### HU-07 - Dashboard de Progreso
**Como** aprendiz, **quiero** ver un resumen gráfico de mi avance académico, **para** visualizar mis competencias alcanzadas.

### HU-08 - Notificaciones Push de Vencimiento
**Como** aprendiz, **quiero** recibir alertas sobre entregas próximas a vencer, **para** no olvidar mis compromisos.

### HU-09 - Filtros y Búsqueda Avanzada
**Como** aprendiz, **quiero** buscar actividades por competencia o estado, **para** encontrar información rápidamente.

### HU-10 - Modo Offline (Sincronización)
**Como** aprendiz, **quiero** registrar datos sin conexión a internet, **para** que se sincronicen automáticamente al recuperar el acceso.

### HU-11 - Gestión de Retroalimentación
**Como** aprendiz, **quiero** ver los comentarios del instructor sobre mis evidencias, **para** mejorar mi proceso de aprendizaje.

### HU-12 - Calendario Formativo
**Como** aprendiz, **quiero** ver mis actividades en una vista de calendario, **para** visualizar mi carga académica mensual.

### HU-13 - Descarga de Certificados
**Como** aprendiz, **quiero** descargar mis certificados de asistencia y notas, **para** utilizarlos en trámites administrativos.

### HU-14 - Reporte de Novedades e Inasistencias
**Como** aprendiz, **quiero** reportar excusas médicas o permisos, **para** justificar mis inasistencias de forma oficial.

### HU-15 - Preferencias y Accesibilidad
**Como** usuario, **quiero** ajustar el tema (oscuro/claro) y el tamaño de fuente, **para** mejorar mi experiencia de uso.

---

# Tecnologías utilizadas

- Kotlin, Jetpack Compose, Clean Architecture, MVVM, UDF, Retrofit, OkHttp, MockWebServer, Espresso.

---

# Evolución del Proyecto

## Semana 5: Implementación Integral (Clean Architecture & UDF)
- **Agile Coaching:** Definición y estructuración de 15 Historias de Usuario con estándares profesionales.
- **Testing Estratégico:** Implementación de pruebas unitarias y de UI automatizadas para el flujo de Bitácora y Actividades.
- **Documentación Técnica:** Creación de registros de Riesgos y Plan de Pruebas centralizado.
- **Arquitectura Robusta:** Refactorización a Clean Architecture con manejo de estado unidireccional (UDF).
