package dev.stefano.enuventory.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.stefano.enuventory.R
import dev.stefano.enuventory.ui.components.EnuButton
import dev.stefano.enuventory.ui.components.EnuButtonVariant
import dev.stefano.enuventory.ui.components.EnuTextField
import dev.stefano.enuventory.ui.theme.EnuTheme

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val signInState by viewModel.signInState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    // Navigasi jika login sukses
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onLoginSuccess()
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = EnuTheme.colors.surfaceDefaultBase
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(EnuTheme.colors.surfaceDefaultBase),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header / Logo
                Text(
                    text = "Enuventory",
                    style = EnuTheme.typography.content.headings.h3,
                    color = EnuTheme.colors.contentBrandPrimaryDefault,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Aplikasi Peminjaman Alat & Inventaris",
                    style = EnuTheme.typography.ui.labels.normalCase.base,
                    color = EnuTheme.colors.contentDefaultSubtle,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input Email
                EnuTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Masukkan email anda",
                    label = "Email",
                    isRequired = true,
                    leadingIcon = R.drawable.ic_user
                )

                // Input Password
                // Catatan: Karena EnuTextField bawaan belum mendukung VisualTransformation secara parameter langsung,
                // kita bisa memodifikasinya jika diperlukan, atau sementara pakai standard text input untuk password.
                EnuTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Masukkan password anda",
                    label = "Password",
                    isRequired = true,
                    leadingIcon = R.drawable.ic_settings // menggunakan icon gear sebagai penanda
                )

                // Error Message jika ada
                if (signInState is SignInState.Error) {
                    Text(
                        text = (signInState as SignInState.Error).message,
                        color = EnuTheme.colors.contentSignalErrorDefault,
                        style = EnuTheme.typography.ui.labels.normalCase.small,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol Login
                EnuButton(
                    text = "Masuk",
                    variant = if (signInState is SignInState.Loading) EnuButtonVariant.Loading else EnuButtonVariant.Normal,
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.signIn(email.trim(), password.trim())
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = EnuTheme.colors.borderDefaultMedium
                )

                // Bypass/Demo Mode Button
                EnuButton(
                    text = "Coba Mode Demo",
                    variant = EnuButtonVariant.Normal,
                    onClick = {
                        // Untuk kemudahan demo/development jika firebase belum di-setup di console
                        // Kita bisa login menggunakan email/password dummy atau anonymous sign-in jika didukung,
                        // atau kita trigger mock login sukses.
                        // Di sini kita coba login menggunakan akun dummy bawaan.
                        viewModel.signIn("demo@enuventory.com", "password123")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
