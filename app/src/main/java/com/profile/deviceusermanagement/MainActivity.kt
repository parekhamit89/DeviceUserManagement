package com.profile.deviceusermanagement



import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.profile.deviceusermanagement.databinding.ActivityMainBinding
import kotlinx.coroutines.NonCancellable.cancel


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var deviceUserManager: DeviceUserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

       /* binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/


        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.areAllPermissionsGranted()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {

                    if (token != null) {
                        showPermissionRationale(token)
                    }
                }
            }).check()
    }


    fun showPermissionRationale(token: PermissionToken) {
        AlertDialog.Builder(this).setTitle(R.string.permission_rationale_title)
            .setMessage(R.string.permission_rationale_message)
            .setNegativeButton(
                R.string.cancel,
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    token.cancelPermissionRequest()
                })
            .setPositiveButton(
                R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    token.continuePermissionRequest()
                })
            .setOnDismissListener(DialogInterface.OnDismissListener { token.cancelPermissionRequest() })
            .show()
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}