package com.example.rawgamesdb.features.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.core.data.Resource
import com.example.core.domain.model.Game
import com.example.core.utils.Constant
import com.example.rawgamesdb.R
import com.example.rawgamesdb.databinding.FragmentGameDetailBinding
import com.example.rawgamesdb.features.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class GameDetailFragment : Fragment() {

    private var _binding : FragmentGameDetailBinding ?= null
    private val binding get() = _binding!!
    private lateinit var game : Game
    private val gameDetailViewModel : GameDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gameId = arguments?.getInt(HomeFragment.EXTRA_ID) ?: 0

        binding.additionalBtn.setOnClickListener{
            val intent = Intent(requireActivity(), Class.forName("com.example.additional.AdditionalActivity"))
            intent.putExtra("additional_game",game)
            startActivity(intent)
        }

        gameDetailViewModel.getGameDetail(gameId.toString(), Constant.tokenRAWGamesApi)
            .observe(viewLifecycleOwner){
                if ( it!= null) {
                    when (it) {
                        is Resource.Loading<*> -> {
                            changeUI(isLoading = true, isFavourited = false)
                        }
                        is Resource.Success<*> -> {
                            game = it.data!!

                            setGameDetail(game)
                            if (it.data?.favorite == true) {
                                changeUI(isLoading = false, isFavourited = true)
                                binding.wishlistBtn.setOnClickListener { view ->
                                    val gameDeletedText =
                                        StringBuilder(
                                            "Deleted ${game.name} from favourite list")
                                            .toString()
                                    clickAction(
                                        view,gameDeletedText,game
                                    )
                                }

                            } else {
                                changeUI(isLoading = false, isFavourited = false)
                                binding.wishlistBtn.setOnClickListener { view ->
                                    val gameAddedText =
                                        StringBuilder(
                                            "Added ${game.name} to favourite list")
                                            .toString()
                                    clickAction(
                                        view,gameAddedText, game
                                    )
                                }
                            }
                        }
                        is Resource.Error<*> -> {
                            changeUI(isLoading = false, isFavourited = false)
                        }
                    }
                }
        }
    }

    private fun changeUI(isLoading:Boolean,isFavourited:Boolean){
        if (isLoading) {
            binding.loadingDetail.visibility = View.VISIBLE
            binding.yourGameIsLoadingTv.visibility = View.VISIBLE
            binding.contentContainer.visibility = View.INVISIBLE
        } else {
            binding.loadingDetail.visibility = View.GONE
            binding.yourGameIsLoadingTv.visibility = View.GONE
            binding.contentContainer.visibility = View.VISIBLE
            if (isFavourited) {
                binding.wishlistBtn.text = StringBuilder("Delete from wishlist")
                    .toString()
                binding.wishlistBtn.backgroundTintList =
                    AppCompatResources.getColorStateList(
                        requireActivity(),R.color.steam_text_primary
                    )
            }
        }
    }

    private fun clickAction(view:View,text:String,game: Game){
        lifecycleScope.launch {
            gameDetailViewModel.updateFavouriteGame(game,game.favorite)
        }
        Toast.makeText(
            requireActivity(),
            text,
            Toast.LENGTH_SHORT).show()
        view.findNavController().navigate(R.id.action_gameDetailFragment_to_homeFragment)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setGameDetail(game: Game?){
        if (game?.id != null){
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            val formattedDate = LocalDate.parse(game.released).format(formatter)
            Glide.with(binding.root)
                .load(game.backgroundImage)
                .fitCenter()
                .centerCrop()
                .into(binding.detailGameIv)
            binding.apply {
                detailGameNameTv.text = game.name
                detailReleaseValueTv.text = formattedDate
                detailScoreValueTv.text = String.format(
                    getString(R.string.score_value_text),
                    game.rating.toString()
                )
                detailDescTv.text = HtmlCompat.fromHtml(
                    String.format(getString(R.string.desc_html_format_text), game.description),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        } else {
            Log.d("DATA KOSONG", "setGameDetail: ")
        }
    }
}