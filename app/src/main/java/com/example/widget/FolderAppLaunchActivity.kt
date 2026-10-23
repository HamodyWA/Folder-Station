package com.example.widget

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.R

class FolderAppLaunchActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        val activityName = intent.getStringExtra(EXTRA_ACTIVITY_NAME)

        if (!packageName.isNullOrBlank()) {
            try {
                val launchIntent = if (!activityName.isNullOrBlank()) {
                    Intent().apply {
                        setClassName(packageName, activityName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    }
                } else {
                    packageManager.getLaunchIntentForPackage(packageName)?.apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    }
                }

                if (launchIntent != null) {
                    startActivity(launchIntent)
                } else {
                    Toast.makeText(this, getString(R.string.app_not_found), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.app_not_found), Toast.LENGTH_SHORT).show()
            }
        }

        finish()
    }

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_ACTIVITY_NAME = "extra_activity_name"
    }
}
