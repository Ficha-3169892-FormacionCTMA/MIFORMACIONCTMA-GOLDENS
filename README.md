# Mi Formación CTMA

## Descripción del proyecto

Mi Formación CTMA es una aplicación móvil desarrollada para Android con Kotlin y Jetpack Compose. Su propósito es ayudar a los aprendices del SENA a organizar actividades, compromisos y evidencias de su proceso formativo en un solo lugar. La aplicación permitirá llevar un mejor control de las tareas asignadas, consultar fechas importantes y facilitar el seguimiento del avance académico de manera sencilla y organizada.

---

# Problema

Actualmente los aprendices administran sus actividades, enlaces, evidencias y fechas en diferentes plataformas, lo que genera desorganización, pérdida de información y dificultad para hacer seguimiento a su proceso de formación. Mi Formación CTMA busca centralizar esta información en una sola aplicación móvil para mejorar la organización, la productividad y la trazabilidad del aprendizaje.

---

# Usuarios

## 1. Aprendiz

**Necesidad:**
Consultar actividades, registrar evidencias y hacer seguimiento a su progreso académico.

## 2. Instructor

**Necesidad:**
Publicar actividades, establecer fechas de entrega y realizar seguimiento al avance de los aprendices.

---

# Historias de Usuario

## Historia 1

**Como** aprendiz,

**quiero** consultar mis actividades pendientes,

**para** organizar mejor mi tiempo y cumplir con las fechas establecidas.

### Criterio de aceptación

- La aplicación debe mostrar una lista con las actividades registradas.
- El aprendiz puede visualizar el nombre y la fecha de cada actividad.

---

## Historia 2

**Como** instructor,

**quiero** publicar nuevas actividades,

**para** que los aprendices puedan consultarlas desde la aplicación.

### Criterio de aceptación

- El instructor puede crear una nueva actividad.
- La actividad debe quedar disponible para ser consultada por los aprendices.

---

## Historia 3

**Como** aprendiz,

**quiero** registrar las evidencias de las actividades realizadas,

**para** llevar un control de mi progreso durante la formación.

### Criterio de aceptación

- El aprendiz puede marcar una actividad como completada.
- La aplicación debe reflejar el cambio de estado de la actividad.

---

# Tecnologías utilizadas

- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3
- Navigation Compose
- Lifecycle ViewModel
- Gradle
- Git
- GitHub

---

# Estado del proyecto

✅ Proyecto en desarrollo - Semana 4 (Incremento Interactivo)

Actualmente la aplicación cuenta con:

- Arquitectura Unidirectional Data Flow (UDF).
- Flujo de navegación multi-pantalla.
- Formulario con validaciones en tiempo real.
- Gestión de estado con ViewModel.
- Pantalla de detalle dinámica.

---

# Semana 2 - Fundamentos
- Se creó el paquete model.
- Se implementó la clase ActividadFormativa.
- Se creó el enum Prioridad.
- Se desarrollaron las reglas de negocio iniciales.

---

# Semana 3 - Calidad y Pruebas
- Diseño sistemático de 12 casos de prueba.
- Ejecución de pruebas unitarias y reporte de defectos.
- Implementación de `TarjetaActividad` con Material 3 y accesibilidad.

---

# Semana 4 - Estado, Formularios y Navegación

En esta etapa se implementó un flujo Android multipantalla completo, gestionando estado observable y eventos mediante UDF.

## 🚀 Implementaciones Clave

### 1. Arquitectura UDF (Unidirectional Data Flow)
- **UI State:** Se creó `FormularioActividadUiState` para manejar el estado del formulario de forma atómica e inmutable, incluyendo lógica de validación para título, descripción, progreso y fechas.
- **Eventos:** Se implementó una `sealed interface FormularioActividadEvento` para canalizar todas las acciones del usuario (cambios de texto, guardar, cancelar) hacia el ViewModel.

### 2. Gestión de Estado con ViewModel
- Implementación de `ActividadViewModel` para desacoplar la lógica de negocio de la UI.
- Uso de `StateFlow` para exponer la lista de actividades y el estado del formulario.
- Persistencia del estado ante cambios de configuración (rotación de pantalla).

### 3. Navegación con Navigation Compose
Se configuró un `NavHost` con tres destinos principales:
- **Lista:** Visualización de todas las actividades.
- **Formulario:** Creación de nuevas actividades con validación obligatoria.
- **Detalle:** Vista ampliada de una actividad seleccionada por su ID.

### 4. Validaciones y Reglas de Negocio
- **Título:** Obligatorio (3-80 caracteres).
- **Fechas:** Validación de formato y coherencia cronológica (Fin no menor a Inicio).
- **Progreso:** Restringido numéricamente entre 0 y 100.
- **Protección de Doble Toque:** Deshabilitación del botón de guardado durante el procesamiento.

### 5. Adaptación del Modelo
- Evolución de `ActividadFormativa` para soportar `LocalDate` y IDs de tipo `Long`.
- Actualización de `ReglasActividad` para simplificar el cálculo de estados basado en el progreso.
