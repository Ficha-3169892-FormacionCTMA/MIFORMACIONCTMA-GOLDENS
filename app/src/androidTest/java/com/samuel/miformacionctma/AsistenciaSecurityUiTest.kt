package com.samuel.miformacionctma

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AsistenciaSecurityUiTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun verificarBloqueoUIAlRecibirError403DeAutorizacion() {
        // Nota: Estos IDs deben estar vinculados a la UI (Compose o XML)
        // En Compose se pueden vincular usando Modifier.semantics { testTag = "..." } 
        // o mediante resourceId en vistas tradicionales.
        
        // 1. Ingresar credenciales del Aprendiz A
        onView(withId(R.id.et_documento)).perform(typeText("1001"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("Sena2026*"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        // 2. Intentar consultar la ficha/asistencia del Aprendiz B (1002)
        onView(withId(R.id.et_buscar_aprendiz)).perform(typeText("1002"), closeSoftKeyboard())
        onView(withId(R.id.btn_consultar)).perform(click())

        // 3. Asercion de Interfaz: Confirmar que la app despliega el mensaje de acceso denegado
        onView(withId(R.id.tv_error_status))
            .check(matches(isDisplayed()))
            .check(matches(withText("Acceso Denegado (403): No tiene permisos para consultar este registro")))
    }
}
