package com.cognevance.foodapp.ui.screens.checkout

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cognevance.foodapp.ui.components.PrimaryButton
import com.cognevance.foodapp.viewmodel.CheckoutState
import com.cognevance.foodapp.viewmodel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onCheckoutSuccess: (String) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash on Delivery") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val paymentOptions = listOf("Cash on Delivery", "UPI", "Credit Card (Simulation)")

    val checkoutState by viewModel.checkoutState.collectAsState()

    LaunchedEffect(checkoutState) {
        if (checkoutState is CheckoutState.Success) {
            Toast.makeText(context, "Your order placed successfully!", Toast.LENGTH_LONG).show()
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onCheckoutSuccess((checkoutState as? CheckoutState.Success)?.orderId ?: "")
            },
            title = { Text("Order Placed!") },
            text = { Text("Your order placed successfully!") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onCheckoutSuccess((checkoutState as? CheckoutState.Success)?.orderId ?: "")
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Delivery Address", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address (House No, Building, Street, Area)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("State") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = pincode,
                onValueChange = { pincode = it },
                label = { Text("Pincode") },
                modifier = Modifier.fillMaxWidth(0.5f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Payment Method", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            paymentOptions.forEach { method ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = (method == paymentMethod),
                        onClick = { paymentMethod = method }
                    )
                    Text(text = method, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (checkoutState is CheckoutState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally))
            } else {
                PrimaryButton(
                    text = "Place Order",
                    onClick = {
                        viewModel.placeOrder(
                            fullName, phone, address, city, state, pincode, paymentMethod
                        )
                    },
                    enabled = fullName.isNotBlank() && phone.isNotBlank() && address.isNotBlank() && city.isNotBlank() && state.isNotBlank() && pincode.isNotBlank()
                )
            }

            if (checkoutState is CheckoutState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (checkoutState as CheckoutState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
                )
            }
        }
    }
}
