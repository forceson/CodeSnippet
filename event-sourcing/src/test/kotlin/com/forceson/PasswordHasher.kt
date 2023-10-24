package com.forceson

interface PasswordHasher {
    fun hashPassword(password: String): String
}