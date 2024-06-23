package com.example.batmobile.fragments.neulogovan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.batmobile.R
import com.example.batmobile.activities.LoginActivity

class ProfileNeulogovanFragment : Fragment() {
    private lateinit var view: View
    private lateinit var back_button: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view =inflater.inflate(R.layout.fragment_profile_neulogovan, container, false)
        back_button = view.findViewById<Button>(R.id.back)
        back_button.setOnClickListener{returnToRegister()}
        return view
    }

    fun returnToRegister(){
        val intent: Intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}