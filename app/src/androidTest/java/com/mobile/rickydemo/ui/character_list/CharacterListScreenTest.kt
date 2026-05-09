package com.mobile.rickydemo.ui.character_list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.mobile.rickydemo.ui.theme.RickydemoTheme
import org.junit.Rule
import org.junit.Test

class CharacterListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun characterListScreen_showsSearchPlaceholder() {
        composeTestRule.setContent {
            RickydemoTheme {
                // We'll test a static version or mock the VM if needed, 
                // but for a basic UI test we can just check if the screen renders
                // with its initial components.
                CharacterListScreen(onCharacterClick = {})
            }
        }

        composeTestRule.onNodeWithText("Search characters...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alive").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dead").assertIsDisplayed()
    }
}
