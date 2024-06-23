package com.example.batmobile.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.Navigation
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.batmobile.R

class KupacActivity : AppCompatActivity() {

    private lateinit var fragment: FragmentContainerView
    private lateinit var bottomNavigation: MeowBottomNavigation
    private fun getComponent(){
        fragment = findViewById<FragmentContainerView>(R.id.fragmentContainerKupac)

        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.show(1,true)
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.home))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.explore_menu))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.korpa))
        bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.profile))
    }

    private fun setOnClickMenu(model: MeowBottomNavigation.Model){
        when (model.id) {
            1 -> { Navigation.findNavController(fragment).navigate(R.id.action_kupacHomeOld) }
            2 -> { Navigation.findNavController(fragment).navigate(R.id.action_ExploreNeulogovanFragment) /*Navigation.findNavController(fragment).navigate(R.id.action_kupacExplore)*/ }
            3 -> { Navigation.findNavController(fragment).navigate(R.id.action_kupacKorpa) }
            4 -> { Navigation.findNavController(fragment).navigate(R.id.action_kupacProfile) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kupac)
        getComponent()
        bottomNavigation.setOnClickMenuListener { model -> setOnClickMenu(model) }
    }
}