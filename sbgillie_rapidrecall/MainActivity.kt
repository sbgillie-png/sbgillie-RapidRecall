package com.example.sbgillie_rapidrecall

import android.os.Bundle
import androidx.compose.foundation.shape.CircleShape
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
/*
*
* This is the display/usr interface of the application
* This is where all the controls and displays are located, the inputs are sent through GameEngine
* The main issue for everything would be that the data that is collected only stays in local memory until the app is terminated
* */
private enum class Screen {
    HOME,
    LENGTH,
    DISPLAY,
    INPUT,
    RESULT,
    LOG,
    SUMMARY
}

/**
 * Main activity for the RapidRecall application.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RapidRecallApp()
                }
            }
        }
    }
}

@Composable
private fun RapidRecallApp() {

    val gameEngine = remember {
        GameEngine()
    }

    var screen by remember {
        mutableStateOf(Screen.HOME)
    }

    var userInput by remember {
        mutableStateOf("")
    }

    when (screen) {

        Screen.HOME -> {
            HomeScreen(
                onStart = {
                    screen = Screen.LENGTH
                },
                onLog = {
                    screen = Screen.LOG
                },
                onSummary = {
                    screen = Screen.SUMMARY
                }
            )
        }

        Screen.LENGTH -> {
            LengthScreen(
                onLengthSelected = { length ->
                    gameEngine.startGame(length)
                    screen = Screen.DISPLAY
                },
                onBack = {
                    screen = Screen.HOME
                }
            )
        }

        Screen.DISPLAY -> {
            DisplayScreen(
                sequence = gameEngine.targetSequence,
                onDisplayFinished = {
                    userInput = ""
                    screen = Screen.INPUT
                },
                onBack = {
                    screen = Screen.HOME
                }
            )
        }

        Screen.INPUT -> {
            InputScreen(
                input = userInput,
                sequenceLength = gameEngine.sequenceLength,
                onInputChanged = { value ->
                    if (
                        value.length <= gameEngine.sequenceLength &&
                        value.all { it.isDigit() }
                    ) {
                        userInput = value
                    }
                },
                onSubmit = {
                    gameEngine.submitGuess(userInput)
                    screen = Screen.RESULT
                },
                onBack = {
                    screen = Screen.HOME
                }
            )
        }

        Screen.RESULT -> {
            ResultScreen(
                attempt = gameEngine.lastAttempt,
                onHome = {
                    screen = Screen.HOME
                },
                onLog = {
                    screen = Screen.LOG
                },
                onSummary = {
                    screen = Screen.SUMMARY
                }
            )
        }

        Screen.LOG -> {
            LogScreen(
                attempts = gameEngine.attemptLog.getAttempts(),
                onBack = {
                    screen = Screen.HOME
                }
            )
        }

        Screen.SUMMARY -> {
            SummaryScreen(
                total = gameEngine.attemptLog.getTotalAttempts(),
                correct = gameEngine.attemptLog.getCorrectAttempts(),
                incorrect = gameEngine.attemptLog.getIncorrectAttempts(),
                accuracy = gameEngine.attemptLog.getAccuracy(),
                onBack = {
                    screen = Screen.HOME
                }
            )
        }
    }
}

@Composable
private fun HomeScreen(
    onStart: () -> Unit,
    onLog: () -> Unit,
    onSummary: () -> Unit
) {
    ScreenContainer {

        Text(
            text = "Welcome to Rapid Recall",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )



        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Button(
            onClick = onStart,
            modifier = Modifier
                .padding(6.dp)
                .width(160.dp)
                .height(160.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F20)
            )

        ) {
            Text("START")
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text = "Try to remember as many digits as you can",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            OutlinedButton(
                onClick = onLog,
                modifier = Modifier.weight(1f).height(64.dp)
            ) {
                Text("LOG")
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            OutlinedButton(
                onClick = onSummary,
                modifier = Modifier.weight(1f).height(64.dp)
            ) {
                Text("SUMMARY")
            }
        }
    }
}

@Composable
private fun LengthScreen(
    onLengthSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    ScreenContainer {

        Text(
            text = "How Many Numbers would you like to Remember?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        for (rowStart in 1..10 step 3) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                for (length in rowStart..minOf(rowStart + 2, 10)) {

                    Button(
                        onClick = {
                            onLengthSelected(length)
                        },
                        modifier = Modifier
                            .padding(6.dp)
                            .width(80.dp)
                            .height(80.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F20)),
                        shape = CircleShape
                    ) {
                        Text(
                            text = length.toString(),
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        TextButton(
            onClick = onBack
        ) {
            Text("BACK")
        }
    }
}

@Composable
private fun DisplayScreen(
    sequence: String,
    onDisplayFinished: () -> Unit,
    onBack: () -> Unit
) {
    var currentIndex by remember(sequence) {
        mutableIntStateOf(0)
    }

    var showDigit by remember(sequence) {
        mutableStateOf(true)
    }

    LaunchedEffect(sequence) {

        for (index in sequence.indices) {

            currentIndex = index
            showDigit = true

            delay(800.milliseconds)

            showDigit = false

            delay(400.milliseconds)
        }

        onDisplayFinished()
    }

    ScreenContainer {

        Text(
            text = "Remember this digit",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text = if (showDigit) {
                sequence[currentIndex].toString()
            } else {
                ""
            },
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(
            modifier = Modifier.height(20.dp)
        )
        Text(
            text = "${currentIndex + 1} of ${sequence.length}",

        )
        Spacer(
            modifier = Modifier.height(24.dp)
        )

        TextButton(
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F20)),
            onClick = onBack
        ) {
            Text("CANCEL")
        }
    }
}

@Composable
private fun InputScreen(
    input: String,
    sequenceLength: Int,
    onInputChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    ScreenContainer {

        Text(
            text = "Enter the sequence",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Enter all $sequenceLength digits in the same order."
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = input,
            onValueChange = onInputChanged,
            label = {
                Text("Your answer")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = onSubmit,
            enabled = input.length == sequenceLength,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F20)),
        ) {
            Text("SUBMIT")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F20)),
            onClick = onBack
        ) {
            Text("CANCEL")
        }
    }
}

@Composable
private fun ResultScreen(
    attempt: Attempt?,
    onHome: () -> Unit,
    onLog: () -> Unit,
    onSummary: () -> Unit
) {
    ScreenContainer {

        if (attempt == null) {

            Text("No completed attempt.")

        } else {

            Text(
                text = if (attempt.correct) "Correct!" else "Incorrect",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Target: ${attempt.targetSequence}",
                fontSize = 20.sp
            )

            Text(
                text = "Input:  ${attempt.userInput}",
                fontSize = 20.sp
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Button(
                onClick = onHome,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F20)),
            ) {
                Text("BACK TO HOME")
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedButton(
                onClick = onLog,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("VIEW LOG")
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedButton(
                onClick = onSummary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("VIEW SUMMARY")
            }
        }
    }
}

@Composable
private fun LogScreen(
    attempts: List<Attempt>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Attempt Log",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (attempts.isEmpty()) {

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No attempts yet"
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(attempts) { attempt ->
                    AttemptCard(attempt)
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("BACK")
        }
    }
}

@Composable
private fun AttemptCard(
    attempt: Attempt
) {
    val formatter = remember {
        SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = if (attempt.correct) "Correct" else "Incorrect",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Length: ${attempt.sequenceLength}"
            )

            Text(
                text = "Target: ${attempt.targetSequence}"
            )

            Text(
                text = "Input: ${attempt.userInput}"
            )

            Text(
                text = "Time: ${formatter.format(Date(attempt.timestamp))}"
            )
        }
    }
}

@Composable
private fun SummaryScreen(
    total: Int,
    correct: Int,
    incorrect: Int,
    accuracy: Double,
    onBack: () -> Unit
) {
    ScreenContainer {

        Text(
            text = "Attempt Summary",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(64.dp)
        )

        SummaryRow(
            label = "Attempts:",
            value = total.toString()
        )

        SummaryRow(
            label = "Correctly Done Attempts:",
            value = correct.toString()
        )

        SummaryRow(
            label = "Incorrectly Done Attempts:",
            value = incorrect.toString()
        )

        SummaryRow(
            label = "Accuracy of Wins:",
            value = String.format(
                Locale.getDefault(),
                "%.1f%%",
                accuracy
            )
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        TextButton(
            onClick = onBack
        ) {
            Text("BACK")
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            fontSize = 18.sp
        )

        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ScreenContainer(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content
    )
}