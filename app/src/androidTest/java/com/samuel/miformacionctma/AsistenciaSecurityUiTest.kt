package com.samuel.miformacionctma

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AsistenciaSecurityUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun verificarBloqueoUIAlRecibirError403DeAutorizacion() {
        // 1. Ingresar credenciales del Aprendiz A
        composeTestRule.onNodeWithTag("et_documento").performTextInput("1001")
        composeTestRule.onNodeWithTag("et_password").performTextInput("Sena2026*")
        composeTestRule.onNodeWithTag("btn_login").performClick()

        // 2. Intentar consultar la ficha/asistencia del Aprendiz B (1002)
        composeTestRule.onNodeWithTag("et_buscar_aprendiz").performTextInput("1002")
        composeTestRule.onNodeWithTag("btn_consultar").performClick()

        // 3. Aserción de Interfaz: Confirmar que la app despliega el mensaje de acceso denegado
        composeTestRule.onNodeWithTag("tv_error_status")
            .assertIsDisplayed()
            .assertTextEquals("Acceso Denegado (403): No tiene permisos para consultar este registro")
    }
}