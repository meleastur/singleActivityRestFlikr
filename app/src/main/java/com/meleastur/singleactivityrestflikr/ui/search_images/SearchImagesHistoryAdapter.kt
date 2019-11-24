package com.meleastur.singleactivityrestflikr.ui.search_images

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.meleastur.singleactivityrestflikr.R


class SearchImagesHistoryAdapter(
    context: Context,
    private var searchImageList: ArrayList<String>,
    cursor: Cursor
) : CursorAdapter(context, cursor, true) {

    private var textView: TextView? = null

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view =
            inflater.inflate(R.layout.item_search_images_history, parent, false)

        textView = view.findViewById(R.id.text) as TextView

        return view
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        textView!!.text = searchImageList[cursor!!.position]
    }

    interface ItemClickListener {
        /*   fun itemDetail(searchImage: SearchImage)

           fun itemPositionChange(page: Int, perPage: Int, position: Int)

           fun itemBottomReached()*/
    }
}