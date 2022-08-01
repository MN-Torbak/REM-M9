package com.openclassrooms.firebaseREM

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnFailureListener
import com.openclassrooms.firebaseREM.viewmodel.MainViewModel
import java.util.*

private const val RC_SIGN_IN = 123

class LoginActivity : AppCompatActivity() {

    var mMainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        if (this.isCurrentUserLogged() == true) {
            //this.startDrawerActivity() TODO: doit renvoyer vers la ItemActivity
        } else {
            startSignInActivity()
        }
    }

    private fun startSignInActivity() {

        // Choose authentication providers
        val providers: List<IdpConfig> = Collections.singletonList(EmailBuilder().build())

        // Launch the activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_launcher_background)
                        .build(),
                RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handleResponseAfterSignIn(requestCode, resultCode, data!!)
    }

    private fun handleResponseAfterSignIn(requestCode: Int, resultCode: Int, data: Intent) {
        val response = IdpResponse.fromResultIntent(data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                if (response != null && response.isNewUser) {
                    this.createUserInFirestore()
                }
                //startDrawerActivity() TODO: doit renvoyer vers la ItemActivity
            } else {
                if (response != null && response.error != null) {
                    Toast.makeText(applicationContext, toFriendlyMessage(response.error!!.errorCode), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun createUserInFirestore() {
        if (this.mMainViewModel?.getCurrentUser() != null) {
            val id: String? = this.mMainViewModel?.getCurrentUser()!!.id
            val avatar = if (this.mMainViewModel?.getCurrentUser()!!.avatar != null) this.mMainViewModel?.getCurrentUser()?.avatar else "https://www.icône.com/images/icones/2/9/face-plain-2.png"
            val name: String? = this.mMainViewModel?.getCurrentUser()!!.name
            mMainViewModel?.createUser(id, avatar, name)?.addOnFailureListener(this.onFailureListener())
        }
    }

    protected fun isCurrentUserLogged(): Boolean {
        return mMainViewModel?.getCurrentUser() != null
    }

    protected fun onFailureListener(): OnFailureListener {
        return OnFailureListener { Toast.makeText(applicationContext, getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show() }
    }

    fun toFriendlyMessage(@ErrorCodes.Code code: Int): Int {
        return when (code) {
            ErrorCodes.UNKNOWN_ERROR -> R.string.unknown_error
            ErrorCodes.NO_NETWORK -> R.string.no_internet_connection
            ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED -> R.string.play_services_update_cancelled
            ErrorCodes.DEVELOPER_ERROR -> R.string.developer_error
            ErrorCodes.PROVIDER_ERROR -> R.string.provider_error
            ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT -> R.string.user_account_merge_conflict
            ErrorCodes.EMAIL_MISMATCH_ERROR -> R.string.attempting_to_sign_in_different_email
            ErrorCodes.INVALID_EMAIL_LINK_ERROR -> R.string.attempting_to_sign_with_an_invalid_email_link
            ErrorCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_ERROR -> R.string.please_enter_your_email_to_continue_signing_in
            ErrorCodes.EMAIL_LINK_WRONG_DEVICE_ERROR -> R.string.you_must_open_the_email_link_on_the_same_device
            ErrorCodes.EMAIL_LINK_CROSS_DEVICE_LINKING_ERROR -> R.string.you_must_determine_if_you_want_continue_linking_or_complete_the_sign_in
            ErrorCodes.EMAIL_LINK_DIFFERENT_ANONYMOUS_USER_ERROR -> R.string.the_session_associated_with_this_sign_in_request_has_either_expired_or_was_cleared
            ErrorCodes.ERROR_USER_DISABLED -> R.string.the_user_account_has_been_disabled_by_an_administrator
            ErrorCodes.ERROR_GENERIC_IDP_RECOVERABLE_ERROR -> R.string.generic_idp_recoverable_error
            else -> throw IllegalArgumentException(java.lang.String.valueOf(R.string.unknown_code + code))
        }
    }
}