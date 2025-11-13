package bob.colbaskin.umirhack7.point_picker.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import bob.colbaskin.umirhack7.common.design_system.theme.UMIRHack7Theme
import dagger.hilt.android.AndroidEntryPoint
import org.maplibre.android.MapLibre

@AndroidEntryPoint
class PointPickerActivity : ComponentActivity() {

    private val viewModel: PointPickerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(this)

        val zoneId = intent.getIntExtra(EXTRA_ZONE_ID, -1)
        if (zoneId == -1) {
            finish()
            return
        }

        setContent {
            UMIRHack7Theme {
                val state = viewModel.state

                PointPickerScreen(
                    state = state,
                    onAction = viewModel::onAction,
                    onBack = { finish() },
                    onConfirm = { point ->
                        setResult(RESULT_OK, Intent().apply {
                            putExtra(RESULT_POINT_LATITUDE, point.latitude)
                            putExtra(RESULT_POINT_LONGITUDE, point.longitude)
                        })
                        finish()
                    }
                )
            }
        }

        viewModel.onAction(PointPickerAction.LoadZoneData(zoneId))
    }

    companion object {
        private const val EXTRA_ZONE_ID = "extra_zone_id"
        private const val RESULT_POINT_LATITUDE = "result_point_latitude"
        private const val RESULT_POINT_LONGITUDE = "result_point_longitude"

        fun createIntent(context: Context, zoneId: Int): Intent {
            return Intent(context, PointPickerActivity::class.java).apply {
                putExtra(EXTRA_ZONE_ID, zoneId)
            }
        }

        fun getResultPoint(data: Intent): android.location.Location {
            return android.location.Location("").apply {
                latitude = data.getDoubleExtra(RESULT_POINT_LATITUDE, 0.0)
                longitude = data.getDoubleExtra(RESULT_POINT_LONGITUDE, 0.0)
            }
        }
    }
}
