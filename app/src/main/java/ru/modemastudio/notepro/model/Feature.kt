package ru.modemastudio.notepro.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.modemastudio.notepro.model.Feature.Contract.tableName

/**
 * Data class represents shortcut button for using markdown features.
 * @param name is spanned string that will be shown in button. Must be styled by own tag.
 * Example for H1 - #H1.
 *
 * @param tag is HTML, MD, LaTeX and s.o. that used for styling body of a note.
 * Example for H1 - #, for underline in HTML - <u></u>
 *
 * @param cursorOffset is position for cursor after the tag was placed. For example, after using H3,
 * cursor must be moved by 3 characters right, for HTML tag <strike> it must be moved by 8.
 *
 * @param isEnabled is boolean flag that shows is the feature will be shown on the note editing screen.
 *
 * [getAllFeatures] must include all features that should be added on first application launch
 * */
@Entity(tableName = tableName)
open class Feature(
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

    class H1 : Feature(
        name = "# H1",
        tag = "# ",
        cursorOffset = 2
    )

    class H2 : Feature(
        name = "# H2",
        tag = "## ",
        cursorOffset = 3
    )

    class H3 : Feature(
        name = "# H3",
        tag = "### ",
        cursorOffset = 4
    )

    class H4 : Feature(
        name = "# H4",
        tag = "#### ",
        cursorOffset = 5
    )

    class H5 : Feature(
        name = "# H5",
        tag = "##### ",
        cursorOffset = 6
    )

    class H6 : Feature(
        name = "# H6",
        tag = "###### ",
        cursorOffset = 7
    )

    companion object Contract {
        const val tableName = "features"

        object Columns {
            const val featureId = "feature_id"
            const val name = "name"
            const val tag = "tag"
            const val cursorOffset = "cursor_offset"
            const val isEnabled = "is_enabled"
        }

        fun getAllFeatures(): List<Feature> {
            return listOf(H1(), H2(), H3(), H4(), H5(), H6())
        }
    }
}