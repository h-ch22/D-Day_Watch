package com.cj.d_daywatch.settings.ui

import android.content.Intent
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.cj.d_daywatch.R
import com.cj.d_daywatch.frameworks.helper.AES256Util
import com.cj.d_daywatch.frameworks.helper.DataStoreUtil
import com.cj.d_daywatch.frameworks.ui.MainActivity
import com.cj.d_daywatch.frameworks.ui.StartActivity
import com.cj.d_daywatch.settings.models.SettingsAlertTypeModel
import com.cj.d_daywatch.ui.theme.DDayWatchTheme
import com.cj.d_daywatch.userManagement.helper.UserManagement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(parent: NavHostController){
    var showProgress by remember {
        mutableStateOf(false)
    }

    var showAlert by remember {
        mutableStateOf(false)
    }
    
    var alertType by remember {
        mutableStateOf<SettingsAlertTypeModel?>(null)
    }

    val context = LocalContext.current
    val helper = UserManagement()
    val dataStoreUtil = DataStoreUtil(context)

    DDayWatchTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = stringResource(id = R.string.TXT_SETTINGS)) },
                    navigationIcon = {
                        IconButton(onClick = { parent.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null
                            )
                        }
                    })
            }
        ) {
            Surface(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    ElevatedButton(onClick = {
                        alertType = SettingsAlertTypeModel.CONFIRM_SIGN_OUT
                        showAlert = true
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = null)

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(text = stringResource(id = R.string.TXT_SIGN_OUT))

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    ElevatedButton(onClick = {
                        alertType = SettingsAlertTypeModel.CONFIRM_CANCEL_MEMBERSHIP
                        showAlert = true
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(imageVector = Icons.Rounded.Cancel, contentDescription = null)
                        Spacer(modifier = Modifier.width(5.dp))

                        Text(text = stringResource(id = R.string.TXT_CANCEL_MEMBERSHIP))

                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                if(showAlert && alertType != null){
                    AlertDialog(onDismissRequest = { showAlert = false },
                        confirmButton = {
                            TextButton(onClick = {
                                showProgress = true

                                if(alertType == SettingsAlertTypeModel.CONFIRM_SIGN_OUT){
                                    helper.signOut {
                                        showProgress = false

                                        alertType = if(it) SettingsAlertTypeModel.SIGN_OUT_SUCCESS else SettingsAlertTypeModel.SIGN_OUT_FAIL
                                        showAlert = true
                                    }
                                } else if(alertType == SettingsAlertTypeModel.CONFIRM_CANCEL_MEMBERSHIP){
                                    helper.cancelMembership {
                                        showProgress = false

                                        alertType = if(it) SettingsAlertTypeModel.CANCEL_MEMBERSHIP_SUCCESS else SettingsAlertTypeModel.CANCEL_MEMBERSHIP_FAIL
                                        showAlert = true
                                    }
                                } else if(alertType == SettingsAlertTypeModel.CANCEL_MEMBERSHIP_SUCCESS || alertType == SettingsAlertTypeModel.SIGN_OUT_SUCCESS){
                                    context.startActivity(Intent(context, StartActivity::class.java))
                                } else if(alertType == SettingsAlertTypeModel.SIGN_OUT_FAIL || alertType == SettingsAlertTypeModel.CANCEL_MEMBERSHIP_FAIL){
                                    showAlert = false
                                }
                            }) {
                                Text(text = stringResource(id = R.string.TXT_OK))
                            }
                        },
                        dismissButton = {
                            if(alertType == SettingsAlertTypeModel.CONFIRM_SIGN_OUT || alertType == SettingsAlertTypeModel.CONFIRM_CANCEL_MEMBERSHIP){
                                TextButton(onClick = {
                                    showAlert = false
                                }) {
                                    Text(text = stringResource(id = R.string.TXT_CANCEL))
                                }
                            }
                        },
                        title = {
                            Text(text = alertType!!.getTitle(context, alertType!!))
                        },
                        text = {
                            Text(text = alertType!!.getMessage(context, alertType!!))
                        },
                        icon = {
                            when(alertType){
                                SettingsAlertTypeModel.CONFIRM_SIGN_OUT,
                                SettingsAlertTypeModel.CONFIRM_CANCEL_MEMBERSHIP -> {
                                    Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                                }

                                SettingsAlertTypeModel.SIGN_OUT_SUCCESS,
                                    SettingsAlertTypeModel.CANCEL_MEMBERSHIP_SUCCESS -> {
                                    Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = null)
                                }

                                SettingsAlertTypeModel.SIGN_OUT_FAIL,
                                    SettingsAlertTypeModel.CANCEL_MEMBERSHIP_FAIL -> {
                                    Icon(imageVector = Icons.Rounded.Warning, contentDescription = null)
                                }

                                else ->
                                    Icon(imageVector = Icons.Rounded.Close, contentDescription = null)

                            }
                        })
                }

                if(showProgress){
                    Dialog(onDismissRequest = { showProgress = false }, properties = DialogProperties(false, false)) {
                        Box(modifier = Modifier
                            .size(80.dp)
                            .background(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                                RoundedCornerShape(15.dp)
                            )){
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }

                if(alertType == SettingsAlertTypeModel.SIGN_OUT_SUCCESS || alertType == SettingsAlertTypeModel.CANCEL_MEMBERSHIP_SUCCESS){
                    LaunchedEffect(key1 = true) {
                        dataStoreUtil.clearDataStore()

                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsViewPreview(){
    SettingsView(parent = rememberNavController())
}
