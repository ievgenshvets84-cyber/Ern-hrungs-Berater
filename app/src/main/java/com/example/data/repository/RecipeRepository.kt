package com.example.data.repository

import com.example.R
import com.example.data.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecipeRepository {

    private val initialRecipes = listOf(
        Recipe(
            id = "rec_currywurst",
            title = "Gesunde Berliner Currywurst & Ofen-Pommes",
            calories = 480,
            protein = 28.0,
            carbs = 46.0,
            fat = 18.0,
            fiber = 6.0,
            prepTimeMinutes = 25,
            category = "Comfort Healthy",
            difficulty = "Einfach",
            vibeTag = "Klassiker ohne Reue 🌭",
            imageResId = R.drawable.sample_currywurst_1790677151376,
            ingredients = listOf(
                "150g magere Geflügel-Bratwurst",
                "200g Süßkartoffel oder Kartoffeln (in Spalten geschnitten)",
                "150ml passierte Tomaten",
                "1 TL Currypulver edelsüß + 1/2 TL Madras-Curry",
                "1 TL Apfelessig & 1 Spritzer Ahornsirup",
                "1 TL Olivenöl für die Ofenspalten",
                "Prise Meersalz und Pfeffer"
            ),
            instructions = listOf(
                "Backofen auf 200°C Ober-/Unterhitze vorheizen. Süßkartoffelspalten mit 1 TL Olivenöl und Meersalz vermengen und ca. 20 Minuten goldbraun backen.",
                "In einer kleinen Pfanne passierte Tomaten, Currypulver, Apfelessig und Ahornsirup 5 Minuten sanft einköcheln lassen.",
                "Geflügelwurst in einer beschichteten Pfanne ohne Fett goldbraun anbraten und in Scheiben schneiden.",
                "Wurstscheiben auf dem Teller anrichten, mit der warmen Currysoße übergießen, reichlich Currypulver darüber stäuben und mit den Ofen-Pommes servieren."
            )
        ),
        Recipe(
            id = "rec_salmon_bowl",
            title = "Lachs & Avocado Quinoa Nourish Bowl",
            calories = 520,
            protein = 36.0,
            carbs = 42.0,
            fat = 22.0,
            fiber = 9.0,
            prepTimeMinutes = 20,
            category = "High Protein",
            difficulty = "Einfach",
            vibeTag = "Omega-3 Glow 🥗",
            imageResId = R.drawable.sample_avocado_bowl_1790677164525,
            ingredients = listOf(
                "140g frisches Lachsfilet",
                "50g Quinoa (ungekocht)",
                "1/2 reife Avocado (in Scheiben)",
                "80g gedämpfte Edamame",
                "6 Kirschtomaten (halbiert)",
                "1 EL Zitronensaft & 1 TL Sesamöl",
                "Frischer Koriander oder Petersilie"
            ),
            instructions = listOf(
                "Quinoa nach Packungsanleitung in leicht gesalzenem Wasser ca. 12 Minuten gar kochen und kurz ausdampfen lassen.",
                "Lachsfilet von beiden Seiten mit etwas Meersalz und Zitrone würzen und in einer Pfanne ca. 3–4 Minuten pro Seite medium-zart anbraten.",
                "Quinoa als Basis in eine Schale geben. Lachs, Avocadoscheiben, Edamame und Kirschtomaten farbenfroh darauf arrangieren.",
                "Mit Zitronensaft und etwas Sesamöl beträufeln und mit frischen Kräutern garnieren."
            )
        ),
        Recipe(
            id = "rec_zoodles_bolognese",
            title = "High-Protein Zucchini-Nudel Bolognese",
            calories = 390,
            protein = 42.0,
            carbs = 18.0,
            fat = 14.0,
            fiber = 6.0,
            prepTimeMinutes = 20,
            category = "Low Calorie",
            difficulty = "Einfach",
            vibeTag = "Leichte italienische Freude 🍝",
            ingredients = listOf(
                "180g mageres Rinderhackfleisch (max. 5% Fett)",
                "2 mittelgroße Zucchini (mit Spiralschneider zu Zoodles verarbeitet)",
                "200g stückige Tomaten (Dose)",
                "1 Knoblauchzehe & 1/2 rote Zwiebel",
                "1 TL italienische Kräuter (Oregano, Basilikum)",
                "15g geriebener Parmesan zum Bestreuen"
            ),
            instructions = listOf(
                "Zwiebel und Knoblauch fein hacken und in einer Pfanne kurz anschwitzen.",
                "Hackfleisch dazugeben und krümelig anbraten. Stückige Tomaten und Kräuter einrühren, 10 Minuten leise köcheln lassen.",
                "Zucchini-Nudeln in den letzten 2 Minuten direkt in die heiße Soße geben, damit sie knackig ('al dente') bleiben.",
                "Auf einem tiefen Teller anrichten und mit geriebenem Parmesan bestreuen."
            )
        ),
        Recipe(
            id = "rec_teriyaki_tofu",
            title = "Knusprige Teriyaki Tofu & Brokkoli Pfanne",
            calories = 410,
            protein = 26.0,
            carbs = 48.0,
            fat = 12.0,
            fiber = 8.0,
            prepTimeMinutes = 20,
            category = "High Protein",
            difficulty = "Einfach",
            vibeTag = "Pflanzliche Power 🌱",
            ingredients = listOf(
                "180g fester Bio-Tofu (in Würfeln)",
                "150g Brokkoli-Röschen",
                "1 mittelgroße Karotte (in Streifen)",
                "50g Vollkorn-Basmatireis",
                "2 EL Sojasoße (natriumreduziert) + 1 TL Honig + 1 TL geriebener Ingwer",
                "1 TL Sesam"
            ),
            instructions = listOf(
                "Reis in Salzwasser kochen. Tofuwürfel trocken tupfen und in einer beschichteten Pfanne von allen Seiten kross anbraten.",
                "Brokkoli und Karotten hinzufügen und 4 Minuten bissfest mitbraten.",
                "Sojasoße, Honig und Ingwer verrühren, über das Gemüse gießen und kurz karamellisieren lassen.",
                "Mit Vollkornreis servieren und mit Sesam bestreuen."
            )
        ),
        Recipe(
            id = "rec_skyr_parfait",
            title = "Protein Berry Skyr Schale mit Chiasamen",
            calories = 290,
            protein = 34.0,
            carbs = 26.0,
            fat = 4.0,
            fiber = 7.0,
            prepTimeMinutes = 5,
            category = "Quick & Fresh",
            difficulty = "Super Schnell",
            vibeTag = "Morgenfrische & Fokus 🫐",
            ingredients = listOf(
                "250g Isländischer Natur-Skyr oder Magerquark",
                "100g frische Heidelbeeren und Himbeeren",
                "1 TL Chiasamen",
                "10g gehackte Walnüsse oder Mandeln",
                "Prise Bourbon-Vanille & Spritzer Agavendicksaft"
            ),
            instructions = listOf(
                "Skyr mit einer Prise Vanille und etwas Wasser cremig rühren.",
                "In ein schönes Glas oder eine Schüssel füllen.",
                "Mit frischen Beeren, Chiasamen und gehackten Nüssen toppen. Sofort genießen!"
            )
        ),
        Recipe(
            id = "rec_med_chicken",
            title = "Mediterraner Zitronen-Hähnchen-Salat",
            calories = 380,
            protein = 44.0,
            carbs = 12.0,
            fat = 16.0,
            fiber = 5.0,
            prepTimeMinutes = 15,
            category = "High Protein",
            difficulty = "Einfach",
            vibeTag = "Frisch & Sättigend 🍋",
            ingredients = listOf(
                "160g Hähnchenbrustfilet",
                "120g bunter Romanasalat & Rucola",
                "6 Kalamata-Oliven",
                "30g fettarmer Feta-Käse (zerkrümelt)",
                "1/2 Gurke & 5 Kirschtomaten",
                "1 TL extra natives Olivenöl & Saft 1/2 Zitrone"
            ),
            instructions = listOf(
                "Hähnchenbrustfilet mit Zitrone, Oregano, Salz und Pfeffer würzen und in der Grillpfanne von beiden Seiten goldbraun braten (ca. 4-5 Min. je Seite).",
                "Salat, Gurkenscheiben und Kirschtomaten in eine Schüssel geben.",
                "Hähnchen in Streifen schneiden und auf dem Salat anrichten.",
                "Feta zerkrümeln, Oliven dazugeben und mit Olivenöl und Zitronensaft beträufeln."
            )
        )
    )

    private val _recipes = MutableStateFlow(initialRecipes)
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    fun toggleFavorite(recipeId: String) {
        _recipes.value = _recipes.value.map {
            if (it.id == recipeId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun addCustomRecipe(recipe: Recipe) {
        _recipes.value = listOf(recipe) + _recipes.value
    }
}
