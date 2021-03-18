package com.foobarust.deliverer.data.mappers

import com.foobarust.deliverer.data.models.AuthProfile
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

/**
 * Created by kevin on 9/21/20
 */

class AuthMapper @Inject constructor() {

    fun toAuthProfile(firebaseUser: FirebaseUser): AuthProfile {
        val email = firebaseUser.email!!
        return AuthProfile(
            id = firebaseUser.uid,
            email = email,
            username = getUsernameFromEmail(email)
        )
    }

    private fun getUsernameFromEmail(email: String): String {
        return email.substring(0, email.indexOf('@'))
    }
}