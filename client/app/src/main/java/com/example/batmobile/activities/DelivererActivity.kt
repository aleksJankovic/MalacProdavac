package com.example.batmobile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.Navigation
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.batmobile.R

class DelivererActivity : AppCompatActivity() {
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
            1 -> { Navigation.findNavController(fragment).navigate(R.id.action_HomeDelivererFragment) }
            2 -> { Navigation.findNavController(fragment).navigate(R.id.action_ExploreSeller)}
            3 -> { Navigation.findNavController(fragment).navigate(R.id.action_delivererKorpa) }
            4 -> { Navigation.findNavController(fragment).navigate(R.id.action_delivererProfile) }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deliverer)
        getComponent()
        bottomNavigation.setOnClickMenuListener { model -> setOnClickMenu(model) }
    }
}