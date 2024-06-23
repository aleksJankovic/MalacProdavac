package com.example.batmobile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.Navigation
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.batmobile.R

class NeulogovanActivity : AppCompatActivity() {

    private lateinit var fragment: FragmentContainerView
    private lateinit var bottomNavigation: MeowBottomNavigation
    private fun getComponent(){
        fragment = findViewById<FragmentContainerView>(R.id.fragmentContainerNeulogovan)

        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.show(1,true)
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.home))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.explore_menu))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.inspect))
        bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.profile))
    }

    private fun setOnClickMenu(model: MeowBottomNavigation.Model){
        when (model.id) {
            1 -> { Navigation.findNavController(fragment).navigate(R.id.action_HomeNeulogovanFragment) }
            2 -> { Navigation.findNavController(fragment).navigate(R.id.action_ExploreNeulogovanFragment) }
            3 -> { Navigation.findNavController(fragment).navigate(R.id.action_InformationNeulogovanFragment) }
            4 -> { Navigation.findNavController(fragment).navigate(R.id.action_ProfileNeulogovanFragment) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_neulogovan)

        getComponent()

        bottomNavigation.setOnClickMenuListener { model -> setOnClickMenu(model) }

    }
}

