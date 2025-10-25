package com.example.collegeclientandroid.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegeclientandroid.utils.rememberImagePicker
import com.example.collegeclientandroid.viewmodel.ProfileScreenViewModel

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: ProfileScreenViewModel = hiltViewModel()
) {
    val userInfo = viewModel.getUserInfo()
    val userPhoto by viewModel.userPhoto.collectAsState()
    val isLoadingPhoto by viewModel.isLoadingPhoto.collectAsState()
    var showFullScreenImage by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadUserPhoto()
    }

    val pickImage = rememberImagePicker { file ->
        viewModel.uploadUserPhoto(file)
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Профиль",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Button(
                onClick = { onBackClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    disabledContentColor = Color.Black,
                    disabledContainerColor = Color.White
                )
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Назад", color = Color.White)
            }
        }

        Text(
            text = "ФИО",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding()
        )
        OutlinedTextField(
            modifier = Modifier
                .padding()
                .fillMaxWidth(),
            readOnly = true,
            value = userInfo?.fio ?: "",
            textStyle = MaterialTheme.typography.bodyMedium,
            onValueChange = {}
        )

        Text(
            text = "Группа",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding()
        )
        OutlinedTextField(
            modifier = Modifier
                .padding()
                .fillMaxWidth(),
            readOnly = true,
            value = userInfo?.group ?: "",
            textStyle = MaterialTheme.typography.bodyMedium,
            onValueChange = {}
        )

        Text(
            text = "Почта",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding()
        )
        OutlinedTextField(
            modifier = Modifier
                .padding()
                .fillMaxWidth(),
            readOnly = true,
            value = userInfo?.email ?: "",
            textStyle = MaterialTheme.typography.bodyMedium,
            onValueChange = {}
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "Студенческий билет",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.size(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoadingPhoto) {
                Text("Загрузка фото...")
            } else if (userPhoto != null) {
                val isLandscape = remember(userPhoto) {
                    userPhoto!!.width > userPhoto!!.height
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isLandscape) 120.dp else 180.dp)
                    ) {
                        Image(
                            bitmap = userPhoto!!.asImageBitmap(),
                            contentDescription = "Фото пользователя",
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { showFullScreenImage = true },
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Фото не загружено",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        if (showFullScreenImage && userPhoto != null) {
            ZoomableFullScreenImage(
                imageBitmap = userPhoto!!.asImageBitmap(),
                onDismiss = { showFullScreenImage = false }
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = pickImage,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContentColor = Color.Black,
                disabledContainerColor = Color.DarkGray
            ),
            enabled = !isLoadingPhoto
        ) {
            Text(
                text = if (isLoadingPhoto) "Загрузка..." else "Загрузить фото",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.logout()
                onLogoutClick()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White
            )
        ) {
            Text(text = "Выйти из системы", color = Color.White)
        }
    }
}

@Composable
fun ZoomableFullScreenImage(
    imageBitmap: ImageBitmap,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var rotation by remember { mutableStateOf(0f) }

    val isLandscape = remember(imageBitmap) {
        imageBitmap.width > imageBitmap.height
    }

    LaunchedEffect(imageBitmap) {
        if (isLandscape) {
            rotation = 90f
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, rotate ->
                            scale = maxOf(0.5f, minOf(scale * zoom, 5f))

                            offset += pan

                            rotation += rotate
                        }
                    }
            ) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Фото пользователя (полный экран)",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                            rotationZ = rotation
                        },
                    contentScale = if (isLandscape) ContentScale.Inside else ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Закрыть",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onDismiss() }
                        .padding(bottom = 12.dp),
                    tint = Color.White
                )

                if (scale > 1.1f || rotation != 0f) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Сбросить",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                scale = 1f
                                offset = androidx.compose.ui.geometry.Offset.Zero
                                rotation = if (isLandscape) 90f else 0f
                            }
                            .padding(bottom = 12.dp),
                        tint = Color.White
                    )
                }

            }
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        onBackClick = {},
        onLogoutClick = {}
    )
}