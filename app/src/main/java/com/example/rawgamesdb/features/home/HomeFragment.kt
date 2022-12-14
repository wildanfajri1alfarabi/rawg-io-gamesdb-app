package com.example.rawgamesdb.features.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.core.data.Resource
import com.example.core.domain.model.Game
import com.example.core.ui.HomeAdapter
import com.example.core.utils.Constant
import com.example.rawgamesdb.R
import com.example.rawgamesdb.databinding.FragmentHomeBinding
import com.example.rawgamesdb.features.detail.GameDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel : HomeViewModel by viewModels ()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gamesRv.setHasFixedSize(true)

        binding.gotoFavActionBtn.setOnClickListener {
            val intent = Intent(
                requireActivity(),
                Class.forName("com.example.additional.FavouriteActivity")
            )
            startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener {
            homeViewModel.logout
            view.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        homeViewModel.getAllGameFromApi(Constant.tokenRAWGamesApi).observe(viewLifecycleOwner){
            if ( it!= null) {
                when (it) {
                    is Resource.Loading<*> -> {
                        showLoading(true)
                    }
                    is Resource.Success<*> -> {
                        showLoading(false)
                        setRvData(it.data)
                    }
                    is Resource.Error<*> -> {
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun setRvData(gameList: List<Game>?){
        binding.gamesRv.layoutManager = GridLayoutManager(requireActivity(),2)
        if (!gameList.isNullOrEmpty()){
            val rvAdapter = HomeAdapter(gameList, onClick = {
                val intent = Intent(requireActivity(),GameDetailActivity::class.java)
                intent.putExtra(EXTRA_ID,it.id)
                startActivity(intent)
            })
            binding.gamesRv.adapter = rvAdapter
        }
    }

    private fun showLoading(isLoading:Boolean){
        binding.apply {
            if (isLoading) {
                loadingGameList.visibility = View.VISIBLE
                yourGameIsLoadingTv.visibility = View.VISIBLE
                contentContainer.visibility = View.INVISIBLE
            } else {
                loadingGameList.visibility = View.GONE
                yourGameIsLoadingTv.visibility = View.GONE
                contentContainer.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}