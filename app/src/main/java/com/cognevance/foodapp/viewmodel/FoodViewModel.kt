package com.cognevance.foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognevance.foodapp.data.model.FavoriteItem
import com.cognevance.foodapp.data.model.FoodItem
import com.cognevance.foodapp.data.model.RecentlyViewedItem
import com.cognevance.foodapp.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FoodUiState {
    object Loading : FoodUiState
    data class Success(val items: List<FoodItem>) : FoodUiState
    data class Error(val message: String) : FoodUiState
}

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FoodUiState>(FoodUiState.Loading)
    val uiState: StateFlow<FoodUiState> = _uiState.asStateFlow()

    // Filter states
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val selectedMinRating = MutableStateFlow(0f)
    val maxPrice = MutableStateFlow(100f)
    val selectedMaxPrice = MutableStateFlow(100f)

    // Room Favorites
    val favoriteItems: StateFlow<List<FavoriteItem>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room Recently Viewed
    val recentlyViewedItems: StateFlow<List<RecentlyViewedItem>> = repository.getRecentlyViewed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Detailed item state
    private val _detailItem = MutableStateFlow<FoodItem?>(null)
    val detailItem: StateFlow<FoodItem?> = _detailItem.asStateFlow()

    init {
        loadFoodItems()
    }

    fun loadFoodItems() {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            try {
                val result = repository.getGroceries()
                if (result.isSuccess) {
                    val products = result.getOrDefault(emptyList())
                    val foodItems = products.map { p ->
                        FoodItem(
                            id = p.id,
                            title = p.title,
                            description = p.description,
                            price = p.price,
                            rating = p.rating,
                            category = p.category,
                            thumbnail = p.thumbnail,
                            images = p.images
                        )
                    }
                    _uiState.value = FoodUiState.Success(foodItems)
                    
                    // Determine max price dynamically from items
                    if (foodItems.isNotEmpty()) {
                        val highestPrice = foodItems.maxOf { item -> item.price }.toFloat()
                        maxPrice.value = highestPrice
                        selectedMaxPrice.value = highestPrice
                    }
                } else {
                    _uiState.value = FoodUiState.Error(result.exceptionOrNull()?.message ?: "Failed to fetch food items")
                }
            } catch (e: Exception) {
                _uiState.value = FoodUiState.Error(e.message ?: "Failed to fetch food items")
            }
        }
    }

    // Dynamic categorization helper
    fun getSubcategory(item: FoodItem): String {
        val title = item.title.lowercase()
        return when {
            title.contains("apple") || title.contains("banana") || title.contains("kiwi") || 
            title.contains("lemon") || title.contains("strawberry") || title.contains("fruit") -> "Fruits"
            
            title.contains("potato") || title.contains("tomato") || title.contains("onion") || 
            title.contains("cucumber") || title.contains("carrot") || title.contains("cabbage") -> "Vegetables"
            
            title.contains("milk") || title.contains("cheese") || title.contains("butter") || 
            title.contains("egg") || title.contains("yogurt") || title.contains("cream") -> "Dairy"
            
            title.contains("bread") || title.contains("flour") || title.contains("bakery") || 
            title.contains("croissant") -> "Bakery"
            
            title.contains("spice") || title.contains("salt") || title.contains("sugar") || 
            title.contains("honey") || title.contains("oil") || title.contains("sauce") || 
            title.contains("powder") -> "Spices"
            
            else -> "Pantry"
        }
    }

    // Combined filtered food items
    val filteredFoodItems: StateFlow<List<FoodItem>> = combine(
        _uiState,
        searchQuery,
        selectedCategory,
        selectedMinRating,
        selectedMaxPrice,
        favoriteItems
    ) { flows ->
        val uiState = flows[0] as FoodUiState
        val query = flows[1] as String
        val category = flows[2] as String
        val minRating = flows[3] as Float
        val maxPriceValue = flows[4] as Float
        val favoritesList = flows[5] as List<FavoriteItem>

        when (uiState) {
            is FoodUiState.Success -> {
                uiState.items.filter { item ->
                    val matchesQuery = item.title.contains(query, ignoreCase = true) || 
                                       item.description.contains(query, ignoreCase = true)
                    
                    val matchesCategory = if (category == "Favorites") {
                        favoritesList.any { it.id == item.id }
                    } else {
                        category == "All" || getSubcategory(item) == category
                    }
                    val matchesRating = item.rating >= minRating.toDouble()
                    val matchesPrice = item.price <= maxPriceValue.toDouble()

                    matchesQuery && matchesCategory && matchesRating && matchesPrice
                }
            }
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorites operations
    fun toggleFavorite(foodItem: FoodItem) {
        viewModelScope.launch {
            val isFav = favoriteItems.value.any { it.id == foodItem.id }
            if (isFav) {
                repository.removeFavorite(foodItem.id)
            } else {
                repository.addFavorite(
                    FavoriteItem(
                        id = foodItem.id,
                        title = foodItem.title,
                        price = foodItem.price,
                        rating = foodItem.rating,
                        category = foodItem.category,
                        thumbnail = foodItem.thumbnail
                    )
                )
            }
        }
    }

    fun isItemFavorite(id: Int): Flow<Boolean> {
        return repository.isFavorite(id)
    }

    // Set detailed food item and add to recently viewed list
    fun setDetailItem(foodItem: FoodItem) {
        _detailItem.value = foodItem
        viewModelScope.launch {
            repository.addRecentlyViewed(foodItem)
        }
    }
}
