package com.writebugs.videochanger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.writebugs.videochanger.ui.AppRoot
import com.writebugs.videochanger.ui.VideoChangerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoChangerTheme {
                AppRoot(activity = this)
            }
        }
    }
}
