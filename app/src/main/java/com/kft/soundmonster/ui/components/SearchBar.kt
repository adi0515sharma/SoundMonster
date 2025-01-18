package com.kft.soundmonster.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kft.soundmonster.R

@Composable
fun CustomSearchBar(searchText : String, onTextChange : (String)->Unit){

    Row(modifier = Modifier
        .fillMaxWidth(1f)
        .background(color =  Color(0xFF282828))
        .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically){

        BasicTextField(
            value = searchText,
            onValueChange = { newText -> onTextChange(newText) },
            modifier = Modifier
                .weight(1f) // Allow TextField to take available space
                .padding(vertical = 10.dp), // Transparent background
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp) // Padding inside the text field box
                ) {
                    if (searchText.isEmpty()) {
                        Text(
                            text = "Search by name",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 18.sp,
                            color = Color(0xFF898989) // Placeholder text color
                        )
                    }
                    innerTextField() // Draws the actual text field
                }
            },
            textStyle = TextStyle(
                color = Color.White, // Text color
                fontSize = 18.sp
            ),
            cursorBrush = SolidColor(Color.White) // Cursor color
        )

        if(searchText.isNotEmpty()){
            Image(
                painter = painterResource(id = R.drawable.baseline_cancel_24), // Replace with your drawable
                contentDescription = "App Icon",
                colorFilter = ColorFilter.tint(color = Color(0xFF898989)),
                modifier = Modifier
                    .size(23.dp)
                    .clickable {
                        onTextChange("")
                    }
            )
        }

    }

}