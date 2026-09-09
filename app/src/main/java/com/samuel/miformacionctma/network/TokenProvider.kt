package com.samuel.miformacionctma.network

interface TokenProvider {
    fun getToken(): String?
}
