/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.tiptime

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}
fun getBmiCategory(bmi: Double): String {
    return when {
        bmi < 18.5 -> "Underweight"
        bmi in 18.5..24.9 -> "Normal weight"
        bmi in 25.0..29.9 -> "Overweight"
        bmi in 30.0..34.9 -> "Moderate obesity"
        bmi in 35.0..39.9 -> "Severe obesity"
        else -> "Very severe or morbid obesity"
    }
}
@Composable
fun EditNumberField(
    @StringRes label: Int,
    value: String,
    keyboardOptions: KeyboardOptions,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(stringResource(label)) },
        keyboardOptions = keyboardOptions
    )
}
@Composable
fun RoundTheTipRow(
    type:Int,
    roundUp: Boolean,
    onRoundUpChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(type == 0){
            Text(text = stringResource(R.string.ft))
        }
        else{
            Text(text = stringResource(R.string.lb))
        }
        Switch(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            checked = roundUp,
            onCheckedChange = onRoundUpChanged
        )
    }
}
@Composable
fun TipTimeLayout() {
    var heightInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }
    var weight = weightInput.toDoubleOrNull() ?: 0.0
    var height = heightInput.toDoubleOrNull() ?: 0.0
    //var roundUp by remember { mutableStateOf(false) }
    var bmi by remember { mutableDoubleStateOf(0.0) }
    var isCalculated by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(true) }
    var useft by remember { mutableStateOf(false) }
    var uselb by remember { mutableStateOf(false) }
    if(useft){
        height *= 30.48
    }
    if(uselb){
        weight *=0.453592
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_bmi),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )
        EditNumberField(
            label = R.string.height,
            value = heightInput,
            onValueChanged = { heightInput = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth()
        )
        EditNumberField(
            label = R.string.weight,
            value = weightInput,
            onValueChanged = { weightInput = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth()
        )
        RoundTheTipRow(
            type = 0,
            roundUp = useft,
            onRoundUpChanged = { useft = it },
            modifier = Modifier.padding(bottom = 16.dp)
        )
        RoundTheTipRow(
            type = 1,
            roundUp = uselb,
            onRoundUpChanged = { uselb = it },
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                bmi = calculateBmi(height, weight)
                isCalculated = true
                Log.d("BMI_CALCULATION", "BMI: $bmi")
                if(bmi == 0.0){
                    isError = true
                }
                else{
                    isError = false
                }
            },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.calculate))
        }
        Button(
            onClick = {
                heightInput = ""
                weightInput = ""
                isCalculated = false
                isError = true
            },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.clear))
        }
        if (isCalculated) {
            if(isError){
                Text(
                    text = stringResource(R.string.error_message),
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.displaySmall
                )
            }
            else{
                Text(
                    text = stringResource(R.string.your_bmi_is, bmi),
                    style = MaterialTheme.typography.displaySmall
                )
            }

        }
        if(!isError){
            val answer = getBmiCategory(bmi)
            Text(
                text = answer,
                style = MaterialTheme.typography.displaySmall
            )
        }
        Spacer(modifier = Modifier.height(150.dp))
    }
}
@SuppressLint("DefaultLocale")
private fun calculateBmi(height: Double, weight: Double): Double {

    if(height>0.0 && weight >0.0){
        val bmi = weight*10000 / (height * height)
        return bmi
    }
    return 0.0
}

@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}
