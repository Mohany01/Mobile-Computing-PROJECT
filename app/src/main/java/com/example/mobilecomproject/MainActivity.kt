package com.example.mobilecomproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilecomproject.data.Booking
import com.example.mobilecomproject.data.GymDatabase
import com.example.mobilecomproject.data.GymRepository
import com.example.mobilecomproject.ui.theme.MobileComProjectTheme
import com.example.mobilecomproject.viewmodel.GymViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = GymDatabase.getInstance(applicationContext)
        val repository = GymRepository(db.bookingDao())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GymViewModel(repository) as T
            }
        }

        setContent {
            MobileComProjectTheme {
                val vm: GymViewModel = viewModel(factory = factory)
                val navController = rememberNavController()
                GymApp(navController, vm)
            }
        }
    }
}

private object Routes {
    const val HOME = "home"
    const val ADD = "add"
    const val LIST = "list"
}

@Composable
fun GymApp(navController: NavHostController, vm: GymViewModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showConflictDialog by remember { mutableStateOf(false) }

    if (showConflictDialog) {
        AlertDialog(
            onDismissRequest = { showConflictDialog = false },
            title = { Text("Booking Conflict") },
            text = { Text("This time slot is already booked. Please choose a different time.") },
            confirmButton = {
                Button(onClick = { showConflictDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onBookClick = { navController.navigate(Routes.ADD) },
                onMyBookingsClick = { navController.navigate(Routes.LIST) }
            )
        }

        composable(Routes.ADD) {
            AddBookingScreen(
                onSave = { cls, trainer, startTime, endTime, age, gender ->
                    scope.launch {
                        val success = vm.addBooking(cls, trainer, startTime, endTime, age, gender)
                        if (success) {
                            Toast.makeText(context, "Booking saved!", Toast.LENGTH_SHORT).show()
                            navController.navigate(Routes.LIST)
                        } else {
                            showConflictDialog = true
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LIST) {
            val bookings by vm.bookings.collectAsState()
            BookingListScreen(
                bookings = bookings,
                onDelete = { vm.deleteBooking(it) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/* ---------------------------- SCREENS ---------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBookClick: () -> Unit,
    onMyBookingsClick: () -> Unit
) {
    val context = LocalContext.current
    val goldenColor = Color(0xFFFFD700)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Golden Gym") }) }
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gymlogo),
                        contentDescription = "Golden Gym Logo",
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.White)
                    )
                }
                Spacer(Modifier.height(16.dp))

                Text("Feel Free To Booking", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))

                // Row with Open map + Gym branches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Button 1: open map
                    Button(
                        onClick = {
                            val uri = Uri.parse("geo:0,0?q=Gym+Near+Me")
                            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(mapIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = goldenColor)
                    ) {
                        Text("Open map")
                    }

                    Spacer(Modifier.width(16.dp))

                    // Button 2: open the Flutter screen inside this app
                    Button(
                        onClick = {
                            // This matches the intent-filter for FlutterActivity in AndroidManifest.xml
                            val uri = Uri.parse("flutterapp://home")
                            val flutterIntent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(flutterIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = goldenColor)
                    ) {
                        Text("Gym branches")
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onBookClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = goldenColor)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Book a class")
                    Spacer(Modifier.width(8.dp))
                    Text("Book a class")
                }
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onMyBookingsClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = goldenColor)
                ) {
                    Icon(Icons.Filled.List, contentDescription = "My bookings")
                    Spacer(Modifier.width(8.dp))
                    Text("My bookings")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookingScreen(
    onSave: (String, String, Long, Long, Int, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val goldenColor = Color(0xFFFFD700)
    val classOptions = listOf("Yoga", "Gym", "Spa", "Zumba")
    var selectedClass by remember { mutableStateOf(classOptions[0]) }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Men") }
    var selectedHours by remember { mutableStateOf(15f..16f) }

    val days = remember {
        val calendar = Calendar.getInstance()
        (0..6).map {
            val cal = calendar.clone() as Calendar
            cal.add(Calendar.DAY_OF_YEAR, it)
            cal
        }
    }
    var selectedDay by remember { mutableStateOf(days[0]) }
    val dayFormat = remember { SimpleDateFormat("EEE d", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Booking") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(16.dp))

            Text("Select a Class", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(classOptions.size) { index ->
                    val cls = classOptions[index]
                    OutlinedButton(
                        onClick = { selectedClass = cls },
                        shape = RoundedCornerShape(50),
                        colors = if (selectedClass == cls) ButtonDefaults.outlinedButtonColors(
                            containerColor = goldenColor
                        ) else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(cls)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(80.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text("Select Gender", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { selectedGender = "Men" },
                    shape = RoundedCornerShape(50),
                    colors = if (selectedGender == "Men") ButtonDefaults.outlinedButtonColors(
                        containerColor = goldenColor
                    ) else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Men")
                }
                OutlinedButton(
                    onClick = { selectedGender = "Women" },
                    shape = RoundedCornerShape(50),
                    colors = if (selectedGender == "Women") ButtonDefaults.outlinedButtonColors(
                        containerColor = goldenColor
                    ) else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Women")
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Select a Day", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(days.size) { index ->
                    val day = days[index]
                    OutlinedButton(
                        onClick = { selectedDay = day },
                        shape = RoundedCornerShape(50),
                        colors = if (selectedDay == day) ButtonDefaults.outlinedButtonColors(
                            containerColor = goldenColor
                        ) else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(dayFormat.format(day.time))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Select Time Range (from ${selectedHours.start.toInt()}:00 to ${selectedHours.endInclusive.toInt()}:00)",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            RangeSlider(
                value = selectedHours,
                onValueChange = { selectedHours = it },
                valueRange = 6f..22f,
                steps = 15,
                colors = SliderDefaults.colors(
                    thumbColor = goldenColor,
                    activeTrackColor = goldenColor
                )
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                    } else {
                        val startCal = selectedDay.clone() as Calendar
                        startCal.set(Calendar.HOUR_OF_DAY, selectedHours.start.toInt())
                        startCal.set(Calendar.MINUTE, 0)
                        startCal.set(Calendar.SECOND, 0)
                        startCal.set(Calendar.MILLISECOND, 0)

                        val endCal = selectedDay.clone() as Calendar
                        endCal.set(Calendar.HOUR_OF_DAY, selectedHours.endInclusive.toInt())
                        endCal.set(Calendar.MINUTE, 0)
                        endCal.set(Calendar.SECOND, 0)
                        endCal.set(Calendar.MILLISECOND, 0)

                        val ageInt = age.toIntOrNull() ?: 0
                        onSave(
                            selectedClass,
                            name,
                            startCal.timeInMillis,
                            endCal.timeInMillis,
                            ageInt,
                            selectedGender
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = goldenColor)
            ) {
                Text("Save booking")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingListScreen(
    bookings: List<Booking>,
    onDelete: (Booking) -> Unit,
    onBack: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("EEE d MMM", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My bookings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(count = bookings.size) { index ->
                val b = bookings[index]
                Card(modifier = Modifier.padding(bottom = 8.dp)) {
                    Column(Modifier.padding(all = 16.dp)) {
                        Text(text = "${b.className} with ${b.trainerName} (Age: ${b.age}, Gender: ${b.gender})")
                        val startTime = timeFormat.format(Date(b.timeMillis))
                        val endTime = timeFormat.format(Date(b.endTimeMillis))
                        Text(text = "${sdf.format(Date(b.timeMillis))}, $startTime - $endTime")
                        Spacer(Modifier.height(4.dp))
                        TextButton(onClick = { onDelete(b) }) {
                            Text(text = "Cancel", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
