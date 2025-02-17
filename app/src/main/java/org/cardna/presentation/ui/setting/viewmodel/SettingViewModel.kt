package org.cardna.presentation.ui.setting.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.cardna.data.local.singleton.CardNaRepository
import org.cardna.data.remote.model.user.RequestDeleteUserData
import org.cardna.domain.repository.UserRepository
import org.cardna.presentation.ui.setting.view.SecessionActivity
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _pushAlarmOn = MutableLiveData(CardNaRepository.pushAlarmOn)
    val pushAlarmOn: LiveData<Boolean> = _pushAlarmOn

    private val _secessionReasonOneCheck = MutableLiveData(false)
    val secessionReasonOneCheck: LiveData<Boolean> = _secessionReasonOneCheck

    private val _secessionReasonTwoCheck = MutableLiveData(false)
    val secessionReasonTwoCheck: LiveData<Boolean> = _secessionReasonTwoCheck

    private val _secessionReasonThreeCheck = MutableLiveData(false)
    val secessionReasonThreeCheck: LiveData<Boolean> = _secessionReasonThreeCheck

    private val _secessionReasonFourCheck = MutableLiveData(false)
    val secessionReasonFourCheck: LiveData<Boolean> = _secessionReasonFourCheck

    private val _secessionReasonFiveCheck = MutableLiveData(false)
    val secessionReasonFiveCheck: LiveData<Boolean> = _secessionReasonFiveCheck

    private val _secessionReasonSixCheck = MutableLiveData(false)
    val secessionReasonSixCheck: LiveData<Boolean> = _secessionReasonSixCheck

    private val _isEtcContentValid = MutableLiveData(false)
    val isEtcContentValid: LiveData<Boolean> = _isEtcContentValid

    private val _isSecessionReasonValid = MutableLiveData(false)
    val isSecessionReasonValid: LiveData<Boolean> = _isSecessionReasonValid

    private val _isDeleteUserSuccess = MutableLiveData<Boolean>()
    val isDeleteUserSuccess: LiveData<Boolean> = _isDeleteUserSuccess

    private val _secessionReasonList = MutableLiveData(mutableListOf<Int>())
    val secessionReasonList: LiveData<MutableList<Int>> = _secessionReasonList

    private val _etcContent = MutableLiveData<String?>()
    val etcContent: LiveData<String?> = _etcContent

    private val _isAcceptPush = MutableLiveData<Boolean>(true)
    val isAcceptPush: LiveData<Boolean> = _isAcceptPush


    // 네이버 회원탈퇴 성공했는지 여부
    private val _isDeleteNaverServerUserSuccess = MutableLiveData<Boolean>()
    val isDeleteNaverServerUserSuccess: LiveData<Boolean> = _isDeleteNaverServerUserSuccess

    fun setSecessionReasonOneStatus(status: Boolean) {
        _secessionReasonOneCheck.value = status
        setSecessionReasonValid()
    }

    fun setSecessionReasonTwoStatus(status: Boolean) {
        _secessionReasonTwoCheck.value = status
        setSecessionReasonValid()
    }

    fun setSecessionReasonThreeStatus(status: Boolean) {
        _secessionReasonThreeCheck.value = status
        setSecessionReasonValid()
    }

    fun setSecessionReasonFourStatus(status: Boolean) {
        _secessionReasonFourCheck.value = status
        setSecessionReasonValid()
    }

    fun setSecessionReasonFiveStatus(status: Boolean) {
        _secessionReasonFiveCheck.value = status
        setSecessionReasonValid()
    }

    fun setSecessionReasonSixStatus(status: Boolean) {
        _secessionReasonSixCheck.value = status
        setSecessionReasonValid()
    }

    fun setEtcContentStatus(status: Boolean) {
        _isEtcContentValid.value = status
        setSecessionReasonValid()
    }

    private fun setSecessionReasonValid() {
        _isSecessionReasonValid.value =
            _secessionReasonOneCheck.value == true || _secessionReasonTwoCheck.value == true || _secessionReasonThreeCheck.value == true ||
                    _secessionReasonFourCheck.value == true || _secessionReasonFiveCheck.value == true || (_secessionReasonSixCheck.value == true && _isEtcContentValid.value == true)

        if (_secessionReasonSixCheck.value == true && _isEtcContentValid.value == false)
            _isSecessionReasonValid.value = false
    }

    fun setEtcContent(etcContent: String, status: Boolean) {
        setEtcContentStatus(!status)
        _etcContent.value = etcContent
    }


    // 네이버 회원탈퇴
    fun deleteNaverUser(context: Context){
        val deleteUserCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                Timber.d("naver 회원탈퇴 성공")
                // 서버에서 토큰 삭제에 성공한 상태입니다.
                _isDeleteNaverServerUserSuccess.value = true
            }

            override fun onFailure(httpStatus: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                Timber.d("naver 회원탈퇴 실패")
                Log.d("naver", "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                Log.d("naver", "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
                _isDeleteNaverServerUserSuccess.value = false
            }

            override fun onError(errorCode: Int, message: String) {
                Timber.d("naver 회원탈퇴 에러")
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                _isDeleteNaverServerUserSuccess.value = false
                onFailure(errorCode, message)
            }
        }

        // 네이버에서 계정 탈퇴 호출
        NidOAuthLogin().callDeleteTokenApi(context, deleteUserCallback)
    }

    // 서버 자체 회원탈퇴
    fun deleteUser() {
        if (_secessionReasonOneCheck.value == true) {
            _secessionReasonList.value?.add(SecessionActivity.SECESSION_REASON_ONE)
        }
        if (_secessionReasonTwoCheck.value == true) {
            _secessionReasonList.value?.add(SecessionActivity.SECESSION_REASON_TWO)
        }
        if (_secessionReasonThreeCheck.value == true) {
            _secessionReasonList.value?.add(SecessionActivity.SECESSION_REASON_THREE)
        }
        if (_secessionReasonFourCheck.value == true) {
            _secessionReasonList.value?.add(SecessionActivity.SECESSION_REASON_FOUR)
        }
        if (_secessionReasonFiveCheck.value == true) {
            _secessionReasonList.value?.add(SecessionActivity.SECESSION_REASON_FIVE)
        }
        if (_secessionReasonSixCheck.value == true) {
            _secessionReasonList.value?.add(SecessionActivity.SECESSION_REASON_SIX)
        }

        viewModelScope.launch {
            runCatching {
                userRepository.deleteUser(RequestDeleteUserData(_secessionReasonList.value!!, _etcContent.value ?: ""))
            }.onSuccess {
                CardNaRepository.apply {
                    Timber.e("자체 회원탈퇴 성공")
                    Timber.e("${isDeleteNaverServerUserSuccess.toString()}")
                    if (userSocial == "kakao") {
                        userSocial = ""
                        userUuid = ""
                        kakaoUserfirstName = ""
                        kakaoUserToken = ""
                        kakaoUserRefreshToken = ""
                    } else {
                        userSocial = ""
                        userUuid = ""
                        naverUserfirstName = ""
                        naverUserToken = ""
                        naverUserRefreshToken = ""
                    }
                }
                _isDeleteUserSuccess.value = true
            }.onFailure {
                Timber.e("자체 회원탈퇴 실패")
                Timber.e("${isDeleteNaverServerUserSuccess.toString()}")
                _isDeleteUserSuccess.value = false
                Timber.e(it.message)
            }
        }
    }

    fun switchPushAlarm() {
        CardNaRepository.pushAlarmOn = !CardNaRepository.pushAlarmOn
        Log.d("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", "$CardNaRepository.pushAlarmOn")
        _pushAlarmOn.value = CardNaRepository.pushAlarmOn
    }
}