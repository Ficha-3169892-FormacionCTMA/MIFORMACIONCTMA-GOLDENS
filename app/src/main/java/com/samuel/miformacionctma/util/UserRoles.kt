package com.samuel.miformacionctma.util

object UserRoles {
    const val APRENDIZ = "aprendiz"
    const val INSTRUCTOR = "instructor"

    fun isInstructor(role: String?): Boolean =
        role?.equals(INSTRUCTOR, ignoreCase = true) == true

    fun isAprendiz(role: String?): Boolean =
        role?.equals(APRENDIZ, ignoreCase = true) == true
}
