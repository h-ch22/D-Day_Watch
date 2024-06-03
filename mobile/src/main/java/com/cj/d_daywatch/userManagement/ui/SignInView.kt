package com.cj.d_daywatch.userManagement.ui

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cj.d_daywatch.R
import com.cj.d_daywatch.frameworks.helper.AES256Util
import com.cj.d_daywatch.frameworks.helper.DataStoreUtil
import com.cj.d_daywatch.frameworks.ui.MainActivity
import com.cj.d_daywatch.ui.theme.DDayWatchColorPalette
import com.cj.d_daywatch.ui.theme.DDayWatchTheme
import com.cj.d_daywatch.ui.theme.gray
import com.cj.d_daywatch.userManagement.helper.UserManagement
import com.cj.d_daywatch.userManagement.models.AuthInfoModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SignInView() {
    var showProgress by remember {
        mutableStateOf(false)
    }

    var showEmailView by remember {
        mutableStateOf(false)
    }

    var showSignUpView by remember {
        mutableStateOf(false)
    }

    var showAlert by remember {
        mutableStateOf(false)
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var isSignedIn by remember {
        mutableStateOf(false)
    }

    var signInMethod by remember {
        mutableIntStateOf(0)
    }

    var userToken by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    val helper = UserManagement()
    val dataStoreUtil = DataStoreUtil(context)
    val signInToken = stringResource(id = R.string.WEB_CLIENT_ID)
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            helper.signInWithGoogle(
                activityResult = result,
                onSuccess = {token ->
                    showProgress = false
                    userToken = token
                    signInMethod = 0
                    isSignedIn = true
                }, onFail = {
                    showProgress = false
                    showAlert = true
                })
        }

    LaunchedEffect(key1 = true) {
        dataStoreUtil.getFromDataStore().collect{
            if(it.credential != ""){
                showProgress = true

                helper.signInWithToken(AES256Util.decrypt(it.credential)) {
                    showProgress = false

                    if (it) {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    } else {
                        showAlert = true
                    }
                }
            } else if(it.email != "" && it.password != ""){
                showProgress = true

                helper.signIn(
                    AES256Util.decrypt(it.email),
                    AES256Util.decrypt(it.password)
                ) {
                    showProgress = false

                    if (it) {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    } else {
                        showAlert = true
                    }
                }
            }
        }
    }

    BackHandler {
        Runtime.getRuntime().runFinalization()
    }

    DDayWatchTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_main),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(
                            RoundedCornerShape(15.dp)
                        )
                )

                Text(
                    text = stringResource(id = R.string.app_name).uppercase(Locale.ROOT),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.weight(1f))

                if (!showProgress && !showEmailView) {
                    Button(
                        onClick = {
                            showProgress = true

                            val googleSignInOptions = GoogleSignInOptions
                                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(signInToken)
                                .requestEmail()
                                .build()

                            val googleSignInClient =
                                GoogleSignIn.getClient(context, googleSignInOptions)

                            launcher.launch(googleSignInClient.signInIntent)
                        }
                    ) {
                        Image(
                            painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.android_dark_rd_na_4x else R.drawable.android_light_rd_na_4x),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )

                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = stringResource(id = R.string.TXT_SIGN_IN_WITH_GOOGLE))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(id = R.string.TXT_OR),
                        color = gray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = { showEmailView = true }) {
                        Icon(imageVector = Icons.Rounded.Mail, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = stringResource(id = R.string.TXT_SIGN_IN_WITH_EMAIL))
                    }
                } else if (showEmailView) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = gray,
                                shape = RoundedCornerShape(15.dp)
                            )
                            .padding(20.dp)
                    ) {
                        if (!showProgress) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                IconButton(onClick = {
                                    if (!showSignUpView) showEmailView = false else showSignUpView =
                                        false
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            }
                        }

                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.AlternateEmail,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(text = stringResource(id = R.string.TXT_EMAIL))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            enabled = !showProgress
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Key,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(text = stringResource(id = R.string.TXT_PASSWORD))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = PasswordVisualTransformation(),
                            enabled = !showProgress
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (!showProgress) {
                            if (!showSignUpView) {
                                Button(onClick = {
                                    showProgress = true

                                    helper.signIn(email, password) {
                                        showProgress = false

                                        if (!it) {
                                            showAlert = true
                                        } else {
                                            signInMethod = 1
                                            isSignedIn = true
                                        }
                                    }
                                }) {
                                    Text(text = stringResource(id = R.string.TXT_SIGNIN))
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = stringResource(id = R.string.TXT_OR),
                                    color = gray,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                TextButton(onClick = { showSignUpView = true }) {
                                    Text(text = stringResource(id = R.string.TXT_SIGNUP))
                                }
                            } else {
                                Button(onClick = {
                                    showProgress = true

                                    helper.signUp(email, password) {
                                        showProgress = false

                                        if (!it) {
                                            showAlert = true
                                        } else {
                                            signInMethod = 1
                                            isSignedIn = true
                                        }
                                    }
                                }) {
                                    Text(text = stringResource(id = R.string.TXT_SIGNUP))
                                }
                            }
                        } else {
                            CircularProgressIndicator()
                        }

                    }
                } else if (!showEmailView && !showSignUpView && showProgress) {
                    CircularProgressIndicator()
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(id = R.string.TXT_COPYRIGHT),
                    color = gray,
                    fontSize = 12.sp
                )

                if (showAlert) {
                    AlertDialog(
                        onDismissRequest = { showAlert = false },
                        confirmButton = {
                            TextButton(onClick = {
                                showAlert = false
                            }) {
                                Text(text = stringResource(id = R.string.TXT_OK))
                            }
                        },
                        title = {
                            Text(text = stringResource(id = R.string.TXT_ERROR))
                        },
                        icon = {
                            Icon(imageVector = Icons.Rounded.Warning, contentDescription = null)
                        },
                        text = {
                            Text(
                                text = stringResource(
                                    id = if (!showSignUpView) R.string.TXT_DESCRIPTION_SIGN_IN_ERROR else R.string.TXT_DESCRIPTION_SIGN_UP_ERROR
                                )
                            )
                        }
                    )
                }

                if (isSignedIn) {
                    LaunchedEffect(key1 = true) {
                        if (signInMethod == 0) {
                            dataStoreUtil.saveToDataStore(null, null, AES256Util.encrypt(userToken))
                        } else {
                            dataStoreUtil.saveToDataStore(
                                AES256Util.encrypt(email),
                                AES256Util.encrypt(password),
                                null
                            )
                        }

                        context.startActivity(Intent(context, MainActivity::class.java))
                    }
                }
            }
        }
    }
}

@Preview(device = "id:pixel_8_pro", showBackground = true)
@Composable
fun SignInViewPreview() {
    SignInView()
}