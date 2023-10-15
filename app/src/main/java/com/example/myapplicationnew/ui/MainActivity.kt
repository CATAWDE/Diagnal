package com.example.myapplicationnew.ui


import android.content.res.Configuration
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationnew.R
import com.example.myapplicationnew.databinding.ActivityMainBinding
import com.example.myapplicationnew.di.MainApplication
import com.example.myapplicationnew.di.MainViewModelFactory
import com.example.myapplicationnew.domain.entity.Content
import com.example.myapplicationnew.ui.adapter.MediaListAdapter
import com.example.myapplicationnew.utils.DataUtils
import com.example.myapplicationnew.viewmodel.MediaViewModel
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mainList: ArrayList<Content> = ArrayList()
    private var mediaListAdapter: MediaListAdapter? = null
    private var currentPageNo = 1
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mediaViewModel: MediaViewModel

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory // Dagger will provide the object to this variable through field injection
    private var isFirstTimeCall = true

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as MainApplication).mComponent?.inject(this)
        mediaViewModel = ViewModelProvider(this, mainViewModelFactory)[MediaViewModel::class.java]

        setUpUI()
        addObservers()
        clickScrollListeners()
    }

    private fun setUpUI() {
        setUpToolBar()
        fetchData()
    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        binding.customTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40f)
    }

    private fun addObservers() {
        /* Get Response in Livedata observables */
        mediaViewModel.mediaResponseLiveData.observe(this) { mainResponse ->
            binding.customTitle.text = mainResponse?.page?.title ?: ""
            mainResponse?.page?.content_items?.content?.let { mainList.addAll(it) }
            if (mediaListAdapter == null) {
                mediaListAdapter = MediaListAdapter(mainList)
                binding.mainRecyclervw.adapter = mediaListAdapter
                mGridLayoutManager = GridLayoutManager(
                    this,  //number of grid columns
                    3
                )
                binding.mainRecyclervw.layoutManager = mGridLayoutManager
            } else {
                mainResponse?.page?.page_size?.toInt()?.let {
                    mediaListAdapter?.notifyItemRangeChanged(
                        mainList.size - 1,
                        it
                    )
                }
            }
            currentPageNo++
        }
        binding.mainRecyclervw.itemAnimator = null
    }

    private fun clickScrollListeners() {

        binding.mainRecyclervw.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isFirstTimeCall) {
                        isFirstTimeCall = false
                        val currentItems = mGridLayoutManager.childCount
                        val totalItems = mGridLayoutManager.itemCount
                        val scrolledOutItems = mGridLayoutManager.findFirstVisibleItemPosition()
                        /* On Scroll as current page ends, fetch data of next page */
                        if ((currentItems + scrolledOutItems == totalItems) && currentPageNo <= 3) {
                            fetchData()
                        }
                    }
                }
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isFirstTimeCall = true
                }
            }
        })

        binding.backImg.setOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchData() {
        mediaViewModel.getPageResponseFromPageNumber(currentPageNo.toString(), this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val orientation = newConfig.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridLayoutManager.spanCount = 3
            notifyInvalidate()
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mGridLayoutManager.spanCount = 7
            notifyInvalidate()
        }
    }

    private fun notifyInvalidate() {
        mediaListAdapter?.notifyDataSetChanged()
        binding.mainRecyclervw.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        /* Get Menu Items */
        val mSearchMenuItem: MenuItem = menu.findItem(R.id.action_search)

        /* Get Search View from Menu Items */
        val searchView = mSearchMenuItem.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint =
            Html.fromHtml("<font color = #ffffff>" + getString(R.string.search_hint) + "</font>")

        /* Get Search View- Close button */
        setSearchCloseBtnClick(searchView, mSearchMenuItem)

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty() && newText.length > 2) {
                    if (newText.length > 6) {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.search_hint_max),
                            Toast.LENGTH_SHORT
                        ).show()
                        return false
                    }
                    /* Filter list as per query and pass it to adapter */
                    val filtered: ArrayList<Content> = ArrayList()
                    mainList.filter {
                        (it.name.lowercase().startsWith(newText.lowercase()) || (it.name.lowercase()
                            .contains(newText.lowercase())))
                    }.onEach { it1 -> filtered.add(it1) }
                    mediaListAdapter?.setData(filtered)
                } else if (newText.isNullOrEmpty()) {
                    mediaListAdapter?.setData(mainList)
                } else if (newText.length < 2) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.search_hint),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return false
            }
        })
        return true
    }


    private fun setSearchCloseBtnClick(searchView: SearchView, mSearchMenuItem: MenuItem) {

        /* Get Search View- Close button's Image as provided */

        val closeButtonImage: ImageView =
            searchView.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeButtonImage.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.search_cancel
            )
        )
        /* Get Search View- Close button's Click  */
        closeButtonImage.setOnClickListener {
            mediaListAdapter?.setData(mainList)
            //Clear query
            searchView.setQuery("", false)
            //Collapse the action view
            searchView.onActionViewCollapsed()
            //Collapse the search widget
            mSearchMenuItem.collapseActionView()
        }
    }

    override fun onStop() {
        super.onStop()
        mediaViewModel.mediaResponseLiveData.removeObservers(this)
    }


    override fun onBackPressed() {
        /* On Back Pressed, Show exit popup */
        val factory = LayoutInflater.from(this)
        val exitDialogView: View = factory.inflate(R.layout.exit_dialog, null)
        val exitDialog = AlertDialog.Builder(this).create()
        exitDialog.setView(exitDialogView)
        exitDialogView.findViewById<View>(R.id.ysBtn)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    //your business logic
                    super@MainActivity.onBackPressed()
                }
            })
        exitDialogView.findViewById<View>(R.id.noButton)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    exitDialog.dismiss()
                }
            })

        exitDialog.show()
    }

}