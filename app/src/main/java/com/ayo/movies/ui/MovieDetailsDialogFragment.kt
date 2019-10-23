package com.ayo.movies.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.ayo.domain.model.MovieDomain
import com.ayo.movies.R
import com.bumptech.glide.Glide
import dagger.android.support.DaggerDialogFragment
import kotlinx.android.synthetic.main.fragment_movie_details.*

class MovieDetailsDialogFragment : DaggerDialogFragment() {

    private val viewModel by lazy { (activity as MainActivity).viewModel }

    private var movieId: Int? = null
    private lateinit var movie: MovieDomain

    companion object {
        const val TAG = "MovieDetailsDialogFragment"
        fun newInstance(id: Int): MovieDetailsDialogFragment {
            val fragment = MovieDetailsDialogFragment()
            fragment.movieId = id
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        loading_flipper.displayedChild = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpListener()
        loadData()
        observeViewModel()
    }

    private fun setUpView() {
        when (viewModel.isMovieFavourite(movieId)) {
            true -> {
                favourite_switch.isChecked = true
                favourite_switch.text = getString(R.string.remove_from_favourites)
            }
            false -> {
                favourite_switch.isChecked = false
                favourite_switch.text = getString(R.string.add_to_favourites)
            }
        }
    }

    private fun setUpListener() {
        favourite_switch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.apply {
                if (isChecked) {
                    addMovieToFavourites(movie)
                    favourite_switch.text = getString(R.string.remove_from_favourites)
                } else {
                    removeMovieFromFavourites(movie.id)
                    favourite_switch.text = getString(R.string.add_to_favourites)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.movieDetailsLiveData.observe(this, Observer { handleMovieDetails(it) })
    }

    private fun handleMovieDetails(movie: MovieDomain) {
        this.movie = movie
        val imageUrl = "https://image.tmdb.org/t/p/w500${movie.imgUrl}"
        Glide
            .with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .into(image)

        title.text = movie.title
        details.text = movie.overview
        val runTimeText = "${movie.runtime} mins"
        runtime.text = runTimeText

        loading_flipper.displayedChild = 1

    }

    private fun loadData() {
        movieId?.let { id -> viewModel.loadMovieDetails(id) }
    }
}