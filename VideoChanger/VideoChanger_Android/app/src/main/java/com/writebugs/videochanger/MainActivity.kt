package com.writebugs.videochanger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.writebugs.videochanger.ui.AppRoot
import com.writebugs.videochanger.ui.VideoChangerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 浅色主题下状态栏/导航栏用深色图标，避免白图标叠在白底上看不清。
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent {
            VideoChangerTheme {
                AppRoot(activity = this)
            }
        }
    }
}
