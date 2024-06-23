package com.example.batmobile.services

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.AsyncTask
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.batmobile.DTOFromServer.Seller
import com.example.batmobile.R
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.infowindow.InfoWindow

class Map {
    companion object{

        fun setMap(mapView: MapView, latitude:Double, longitude:Double, activity: Activity, context: Context){
            Configuration.getInstance().userAgentValue = activity.packageName
            // Postavite parametre mape
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setBuiltInZoomControls(true)

            val newPoint = GeoPoint(latitude, longitude)
            mapView.controller.setCenter(newPoint)
            mapView.controller.setZoom(13.0)
            // Dodavanje pina na tacnu lokaciju
            val items = ArrayList<OverlayItem>()
            val overlayItem = OverlayItem("Lokacija", "Lokacija domacinstva", newPoint)
            overlayItem.setMarker(ContextCompat.getDrawable(context, R.drawable.location_pin))
            items.add(overlayItem)

            val overlay = ItemizedIconOverlay<OverlayItem>(items, null, context)
            mapView.overlays.clear()
            mapView.overlays.add(overlay)
        }

        fun setInitMap(mapView: MapView, context: Context){
            Configuration.getInstance().userAgentValue = context.packageName
            // Postavite parametre mape
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setBuiltInZoomControls(true)
            mapView.controller.setZoom(8.0)

            // Postavite početnu tačku mape (npr. Beograd)
            val startPoint = GeoPoint(44.016521, 21.005859)
            mapView.controller.setCenter(startPoint)
        }

        class RoutingTask(
            private val context: Context,
            private val mapView: MapView,
            private val startPoint: GeoPoint,
            private val endPoint: GeoPoint
        ) : AsyncTask<Void, Void, Road>() {

            override fun doInBackground(vararg params: Void): Road? {
                try {
                    val roadManager: RoadManager = OSRMRoadManager(context)

                    val waypoints = ArrayList<GeoPoint>()
                    waypoints.add(startPoint)
                    waypoints.add(endPoint)

                    return roadManager.getRoad(waypoints)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }

            override fun onPostExecute(result: Road?) {
                super.onPostExecute(result)

                if (result != null) {
                    val roadOverlay = RoadManager.buildRoadOverlay(result)
                    mapView.overlays.add(roadOverlay)

                    val pinOverlay = createPinOverlay()
                    mapView.overlays.add(pinOverlay)
                    if(result.mNodes.size > 0)
                        mapView.controller.setCenter(result.mNodes[0].mLocation)
                }
            }
            private fun createPinOverlay(): ItemizedIconOverlay<OverlayItem> {
                val pinOverlay = ItemizedIconOverlay<OverlayItem>(
                    context, ArrayList<OverlayItem>(),
                    null
                )

                val startPin = OverlayItem("Start", "Starting Point", startPoint)
                startPin.setMarker(context.resources.getDrawable(R.drawable.start_flag))
                pinOverlay.addItem(startPin)

                val endPin = OverlayItem("End", "Ending Point", endPoint)
                endPin.setMarker(context.resources.getDrawable(R.drawable.end_flag))
                pinOverlay.addItem(endPin)

                return pinOverlay
            }
        }

    }
}