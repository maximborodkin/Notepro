package ru.modemastudio.notepro.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.modemastudio.notepro.model.Feature.Contract.tableName

/**
 * Data class represents shortcut button for using markdown features.
 * @param name is spanned string that will be shown in button. Must be styled by own tag.
 * Example for H1 - # H1.
 *
 * @param tag is HTML, MD, LaTeX and s.o. that used for styling body of a note.
 * Example for H1 - #, for underline in HTML - <u></u>
 *
 * @param cursorOffset is position for cursor after the tag was placed. For example, after using H3,
 * cursor must be moved by 3 characters right, for HTML tag <strike> it must be moved by 8.
 *
 * @param isEnabled is boolean flag that shows is the feature will be shown on the note editing screen.
 * */
@Entity(tableName = tableName)
data class Feature(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.featureId)
    val featureId: Long = 0,

    @ColumnInfo(name = Columns.name)
    val name: String,

    @ColumnInfo(name = Columns.tag)
    val tag: String,

    @ColumnInfo(name = Columns.cursorOffset)
    val cursorOffset: Int,

    @ColumnInfo(name = Columns.isEnabled)
    val isEnabled: Boolean = true
) {
    companion object Contract {
        const val tableName = "features"

        object Columns {
            const val featureId = "feature_id"
            const val name = "name"
            const val tag = "tag"
            const val cursorOffset = "cursor_offset"
            const val isEnabled = "is_enabled"
        }

        val H1 = Feature(
            name = "H1",
            tag = "# ",
            cursorOffset = 2
        )

        val H2 = Feature(
            name = "H2",
            tag = "## ",
            cursorOffset = 3
        )

        val H3 = Feature(
            name = "H3",
            tag = "### ",
            cursorOffset = 4
        )

        val H4 = Feature(
            name = "H4",
            tag = "#### ",
            cursorOffset = 5
        )

        val H5 = Feature(
            name = "H5",
            tag = "##### ",
            cursorOffset = 6
        )

        val H6 = Feature(
            name = "H6",
            tag = "###### ",
            cursorOffset = 7
        )

        val bold = Feature(
            name = "bold",
            tag = "****",
            cursorOffset = 2
        )

        val italic = Feature(
            name = "italic",
            tag = "**",
            cursorOffset = 1
        )

        val listItem = Feature(
            name = "⚫",
            tag = "\n+ ",
            cursorOffset = 3
        )

        val checkbox = Feature(
            name = "▢",
            tag = "- [ ] ",
            cursorOffset = 6
        )

        val underline = Feature(
            name = "A̲",
            tag = "<u></u>",
            cursorOffset = 3
        )

        val strikethrough = Feature(
            name = "ʉ",
            tag = "~~~~",
            cursorOffset = 2
        )

        val table = Feature(
            name = "table",
            tag = """
                || 1 | 2 |
                || - | - |
                || a | b |
            """.trimMargin(),
            cursorOffset = 3
        )
    }
}