package com.forceson

class SimplePasswordHasher : PasswordHasher {
    override fun hashPassword(password: String): String {
        return password
    }
}