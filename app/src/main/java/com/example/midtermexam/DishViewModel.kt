package com.example.midtermexam

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DishViewModel : ViewModel() {

    private val _dishes = MutableStateFlow(
        listOf(
            Dish(id = 1, name = "Chicken Adobo"),
            Dish(id = 2, name = "Sinigang na Baboy")
        )
    )
    val dishes: StateFlow<List<Dish>> = _dishes.asStateFlow()

    private var nextId = 100

    fun addDish(name: String) {
        if (name.isBlank()) return
        val newDish = Dish(id = nextId++, name = name.trim())
        _dishes.value = _dishes.value + newDish
    }

    // READ -- GIVEN.
    fun getDish(dishId: Int): Dish? {
        return _dishes.value.find { it.id == dishId }
    }

    // UPDATE -- TODO 1 (5 pts)
    fun updateDish(dishId: Int, newName: String) {
        if (newName.isBlank()) return
        _dishes.value = _dishes.value.map { dish ->
            if (dish.id == dishId) dish.copy(name = newName.trim()) else dish
        }
    }

    // DELETE -- TODO 2 (5 pts)
    fun deleteDish(dishId: Int) {
        _dishes.value = _dishes.value.filter { it.id != dishId }
    }

    // ---------------- RECIPE CRUD ----------------
    // CREATE -- TODO 3 (5 pts)
    fun addRecipe(dishId: Int, text: String) {
        if (text.isBlank()) return
        _dishes.value = _dishes.value.map { dish ->
            if (dish.id == dishId) {
                val newRecipe = Recipe(id = nextId++, text = text.trim())
                dish.copy(recipes = dish.recipes + newRecipe)
            } else {
                dish
            }
        }
    }

    // UPDATE -- TODO 4 (5 pts)
    fun updateRecipe(dishId: Int, recipeId: Int, newText: String) {
        if (newText.isBlank()) return
        _dishes.value = _dishes.value.map { dish ->
            if (dish.id == dishId) {
                val updatedRecipes = dish.recipes.map { recipe ->
                    if (recipe.id == recipeId) recipe.copy(text = newText.trim()) else recipe
                }
                dish.copy(recipes = updatedRecipes)
            } else {
                dish
            }
        }
    }

    // DELETE -- TODO 5 (5 pts)
    fun deleteRecipe(dishId: Int, recipeId: Int) {
        _dishes.value = _dishes.value.map { dish ->
            if (dish.id == dishId) {
                val filteredRecipes = dish.recipes.filter { it.id != recipeId }
                dish.copy(recipes = filteredRecipes)
            } else {
                dish
            }
        }
    }
}