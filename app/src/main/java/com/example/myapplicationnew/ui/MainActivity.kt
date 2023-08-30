package com.example.myapplicationnew.ui

import android.content.res.Configuration
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationnew.R
import com.example.myapplicationnew.databinding.ActivityMainBinding
import com.example.myapplicationnew.domain.entity.Content
import com.example.myapplicationnew.ui.adapter.MediaListAdapter
import com.example.myapplicationnew.utils.AppConstant
import com.example.myapplicationnew.utils.FontUtils
import com.example.myapplicationnew.viewmodel.MediaViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var binding: ActivityMainBinding
    private var mapList: ArrayList<Content> = ArrayList()
    private var mediaListAdapter:MediaListAdapter?=null
    private var currentItems:Int =0
    private var totalItems:Int= 0
    private var scrolledOutItems:Int=0
    private var isScrolling:Boolean= false
    private var currentPageNo = 1
    private var mediaViewModel:MediaViewModel?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaViewModel= MediaViewModel()
        setUpToolBar()
        initUI()
        addObservers()
    }

    private fun setUpToolBar() {
        FontUtils.setTypeface(binding.customTitle, AppConstant.FONT_TYPE_SEMIBOLD)
        setSupportActionBar(binding.toolbar)
        binding.customTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45f)
        supportActionBar?.title = ""
        binding.toolbar.navigationIcon= getDrawable(R.drawable.back)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val mSearchMenuItem:MenuItem= menu.findItem(R.id.action_search)
        mSearchMenuItem.icon= getDrawable(R.drawable.search)

        val searchView= mSearchMenuItem.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint = Html.fromHtml("<font color = #ffffff>" + getString(R.string.search_hint) + "</font>")

        val closeButtonImage: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeButtonImage.setImageDrawable(getDrawable(R.drawable.search_cancel))

        searchView.setOnQueryTextListener(object :OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty() && query.length > 2) {
                    //mediaListAdapter.getFilter().filter(query)
                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.search_condition), Toast.LENGTH_LONG).show()
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

    private fun initUI(){
        fetchData()
        binding.mainRecyclervw.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItems =mGridLayoutManager.childCount
                totalItems=mGridLayoutManager.itemCount
                scrolledOutItems= mGridLayoutManager.findFirstVisibleItemPosition()
                if(isScrolling && (currentItems + scrolledOutItems == totalItems) && currentPageNo < 4){
                    //fetch data
                    isScrolling =false
                    fetchData()
                }
            }
        })

    }

    private fun addObservers() {
        mediaViewModel?.mediaResponseLiveData?.observe(this){ mainResponse ->
            binding.customTitle.text = mainResponse?.page?.title?:""
            mainResponse?.page?.content_items?.content?.let { mapList.addAll(it) }
            if(mediaListAdapter == null){
                mediaListAdapter = MediaListAdapter(mapList)
                binding.mainRecyclervw.adapter = mediaListAdapter
                mGridLayoutManager = GridLayoutManager(
                    this,  //number of grid columns
                    3
                )
                binding.mainRecyclervw.layoutManager = mGridLayoutManager
            }
            else{
                mainResponse?.page?.page_size?.toInt()?.let {
                    mediaListAdapter?.notifyItemRangeChanged(mapList.size-1,
                        it
                    )
                }
            }
            currentPageNo++
        }
    }


    private fun fetchData() {
         mediaViewModel?.getPageResponseFromPageNumber(currentPageNo.toString(),this)
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
}