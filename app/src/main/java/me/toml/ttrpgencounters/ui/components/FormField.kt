package me.toml.ttrpgencounters.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun FormField(
    value: String,
    onValueChange: (value: String) -> Unit,
    placeholder: @Composable () -> Unit,
    password: Boolean = false,
    error: Boolean = false,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation =
        if (password) PasswordVisualTransformation() else VisualTransformation.None,
        isError = error,
        shape = MaterialTheme.shapes.small,
        // Remove underline
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
    Spacer(modifier = Modifier.height(4.dp))
}