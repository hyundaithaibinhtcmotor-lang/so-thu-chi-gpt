package vn.tung.taichinh
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import vn.tung.taichinh.data.FinanceDb
import vn.tung.taichinh.ui.FinanceApp

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val db = FinanceDb(this)
  setContent { MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF2864F0))) { FinanceApp(db) } }
 }
}
