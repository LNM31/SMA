package com.example.todlist.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** O listă de task-uri (ex. "Cumpărături", "Școală"). */
@Entity(tableName = "lists")
data class TodoList(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)

/** Un task dintr-o listă. Când lista e ștearsă, task-urile ei se șterg automat (CASCADE). */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = TodoList::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("listId")],
)
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val title: String,
    val description: String = "",
    val done: Boolean = false,
)

/** O listă împreună cu câte task-uri are (pentru ecranul principal). */
data class ListSummary(
    val id: Long,
    val name: String,
    val total: Int,
    val done: Int,
)
