package com.example.moneyminder.presentation.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyminder.domain.model.Category
import com.example.moneyminder.domain.model.Transaction
import com.example.moneyminder.domain.model.TransactionType
import com.example.moneyminder.domain.repository.TransactionRepository
import com.example.moneyminder.util.VoiceToTextParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceCommandViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val voiceToTextParser: VoiceToTextParser
) : ViewModel() {

    private val _state = MutableStateFlow(VoiceCommandState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            voiceToTextParser.state.collectLatest { voiceState ->
                _state.update { it.copy(
                    spokenText = voiceState.spokenText,
                    isListening = voiceState.isSpeaking,
                    error = voiceState.error
                ) }
                
                if (voiceState.spokenText.isNotBlank() && !voiceState.isSpeaking) {
                    processVoiceCommand(voiceState.spokenText)
                }
            }
        }
    }

    fun startListening() {
        voiceToTextParser.startListening()
    }

    fun stopListening() {
        voiceToTextParser.stopListening()
    }

    private fun processVoiceCommand(command: String) {
        val lowerCommand = command.lowercase()
        
        // Extract amount (handles integers and decimals)
        val amountRegex = "(\\d+(\\.\\d+)?)".toRegex()
        val amount = amountRegex.find(lowerCommand)?.value?.toDoubleOrNull() ?: return

        // Determine category
        val category = Category.entries.find {
            lowerCommand.contains(it.title.lowercase()) 
        } ?: Category.OTHERS

        // Determine type based on category
        val type = when (category) {
            Category.SALARY, Category.GIFT, Category.INVESTMENT -> TransactionType.INCOME
            else -> TransactionType.EXPENSE
        }

        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    amount = amount,
                    type = type,
                    category = category,
                    date = System.currentTimeMillis(),
                    note = "Added via voice: \"$command\""
                )
            )
            _state.update { it.copy(commandProcessed = true, lastAddedTransaction = "Added $amount on ${category.title}") }
        }
    }

    fun resetState() {
        _state.update { VoiceCommandState() }
    }
}

data class VoiceCommandState(
    val spokenText: String = "",
    val isListening: Boolean = false,
    val error: String? = null,
    val commandProcessed: Boolean = false,
    val lastAddedTransaction: String? = null
)
