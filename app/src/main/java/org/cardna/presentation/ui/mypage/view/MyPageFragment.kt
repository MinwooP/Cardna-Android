package org.cardna.presentation.ui.mypage.view

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import org.cardna.R
import org.cardna.databinding.FragmentMyPageBinding
import dagger.hilt.android.AndroidEntryPoint
import org.cardna.presentation.base.BaseViewUtil
import org.cardna.presentation.ui.maincard.view.MainCardActivity
import org.cardna.presentation.ui.maincard.view.MainCardFragment
import org.cardna.presentation.ui.mypage.adapter.MyPageFriendAdapter
import org.cardna.presentation.ui.mypage.viewmodel.MyPageViewModel
import org.cardna.presentation.ui.setting.view.SettingActivity
import org.cardna.presentation.util.*
import timber.log.Timber


@AndroidEntryPoint
class MyPageFragment : BaseViewUtil.BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val myPageViewModel: MyPageViewModel by activityViewModels()
    private lateinit var myPageFriendAdapter: MyPageFriendAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.myPageViewModel = myPageViewModel
        binding.myPageFragment = this
        initView()
    }

    override fun onResume() {
        super.onResume()
        binding.etMypageNameSearchBackground.clearFocus()
//        initData()
    }

    override fun initView() {
      //  initData()

        //처음 들어갈떄 뿌리긴 해야하니까

        setStickyScroll()
        setMyPageFriendAdapter()
        setInputField()
        setObserve()
        copyMyCodeClickListener()
        setSettingBtnValidObserve()
        initRootClickEvent(binding.ctlMypageTop)
        initRootClickEvent(binding.ctlMypageHeader)
    }

    private fun initData() {
        val query = myPageViewModel.searchNameQuery.value ?: ""

        if ((query.isNullOrEmpty() && myPageViewModel.updateSearchNameQuerySuccess.value == true) ||
            (query.isNullOrEmpty() && myPageViewModel.updateSearchNameQuerySuccess.value == false)
        ) {
           myPageViewModel.getUserMyPage()
            myPageViewModel.setUpdateSearchNameQueryState(false)
        } else if ((query.isNotEmpty() && myPageViewModel.updateSearchNameQuerySuccess.value == false)) {
          //  myPageViewModel.updateSearchNameQuery(query)
        }
    }

    private fun setStickyScroll() {
        binding.scMypage.run {
            header = binding.ctlMypageHeader
        }
    }

    fun setSettingClickListener() {
        startActivity(Intent(requireContext(), SettingActivity::class.java))
    }

    fun setSearchCodeClickListener() {
        startActivity(Intent(requireContext(), SearchFriendCodeActivity::class.java))
    }

    private fun setMyPageFriendAdapter() {
        myPageFriendAdapter = MyPageFriendAdapter(requireActivity(), myPageViewModel) { item ->
            val bundle = Bundle().apply {
                putInt("id", item.id)
                putString("name", item.name)
                putString("sentence", item.sentence)
            }

            val mainCardFragment = MainCardFragment()
            mainCardFragment.arguments = bundle

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .add(R.id.fcv_main, mainCardFragment)
            transaction.commit()
        }
        binding.rvMypage.addItemDecoration(MyPageItemVerticalDecoration())
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        with(binding) {
            rvMypage.layoutManager = gridLayoutManager
            rvMypage.adapter = myPageFriendAdapter
        }
    }

    fun setInputField() {
        with(binding.etMypageNameSearchBackground) {
            setTextSize(16f)
            setTextColor(requireContext(), R.color.white_2, R.color.white_1)


            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(newText: String?): Boolean {
                    if (newText?.isNotEmpty() == true) {
                        //todo 검색 -> 검색내용 업데이트
                        myPageViewModel.updateSearchNameQuery(newText.toString())
                        clearFocus()
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //todo 초기화-> X버튼 눌렀을때
                    if (newText.isNullOrEmpty()) {
                        myPageViewModel.updateSearchNameQuery("")
                   /*     myPageViewModel.setUpdateSearchNameQueryState(true)
                        myPageViewModel.updateSearchNameQuerySuccess.observe(viewLifecycleOwner) {
                            if (it) initData()
                        }*/
                    }
                    return false
                }
            })
        }
    }

    private fun setObserve() {
        //todo 쿼리가 업데이트 되면 검색api호출
        myPageViewModel.searchNameQuery.observe(viewLifecycleOwner) {
            myPageViewModel.searchNamePost()
        }


        //이건 걍 내정보
        if (binding.etMypageNameSearchBackground.query.isNullOrEmpty()) {
            myPageViewModel.myPage.observe(viewLifecycleOwner) { myPage ->
                //todo 맨처음에는 마이페이지 친구 리스트 던져야함
                myPageFriendAdapter.submitList(myPage.friendList)
                requireActivity().setSrcWithGlide(myPage.userImg, binding.ivMypageUserimg)
            }
        }

        //todo 검색결과 있으면 submitlist
        myPageViewModel.searchFriendNameResult.observe(viewLifecycleOwner) { searchFriendNameResult ->
            myPageFriendAdapter.submitList(searchFriendNameResult)
        }
    }

    private fun copyMyCodeClickListener() {
        binding.ivMypageCode.setOnClickListener {
            createClipData(binding.tvMypageCode.text.toString())
        }
    }

    private fun createClipData(message: String) {
        val clipBoardManger: ClipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("TAG", message)
        clipBoardManger.setPrimaryClip(clipData)
        requireContext().shortToast("코드가 복사되었습니다")
    }

    private fun setSettingBtnValidObserve() {
        myPageViewModel.settingBtnIsValid.observe(viewLifecycleOwner) {
            binding.ivMypageSetting.isClickable = it
        }
    }
}