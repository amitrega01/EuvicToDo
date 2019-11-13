package com.insudev.euvictodo.newTodoFragment.mvi


sealed class NewTodoStateChange {

    data class ContentChange(val result: ContentChangeResult) : NewTodoStateChange()
}
