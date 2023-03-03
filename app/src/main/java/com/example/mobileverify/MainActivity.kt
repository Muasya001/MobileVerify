package com.example.mobileverify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var edtPhone: EditText? = null
    private var edtOTP: EditText? = null
    private var VerifyOTPBTN: Button? = null
    private var generateOTPBTN: Button? = null
    private var verificationID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        edtPhone= findViewById(R.id.idEdtPhoneNum)
        edtOTP= findViewById(R.id.idEdtOP)
        VerifyOTPBTN= findViewById(R.id.idBTNverify)
        generateOTPBTN= findViewById(R.id.idBtnGetOTP)

        generateOTPBTN!!.setOnClickListener{
            if (TextUtils.isEmpty(edtPhone!!.text.toString())){
                Toast.makeText(this@MainActivity,
                    "Please Enter a valid number", Toast.LENGTH_SHORT).show()
            }
            else{
                val phone = "+254" + edtPhone!!.text.toString()
                sendVerificationCode(phone)
            }
        }
        VerifyOTPBTN!!.setOnClickListener{
            if (TextUtils.isEmpty(edtPhone!!.text.toString())){
                Toast.makeText(this@MainActivity,
                    "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
            else{
                val phone = "+254" + edtPhone!!.text.toString()
                verifyCode(edtOTP!!.text.toString())
            }

        }
    }
    private fun signInWithCredentials(credential: PhoneAuthCredential){
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(object :OnCompleteListener<AuthResult?>{
                override fun onComplete(task: Task<AuthResult?>) {
                    if (task.isSuccessful){
                        val i = Intent(this@MainActivity,HomeActivity::class.java)
                        startActivity(i)
                        finish()
                }else{
                    Toast.makeText(this@MainActivity,
                        task.exception!!.message,
                            Toast.LENGTH_SHORT
                    ).show()
                    }
                }
            })


    }
    private fun sendVerificationCode(number: String){
        val options : PhoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(number)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private val mCallBack : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onCodeSent(s: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(s, p1)
            verificationID = s
        }
        override fun onVerificationCompleted(PhoneAuthCredential: PhoneAuthCredential) {
            val code : String = PhoneAuthCredential.smsCode!!
            if (code!=null){
                edtOTP!!.setText(code)
                verifyCode(code)
            }

        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@MainActivity,
                e.message,
                Toast.LENGTH_SHORT).show()
        }
        }
    private fun verifyCode(code:String){
        val credential: PhoneAuthCredential = PhoneAuthProvider
            .getCredential(verificationID!!,code)
        signInWithCredentials(credential)

    }

}
