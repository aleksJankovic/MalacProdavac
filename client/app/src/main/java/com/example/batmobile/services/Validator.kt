package com.example.batmobile.services

import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.example.batmobile.R

class Validator {
    companion object{
        public fun regexEmailValidationPattern(email:String): Boolean {
            val regexString: String =
                "([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})"
            val regex = Regex(regexString)
            if (email.matches(regex))
                return true
            return false
        }
        fun validateEmail(email: String): String {
            val email_text = email
            if(email_text.isEmpty()){
                return "*Morate uneti email adresu"
            }
            else if(!regexEmailValidationPattern(email_text)){
                return "*Uneta email adresa nije ispravna"
            }
            else {
                return "true"
            }
        }

        private fun regexPasswordValidationPattern(password:String): Boolean {
            val regexString: String = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}\$"
            val regex = Regex(regexString)
            return regex.matches(password)
        }

        fun validatePassword(password: String): String {
            val password_text = password
            if(password_text.isEmpty()){
                return "*Morate uneti neku šifru"
            }
            else if(!regexPasswordValidationPattern(password_text)){
                return "*Šifra mora sadržati 8+ znakova, velika i mala slova, brojeve"
            }
            else {
                return "true"
            }
        }

        fun validateUsername(username: String):Boolean{
            val regexString = "^[a-zA-Z0-9!@#\$%^&*()-_+=]*\$"
            val regex = Regex(regexString)
            return regex.matches(username)
        }

        fun validatePhoneNum(phoneNumber: String):Boolean{
            val regex = """^(\+3816[0-9]|06[0-9])\/?[0-9]{6,7}$""".toRegex()
            return regex.matches(phoneNumber)
        }

        private fun validateBasicInput(password: EditText): Boolean {
            val password_text = password.text.toString()

            if(password_text.isEmpty()){
                password.error = "Password cannot be empty"
                return false
            }
            else {
                password.error = null;
                return true
            }
        }
        fun authenticateLogin(username: EditText, password: EditText):Boolean{
            if( !validateBasicInput(username) || !validateBasicInput(password)){
                return false
            }
            return true
        }
        fun showPassword(view: View, input: EditText){
            val selectionStart = input.selectionStart
            val selectionEnd = input.selectionEnd

            val imageView = view as ImageView

            if (input.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                imageView.setImageResource(R.drawable.icons8_not_visible_96___)
            }
            else{
                input.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                imageView.setImageResource(R.drawable.icons8_visible_96___)
            }

            input.setSelection(selectionStart, selectionEnd)
        }

    }
}