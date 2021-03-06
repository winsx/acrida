package net.gility.acrida.ui.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import net.gility.acrida.R;
import net.gility.acrida.android.ApplicationLoader;
import net.gility.acrida.content.Tweet;
import net.gility.acrida.ui.widget.RecordButton;
import net.gility.acrida.ui.widget.RecordButton.OnFinishedRecordListener;
import net.gility.acrida.ui.widget.RecordButtonUtil.OnPlayListener;
import net.gility.acrida.utils.ServerTaskUtils;
import net.gility.acrida.utils.StringUtils;
import net.gility.acrida.utils.TDevice;
import net.gility.acrida.utils.UIHelper;

import org.kymjs.kjframe.utils.DensityUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 语音动弹发布界面
 * 
 * @author kymjs(kymjs123@gmail.com)
 * 
 */
public class TweetRecordFragment extends BaseFragment {

    @BindView(R.id.tweet_layout_record)
    RelativeLayout mLayout;
    @BindView(R.id.tweet_btn_record)
    RecordButton mBtnRecort;
    @BindView(R.id.tweet_time_record)
    TextView mTvTime;
    @BindView(R.id.tweet_text_record)
    TextView mTvInputLen;
    @BindView(R.id.tweet_edit_record)
    EditText mEtSpeech;
    @BindView(R.id.tweet_img_volume)
    ImageView mImgVolume;

    public static int MAX_LEN = 160;

    private AnimationDrawable drawable; // 录音播放时的动画背景

    private String strSpeech = "#语音动弹#";
    private int currentRecordTime = 0;

    @Override
    public void onClick(View v) {
        if (v == mLayout) {
            mBtnRecort.playRecord();
        }
    }

    @Override
    public void initView(View view) {
        RelativeLayout.LayoutParams params = (LayoutParams) mBtnRecort
                .getLayoutParams();
        params.width = DensityUtils.getScreenW(getActivity());
        params.height = (int) (DensityUtils.getScreenH(getActivity()) * 0.4);
        mBtnRecort.setLayoutParams(params);
        mLayout.setOnClickListener(this);

        mBtnRecort.setOnFinishedRecordListener(new OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int recordTime) {
                currentRecordTime = recordTime;
                mLayout.setVisibility(View.VISIBLE);
                if (recordTime < 10) {
                    mTvTime.setText("0" + recordTime + "\"");
                } else {
                    mTvTime.setText(recordTime + "\"");
                }
            }

            @Override
            public void onCancleRecord() {
                mLayout.setVisibility(View.GONE);
            }
        });

        drawable = (AnimationDrawable) mImgVolume.getBackground();
        mBtnRecort.getAudioUtil().setOnPlayListener(new OnPlayListener() {
            @Override
            public void stopPlay() {
                drawable.stop();
                mImgVolume.setBackgroundDrawable(drawable.getFrame(0));
            }

            @Override
            public void starPlay() {
                mImgVolume.setBackgroundDrawable(drawable);
                drawable.start();
            }
        });

        mEtSpeech.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (s.length() > MAX_LEN) {
                    mTvInputLen.setText("已达到最大长度");
                } else {
                    mTvInputLen.setText("您还可以输入" + (MAX_LEN - s.length())
                            + "个字符");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MAX_LEN) {
                    mEtSpeech.setText(s.subSequence(0, MAX_LEN));
                    CharSequence text = mEtSpeech.getText();
                    if (text instanceof Spannable)
                        Selection.setSelection((Spannable) text, MAX_LEN);
                }
            }
        });
    }

    @Override
    public void initData() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.item_tweet_pub_record,
                container, false);
        ButterKnife.bind(this, rootView);
        initView(rootView);
        initData();
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pub_tweet_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.public_menu_send:
            handleSubmit(mBtnRecort.getCurrentAudioPath());
            break;
        }
        return true;
    }

    /**
     * 发布动弹
     */
    private void handleSubmit(String audioPath) {
        if (!TDevice.hasInternet()) {
            ApplicationLoader.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!ApplicationLoader.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        if (StringUtils.isEmpty(audioPath)) {
            ApplicationLoader.showToastShort(R.string.record_sound_notfound);
            return;
        }
        File file = new File(audioPath);
        if (!file.exists()) {
            ApplicationLoader.showToastShort(R.string.record_sound_notfound);
            return;
        }

        String body = mEtSpeech.getText().toString();
        if (!StringUtils.isEmpty(body)) {
            strSpeech = body;
        }
        Tweet tweet = new Tweet();
        tweet.setAuthorid(ApplicationLoader.getInstance().getLoginUid());
        tweet.setAudioPath(audioPath);
        tweet.setBody(strSpeech);
        ServerTaskUtils.pubTweet(getActivity(), tweet);
        getActivity().finish();
    }
}
