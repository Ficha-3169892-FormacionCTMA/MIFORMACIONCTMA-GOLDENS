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
**Como** aprendiz, **quiero** consultar mis actividades pendientes, **para** organizar mejor mi tiempo y cumplir con las fechas establecidas.

## Historia 2
**Como** instructor, **quiero** publicar nuevas actividades, **para** que los aprendices puedan consultarlas desde la aplicación.

## Historia 3
**Como** aprendiz, **quiero** registrar las evidencias de las actividades realizadas, **para** llevar un control de mi progreso durante la formación.

---

# Tecnologías utilizadas

- Kotlin & Coroutines
- Jetpack Compose (Material 3)
- Navigation Compose
- Architecture Components (ViewModel, StateFlow)
- Retrofit 2 & OkHttp 3 (Networking)
- MockWebServer (Test API mocking)
- Espresso (UI Testing)
- JUnit 4

---

# Estado del proyecto

✅ Proyecto en desarrollo - Semana 4 (Networking & Seguridad)

---

# Registro de Incrementos

## Semana 2 - Fundamentos
- Estructura base de paquetes (`model`, `ui`).
- Implementación de lógica de negocio inicial y modelo `ActividadFormativa`.

## Semana 3 - Calidad y Accesibilidad
- Diseño de matriz de trazabilidad y casos de prueba.
- Implementación de `TarjetaActividad` con soporte para accesibilidad (TalkBack).

## Semana 4 (Parte A) - Estado y Navegación
- **UDF (Unidirectional Data Flow):** Separación de UI State y Eventos.
- **Navigation:** Implementación de rutas Lista -> Formulario -> Detalle.
- **Formulario:** Validaciones síncronas de fechas, progreso y campos obligatorios.

## Semana 4 (Parte B) - Networking, Monitoreo y Pruebas HTTP
En esta fase se integraron componentes de red profesionales y se robusteció el pipeline de pruebas.

### 📶 Monitoreo HTTP (ApiClient)
- **DevTools Network en Android:** Se implementó `HttpLoggingInterceptor` para inspeccionar peticiones, encabezados y payloads directamente en el Logcat.
- **Seguridad:** Interceptor de red para adjuntar automáticamente el Token Bearer desde el `SessionManager`.
- **Retrofit:** Configuración centralizada para la API de Staging.

### 🧪 Estrategia de Pruebas (8 Test Pass)
Se implementaron pruebas en tres niveles de la pirámide de automatización:
1. **Pruebas Unitarias de Negocio (`ActividadPruebasTest`):** Validación de reglas de estado basadas en el progreso.
2. **Pruebas de Servicios API (`BitacoraApiTest`):**
   - Uso de **MockWebServer** para simular fallos de servidor.
   - Verificación de manejo de errores HTTP 403 (Forbidden) y 422 (Unprocessable Content).
3. **Pruebas de Interfaz (`AsistenciaSecurityUiTest`):**
   - Automatización con **Espresso** en emulador.
   - Verificación de feedback visual (Mensajes de error en UI) ante bloqueos de seguridad.

---
*Ultima verificación de compilación: Exitosa (8 unit tests passed)*
