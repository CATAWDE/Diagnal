package com.example.myapplicationnew.ui

import android.content.res.Configuration
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationnew.R
import com.example.myapplicationnew.databinding.ActivityMainBinding
import com.example.myapplicationnew.di.MainApplication
import com.example.myapplicationnew.di.MainViewModelFactory
import com.example.myapplicationnew.domain.entity.Content
import com.example.myapplicationnew.ui.adapter.MediaListAdapter
import com.example.myapplicationnew.utils.AppConstant
import com.example.myapplicationnew.utils.FontUtils
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
        binding.customTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45f)
        FontUtils.setTypeface(binding.customTitle, AppConstant.FONT_TYPE_SEMIBOLD)
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
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) //check for scroll down
                {
                    val currentItems = mGridLayoutManager.childCount
                    val totalItems = mGridLayoutManager.itemCount
                    val scrolledOutItems = mGridLayoutManager.findFirstVisibleItemPosition()
                    /* On Scroll as curret page ends, fetch data of next page */
                    if ((currentItems + scrolledOutItems == totalItems) && currentPageNo < 4) {
                        fetchData()
                    }
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
        // Get Menu Items
        val mSearchMenuItem: MenuItem = menu.findItem(R.id.action_search)
        // Get Search view from Menu Items
        val searchView = mSearchMenuItem.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint =
            Html.fromHtml("<font color = #ffffff>" + getString(R.string.search_hint) + "</font>")

        //Get Close button from Search View
        setSearchClosebtnClick(searchView, mSearchMenuItem)

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty() && newText.length > 2) {
                    val filtered: ArrayList<Content> = ArrayList()
                    mainList.filter {
                        (it.name.lowercase().startsWith(newText.lowercase()) || (it.name.lowercase()
                            .contains(newText.lowercase())))
                    }.onEach { it1 -> filtered.add(it1) }
                    mediaListAdapter?.setData(filtered)
                }else if(newText.isNullOrEmpty()){
                    mediaListAdapter?.setData(mainList)

                }
                return false
            }
        })
        return true
    }


    fun setSearchClosebtnClick(searchView: SearchView, mSearchMenuItem: MenuItem) {

        val closeButtonImage: ImageView =
            searchView.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeButtonImage.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.search_cancel
            )
        )

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
        //On Back pressed, Show exit popup
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.Dialog)
        builder.setTitle(getString(R.string.exit_app))
            .setMessage(getString(R.string.exit_msg))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.yes_text)
            ) { _, _ -> super@MainActivity.onBackPressed() }
            .setNegativeButton(
                getString(R.string.no_text)
            ) { dialog, _ -> dialog.cancel() }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
        setTextFontPopUI(alertDialog)
    }

    private fun setTextFontPopUI(alertDialog: AlertDialog) {
        val textView = alertDialog.window?.findViewById<View>(android.R.id.message) as TextView
        val yesBtn = alertDialog.window?.findViewById<View>(android.R.id.button1) as Button
        val noBtn = alertDialog.window?.findViewById<View>(android.R.id.button2) as Button

        textView.setTextColor(ContextCompat.getColor(this, R.color.black))
        yesBtn.setTextColor(ContextCompat.getColor(this, R.color.black))
        noBtn.setTextColor(ContextCompat.getColor(this, R.color.black))

        FontUtils.setTypeface(textView, AppConstant.FONT_TYPE_SEMIBOLD)
        FontUtils.setTypeface(yesBtn, AppConstant.FONT_TYPE_SEMIBOLD)
        FontUtils.setTypeface(noBtn, AppConstant.FONT_TYPE_SEMIBOLD)
    }
}