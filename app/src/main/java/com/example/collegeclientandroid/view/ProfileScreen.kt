package com.example.collegeclientandroid.view

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val photoError by viewModel.photoError.collectAsState()
    val photoSource by viewModel.photoSource.collectAsState()

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
            )
            {
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
            onValueChange = {})

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
            onValueChange = {})

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
            onValueChange = {})

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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        bitmap = userPhoto!!.asImageBitmap(),
                        contentDescription = "Фото пользователя",
                        modifier = Modifier
                            .size(150.dp),
                        contentScale = ContentScale.Crop
                    )
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
                    if (photoError != null) {
                        Text(
                            text = photoError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
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
            Text(text = if (isLoadingPhoto) "Загрузка..." else "Загрузить фото",color = Color.White)
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
            Text(text = "Выйти из системы",color = Color.White)
        }
    }
}
