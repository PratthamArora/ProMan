package com.pratthamarora.proman.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.pratthamarora.proman.R
import com.pratthamarora.proman.activities.TaskListActivity
import com.pratthamarora.proman.model.Task
import kotlinx.android.synthetic.main.item_task.view.*
import java.util.*


open class TaskListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Task>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // A global variable for position dragged FROM.
    private var mPositionDraggedFrom = -1

    // A global variable for position dragged TO.
    private var mPositionDraggedTo = -1

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        // Here the layout params are converted dynamically according to the screen size as width is 70% and height is wrap_content.
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // Here the dynamic margins are applied to the view.
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            if (position == list.size - 1) {
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.ll_task_item.visibility = View.GONE
            } else {
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.ll_task_item.visibility = View.VISIBLE
            }

            holder.itemView.tv_task_list_title.text = model.title

            holder.itemView.tv_add_task_list.setOnClickListener {

                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.cv_add_task_list_name.visibility = View.VISIBLE
            }

            holder.itemView.ib_close_list_name.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.cv_add_task_list_name.visibility = View.GONE
            }

            holder.itemView.ib_done_list_name.setOnClickListener {
                val listName = holder.itemView.et_task_list_name.text.toString()

                if (listName.isNotEmpty()) {
                    // Here we check the context is an instance of the TaskListActivity.
                    if (context is TaskListActivity) {
                        context.createTaskList(listName)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.ib_edit_list_name.setOnClickListener {

                holder.itemView.et_edit_task_list_name.setText(model.title) // Set the existing title
                holder.itemView.ll_title_view.visibility = View.GONE
                holder.itemView.cv_edit_task_list_name.visibility = View.VISIBLE
            }

            holder.itemView.ib_close_editable_view.setOnClickListener {
                holder.itemView.ll_title_view.visibility = View.VISIBLE
                holder.itemView.cv_edit_task_list_name.visibility = View.GONE
            }

            holder.itemView.ib_done_edit_list_name.setOnClickListener {
                val listName = holder.itemView.et_edit_task_list_name.text.toString()

                if (listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.updateTaskList(position, listName, model)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.ib_delete_list.setOnClickListener {

                alertDialogForDeleteList(position, model.title)
            }

            holder.itemView.tv_add_card.setOnClickListener {

                holder.itemView.tv_add_card.visibility = View.GONE
                holder.itemView.cv_add_card.visibility = View.VISIBLE

                holder.itemView.ib_close_card_name.setOnClickListener {
                    holder.itemView.tv_add_card.visibility = View.VISIBLE
                    holder.itemView.cv_add_card.visibility = View.GONE
                }

                holder.itemView.ib_done_card_name.setOnClickListener {

                    val cardName = holder.itemView.et_card_name.text.toString()

                    if (cardName.isNotEmpty()) {
                        if (context is TaskListActivity) {
                            context.addCardToTaskList(position, cardName)
                        }
                    } else {
                        Toast.makeText(context, "Please Enter Card Detail.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            holder.itemView.rv_card_list.layoutManager = LinearLayoutManager(context)
            holder.itemView.rv_card_list.setHasFixedSize(true)

            val adapter =
                CardListItemsAdapter(context, model.cards)
            holder.itemView.rv_card_list.adapter = adapter

            adapter.setOnClickListener(object :
                CardListItemsAdapter.OnClickListener {
                override fun onClick(cardPosition: Int) {
                    if (context is TaskListActivity) {
                        context.cardDetails(position, cardPosition)
                    }
                }
            })

            /**
             * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
             * {@link LinearLayoutManager}.
             *
             * @param context Current context, it will be used to access resources.
             * @param orientation Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
             */
            val dividerItemDecoration =
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            holder.itemView.rv_card_list.addItemDecoration(dividerItemDecoration)

            //  Creates an ItemTouchHelper that will work with the given Callback.
            val helper = ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

                /*Called when ItemTouchHelper wants to move the dragged item from its old position to
                 the new position.*/
                override fun onMove(
                    recyclerView: RecyclerView,
                    dragged: ViewHolder,
                    target: ViewHolder
                ): Boolean {
                    val draggedPosition = dragged.adapterPosition
                    val targetPosition = target.adapterPosition

                    if (mPositionDraggedFrom == -1) {
                        mPositionDraggedFrom = draggedPosition
                    }
                    mPositionDraggedTo = targetPosition

                    /**
                     * Swaps the elements at the specified positions in the specified list.
                     */
                    Collections.swap(list[position].cards, draggedPosition, targetPosition)

                    // move item in `draggedPosition` to `targetPosition` in adapter.
                    adapter.notifyItemMoved(draggedPosition, targetPosition)

                    return false // true if moved, false otherwise
                }

                // Called when a ViewHolder is swiped by the user.
                override fun onSwiped(
                    viewHolder: ViewHolder,
                    direction: Int
                ) { // remove from adapter
                }

                /*Called by the ItemTouchHelper when the user interaction with an element is over and it
                 also completed its animation.*/
                override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
                    super.clearView(recyclerView, viewHolder)

                    if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo) {

                        (context as TaskListActivity).updateCardsInTaskList(
                            position,
                            list[position].cards
                        )
                    }

                    // Reset the global variables
                    mPositionDraggedFrom = -1
                    mPositionDraggedTo = -1
                }
            })

            /*Attaches the ItemTouchHelper to the provided RecyclerView. If TouchHelper is already
            attached to a RecyclerView, it will first detach from the previous one.*/
            helper.attachToRecyclerView(holder.itemView.rv_card_list)
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function to get density pixel from pixel
     */
    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    /**
     * A function to get pixel from density pixel
     */
    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    /**
     * Method is used to show the Alert Dialog for deleting the task list.
     */
    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}